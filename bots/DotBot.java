/** 
 * What went well...
 *  Dodging during the mid game worked perfectly
 *  My robot could shoot other bots while dodging
 * 
 * What didn't...
 *  Robot got stuck on tombstones (fix with A*)
 *  Robot was chooseing bad targets
 *  Robot didn't shoot enough
 *  Died randomly at the start
 * 
 * TODO:
 *  Make A* like pathfinding stack
 *  Make start scquence path away from other robots
 *  Make Target pioritization
 *   - proximity
 *   - isOverheated
 *   - Time shot
 *   - Time stationary
 * 
 *  Stretch goals ...
 *    Make smart shot leading that will only attempt to lead shots in the correct case  
 *
 * New stratagy 
 *  Track robot stats
 * 
 *  If i can shoot
 *     If a target is aligned shoot
 *     Choose the dir with the most players and no dead
 * 
 *  If i need to dodge 
 *     Dodge
 * 
 *  Weighted sort of targets
 *  Choose best target
 * 
 *  Make path to best target
 *  Move allong path
 */

 /**
  Plans

  Target Finding
    if(target dies or dont have target)
        Choose best target based on tracked methods
    
    OnUpdate()
        FindPath to target
        SetPath variable
        
    Foreach bullet
        if bullet will collide move away

    MoveTowards to next node in path



  Strategy 





  */

  package bots;

  import java.awt.Color;
  import java.awt.Graphics;
  import java.awt.Image;
  import java.util.ArrayList;
  import java.util.HashMap;
  import java.util.List;
  import java.util.Map;
  import java.util.Vector;
  import java.util.Map.Entry;
  import java.util.function.Consumer;
  
  import javax.swing.plaf.basic.BasicBorders.RadioButtonBorder;
  
  import arena.BattleBotArena;
  import arena.BotInfo;
  import arena.Bullet;
  import bots.Vector2;
  
  public class DotBot extends Bot{
  
      
      private final double xMidline = (BattleBotArena.TOP_EDGE  + BattleBotArena.BOTTOM_EDGE)/2;
      private final double yMidline = (BattleBotArena.LEFT_EDGE + BattleBotArena.RIGHT_EDGE )/2;
  
      private final int DANGE_DISTANCE = 150; 
      private final int WALL_DISTANCE = 50;
      private final int BULLET_DISTANCE = 30;
  
      private final int ACCURACY = 5;
      private final int KILL_DISTANCE = 100;
  
      private final double TARGETING_THRESHHOLD = 0.8;
  
      private final int TARGETING_DELAY_START = 100;
      private int targetingDelayCounter = 0;
  
      private int last_spray_direction = 0;
  
      private final int SHOOT_DELAY_START   = 20;
      private int shootDelayCounter = 0;
  
      private double w_proxy      = 1;
      private double w_shooting   = 0.5;
      private double w_stationary = 2;
      private double w_overheated = 5;
      private double w_dodged     = 3;
  
      private int GRID_RESOLUTION = 10;
      private int GRID_SIZE_X = (BattleBotArena.LEFT_EDGE - BattleBotArena.RIGHT_EDGE )/GRID_RESOLUTION;
      private int GRID_SIZE_Y = (BattleBotArena.TOP_EDGE  - BattleBotArena.BOTTOM_EDGE)/GRID_RESOLUTION;
  
      private final int X_TARGET = 300/GRID_RESOLUTION;
      private final int Y_TARGET = 300/GRID_RESOLUTION;
  
      private BotHelper botHelper = new BotHelper();
  
      private BotTracker meTracker = new BotTracker();
  
      private Map<String, BotTracker> trackedInfo = new HashMap<String, BotTracker>();
  
      private int[][] grid;
  
      private List<Vector2> path = new ArrayList(); 
  
      private String currentTarget = "";    
  
      @Override
      public void newRound() {
          shootDelayCounter = SHOOT_DELAY_START;
          targetingDelayCounter = TARGETING_DELAY_START;
  
          meTracker.reset();
  
          for(Entry<String, BotTracker> info : trackedInfo.entrySet()){
              info.getValue().reset();
          }

          for(int i = 0; i < 10; i++){
            path.add(new Vector2(100, 100));
            path.add(new Vector2(500, 100));
          }
      }
  
      // ANCHOR PATHFINDING 
    private int getNextInPath(BotInfo me){
        if(path.size() == 0){
            return BattleBotArena.STAY;
        }

        double x_diff = me.getX() - path.get(0).x;
        double y_diff = me.getY() - path.get(0).y;

        int move_choice;

        // MOVE TO TARGET POSITION
        if(Math.abs(x_diff) > Math.abs(y_diff)){
            if(x_diff <= 0){
                move_choice = BattleBotArena.RIGHT;
            }
            else{
                move_choice = BattleBotArena.LEFT;
            }
        }
        else{
            if(y_diff <= 0){
                move_choice = BattleBotArena.DOWN;
            }
            else{
                move_choice = BattleBotArena.UP;
            }
        }

        return move_choice;
    }

      // OBSTACLE AVOIDANCE
      private boolean checkForObstacles(Vector2 center, BotInfo[] liveBotInfos, BotInfo[] deadBotInfos){        
          for (BotInfo deadBot : deadBotInfos) {
              if(Math.abs(deadBot.getX() - center.x) <= 13*2 &&
                 Math.abs(deadBot.getY() - center.y) <= 13*2){
                  return true;
              } 
          }
  
          for (BotInfo liveBot : liveBotInfos) {
              if(Math.abs(liveBot.getX() - center.x) <= 13*2 &&
                 Math.abs(liveBot.getY() - center.y) <= 13*2){
                  return true;
              } 
          }
  
          return false;
      }
  
  
      // TARGET PRIORITIZATION
      public String getBestTarget(BotInfo me){
  
          String bestBot = "";
          double bestScore = 0;
  
          for (Entry<String, BotTracker>  bot : trackedInfo.entrySet()) {
              double score = bot.getValue().SortMetric(me, w_proxy, w_shooting, w_stationary, w_overheated, w_dodged);
  
              if(score > bestScore){
                  bestBot = bot.getKey();
                  bestScore = score;
              }
          }
  
          return bestBot;
      }
  
      public BotInfo getInfoByName(BotInfo[] bots, String name){
          for (BotInfo botInfo : bots) {
              if(botInfo.getName() == name){
                  return botInfo;
              }
          }
          return bots[0];
      }
  
      // DODGE BULLETS
      public double distance(BotInfo bot, BotInfo other){
          return botHelper.calcDistance(bot.getX(), bot.getY(), other.getX(), other.getY());
      }
      
      public double distance(BotInfo bot, Bullet other){
          return botHelper.calcDistance(bot.getX(), bot.getY(), other.getX(), other.getY());
      }
  
      public Bullet findClosestBullet(BotInfo _me, List<Bullet> _bullets){
          Bullet closest;
          double distance, closestDist;
          closest = _bullets.get(0);
          closestDist = Math.abs(_me.getX() - closest.getX())+Math.abs(_me.getY() - closest.getY());
          for (int i = 1; i < _bullets.size(); i ++){
              distance = Math.abs(_me.getX() - _bullets.get(i).getX())+Math.abs(_me.getY() - _bullets.get(i).getY());
              if (distance < closestDist){
                  closest = _bullets.get(i);
                  closestDist = distance;
              }
          }
          
          return closest;
      }
  
    public int dodge(BotInfo me, Bullet bullet){

        // Coming from y?
        if(bullet.getYSpeed() != 0){
            // is on left wall?
            if(Math.abs(bullet.getX() - BattleBotArena.LEFT_EDGE) < WALL_DISTANCE){
                return BattleBotArena.RIGHT;
            }
            // is on right wall?
            if(Math.abs(bullet.getX() - BattleBotArena.RIGHT_EDGE) < WALL_DISTANCE){
                return BattleBotArena.LEFT;
            }

            // is on left?
            if(bullet.getX() < me.getX()){
                return BattleBotArena.RIGHT;
            }
            // is on right?
            return BattleBotArena.LEFT;
        }

        // Coming from x?
        if(bullet.getXSpeed() != 0){
            // is on the top wall?
            if(Math.abs(bullet.getY() - BattleBotArena.TOP_EDGE) < WALL_DISTANCE){
                return BattleBotArena.DOWN;
            }
            // is on the bottom wall?
            if(Math.abs(bullet.getY() - BattleBotArena.BOTTOM_EDGE) < WALL_DISTANCE){
                return BattleBotArena.UP;
            }

            // Is on above?
            if(bullet.getY() < me.getY()){
                return BattleBotArena.DOWN;
            }
            // Is on below?
            return BattleBotArena.UP;
        }

        return BattleBotArena.STAY;
    }
  
      // SHOOT WHEN INFRONT OF A BOT
      private boolean isShootable(BotInfo me, double x, double y){
          return (Math.abs(me.getY() - y) <= ACCURACY) || (Math.abs(me.getX() - x) <= ACCURACY);
      }
  
      private boolean isShootable(BotInfo me, BotTracker other, double x, double y){
          return (Math.abs(me.getY() - y) <= ACCURACY) || (Math.abs(me.getX() - x) <= ACCURACY);
      }
  
      private int shoot(BotInfo me, double x, double y){
          shootDelayCounter = SHOOT_DELAY_START;
          if(Math.abs(me.getY() - y) <= ACCURACY){
              if(x > me.getX()){
                  return BattleBotArena.FIRERIGHT;
              }
              return BattleBotArena.FIRELEFT;
          }
          if(Math.abs(me.getX() - x) <= ACCURACY){
              if(y > me.getY()){
                  return BattleBotArena.FIREDOWN;
              }
              return BattleBotArena.FIREUP;
          }
          
          return 0;
      }
  
      // SORT BOTS BY PROXY
      // https://www.geeksforgeeks.org/insertion-sort/
      private void sortBots(BotInfo me, BotInfo[] targets){
          int n = targets.length;
  
          for (int i = 1; i < n; ++i) {
              BotInfo key = targets[i];
              int j = i - 1;
   
              while (j >= 0 && botHelper.calcDistance(targets[j].getX(), targets[j].getY(), me.getX(), me.getY()) > 
                               botHelper.calcDistance(key.getX(),        key.getY(),        me.getX(), me.getY())) {
                  targets[j + 1] = targets[j];
                  j = j - 1;
              }
              targets[j + 1] = key;
          }
  
      }    

    public boolean isInRange(double x, double min, double max){
        if(min > max){
            double temp = min;
            min = max;
            max = temp;
        }
        
        return x > min && x < max;

    }
  
    private int getMoveSafe(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets){
        
        Vector2 target = path.get(0);

        double x_diff = target.x - me.getX();
        double y_diff = target.y - me.getY();

        for(Bullet b : bullets){

            // if aligned and parallel
                // vertically
            if(b.getYSpeed() > 0 && 
                (getNextInPath(me) == BattleBotArena.DOWN || getNextInPath(me) == BattleBotArena.UP) && 
                b.getY() < me.getY()+Bot.RADIUS &&
                isInRange(b.getX(), me.getX(), me.getX() + 2*Bot.RADIUS)){
                return BattleBotArena.LEFT;
            }

            if(b.getYSpeed() > 0){
                if(b.getY() < me.getY()+Bot.RADIUS){
                    
                }
            }

            if(b.getYSpeed() < 0 && 
                (getNextInPath(me) == BattleBotArena.DOWN || getNextInPath(me) == BattleBotArena.UP) && 
                b.getY() > me.getY()+Bot.RADIUS &&
                isInRange(b.getX(), me.getX(), me.getX() + 2*Bot.RADIUS)){
                return BattleBotArena.RIGHT;
            }

                // horizontally
            if(b.getXSpeed() > 0 && 
                (getNextInPath(me) == BattleBotArena.LEFT || getNextInPath(me) == BattleBotArena.RIGHT) && 
                b.getX() < me.getX()+Bot.RADIUS &&
                isInRange(b.getY(), me.getY(), me.getY() + 2*Bot.RADIUS)){
                return BattleBotArena.UP;
            }

            if(b.getXSpeed() < 0 && 
                (getNextInPath(me) == BattleBotArena.LEFT || getNextInPath(me) == BattleBotArena.RIGHT) && 
                b.getX() > me.getX()+Bot.RADIUS &&
                isInRange(b.getY(), me.getY(), me.getY() + 2*Bot.RADIUS)){
                return BattleBotArena.DOWN;
            }
            
            // if bullet is traveling perpendicular

            double delta_x = b.getX() - me.getX();
            double delta_y = b.getY() - me.getY();

            if(b.getYSpeed() > 0 && 
                (getNextInPath(me) == BattleBotArena.LEFT || getNextInPath(me) == BattleBotArena.RIGHT) && 
                b.getX() < me.getX()+Bot.RADIUS &&
                isInRange(Math.abs(0.5*delta_y), Math.abs(delta_x), Math.abs(delta_x - 2*Bot.RADIUS))){
                System.out.println(randint(1000, 10000));
                return BattleBotArena.STAY;
            }

            // if(b.getYSpeed() < 0 && 
            //     (getNextInPath(me) == BattleBotArena.LEFT || getNextInPath(me) == BattleBotArena.RIGHT) && 
            //     b.getX() > me.getX()+Bot.RADIUS &&
            //     isInRange(Math.abs(0.5*delta_y), Math.abs(delta_x), Math.abs(delta_x + 2*Bot.RADIUS))){
            //     System.out.println(0.5*delta_y - delta_x);
            //     return BattleBotArena.STAY;
            // }

            // if(b.getXSpeed() > 0 && 
            //     (getNextInPath(me) == BattleBotArena.DOWN || getNextInPath(me) == BattleBotArena.UP) && 
            //     b.getX() < me.getX()+Bot.RADIUS &&
            //     isInRange(Math.abs(0.5*delta_x), Math.abs(delta_y), Math.abs(delta_y + 2*Bot.RADIUS))){
            //     System.out.println(0.5*delta_x - delta_y);
            //     return BattleBotArena.STAY;
            // }

            // if(b.getXSpeed() < 0 && 
            //     (getNextInPath(me) == BattleBotArena.DOWN || getNextInPath(me) == BattleBotArena.UP) && 
            //     b.getX() > me.getX()+Bot.RADIUS &&
            //     isInRange(Math.abs(0.5*delta_x), Math.abs(delta_y), Math.abs(delta_y + 2*Bot.RADIUS))){
            //         System.out.println(0.5*delta_x - delta_y);
            //     return BattleBotArena.STAY;
            // }
        }

        if((Math.abs(me.getX() - path.get(0).x) < 4) && (Math.abs(me.getY() - path.get(0).y) < 4))
        {
            path.remove(0);
        }

        return getNextInPath(me);
        
    }
  
      @Override
      public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
          try {
              sortBots(me, liveBots);

              shootDelayCounter --;
              targetingDelayCounter --;
  
              meTracker.updateTracker(me);            
  
              return getMoveSafe(me, shotOK, liveBots, deadBots, bullets);
          } catch (Exception e) {
              // e.printStackTrace();
              return BattleBotArena.STAY;
          }
      }
  
      // https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
      private int randint(int Min, int Max){
          return Min + (int)(Math.random() * ((Max - Min) + 1));
      }
  
      private int dot_x = 5;
      private int dot_y = 5;
      @Override
      public void draw(Graphics g, int x, int y) {
          // if(meTracker.cycleNumber % 5 == 0){
          //     dot_x = randint(0, 26);
          //     dot_y = randint(0, 26);
          // }
  
          g.setColor(new Color(randint(0, 255), randint(0, 255), randint(0, 255)));
          g.fillRect(x, y, Bot.RADIUS *2, Bot.RADIUS *2);
          
      }
  
      @Override
      public String getName() {
          return "dot-bot";
      }
  
      @Override
      public String getTeamName() {
          // TODO Auto-generated method stub
          return null;
      }
  
      @Override
      public String outgoingMessage() {
          // TODO Auto-generated method stub
          return null;
      }
  
      @Override
      public void incomingMessage(int botNum, String msg) {
          // TODO Auto-generated method stub
          
      }
  
      @Override
      public String[] imageNames() {
          // TODO Auto-generated method stub
          return null;
      }
  
      @Override
      public void loadedImages(Image[] images) {
          // TODO Auto-generated method stub
          
      }
  
  }
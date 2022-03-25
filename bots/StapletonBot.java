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

public class StapletonBot extends Bot{

    
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
    }

    // PATH FINDING

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

    private int AvoidObstacle(int move, BotInfo me, Vector2 target, BotInfo[] liveBotInfos, BotInfo[] deadBotInfos){
        boolean canMoveRight = true;
        boolean canMoveLeft  = true;
        boolean canMoveUp    = true;
        boolean canMoveDown  = true;

        switch(move){
            case BattleBotArena.RIGHT:
                if(!checkForObstacles(new Vector2(me.getX()+26, me.getY()), liveBotInfos, deadBotInfos)){
                    return BattleBotArena.RIGHT;
                }
                canMoveRight = false;
                break;

            case BattleBotArena.LEFT:
                if(!checkForObstacles(new Vector2(me.getX()-26, me.getY()), liveBotInfos, deadBotInfos)){
                    return BattleBotArena.LEFT;
                }
                canMoveLeft = false;
                break;

            case BattleBotArena.UP:
                if(!checkForObstacles(new Vector2(me.getX(), me.getY()-26), liveBotInfos, deadBotInfos)){
                    return BattleBotArena.UP;
                }
                canMoveUp = false;
                break;

            case BattleBotArena.DOWN:
                if(!checkForObstacles(new Vector2(me.getX(), me.getY()+26), liveBotInfos, deadBotInfos)){
                    return BattleBotArena.DOWN;
                }
                canMoveDown = false;
                break;
        }

        canMoveRight = !checkForObstacles(new Vector2(me.getX()+26, me.getY()), liveBotInfos, deadBotInfos);
        canMoveLeft  = !checkForObstacles(new Vector2(me.getX()-26, me.getY()), liveBotInfos, deadBotInfos);
        canMoveUp    = !checkForObstacles(new Vector2(me.getX(), me.getY()-26), liveBotInfos, deadBotInfos);
        canMoveDown  = !checkForObstacles(new Vector2(me.getX(), me.getY()+26), liveBotInfos, deadBotInfos);

        if(move == BattleBotArena.RIGHT && !canMoveRight){
            if(canMoveDown && target.y > me.getY()) return BattleBotArena.DOWN;
            if(canMoveUp) return BattleBotArena.UP;
            return BattleBotArena.LEFT;
        }

        if(move == BattleBotArena.LEFT && !canMoveLeft){
            if(canMoveDown && target.y > me.getY()) return BattleBotArena.DOWN;
            if(canMoveUp) return BattleBotArena.UP;
            return BattleBotArena.RIGHT;
        }

        if(move == BattleBotArena.UP && !canMoveUp){
            if(canMoveLeft && target.x < me.getX()) return BattleBotArena.LEFT;
            if(canMoveRight) return BattleBotArena.RIGHT;
            return BattleBotArena.DOWN;
        }

        if(move == BattleBotArena.DOWN && !canMoveDown){
            if(canMoveLeft && target.x < me.getX()) return BattleBotArena.LEFT;
            if(canMoveRight) return BattleBotArena.RIGHT;
            return BattleBotArena.UP;
        }


        return move;
    }

    // TARRGET PRIORITIZATION
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

    private int getMoveSafe(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets){
        sortBots(me, liveBots);
        for(BotInfo info : liveBots){
            // Track their info
            if(!trackedInfo.containsKey(info.getName())){
                trackedInfo.put(info.getName(), new BotTracker());
            }
            try {
                trackedInfo.get(info.getName()).updateTracker(info);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // if we can shoot
            BotTracker tango = trackedInfo.get(info.getName());
            if(shotOK && shootDelayCounter <= 0 && isShootable(me, tango, info.getX(), info.getY())){
                if(info.getName() == currentTarget){
                    tango.bulletsDodged += 1;
                }
                return shoot(me, info.getX(), info.getY());
            }
        }
        
        // FIND DANGEROUS BULLETS
        List<Bullet> dangerous_bullets = new ArrayList<>();
        for(Bullet bullet : bullets){
            // is the bullet close
            double dist_to_bullet = botHelper.calcDistance(me.getX(), me.getY(), bullet.getX(), bullet.getY());
            if(dist_to_bullet > DANGE_DISTANCE || dist_to_bullet < 30) continue;

            //System.out.printf("BULLET(%s, %s) ", bullet.getX(), bullet.getY());

            // will the bullet collide if I stay still
            if(!(bullet.getYSpeed() != 0 && bullet.getX() < me.getX() + BULLET_DISTANCE && bullet.getX() > me.getX() - BULLET_DISTANCE ||
                 bullet.getXSpeed() != 0 && bullet.getY() < me.getY() + BULLET_DISTANCE && bullet.getY() > me.getY() - BULLET_DISTANCE   )) continue;

            if(bullet.getXSpeed() == 0 && bullet.getYSpeed() < 0 && bullet.getY() > me.getY() - BULLET_DISTANCE){
                dangerous_bullets.add(bullet);
            }
            else if(bullet.getXSpeed() == 0 && bullet.getYSpeed() > 0 && bullet.getY() < me.getY() + BULLET_DISTANCE){
                dangerous_bullets.add(bullet);
            }
            else if(bullet.getYSpeed() == 0 && bullet.getXSpeed() < 0 && bullet.getX() > me.getX() - BULLET_DISTANCE){
                dangerous_bullets.add(bullet);
            }
            else if(bullet.getYSpeed() == 0 && bullet.getXSpeed() > 0 && bullet.getX() < me.getX() + BULLET_DISTANCE){
                dangerous_bullets.add(bullet);
            }
        }

        // IF THERE IS A BULLET TO DODGE, DODGE IT
        if(dangerous_bullets.size() != 0){
            return dodge(me, findClosestBullet(me, dangerous_bullets));
        }

        // MOVEMENT PATTERENS
        else{

            // IF THERE ARE ALOT OF BOTS, SPRAY AND PRAY
            // if(shotOK && targetingDelayCounter > 0 && liveBots.length >= BattleBotArena.NUM_BOTS * TARGETING_THRESHHOLD){
            //     return 5 + (int)(Math.random() * ((8 - 5) + 1));
            // }

            
            if(meTracker.cycleNumber < 80){
                return AvoidObstacle(BattleBotArena.UP, me, new Vector2(1000, 0), liveBots, deadBots);
            }

            String targetName = getBestTarget(me);
            currentTarget = targetName;
            BotInfo target = getInfoByName(liveBots, targetName);

            double x_target = me.getX(), y_target = me.getY();

            double yDistToTarget = target.getY() - me.getY();
            double xdistToTarget = target.getX() - me.getX();

            if(Math.abs(xdistToTarget) > Math.abs(yDistToTarget)){
                if(xdistToTarget > 0){
                    y_target = target.getY();
                    x_target = target.getX() - KILL_DISTANCE;
                } 
                else{
                    y_target = target.getY();
                    x_target = target.getX() + KILL_DISTANCE;
                }
            }
            else{
                if(yDistToTarget > 0){
                    y_target = target.getY() - KILL_DISTANCE;
                    x_target = target.getX();
                } 
                else{
                    y_target = target.getY() + KILL_DISTANCE;
                    x_target = target.getX();
                }
            }

            double x_diff = me.getX() - x_target;
            double y_diff = me.getY() - y_target;

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

            return AvoidObstacle(move_choice, me, new Vector2(x_target, y_target), liveBots, deadBots);
        }
        
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        try {
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
        g.fillOval(x + dot_x, y + dot_y, 6, 6);
        
    }

    @Override
    public String getName() {
        return "a-dot";
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
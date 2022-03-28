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

import org.w3c.dom.UserDataHandler;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;
import bots.Vector2;

public class TwoDotBot extends Bot {

    private final double xMidline = (BattleBotArena.TOP_EDGE + BattleBotArena.BOTTOM_EDGE) / 2;
    private final double yMidline = (BattleBotArena.LEFT_EDGE + BattleBotArena.RIGHT_EDGE) / 2;

    private final int DANGER_DISTANCE = 150;
    private final int WALL_DISTANCE = 50;
    private final int BULLET_DISTANCE = 30;

    private final int ACCURACY = 5;
    private final int KILL_DISTANCE = 100;

    private final double TARGETING_THRESHHOLD = 0.8;

    private final int TARGETING_DELAY_START = 100;
    private int targetingDelayCounter = 0;

    private int last_spray_direction = 0;

    private final int SHOOT_DELAY_START = 20;
    private int shootDelayCounter = 0;

    private double w_proxy = 1;
    private double w_shooting = 0.5;
    private double w_stationary = 2;
    private double w_overheated = 5;
    private double w_dodged = 3;

    private int GRID_RESOLUTION = 10;
    private int GRID_SIZE_X = (BattleBotArena.LEFT_EDGE - BattleBotArena.RIGHT_EDGE) / GRID_RESOLUTION;
    private int GRID_SIZE_Y = (BattleBotArena.TOP_EDGE - BattleBotArena.BOTTOM_EDGE) / GRID_RESOLUTION;

    private final int X_TARGET = 300 / GRID_RESOLUTION;
    private final int Y_TARGET = 300 / GRID_RESOLUTION;

    private BotHelper botHelper = new BotHelper();

    private BotTracker meTracker = new BotTracker();

    private Map<String, BotTracker> trackedInfo = new HashMap<String, BotTracker>();

    private int[][] grid;

    private List<Vector2> path = new ArrayList();

    private String currentTarget = "";

    private Dot[] dots = new Dot[2];

    @Override
    public void newRound() {
        shootDelayCounter = SHOOT_DELAY_START;
        targetingDelayCounter = TARGETING_DELAY_START;

        meTracker.reset();

        for (Entry<String, BotTracker> info : trackedInfo.entrySet()) {
            info.getValue().reset();
        }

        dots[0] = new Dot(
                new Vector2(7, 18),
                Vector2.UnitVector(randrange(0,  359)),
                3
            );

        dots[1] = new Dot(
                new Vector2(10, 10),
                Vector2.UnitVector(randrange(0,  359)),
                4
            );

    }

    // PATH FINDING

    // OBSTACLE AVOIDANCE
    private boolean checkForObstacles(Vector2 tl, BotInfo[] liveBotInfos, BotInfo[] deadBotInfos) {
        for (BotInfo deadBot : deadBotInfos) {
            if (tl.x <= deadBot.getX() && deadBot.getX() <= tl.x + RADIUS*2 &&
                tl.y <= deadBot.getY() && deadBot.getY() <= tl.y + RADIUS*2) {
                return true;
            }
        }

        for (BotInfo liveBot : liveBotInfos) {
            if (tl.y <= liveBot.getY() && liveBot.getY() <= tl.y + RADIUS*2 &&
                tl.y <= liveBot.getY() && liveBot.getY() <= tl.y + RADIUS*2) {
                return true;
            }
        }

        return false;
    }

    private int AvoidObstacle(int move, BotInfo me, Vector2 target, BotInfo[] liveBotInfos, BotInfo[] deadBotInfos) {
        boolean canMoveRight = true;
        boolean canMoveLeft = true;
        boolean canMoveUp = true;
        boolean canMoveDown = true;

        switch (move) {
            case BattleBotArena.RIGHT:
                if (!checkForObstacles(new Vector2(me.getX() + 26, me.getY()), liveBotInfos, deadBotInfos)) {
                    return BattleBotArena.RIGHT;
                }
                canMoveRight = false;
                break;

            case BattleBotArena.LEFT:
                if (!checkForObstacles(new Vector2(me.getX() - 26, me.getY()), liveBotInfos, deadBotInfos)) {
                    return BattleBotArena.LEFT;
                }
                canMoveLeft = false;
                break;

            case BattleBotArena.UP:
                if (!checkForObstacles(new Vector2(me.getX(), me.getY() - 26), liveBotInfos, deadBotInfos)) {
                    return BattleBotArena.UP;
                }
                canMoveUp = false;
                break;

            case BattleBotArena.DOWN:
                if (!checkForObstacles(new Vector2(me.getX(), me.getY() + 26), liveBotInfos, deadBotInfos)) {
                    return BattleBotArena.DOWN;
                }
                canMoveDown = false;
                break;
        }

        canMoveRight = !checkForObstacles(new Vector2(me.getX() + 26, me.getY()), liveBotInfos, deadBotInfos);
        canMoveLeft = !checkForObstacles(new Vector2(me.getX() - 26, me.getY()), liveBotInfos, deadBotInfos);
        canMoveUp = !checkForObstacles(new Vector2(me.getX(), me.getY() - 26), liveBotInfos, deadBotInfos);
        canMoveDown = !checkForObstacles(new Vector2(me.getX(), me.getY() + 26), liveBotInfos, deadBotInfos);

        if (move == BattleBotArena.RIGHT && !canMoveRight) {
            if (canMoveDown && target.y > me.getY())
                return BattleBotArena.DOWN;
            if (canMoveUp)
                return BattleBotArena.UP;
            return BattleBotArena.LEFT;
        }

        if (move == BattleBotArena.LEFT && !canMoveLeft) {
            if (canMoveDown && target.y > me.getY())
                return BattleBotArena.DOWN;
            if (canMoveUp)
                return BattleBotArena.UP;
            return BattleBotArena.RIGHT;
        }

        if (move == BattleBotArena.UP && !canMoveUp) {
            if (canMoveLeft && target.x < me.getX())
                return BattleBotArena.LEFT;
            if (canMoveRight)
                return BattleBotArena.RIGHT;
            return BattleBotArena.DOWN;
        }

        if (move == BattleBotArena.DOWN && !canMoveDown) {
            if (canMoveLeft && target.x < me.getX())
                return BattleBotArena.LEFT;
            if (canMoveRight)
                return BattleBotArena.RIGHT;
            return BattleBotArena.UP;
        }

        return move;
    }

    // TARRGET PRIORITIZATION
    public String getBestTarget(BotInfo me) {

        String bestBot = "";
        double bestScore = 0;

        for (Entry<String, BotTracker> bot : trackedInfo.entrySet()) {
            if(bot.getValue().botTeamName == getTeamName()) continue; // dont choose your team as a target
            double score = bot.getValue().SortMetric(me, w_proxy, w_shooting, w_stationary, w_overheated, w_dodged);

            if (score > bestScore) {
                bestBot = bot.getKey();
                bestScore = score;
            }
        }

        return bestBot;
    }

    public BotInfo getInfoByName(BotInfo[] bots, String name) {
        for (BotInfo botInfo : bots) {
            if (botInfo.getName() == name) {
                return botInfo;
            }
        }
        return bots[0];
    }

    public boolean willBulletHit(BotInfo bot, BotTracker tracker, Bullet bullet){

        if (tracker.getSpeed().equals(Vector2.STAY())) {
            if (bullet.getYSpeed() < 0 && // if bullet is moving in negative y
                bot.getY() < bullet.getY() && // if bot is above bullet
                bullet.getX() < bot.getX() && bot.getX() < bullet.getX()){ // if x positions are aligned 

            }

        }


        return false;
    }

    // Shooting
    public int shoot(BotInfo me, BotInfo[] targets) {
        sortBots(me, targets);

        for (BotInfo target : targets) {

            BotTracker tracker = trackedInfo.get(target.getName());

            if (tracker.getSpeed().equals(Vector2.STAY())) {
                if (target.getX() < me.getX() + RADIUS && me.getX() + RADIUS < target.getX() + 2 * RADIUS)
                    return (target.getY() < me.getY()) ? BattleBotArena.FIREUP : BattleBotArena.FIREDOWN;

                if (target.getY() < me.getY() + RADIUS && me.getY() + RADIUS < target.getY() + 2 * RADIUS)
                    return (target.getX() < me.getX()) ? BattleBotArena.FIRELEFT : BattleBotArena.FIRERIGHT;

            } 
        
            else {

                Vector2 target_velocity = tracker.getSpeed();

                if(target_velocity.y != 0)
                    if (target.getX() < me.getX() + RADIUS && me.getX() + RADIUS < target.getX() + 2 * RADIUS)
                        return (target.getY() < me.getY()) ? BattleBotArena.FIREUP : BattleBotArena.FIREDOWN;

                if(target_velocity.x != 0)
                    if (target.getY() < me.getY() + RADIUS && me.getY() + RADIUS < target.getY() + 2 * RADIUS)
                        return (target.getX() < me.getX()) ? BattleBotArena.FIRELEFT : BattleBotArena.FIRERIGHT;


                Vector2 time_to_target = new Vector2(
                        Math.abs(target.getY() - me.getY()) / BattleBotArena.BULLET_SPEED,
                        Math.abs(target.getX() - me.getX()) / BattleBotArena.BULLET_SPEED);


                Vector2 leading_distance = new Vector2(
                        target_velocity.x * time_to_target.x,
                        target_velocity.y * time_to_target.y);

                Vector2 target_pos = new Vector2(
                        Math.max(Math.min(target.getX() + leading_distance.x, BattleBotArena.RIGHT_EDGE - 2 * RADIUS),
                                BattleBotArena.LEFT_EDGE),
                        Math.max(Math.min(target.getY() + leading_distance.y, BattleBotArena.BOTTOM_EDGE - 2 * RADIUS),
                                BattleBotArena.TOP_EDGE));



                if (target_pos.x + 10 < me.getX() + RADIUS && me.getX() + RADIUS < target_pos.x - 10 + 2 * RADIUS) {
                    return (target_pos.y < me.getY()) ? BattleBotArena.FIREUP : BattleBotArena.FIREDOWN;
                }

                if (target_pos.y + 10 < me.getY() + RADIUS && me.getY() + RADIUS < target_pos.y - 10 + 2 * RADIUS)
                    return (target_pos.x < me.getX()) ? BattleBotArena.FIRELEFT : BattleBotArena.FIRERIGHT;
            }
        }

        return -1;
    }

    // DODGE BULLETS
    public double distance(BotInfo bot, BotInfo other) {
        return botHelper.calcDistance(bot.getX(), bot.getY(), other.getX(), other.getY());
    }

    public double distance(BotInfo bot, Bullet other) {
        return botHelper.calcDistance(bot.getX(), bot.getY(), other.getX(), other.getY());
    }

    public Bullet findClosestBullet(BotInfo _me, List<Bullet> _bullets) {
        Bullet closest;
        double distance, closestDist;
        closest = _bullets.get(0);
        closestDist = Math.abs(_me.getX() - closest.getX()) + Math.abs(_me.getY() - closest.getY());
        for (int i = 1; i < _bullets.size(); i++) {
            distance = Math.abs(_me.getX() - _bullets.get(i).getX()) + Math.abs(_me.getY() - _bullets.get(i).getY());
            if (distance < closestDist) {
                closest = _bullets.get(i);
                closestDist = distance;
            }
        }

        return closest;
    }

    public int dodge(BotInfo me, Bullet[] bullets) {
        sortBullets(me, bullets);

        // FIND AND DODGE BULLETS
        for (Bullet b : bullets) {
            // is the bullet close
            double dist_to_bullet = botHelper.calcDistance(me.getX(), me.getY(), b.getX(), b.getY());
            if (dist_to_bullet > DANGER_DISTANCE)
                continue;

            if (b.getYSpeed() != 0) {
                // escape if bullet is not aligned in the x
                if (!(me.getX() -10< b.getX() && b.getX() < me.getX()+10 + 2 * RADIUS))
                    continue;

                // escape if bullet is not moving towards me
                if (!((b.getYSpeed() > 0 && b.getY() < me.getY() + Bot.RADIUS) ||
                        (b.getYSpeed() < 0 && b.getY() > me.getY() + Bot.RADIUS)))
                    continue;

                // if bullet is near wall (TODO: or cant move in dir)
                if (b.getX() < BattleBotArena.LEFT_EDGE + RADIUS * 2)
                    return BattleBotArena.RIGHT;
                if (b.getX() > BattleBotArena.RIGHT_EDGE - RADIUS * 2)
                    return BattleBotArena.LEFT;

                // choose closest side
                return (me.getX() + RADIUS < b.getX()) ? BattleBotArena.LEFT : BattleBotArena.RIGHT;
            }

            if (b.getXSpeed() != 0) {
                // escape if bullet is not aligned in the x
                if (!(me.getY()-10 < b.getY() && b.getY() < me.getY()+10 + 2 * RADIUS))
                    continue;

                // escape if bullet is not moving towards me
                if (!((b.getXSpeed() > 0 && b.getX() < me.getX() + Bot.RADIUS) ||
                        (b.getXSpeed() < 0 && b.getX() > me.getX() + Bot.RADIUS)))
                    continue;

                // if bullet is near wall (TODO: or cant move in dir)
                if (b.getY() < BattleBotArena.TOP_EDGE + RADIUS * 2)
                    return BattleBotArena.DOWN;
                if (b.getY() > BattleBotArena.BOTTOM_EDGE - RADIUS * 2)
                    return BattleBotArena.UP;

                // choose closest side
                return (me.getY() + RADIUS < b.getY()) ? BattleBotArena.UP : BattleBotArena.DOWN;
            }
        }

        return -1;
    }

    private int shoot(BotInfo me, double x, double y) {
        shootDelayCounter = 5;
        if (Math.abs(me.getY() - y) <= ACCURACY) {
            if (x > me.getX()) {
                return BattleBotArena.FIRERIGHT;
            }
            return BattleBotArena.FIRELEFT;
        }
        if (Math.abs(me.getX() - x) <= ACCURACY) {
            if (y > me.getY()) {
                return BattleBotArena.FIREDOWN;
            }
            return BattleBotArena.FIREUP;
        }

        return 0;
    }

    // SORT BOTS BY PROXY
    // https://www.geeksforgeeks.org/insertion-sort/
    private void sortBots(BotInfo me, BotInfo[] targets) {
        int n = targets.length;

        for (int i = 1; i < n; ++i) {
            BotInfo key = targets[i];
            int j = i - 1;

            while (j >= 0 && botHelper.calcDistance(targets[j].getX(), targets[j].getY(), me.getX(),
                    me.getY()) > botHelper.calcDistance(key.getX(), key.getY(), me.getX(), me.getY())) {
                targets[j + 1] = targets[j];
                j = j - 1;
            }
            targets[j + 1] = key;
        }

    }

    private void sortBullets(BotInfo me, Bullet[] targets) {
        int n = targets.length;

        for (int i = 1; i < n; ++i) {
            Bullet key = targets[i];
            int j = i - 1;

            while (j >= 0 && botHelper.calcDistance(targets[j].getX(), targets[j].getY(), me.getX(),
                    me.getY()) > botHelper.calcDistance(key.getX(), key.getY(), me.getX(), me.getY())) {
                targets[j + 1] = targets[j];
                j = j - 1;
            }
            targets[j + 1] = key;
        }

    }

        // // MOVEMENT PATTERENS
        // else{

        // // IF THERE ARE ALOT OF BOTS, SPRAY AND PRAY
        // // if(shotOK && targetingDelayCounter > 0 && liveBots.length >=
        // BattleBotArena.NUM_BOTS * TARGETING_THRESHHOLD){
        // // return 5 + (int)(Math.random() * ((8 - 5) + 1));
        // // }

        // if(meTracker.cycleNumber < 80){
        // return AvoidObstacle(BattleBotArena.UP, me, new Vector2(1000, 0), liveBots,
        // deadBots);
        // }

    

    private int moveToTargetPosition(BotInfo me, Vector2 target_pos){

        double x_diff = me.getX() - target_pos.x;
        double y_diff = me.getY() - target_pos.y;

        // MOVE TO TARGET POSITION
        if(Math.abs(x_diff) > Math.abs(y_diff)){
            if(x_diff <= 0) return BattleBotArena.RIGHT;
            else            return BattleBotArena.LEFT;
        }
        else{
            if(y_diff <= 0) return BattleBotArena.DOWN;
            else            return BattleBotArena.UP;
        }
    }


    private Vector2 getShootingPosition(BotInfo me, BotInfo target, BotInfo[] deadBots){

        Vector2 shootingPosition = new Vector2();

        Vector2 distToTarget = new Vector2(
            target.getX() - me.getX(),
            target.getY() - me.getY()
        );

        if(Math.abs(distToTarget.x) > Math.abs(distToTarget.y)){
            if(distToTarget.x > 0){
                shootingPosition.y = target.getY();
                shootingPosition.x = target.getX() - KILL_DISTANCE;
            }
            else{
                shootingPosition.y = target.getY();
                shootingPosition.x = target.getX() + KILL_DISTANCE;
            }
        }
        else{
            if(distToTarget.y > 0){
                shootingPosition.y = target.getY() - KILL_DISTANCE;
                shootingPosition.x = target.getX();
            }
            else{
                shootingPosition.y = target.getY() + KILL_DISTANCE;
                shootingPosition.x = target.getX();
            }
        }

        return shootingPosition;
    }

    // ANCHOR: GET MOVE
    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        try {
            // update counters
            shootDelayCounter--;
            targetingDelayCounter--;

            // update trackers
            meTracker.updateTracker(me);

            for (BotInfo info : liveBots) {
                // Track their info
                if (!trackedInfo.containsKey(info.getName())) {
                    trackedInfo.put(info.getName(), new BotTracker());
                }
                try {
                    trackedInfo.get(info.getName()).updateTracker(info);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // dodge bullets
            int dodge_dir = dodge(me, bullets);
            if (dodge_dir != -1)
                return dodge_dir;

            // shoot with leading
            if (shootDelayCounter < 0) {
                int shoot_dir = shoot(me, liveBots);
                if (shoot_dir != -1) {
                    shootDelayCounter = SHOOT_DELAY_START;
                    return shoot_dir;
                }
            }

            if(liveBots.length > 0.8* BattleBotArena.NUM_BOTS){
                BotInfo closest = botHelper.findClosestBot(me, liveBots);
                return shoot(me, liveBots);
            }

            // find best target
            
            String targetName = getBestTarget(me);
            currentTarget = targetName;
            BotInfo target = getInfoByName(liveBots, targetName);

            Vector2 targetPos = getShootingPosition(me, target, deadBots);
            System.out.println(
                "t(" + target.getX() + ", " + target.getY() + ")" +
                "->" + targetPos.toString()
            );


            // find best target pos
            // find path to target


            int move_choice = moveToTargetPosition(me, targetPos);
            return move_choice;
            // return AvoidObstacle(move_choice, me, new Vector2(targetPos.x, targetPos.y), liveBots, deadBots);
            
        } catch (Exception e) {
            // e.printStackTrace();
            return BattleBotArena.STAY;
        }
    }

    // https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
    private int randint(int Min, int Max) {
        return Min + (int) (Math.random() * ((Max - Min) + 1));
    }

    private double randrange(double Min, double Max) {
        return Min + (Math.random() * ((Max - Min) + 1));
    }

    private double last_time = System.currentTimeMillis();

    @Override
    public void draw(Graphics g, int x, int y) {

        double current_time = System.currentTimeMillis();

        double dt = (current_time - last_time)/40;
        
        for (Dot dot : dots) {
            dot.move(dt);
            dot.checkWallCollision(13, dt);
        }

        last_time = current_time;

        if(dots[0].radius + dots[1].radius > Math.sqrt(Math.pow((dots[0].pos.x - dots[1].pos.x), 2) + Math.pow((dots[0].pos.y - dots[1].pos.y), 2))){
            //System.out.println("colliding " + Math.sqrt(Math.pow((dots[0].pos.x - dots[1].pos.x), 2) + Math.pow((dots[0].pos.y - dots[1].pos.y), 2)));
            Vector2 d0_vel = dots[0].vel.copy();
            Vector2 d1_vel = dots[1].vel.copy();
            
            // System.out.println("0b"+ dots[0].vel.toString());
            // System.out.println("1b"+ dots[1].vel.toString());

            dots[0].collide(new Dot(dots[1].pos.copy(), d1_vel, dots[1].radius));
            dots[1].collide(new Dot(dots[0].pos.copy(), d0_vel, dots[0].radius));

            // System.out.println("0a"+ dots[0].vel.toString());
            // System.out.println("1a"+ dots[1].vel.toString());

            dots[0].pos = Vector2.sub(dots[0].pos, Vector2.scale(d0_vel, 2*dt));
            dots[1].pos = Vector2.sub(dots[1].pos, Vector2.scale(d1_vel, 2*dt));
        }

        for (Dot dot : dots) {
            dot.draw(g, x, y);
        }

        g.setColor(new Color(255, 255, 255));
        g.drawOval(x-1, y-1, 26, 26);

        g.fillRect(
            (int)trackedInfo.get("Human").currentX, 
            (int)trackedInfo.get("Human").currentY, 
            26, 
            26);

    }

    @Override
    public String getName() {
        return "two-dot";
    }

    @Override
    public String getTeamName() {
        // TODO Auto-generated method stub
        return "QuadDots";
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
package bots;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class StapletonBot extends Bot{

    private final double xMidline = (BattleBotArena.TOP_EDGE  + BattleBotArena.BOTTOM_EDGE)/2;
    private final double yMidline = (BattleBotArena.LEFT_EDGE + BattleBotArena.RIGHT_EDGE )/2;

    private final int DANGE_DISTANCE = 150; 
    private final int WALL_DISTANCE = 50;
    private final int BULLET_DISTANCE = 50;

    private final int ACCURACY = 20;
    private final int KILL_DISTANCE = 100;

    private final double TARGETING_THRESHHOLD = 0.8;

    private final int TARGETING_DELAY_START = 100;
    private int targetingDelayCounter = 0;

    private int last_spray_direction = 0;

    private final int X_TARGET = 300;
    private final int Y_TARGET = 300;

    private final int SHOOT_DELAY_START   = 20;
    private int shootDelayCounter = 0;

    private BotHelper botHelper = new BotHelper();

    @Override
    public void newRound() {
        shootDelayCounter = SHOOT_DELAY_START;
        targetingDelayCounter = TARGETING_DELAY_START;
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

    private int shoot(BotInfo me, double x, double y){
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



    private int getMoveSafe(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets){

        // FIND DANGEROUS BULLETS
        List<Bullet> dangerous_bullets = new ArrayList<>();
        for(Bullet bullet : bullets){
            // is the bullet close
            if(botHelper.calcDistance(me.getX(), me.getY(), bullet.getX(), bullet.getY()) > DANGE_DISTANCE) continue;

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
            if(shotOK && targetingDelayCounter > 0 && liveBots.length >= BattleBotArena.NUM_BOTS * TARGETING_THRESHHOLD){
                return 5 + (int)(Math.random() * ((8 - 5) + 1));
            }

            BotInfo target = botHelper.findClosestBot(me, liveBots);

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
            
            // SHOOT THE TARGET IF IT IS INLINE
            if(shootDelayCounter <= 0 && Math.abs(x_diff) < 20 && Math.abs(y_diff) < 20){
                shootDelayCounter = SHOOT_DELAY_START;
                for (BotInfo bot : liveBots) {
                    if(isShootable(me, bot.getX(), bot.getY()))
                    {
                        return shoot(me, bot.getX(), bot.getY());
                    }
                }
            }

            // MOVE TO TARGET POSITION
            if(Math.abs(x_diff) > Math.abs(y_diff)){
                if(x_diff <= 0){
                    return BattleBotArena.RIGHT;
                }
                return BattleBotArena.LEFT;
            }
            else{
                if(y_diff <= 0){
                    return BattleBotArena.DOWN;
                }
                return BattleBotArena.UP;
            }
        }
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        try {
            shootDelayCounter --;
            targetingDelayCounter --;

            return getMoveSafe(me, shotOK, liveBots, deadBots, bullets);
        } catch (Exception e) {
            e.printStackTrace();
            return BattleBotArena.STAY;
        }
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getName() {
        return "bozo-blaster";
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
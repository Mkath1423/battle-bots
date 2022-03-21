package bots;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class StapletonBot extends Bot{

    private int DANGE_DISTANCE = 150; 
    private int WALL_DISTANCE = 50;
    private int BULLET_DISTANCE = 50;

    private int X_TARGET = 300;
    private int Y_TARGET = 300;


    private BotHelper botHelper = new BotHelper();

    @Override
    public void newRound() {
        // TODO Auto-generated method stub
        
    }

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

    private int getMoveSafe(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets){

        //System.out.printf("DANGER: %s (%s, %s)\n", distance(me, closest), closest.getX(), closest.getY());

        //System.out.printf("\n(%s, %s), (%s, %s) ", me.getX() - 20, me.getX() + 20, me.getY() - 20, me.getY() + 20);

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

        if(!shotOK || dangerous_bullets.size() != 0){
            return dodge(me, findClosestBullet(me, dangerous_bullets));
        }
        else{
            double x_diff = me.getX() - X_TARGET;
            double y_diff = me.getY() - Y_TARGET;
            
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

        // if(!shotOK || dangerous_bullets.size() != 0){
        //     // dodge closest
        // }
        // else{
        //     // line up with a robot and shoot
        // }
        // return 0;
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        try {
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
        return "lex-bot";
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
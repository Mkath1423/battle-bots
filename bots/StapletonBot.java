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
        if(bullet.getXSpeed() == 0 && bullet.getYSpeed() < 0 && bullet.getY() > me.getY()){
            return BattleBotArena.LEFT;
        }
        if(bullet.getXSpeed() == 0 && bullet.getYSpeed() > 0 && bullet.getY() < me.getY()){
            return BattleBotArena.RIGHT;
        }
        if(bullet.getYSpeed() == 0 && bullet.getXSpeed() < 0 && bullet.getX() > me.getX()){
            return BattleBotArena.UP;
        }
        if(bullet.getYSpeed() == 0 && bullet.getXSpeed() > 0 && bullet.getX() < me.getX()){
            return BattleBotArena.DOWN;
        }
        return BattleBotArena.STAY;
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        
       

        Bullet closest = botHelper.findClosestBullet(me, bullets);
        System.out.printf("DANGER: %s (%s, %s)\n", distance(me, closest), closest.getX(), closest.getY());


        List<Bullet> dangerous_bullets = new ArrayList<>();
        for(Bullet bullet : bullets){
            if(botHelper.calcDistance(me.getX(), me.getY(), bullet.getX(), bullet.getY()) > DANGE_DISTANCE) continue;
            if(bullet.getXSpeed() == 0 && bullet.getYSpeed() < 0 && bullet.getY() > me.getY()){
                dangerous_bullets.add(bullet);
            }
            else if(bullet.getXSpeed() == 0 && bullet.getYSpeed() > 0 && bullet.getY() < me.getY()){
                dangerous_bullets.add(bullet);
            }
            else if(bullet.getYSpeed() == 0 && bullet.getXSpeed() < 0 && bullet.getX() > me.getX()){
                dangerous_bullets.add(bullet);
            }
            else if(bullet.getYSpeed() == 0 && bullet.getXSpeed() > 0 && bullet.getX() < me.getX()){
                dangerous_bullets.add(bullet);
            }
        }

        return dodge(me, findClosestBullet(me, dangerous_bullets));

        // if(!shotOK || dangerous_bullets.size() != 0){
        //     // dodge closest
        // }
        // else{
        //     // line up with a robot and shoot
        // }
        // return 0;
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
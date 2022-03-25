// what went well

//what it didn't do

package bots;

import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class BakerBot extends Bot {

    BotHelper helper = new BotHelper();

    Image current, up, down, right, left;

    @Override
    public void newRound() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {

        // bullet dodge program created from help from AlajramiBot
        for(int i = 0; i < bullets.length; i++){
            Bullet bullet = bullets[i];

            // Positive if bullet is above bot, negative if bellow
            double yDiffBullet = me.getY() - bullet.getY();
            // Positive if bullet is to left, negative if to right 
            double xDiffBullet = me.getX() - bullet.getX();

            if(Math.abs(yDiffBullet) < 30 && Math.abs(yDiffBullet) > (30)*-1 && Math.abs(xDiffBullet) < 50){
                if (yDiffBullet > 0){
                    return BattleBotArena.DOWN;
                }
                else if(yDiffBullet < 0){
                    return BattleBotArena.UP;
                }
            }
            if(Math.abs(xDiffBullet) < 30 && Math.abs(xDiffBullet) > (30)*-1 && Math.abs(xDiffBullet) < 50){
                if (xDiffBullet > 0){
                    return BattleBotArena.LEFT;
                }
                else if(xDiffBullet < 0){
                    return BattleBotArena.RIGHT;
                }
            }

        }
        // Positive if bot is above bot, negative if bellow
        double yDiffBot = me.getY() - helper.findClosestBot(me, liveBots).getY();
        // Positive if bot is to left, negative if to right 
        double xDiffBot = me.getX() - helper.findClosestBot(me, liveBots).getX();
        //sorta of works
        if (Math.abs(xDiffBot) < 30 && Math.abs(xDiffBot) > (30)*-1 && Math.abs(xDiffBot) < 50){
            if (me.getY() < 670){
                return BattleBotArena.DOWN;
            }else{
                return BattleBotArena.UP;
            }
        }
        if (Math.abs(yDiffBot) < 30 && Math.abs(yDiffBot) > (30)*-1 && Math.abs(yDiffBot) < 50){
            if (me.getX() < 550){
                return BattleBotArena.RIGHT;
            } 
            else if (me.getX() > 450){
                return BattleBotArena.LEFT;
            }
        }
        if (me.getY() < 670){
        return BattleBotArena.DOWN;
        }
            // look for players above you and shoot at the
            // current program gets killed because players or to close to react to bullet
        int rand = (int)Math.floor(Math.random()*7);
        if(rand == 0 || rand == 1){
            return BattleBotArena.FIREUP;
        }else if(rand == 2 || rand == 3){
            return BattleBotArena.FIRELEFT;
        }else if(rand == 4 || rand == 5){
            return BattleBotArena.FIRERIGHT;
        }else if (rand == 6){
                return BattleBotArena.FIREDOWN;
        }
        return BattleBotArena.STAY;
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        g.drawImage(current, x, y, Bot.RADIUS*2, Bot.RADIUS*2, null);
        
    }

    @Override
    public String getName() {
        return "BakerBot";
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
        String[] paths = { "muffin_man.png", "muffin_man.png", "muffin_man.png", "muffin_man.png" };
		return paths;
    }

    @Override
    public void loadedImages(Image[] images) {
        if (images != null) {
			if (images.length > 0)
				up = images[0];
			if (images.length > 1)
				down = images[1];
			if (images.length > 2)
				right = images[2];
			if (images.length > 3)
				left = images[3];
			current = up;
		}
    }
    
}

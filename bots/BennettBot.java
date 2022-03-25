package bots;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.CloseAction;
import javax.swing.text.html.StyleSheet.BoxPainter;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class BennettBot extends Bot {

    BotHelper helper = new BotHelper();

    private String name = null;
    //private String teamName = null;
    Image current, up, down, right, left;
    private int count = 0;
    private double botX = 0;
    private double botY = 0;
    private double manhattanDist = 0;
    private double closestBulletX = 0;
    private double closestBulletY = 0;
    private double lastX = 0;
    private double lastY = 0;
    private double xPos = 0;
    private double yPos = 0;

    @Override
    public void newRound() {
        count = 0;
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        lastX = xPos;
        lastY = yPos;
        botX = helper.findClosestBot(me, liveBots).getX();
        botY = helper.findClosestBot(me, liveBots).getY();
        xPos = me.getX();
        yPos = me.getY();
        double diffX = Math.abs(botX - xPos);
        double diffY = Math.abs(botY - yPos);
        double xSpeed = 0;
        double ySpeed = 0;

        //boolean debug = false;
        


        switch (count) {          
            case 0: count++; return BattleBotArena.FIRERIGHT;
            case 1: count++; return BattleBotArena.FIRELEFT;
        }
        



        if (bullets.length > 0) {
            Bullet closestBullet = helper.findClosestBullet(me, bullets);
            closestBulletX = (closestBullet).getX();
            closestBulletY = (closestBullet).getY();
            manhattanDist = (BotHelper.manhattanDist(closestBulletX, closestBulletY, (xPos + Bot.RADIUS), (yPos + Bot.RADIUS)));
            xSpeed = closestBullet.getXSpeed();
            ySpeed = closestBullet.getYSpeed();
        }
 
 
 
        if (manhattanDist < 60) {
            if (((xPos - Bot.RADIUS*2) < closestBulletX) && ((xPos + Bot.RADIUS*4) > closestBulletX)) {
                if (((ySpeed < 0) && (yPos < closestBulletY)) || ((ySpeed > 0) && (yPos > closestBulletY))) {
                    if ((xPos + Bot.RADIUS) > closestBulletX) {
                        if (BattleBotArena.RIGHT_EDGE - (xPos + Bot.RADIUS*4) > Bot.RADIUS) {
                            return BattleBotArena.RIGHT;
                        }
                    }
                    else if ((xPos + Bot.RADIUS) < closestBulletX) {
                        if (xPos > Bot.RADIUS) {
                            return BattleBotArena.LEFT;
                        }
                    }
                    else if ((xPos + Bot.RADIUS) == closestBulletX) {
                        if (Math.abs(BattleBotArena.RIGHT_EDGE - xPos) < Math.abs(BattleBotArena.LEFT_EDGE - xPos)) {
                            return BattleBotArena.LEFT;
                        }
                        else {
                            return BattleBotArena.RIGHT;
                        }
                    }
                }
            }

            if (((yPos - Bot.RADIUS*2) < closestBulletY) && ((yPos + Bot.RADIUS*4) > closestBulletY)) {
                if (((xSpeed < 0) && (xPos < closestBulletX)) || ((xSpeed > 0) && (xPos > closestBulletX))) {
                    if ((yPos + Bot.RADIUS) > closestBulletY) {
                        if (BattleBotArena.BOTTOM_EDGE - (yPos + Bot.RADIUS*2) > Bot.RADIUS) {
                            return BattleBotArena.DOWN;
                        }
                    }
                    else if ((yPos + Bot.RADIUS) < closestBulletY) {
                        if (yPos > Bot.RADIUS) {
                            return BattleBotArena.UP;
                        }
                    }
                    
                    else if ((yPos + Bot.RADIUS) == closestBulletY) { 
                        if (Math.abs(BattleBotArena.BOTTOM_EDGE - yPos) < Math.abs(BattleBotArena.TOP_EDGE - yPos)) {
                            return BattleBotArena.UP;
                        }
                        else {
                            return BattleBotArena.DOWN;
                        }
                    
                    }
                }
            }
        }

        
        
    
        if (((xPos + Bot.RADIUS) > botX) && ((xPos + Bot.RADIUS) < (botX + Bot.RADIUS*2))) {
            if (botY > yPos) {
                return BattleBotArena.FIREDOWN;
            }
            else {
                return BattleBotArena.FIREUP;
            }
        }
        else if (((yPos + Bot.RADIUS) > botY) && ((yPos + Bot.RADIUS) < (botY + Bot.RADIUS*2))) {
            if (botX > xPos) {
                return BattleBotArena.FIRERIGHT;
            }
            else {
                return BattleBotArena.FIRELEFT;
            }
        }
        else if (diffX > diffY) {
            if (botY > yPos) {
                return BattleBotArena.DOWN;
            }
            else {
                return BattleBotArena.UP;
            }
        }
        else if (diffX < diffY) {
            if (botX > xPos) {
                return BattleBotArena.RIGHT;
            }
            else {
                return BattleBotArena.LEFT;
            }
        }



        return BattleBotArena.STAY;

        
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        g.drawImage(current, x, y, Bot.RADIUS * 2, Bot.RADIUS * 2, null);
        
    }

    @Override
    public String getName() {
        if (name == null) {
            name = "Tiggle";
        }
        return name;
    }

    @Override
    public String getTeamName() {
        // if (teamName == null) {
        //     teamName = "YMC_2.0";
        // }
        return "Arena";
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
        String[] photos = { "ymclan.jpg" };
		return photos;
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

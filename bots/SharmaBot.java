package bots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class SharmaBot extends Bot {

    BotHelper helper = new BotHelper();

    Image up, down, left, right, current;

    private boolean overheat = false;
    private int sleep = (int) (Math.random() * 5 + 1);
    private int targetNum = (int) (Math.random() * BattleBotArena.NUM_BOTS);

    @Override
    public void newRound() {
        if (botNumber >= targetNum - 3 && botNumber <= targetNum + 3)
			overheat = true;
        
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        // get live bot & bullet info from bot helper
        if (overheat) {
			try {
			Thread.sleep(sleep);
			} catch (Exception e) {
			}
		}

        Bullet closestBullets = helper.findClosestBullet(me, bullets);
        BotInfo closestBots = helper.findClosestBot(me, liveBots);
        
        // get bullet positions
        double xBullets = closestBullets.getX();
        double yBullets = closestBullets.getY();
        
        // get enemy bot positions
        double xBots = closestBots.getX();
        double yBots = closestBots.getY();
        
        // get positions of my bot
        double xPos = me.getX();
        double yPos = me.getY();

        //get speeds (direction of movement) of bullets
        double xBulletSpeed = closestBullets.getXSpeed();
        double yBulletSpeed = closestBullets.getYSpeed();

        //get bullet & bot distances from my bot
        double bulletDistance = helper.calcDistance(xPos, yPos, xBullets, yBullets);
        double botDistance = helper.calcDistance(xPos, yPos, xBots, yBots);

        //get speeds (direction of movement) of bots

        // stay away from edges of the field to prevent getting stuck

        if(xPos + 40 > BattleBotArena.RIGHT_EDGE){
            return BattleBotArena.LEFT;
        }
        else if(xPos - 40 < BattleBotArena.LEFT_EDGE){
            return BattleBotArena.RIGHT;
        }
        else if(yPos + 40 > BattleBotArena.BOTTOM_EDGE){
            return BattleBotArena.UP;
        }
        else if(yPos - 40 < BattleBotArena.TOP_EDGE){
            return BattleBotArena.DOWN;
        }

        // prioritize closets bullets that approach me to dodge
        if (bulletDistance < 120){
            if (((xBullets > xPos) && xBulletSpeed < 0) || ((xBullets < xPos) && xBulletSpeed > 0)){    // Find bullets which approach from left or right
                if (yBullets < yPos) {return BattleBotArena.DOWN;}                                  // if bullets are somewhat above 
                else if (yBullets >= yPos){return BattleBotArena.UP;}                               // if bullets are somewhat below 
            }

            if (((yBullets > yPos) && yBulletSpeed > 0) || ((yBullets < yPos) && yBulletSpeed < 0)){    // Find bullets which approach from top or bottom
                if (xBullets > xPos) {return BattleBotArena.LEFT;}                                // if bullets are somewhat to the right 
                else if (xBullets < xPos) {return BattleBotArena.RIGHT;}                         // if bullets are somewhat to the left 
            }
        }       
        
        
        // prioritize closer bots to attack & when bullets are further 
        if(botDistance >= 80 && bulletDistance > 120){                                            
            if(xBots < (xPos + Bot.RADIUS)){                                    // if enemy bots within the range is to the left or right
                if(yBots < yPos){                                               //
                    return BattleBotArena.FIREDOWN;
                }
                else return BattleBotArena.FIREUP;
            }
            else if(yBots < (yPos + Bot.RADIUS)){
                if(xBots > xPos){
                    return BattleBotArena.FIRERIGHT;
                }
                else return BattleBotArena.FIRELEFT;
            }
        }

        // stay away from bots if too close
        if(botDistance < 80){
            if(xBots < xPos){                   //from the left of my bot
                return BattleBotArena.RIGHT;
            }
            else if(xBots > xPos){              //from the right of my bot
                return BattleBotArena.LEFT;
            }
            if(yBots < yPos){                   //from the bottom of my bot
                return BattleBotArena.UP;
            }
            if(yBots > yPos){                   //from the top of my bot
                return BattleBotArena.DOWN;
            }
        }
         
        return BattleBotArena.STAY;
        
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        if (current!=null) {
            g.drawImage(current, x, y, Bot.RADIUS*2, Bot.RADIUS*2, null);
        } else {
            g.setColor(Color.white);
            g.fillOval(x, y, Bot.RADIUS*2, Bot.RADIUS*2);
        }
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "Sharma";
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
        String[] images = {"roomba_up.png", "roomba_down.png", "roomba_left.png", "roomba_right.png"};
        return images;
    }

    @Override
    public void loadedImages(Image[] images) {
        {
            if (images != null)
            {
                current = up = images[0];
                down = images[1];
                left = images[2];
                right = images[3];
            }
        }
        
    }
    
}


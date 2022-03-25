/** 
 * DAY 1 RESULTS:
 *  GOOD THINGS: Dodging, shooting seemed to be decently effective
 * 
 *  BAD THINGS: Pinned easily, lower shooting cone to allow for movements instead of impossible shots?
 *              Path seemed to not be effective due to the linear nature. Led to getting stuck on tombs.
 * 
 *  TO DO: Tune timing for shots + pathing + grave avoidance
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */



package bots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class AlajramiBot extends Bot {

    int action = 0;
    int counter = 5;
    BotHelper helper = new BotHelper();
    Image currentImage;

    public AlajramiBot(){

    }

    @Override
    public void newRound() {
        // TODO Auto-generated method stub
        action = 0;
        counter = 5;
        
        
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
    
        Boolean moveY = false;
        Boolean moveX = false;
        int safeDistance = 100;
        int detectionBubble = 35;
        counter -= 1;
        
        for(int i = 0; i < bullets.length; i++){
            Bullet bullet = bullets[i];

            // Positive if bullet is above bot, negative if bellow
            double yDiffBullet = me.getY() - bullet.getY();
            // Positive if bullet is to left, negative if to right 
            double xDiffBullet = me.getX() - bullet.getX();

            if(Math.abs(yDiffBullet) < detectionBubble && Math.abs(yDiffBullet) > (detectionBubble)*-1 && Math.abs(xDiffBullet) < safeDistance){
                // If bullet is left of bot AND moving to the right OR If bullet is to the right of bot AND moving to the left
                if(xDiffBullet > 0 && bullet.getXSpeed() > 0 || xDiffBullet < 0 && bullet.getXSpeed() < 0){
                    moveY = true;
                }
                // If bullet is moving left/right above and below bot / above bot
                else if(yDiffBullet > 0 && bullet.getXSpeed() < 0 || yDiffBullet > 0 && bullet.getXSpeed() > 0 || yDiffBullet < 0 && bullet.getXSpeed() < 0 || yDiffBullet < 0 && bullet.getXSpeed() > 0){
                    moveY = true;
                }
                else{
                    moveY = false;
                }
                while(moveY){
                    if(yDiffBullet > 0 && bullet.getXSpeed() < 0 || yDiffBullet > 0 && bullet.getXSpeed() > 0 || bullet.getY() <= 40){// && me.getY() >= 350){
                        return BattleBotArena.DOWN;
                    }
                    else{
                        return BattleBotArena.UP;
                    }
                    
                }
            }     
            // If bullet is on same plane as bot and within certain distance
            if(Math.abs(xDiffBullet) < detectionBubble && Math.abs(xDiffBullet) > (detectionBubble)*-1 && Math.abs(yDiffBullet) < safeDistance){
                // If bullet is above bot AND bullet is moving down OR If bullet is below bot AND bullet is moving up
                if(yDiffBullet > 0 && bullet.getYSpeed() > 0 || yDiffBullet < 0 && bullet.getYSpeed() < 0){
                    moveX = true;
                    
                }
                else if(xDiffBullet > 0 && bullet.getYSpeed() < 0 || xDiffBullet > 0 && bullet.getYSpeed() > 0 || xDiffBullet < 0 && bullet.getYSpeed() < 0 || xDiffBullet < 0 && bullet.getYSpeed() > 0){
                    moveX = true;
                }
                else{
                    moveX = false;
                }
                while(moveX){
                    if(xDiffBullet < 0 && bullet.getYSpeed() < 0 || xDiffBullet < 0 && bullet.getYSpeed() > 0 && bullet.getX() >= 40){// && me.getX() >=500 ){
                        return BattleBotArena.LEFT;
                    }
                    else{
                        return BattleBotArena.RIGHT;
                    }
                }
            }


        }

        for(int i = 0; i < liveBots.length; i++){
            BotInfo bot = liveBots[i];

            // Positive if bot is above bot, negative if below
            double yDiffBot = me.getY() - bot.getY();
            // Positive if bot is to left, negative if to right 
            double xDiffBot = me.getX() - bot.getX();

            if(Math.abs(yDiffBot) < detectionBubble-22 && Math.abs(yDiffBot) > (detectionBubble-22)*-1 && Math.abs(xDiffBot) < 800){
                // If bot is left of me AND moving to the right
                if(xDiffBot > 0 && shotOK && me.getLastMove() != BattleBotArena.FIRELEFT && counter <= 0){
                    counter = 5;
                    return BattleBotArena.FIRELEFT;
                }
                // If bot is to the right of me AND moving to the left
                else if(xDiffBot < 0 && shotOK && me.getLastMove() != BattleBotArena.FIRERIGHT && counter <= 0){
                    counter = 5;
                    return BattleBotArena.FIRERIGHT;
                }
            }     

            // If bot is on same plane as bot and within certain distance
            if(Math.abs(xDiffBot) < detectionBubble-22 && Math.abs(xDiffBot) > (detectionBubble-22)*-1 && Math.abs(yDiffBot) < 800){
                // If bot is above me
                if(yDiffBot > 0 && shotOK && me.getLastMove() != BattleBotArena.FIREUP && counter <= 0){
                    counter = 5;
                    return BattleBotArena.FIREUP;
                }
                // If bot is below me
                else if(yDiffBot < 0 && shotOK && me.getLastMove() != BattleBotArena.FIREDOWN && counter <= 0){
                    counter = 5;
                    return BattleBotArena.FIREDOWN;
                }
            }    
        }
 
        for(int i = 0; i < liveBots.length; i++){
            BotInfo bot = liveBots[i];

            // Positive if tomb is above bot, negative if below
            double yDiffBot = me.getY() - bot.getY();
            // Positive if tomb is to left, negative if to right 
            double xDiffBot = me.getX() - bot.getX();

            if(Math.abs(yDiffBot) < detectionBubble-21.5 && Math.abs(yDiffBot) > (detectionBubble-21.5)*-1 && Math.abs(xDiffBot) < 50){
                // If tomb is to the left
                if(xDiffBot > 0){
                    return BattleBotArena.RIGHT;
                }
                // If tomb is to the right
                else if(xDiffBot < 0){
                    return BattleBotArena.LEFT;
                }
            }     

            // If bot is on same plane as bot and within certain distance
            if(Math.abs(xDiffBot) < detectionBubble-21.5 && Math.abs(xDiffBot) > (detectionBubble-21.5)*-1 && Math.abs(yDiffBot) < 50){
                // If tomb is above me
                if(yDiffBot > 0){
                    return BattleBotArena.DOWN;
                }
                // If bot is below me
                else if(yDiffBot < 0){
                    return BattleBotArena.UP;
                }
            }    
        }

        for(int i = 0; i < deadBots.length; i++){
            BotInfo tomb = deadBots[i];

            // Positive if tomb is above bot, negative if below
            double yDiffTomb = me.getY() - tomb.getY();
            // Positive if tomb is to left, negative if to right 
            double xDiffTomb = me.getX() - tomb.getX();

            if(Math.abs(yDiffTomb) < detectionBubble-21.5 && Math.abs(yDiffTomb) > (detectionBubble-21.5)*-1 && Math.abs(xDiffTomb) < 50){
                // If tomb is to the left
                if(xDiffTomb > 0){
                    return BattleBotArena.RIGHT;
                }
                // If tomb is to the right
                else if(xDiffTomb < 0){
                    return BattleBotArena.LEFT;
                }
            }     

            // If bot is on same plane as bot and within certain distance
            if(Math.abs(xDiffTomb) < detectionBubble-21.5 && Math.abs(xDiffTomb) > (detectionBubble-21.5)*-1 && Math.abs(yDiffTomb) < 50){
                // If tomb is above me
                if(yDiffTomb > 0){
                    return BattleBotArena.DOWN;
                }
                // If bot is below me
                else if(yDiffTomb < 0){
                    return BattleBotArena.UP;
                }
            }    
        }
        if(action <= 100){
            action += 1;
            return BattleBotArena.UP;
        }

        if(action <= 200 && action > 100){
            action += 1;
            return BattleBotArena.RIGHT;
        }

        if(action <= 300 && action > 200){
            action += 1;
            return BattleBotArena.DOWN;
        }

        if(action <= 400 && action > 300){
            action += 1;
            return BattleBotArena.LEFT;
        }

        if(action > 400){
            action = 0;
            return BattleBotArena.DOWN;   
        }

        return BattleBotArena.STAY;
          
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "Alajrami";
    }

    @Override
    public String getTeamName() {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String outgoingMessage() {
        // TODO Auto-generated method stub
        return "Zoo wee mama";
    }

    @Override
    public void incomingMessage(int botNum, String msg) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String[] imageNames() {
        String image[] = {"dead.png"};
        return image;
    }

    @Override
    public void loadedImages(Image[] images) {
        if (images != null) {
            currentImage = images[0];
        }
        
    }
    
    @Override
    public void draw(Graphics g, int x, int y) {
        // TODO Auto-generated method stub
        if(currentImage != null){
            g.drawImage(currentImage, x, y, Bot.RADIUS * 2, Bot.RADIUS * 2, null);
        }
        else{
            g.setColor(Color.yellow);
		    g.fillRect(x+2, y+2, RADIUS*2-4, RADIUS*2-4);
        }
    }

}

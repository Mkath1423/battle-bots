package bots;

import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class JinBot extends Bot{

    BotHelper helper = new BotHelper();
    private int count = 0;

    Image current, up, down, right, left; 

    @Override
    public void newRound() {
        // TODO Auto-generated method stub
        count = 0;
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        // TODO Auto-generated method stub
        BotInfo closestBot = helper.findClosestBot(me, liveBots);
        double closestBulletX = (helper.findClosestBullet(me, bullets)).getX();
        double closestBulletY = (helper.findClosestBullet(me, bullets)).getY();
        double closestBotX = (helper.findClosestBot(me, liveBots)).getX();
        double closestBotY = (helper.findClosestBot(me, liveBots)).getY();
        double xPos = me.getX();
        double yPos = me.getY();
        double trackX = Math.abs(closestBotX-xPos);
        double trackY = Math.abs(closestBotY-yPos);

        if(closestBotX <= xPos && xPos <= closestBotX + 10){
            if(closestBotY > yPos){
                return BattleBotArena.FIREDOWN;
            }
            else if(closestBotY < yPos){
                return BattleBotArena.FIREUP;
            }
        } 

        else if(closestBotY <= yPos && yPos <= closestBotY + 10){
            if(closestBotX > xPos){
                return BattleBotArena.FIRERIGHT;
            }
            else if(closestBotX < xPos){
                return BattleBotArena.FIRELEFT;
            }
        } 

        else if (trackX > trackY){
            if(closestBotX > closestBulletX){
                return BattleBotArena.RIGHT;
            }  
            else{
                return BattleBotArena.LEFT;
            }
        }

        else if (trackX < trackY){
            if(closestBotY > closestBulletY){
                return BattleBotArena.DOWN;
            }
            else{
                return BattleBotArena.UP;
            }
        }
        return BattleBotArena.STAY;

        
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        // TODO Auto-generated method stub
        g.drawImage(current, x, y, Bot.RADIUS * 2, Bot.RADIUS * 2, null);
    }

    String name;
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        if(name == null){
            name = "JINKR";
        }
        return name;
    }

    String teamName;
    @Override
    public String getTeamName() {
        // TODO Auto-generated method stub
        if(teamName == null){
            teamName = "Dgods24";
        }
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
        String images[] = {"pikachu_up.png","pikachu_down.png","pikachu_left.png","pikachu_right.png"};
        return images;
    }

    @Override
    public void loadedImages(Image[] images) {
        // TODO Auto-generated method stub
        if (images != null) {
            if(images.length > 0)
                up = images[0];
            if(images.length > 1)
                down = images[1];
            if(images.length > 2)
                left = images[2];
            if(images.length > 3)
                right = images[3];
            current = up;

        }
    }
    
}

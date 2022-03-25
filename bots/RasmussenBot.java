package bots;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.Action;
import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class RasmussenBot extends Bot {

    BotHelper helper = new BotHelper();
    private int action = 0;


    @Override
    public void newRound() {
        
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        helper.findClosestBot(me, liveBots);
        double closestBulletX = (helper.findClosestBullet(me, bullets)).getX();
        double closestBulletY = (helper.findClosestBullet(me, bullets)).getY();
        double closestBotX = (helper.findClosestBot(me, liveBots)).getX();
        double closestBotY = (helper.findClosestBot(me, liveBots)).getY();
        double xPos = me.getX();
        double yPos = me.getY();
        
         if (((yPos-closestBotY)>=-4)&&((yPos-closestBotY)<=0)){
            action = 0;
        } else if (((yPos-closestBotY)>=1)&&((yPos-closestBotY)<=4)){
            action = 1;
        } else if (((xPos-closestBotX)>=-4)&&((xPos-closestBotX)<=0)){
            action = 2;
        } else if (((xPos-closestBotX)>=1)&&((xPos-closestBotX)<=4)){
            action = 3;
        } else if (((yPos-closestBulletY)>=-6)&&((yPos-closestBulletY)<=0)){
            action = 4;
        } else if (((yPos-closestBulletY)>=1)&&((yPos-closestBulletY)<=6)){
            action = 5;
        } else if (((xPos-closestBulletX)>=-6)&&((xPos-closestBulletX)<=0)){
            action = 6;
        } else if (((xPos-closestBulletX)>=1)&&((xPos-closestBulletX)<=6)){
            action = 7;
        } else {
            int IDK = (int)Math.floor(Math.random()*(3));
            action = IDK;
        }

        switch (action) {
                    case 0: action++; return BattleBotArena.FIRERIGHT;
                    case 1: action++; return BattleBotArena.FIRELEFT;
                    case 2: action++; return BattleBotArena.FIREUP;
                    case 3: action++; return BattleBotArena.FIREDOWN;
                    case 4: action++; return BattleBotArena.UP;
                    case 5: action++; return BattleBotArena.DOWN;
                    case 6: action++; return BattleBotArena.LEFT;
                    case 7: action++; return BattleBotArena.RIGHT;
                }
        return BattleBotArena.STAY;
        }


    @Override
    public void draw(Graphics g, int x, int y) {
        g.setColor(Color.blue);
		g.fillRect(x+2, y+2, RADIUS*2-4, RADIUS*2-4);
        
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "Razzy";
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

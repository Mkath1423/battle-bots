package bots;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.text.html.StyleSheet.BoxPainter;

//import apple.laf.JRSUIConstants.Hit;
import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class JabshehBot extends Bot {

    BotHelper helper = new BotHelper();

    Image current, up, down, right, left;

    private int move = BattleBotArena.UP;

    private double botX = 0;
    private double botY = 0;

    // Bullets Variables:
    int moves = BattleBotArena.STAY;
    int hitBoxRad = 10;

    @Override
    public void newRound() {
        // TODO Auto-generated method stub

    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        // Work here to move the robot in the direction desired:

        // Get the location of the closest Bot Avaliable:
        double myX = botX;
        double myY = botY;

        // Finding Location of Bots
        BotInfo closestAlive = helper.findClosestBot(me, liveBots);
        // Finding Location of Bullets - Fix Later!!
        Bullet closestBullet = helper.findClosestBullet(me, bullets);


        // Coordinates of Bots:
        botX = closestAlive.getX();
        botY = closestAlive.getY();

        // Corrdinates of Bullets:
        double bulletX = closestBullet.getX();
        double bulletY = closestBullet.getY();

        // Get the location of "MY" bot:
        double myXPos = me.getX();
        double myYPos = me.getY();

        // For Bots Variables Math
        double xValDiff = myX - botX;
        double yValDiff = myY - botY;

        // For Buellts Variables Math: 
        double bulletDistance = helper.calcDistance(myXPos, myYPos, bulletX, bulletY);




        // Main Logic and If Statements For Bots
        if ((xValDiff == 0) && (yValDiff == 0)) {

            if (myYPos > botY) {
                return BattleBotArena.FIREUP;
            }

            if (myYPos < botY) {
                return BattleBotArena.FIREDOWN;
            }

            if (myXPos < botX) {
                return BattleBotArena.FIRERIGHT;
            }

            if (myXPos > botX) {
                return BattleBotArena.FIRELEFT;

            }

        }

        // Main Logic and If Statements For Buellets: - Find the closest Bullet 
        if (bulletDistance < 50){
            if ((bulletX > myXPos) || (bulletX < myXPos)){
                if (bulletY > myXPos+hitBoxRad) {
                    moves = BattleBotArena.DOWN;
                }        
                
                // if bullets are closesly above me do this:  
                else if (bulletY <= myYPos-hitBoxRad) {
                moves = BattleBotArena.UP;
                }    // if bullets are somewhat below 
            }
            if ((bulletY > myYPos) || (bulletY < myYPos)){                                 // Find bullets which approach from top or bottom
                if (bulletX > myXPos+hitBoxRad) {
                    moves = BattleBotArena.DOWN;
                }       
                 // if bullets are somewhat to the right 
                else if (bulletX <= myYPos-hitBoxRad) {
                    moves = BattleBotArena.UP;
                } // if bullets are somewhat to the left 
            }
           
        }   
            return moves;
         
    }

       
        

    

        

        
    

    @Override
    public void draw(Graphics g, int x, int y) {
        // TODO Auto-generated method stub
        g.drawImage(current, x, y, Bot.RADIUS * 2, Bot.RADIUS * 2, null);

    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "MJJJJJ";
    }

    @Override
    public String getTeamName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String outgoingMessage() {
        // TODO Auto-generated method stub
        return "Ez Winss!!!!!!";
    }

    @Override
    public void incomingMessage(int botNum, String msg) {
        // TODO Auto-generated method stub

    }

    @Override
    public String[] imageNames() {
        // TODO Auto-generated method stub
        String[] paths = { "Red.png" };
        return paths;
    }

    @Override
    public void loadedImages(Image[] images) {
        // TODO Auto-generated method stub
        current = images[0];

    }

}

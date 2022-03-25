// this bot is mainly to provide support
package bots;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
//import java.awt.event.KeyEvent;
//import java.awt.event.KeyListener;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class MofeBot extends Bot {
    BotHelper helper = new BotHelper();
    private double nearest_botY = 0;
    private double nearest_botX = 0;
    @Override
    public void newRound() {
        
        
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        
        //adding a comment
        //helper.findClosestBot(me, liveBots);
        nearest_botY = (helper.findClosestBot(me, liveBots).getY());
        nearest_botX = (helper.findClosestBot(me, liveBots).getX());
        double XPos = me.getX();
        double YPos = me.getY();
        
      
       
        
        return BattleBotArena.UP;
        
    }

    @Override
    public void draw(Graphics g, int x, int y) {
       
        
    }

    @Override
    public String getName() {
        
        return null;
    }

    @Override
    public String getTeamName() {
        // 
        return null;
    }

    @Override
    public String outgoingMessage() {
       
        return null;
    }

    @Override
    public void incomingMessage(int botNum, String msg) {
        
        
    }

    @Override
    public String[] imageNames() {
       
        return null;
    }

    @Override
    public void loadedImages(Image[] images) {
        
        
    }
    
}

    


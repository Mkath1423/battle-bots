//Things that went well
//
// Things that don't go as planned

//TODO: 
package bots;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;


import arena.BattleBotArena;
import java.awt.event.KeyEvent;

import arena.BotInfo;
import arena.Bullet;
//import java.awt.event.KeyEvent;
//import java.awt.event.KeyListener;



public class KarthikBot extends Bot {
    private Image image = null;
	private int counter=0;
    private boolean[] alert;


    BotHelper helper = new BotHelper();

    @Override
    public void newRound() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        // TODO Auto-generated method stub
        //helper.findClosestBot(_me, _bots)
		
		
		if (--counter <= 0 && shotOK)
		{
			
			counter = 15;
			int num = (int)(Math.random()*4);
			if (num == 0)
				return BattleBotArena.FIRERIGHT;
			else if (num == 1)
				return BattleBotArena.FIRELEFT;
			else if (num == 2)
				return BattleBotArena.FIREDOWN;
			else
				return BattleBotArena.FIREUP;
		}
		else
			return BattleBotArena.UP;
  
    }

/* for (int i=0; i<liveBots.length; i++)
		{
			if (!alert[liveBots[i].getBotNumber()]) 
			{
				//Use Manhattan Distance to recognize other live bots
				double d = Math.abs(me.getX()-liveBots[i].getX())+Math.abs(me.getY()-liveBots[i].getY());
				if (d < 20) 
				{
					return BattleBotArena.LEFT;
				}
			}
		}*/

      
    

    @Override
    public void draw(Graphics g, int x, int y) {
        // TODO Auto-generated method stub
		if (image != null)
			g.drawImage(image, x,y,Bot.RADIUS*1, Bot.RADIUS*1, null);
		else
		{
			g.setColor(Color.pink);
			g.fillOval(x, y, Bot.RADIUS*1, Bot.RADIUS*1);
		}
		
        
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "KarthikBot";
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

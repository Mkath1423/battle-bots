//What went well: Dodging was good as well as the fire when safe or being fired at.
//What did not go well: Spawn killed. Run into other bullets after dodging: Fire is not based on if a robot is near; Tombstones:

//TODO: 
//Move around tombstones:
//Have an option to find other bots but still dodge if they shoot;


package bots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;



public class KyleBot extends Bot {

	/**
	 * My name
	 */
	String name;
	/**
	 * My next message or null if nothing to say
	 */
	String nextMessage = null;
	/**
	 * Array of happy drone messages
	 */
//	private String[] messages = {"I am a drone", "Working makes me happy", "I am content", "I like to vaccuum", "La la la la la...", "I like squares"};
	/**
	 * Image for drawing
	 */
	Image up, down, left, right, current;
	/**
	 * For deciding when it is time to change direction
	 */
	private int counter = 50;
	/**
	 * Current move
	 */
	private int move = BattleBotArena.UP;

	/**
	 * My last location - used for detecting when I am stuck
	 */
	private double x, y;

	private int resume;

    int shooter;
	private int moveCount = 99;
	private int msgCounter = 0;
	int choice = 0;
	int bulletcounter;
	int way = 1;
	int fire;
	int fireCount;
	
	double closeBulletX;
	double closeBulletY;
	double closeBulletXSpeed;
	double closeBulletYSpeed;
	double closeBotX;
	double closeBotY;
	double closeDeadBotX;
	double closeDeadBotY;

	BotHelper helper = new BotHelper();
	/**
	 * Draw the current Drone image
	 */
	public void draw(Graphics g, int x, int y) {
		g.drawImage(current, x, y, Bot.RADIUS*2, Bot.RADIUS*2, null);
		
	}


	/**
	 * Move in squares and fire every now and then.
	 */
	public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots,
		BotInfo[] deadBots, Bullet[] bullets) {
         
	

		
			//
		//Dodging code	
			
		//find closest bullet coordinates
		closeBulletX = helper.findClosestBullet(me, bullets).getX();
		closeBulletY = helper.findClosestBullet(me, bullets).getY();
				
		closeBulletXSpeed = helper.findClosestBullet(me, bullets).getXSpeed();
		closeBulletYSpeed = helper.findClosestBullet(me, bullets).getYSpeed();
		
		//find closest bot
		closeBotX =	helper.findClosestBot(me, liveBots).getX();
		closeBotY =	helper.findClosestBot(me, liveBots).getY();

		//get my position
		x = me.getX();
		y = me.getY();

	//	System.out.println(closeBulletXSpeed);
		double distanceToBot = Math.sqrt((x-closeBotX)*(x-closeBotX) + (y-closeBotY)*(y-closeBotY));
		double distanceToBullet = Math.sqrt((x-closeBulletX)*(x-closeBulletX) + (y-closeBulletY)*(y-closeBulletY));
		
		if (distanceToBullet < distanceToBot){



		



//if bullet is closer than robot, dodge bullet



		//Check to see if there is a bullet within 20 distance
		//if ((closeBulletX <= x+20 && closeBulletX >= x-20)||(closeBulletY <= y+20 && closeBulletY >= y-20))
		//{
			//bullet is on your right and moving left
			if ((closeBulletX >= x && closeBulletXSpeed < 0) && (closeBulletY <= y)) 
				{
				way=2;  //Down
				}
			
					else if ((closeBulletX >= x && closeBulletXSpeed < 0) && (closeBulletY >= y)) 
				{
				way=1;  //Up
				}
		
			
			//bullet is on your left and moving right
			else if ((closeBulletX <= x && closeBulletXSpeed > 0) && (closeBulletY >=y))
				{
				way=1;  //Up
			
			}
				else if ((closeBulletX <= x && closeBulletXSpeed > 0) && (closeBulletY <=y))
				{
				way=2;  //Down
				}
				
			

			//bullet is above and moving down
			else if ((closeBulletY <= y && closeBulletYSpeed > 0) && (closeBulletX >= x))
				{
				way=3;  //Left
				}
				
			else if ((closeBulletY <= y && closeBulletYSpeed > 0) && (closeBulletX <= x))
				{
				way=4;  //Right
				}
			
			//bullet is below and moving up
			else if ((closeBulletY >= y && closeBulletYSpeed < 0)  && (closeBulletX <= x)) 
				{
				way=4;  //Right
				}
			
			else if ((closeBulletY >= y && closeBulletYSpeed < 0)  && (closeBulletX >= x)) 
				{
				way=3;  //Left
				}
				else{
					return BattleBotArena.STAY;
				}
			//fire when not dodging
			/*else{
				//use a switch to fire in all directions (depending on number of bullets on screen)
				switch (fire) {
        	    	case 1: fire+=1;  return BattleBotArena.FIREUP;
                	case 2: fire+=1;  return BattleBotArena.FIRERIGHT;
        			case 3: fire+=1;  return BattleBotArena.FIREDOWN;
        			case 4: fire=1;  return BattleBotArena.FIRELEFT;
				}
				} 

			//}
*/
		////////////////////////////////////////////////////		
		//closeBotX =	helper.findClosestBot(me, liveBots).getX();
		//closeBotY =	helper.findClosestBot(me, liveBots).getY();

		//x = me.getX();
		//y = me.getY();

		// tracks bot up and down

		}
		else{

		
		//fire at bot on the left
		if((closeBotY <= y+10 && closeBotY >= y-10) && (closeBotX >=x)){
			way = 8;
			
						
		}
		// fire at bot on the right
		else if((closeBotY <= y+10 && closeBotY >= y-10) && (closeBotX <=x)){
			way = 7;
			
		}

		
		//move to the bot below and get to the same x-axis
		else if(closeBotY > y){
			
			way = 2;

		}
		//move to the bot above and get to the same x-axis
		else if(closeBotY < y){
			
			way = 1;

		}

		  if((closeBotX <= x+20 && closeBotX >= x-20) && (closeBotY >y)){
			way = 6;
			
		}
		
		else if((closeBotX <= x+20 && closeBotX >= x-20) && (closeBotY <y)){
			way = 5;
			
		}

		//move around gravestone code:
		/*closeDeadBotX =	helper.findClosestBot(me, deadBots).getX();
		closeDeadBotY =	helper.findClosestBot(me, deadBots).getY();

		x = me.getX();
		y = me.getY();

		if ((closeDeadBotX <= x+1 && closeDeadBotX >= x-1)|| (closeDeadBotY <= y+1 && closeDeadBotY >= y-1))
		{
			//grave is on your right 
			if (closeDeadBotX > x) 
				{
				way=3;  //left
				}
			//grave is on your left	
			else if (closeDeadBotX < x) 
				{
				way=4;  //right
				}
			
			
			//grave is above
			else if (closeDeadBotY > y)
				{
				way=2;  //Down
				}
			//grave is below	
				else if (closeBotY < y)
				{
				way=1;  //Up
				}

		
			}
*/
		}

			//use a switch to set a move direction
			switch (way) {
				case 1:
					move = BattleBotArena.UP;
					resume = move;
					break;
				case 2:
					move = BattleBotArena.DOWN;
					resume = move;
					break;
				case 3:
					move = BattleBotArena.LEFT;
					resume = move;
					break;
				case 4:
					move = BattleBotArena.RIGHT;
					resume = move;
					break;	
				case 5:
					move = BattleBotArena.FIREUP;
					resume = move;
					break;
				case 6:
					move = BattleBotArena.FIREDOWN;
					resume = move;
					break;
				case 7:
					move = BattleBotArena.FIRELEFT;
					resume = move;
					break;
				case 8:
					move = BattleBotArena.FIRERIGHT;
					resume = move;
					break;	
				}

			//passing the move direction to the board
			
			return move;
		}
			

	/**
	 * Construct and return my name
	 */
	public String getName()
	{
		if (name == null)
			name = "Kyle";
		return name;
	}

	/**
	 * Team Arena!
	 */
	public String getTeamName() {
		return "24";
	}

	/**
	 * Pick a random starting direction
	 */
    
	public void newRound() {
	
	
	
	
	}

	/**
	 * Image names
	 */
	public String[] imageNames()
	{
		String[] images = {"pikachu_up.png","pikachu_down.png","pikachu_left.png","pikachu_right.png"};
		return images;
	}

	/**
	 * Store the loaded images
	 */
	public void loadedImages(Image[] images)
	{
		if (images != null)
		{
			current = up = images[0];
			down = images[1];
			left = images[2];
			right = images[3];
		}
	}

	/**
	 * Send my next message and clear out my message buffer
	 */
	public String outgoingMessage()
	{
		String msg = nextMessage;
		nextMessage = null;
		return msg;
	}

	/**
	 * Required abstract method
	 */
	public void incomingMessage(int botNum, String msg)
	{

	}

}





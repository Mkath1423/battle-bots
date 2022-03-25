package bots;

import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class Megaman extends Bot {





    // INITIALIZATION





    BotHelper Rush = new BotHelper();
    Image up, down, left, right, current;

	// What phase of the plan am I on?
	private boolean phase1 = true;
	private boolean phase2 = false;
	private boolean phase3 = false;

	// Specialized movement control variables along their axis; hard to explain specifics
	private boolean xLock = false;
	private boolean yLock = false;

	private double oldX;	// X position prior to dodging
	private double oldY;	// Y position prior to dodging

	private int pastMove = BattleBotArena.UP;		// The last move made
	private int pastShot = BattleBotArena.FIREDOWN;	// The last direction fired in
	private int dodge    = 0;						// 0 if I don't have to dodge, direction if I have to

	

	/**
	 * This method is called at the beginning of each round. Use it to perform
	 * any initialization that you require when starting a new round.
	 */
    @Override
    public void newRound() {

    }





	// "MAIN" METHOD





	/**
	 * This method is called at every time step to find out what you want your
	 * Bot to do. The legal moves are defined in constants in the BattleBotArena
	 * class (UP, DOWN, LEFT, RIGHT, FIREUP, FIREDOWN, FIRELEFT, FIRERIGHT, STAY,
	 * SEND_MESSAGE). <br><br>
	 *
	 * The <b>FIRE</b> moves cause a bullet to be created (if there are
	 * not too many of your bullets on the screen at the moment). Each bullet
	 * moves at speed set by the BULLET_SPEED constant in BattleBotArena. <br><br>
	 *
	 * The <b>UP</b>, <b>DOWN</b>, <b>LEFT</b>, and <b>RIGHT</b> moves cause the
	 * bot to move BOT_SPEED
	 * pixels in the requested direction (BOT_SPEED is a constant in
	 * BattleBotArena). However, if this would cause a
	 * collision with any live or dead bot, or would move the Bot outside the
	 * playing area defined by TOP_EDGE, BOTTOM_EDGE, LEFT_EDGE, and RIGHT_EDGE,
	 * the move will not be allowed by the Arena.<br><Br>
	 *
	 * The <b>SEND_MESSAGE</b> move (if allowed by the Arena) will cause a call-back
	 * to this Bot's <i>outgoingMessage()</i> method, which should return the message
	 * you want the Bot to broadcast. This will be followed with a call to
	 * <i>incomingMessage(String)</i> which will be the echo of the broadcast message
	 * coming back to the Bot.
	 *
	 * @param me		A BotInfo object with all publicly available info about this Bot
	 * @param shotOK	True if a FIRE move is currently allowed
	 * @param liveBots	An array of BotInfo objects for the other Bots currently in play
	 * @param deadBots	An array of BotInfo objects for the dead Bots littering the arena
	 * @param bullets	An array of all Bullet objects currently in play
	 * @return			A legal move (use the constants defined in BattleBotArena)
	 */
    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        


        // Battleplan:
		//
		//	Phase 1
        //  Traverse to the top right corner while shooting down and left
		//  
		//	Phase 2
		//	Once very near, traverse to the left while shooting down & opposite to motion
		//  Return to starting position once reached halfway through arena or collision
        //  
		//	Phase 3
		//	Repeat phase 2, except down and up, and shooting left & opposite to motion
		//	Upon conclusion, revert to phase 2
        //  
        // Collisions. If you are within a given distance of:
        //  
		//  Bullet:		Move perpendicularly away, OVERRIDES ALL ELSE		(get out of bullet's trajectory)
        //  Dead Bot:   Shoot in opposite direction and move directly away 	(use cover of tombstone)
        //  Live Bot:   Shoot towards it and move perpendicularly away 		(avoid potential bullet trajectory)



		// Commence Phase 1
		while(phase1) {

			// Attack
			if(shotOK && fire(1, me, liveBots, deadBots, bullets)) return pastShot;
		
			// If you have reached the edge, lock movement along that axis
			if((me.getX() + (RADIUS*8)) >= BattleBotArena.RIGHT_EDGE) xLock = true; 
			if((me.getY() - (RADIUS*5)) <= BattleBotArena.TOP_EDGE  ) yLock = true; 

			// If you have reached the top right corner, conclude phase 1
			if(xLock && yLock) {

				phase1 = false;
				phase2 = true;
				xLock = false;
				yLock = false;
				break;
			}

			// If your old move was up, go right if you can
			if(pastMove == BattleBotArena.UP) {

				pastMove = BattleBotArena.RIGHT;
				if(!xLock) return pastMove;
			}

			// If your old move was right, go up if you can
			if(pastMove == BattleBotArena.RIGHT) {

				pastMove = BattleBotArena.UP;
				if(!yLock) return pastMove;
			}		
		}



		// Commence Phase 2
		while(phase2) {

			// // Dodge
			// if(evasion(2, me, shotOK, liveBots, deadBots, bullets)) 
			// 	if(avoid(me, liveBots, deadBots, bullets) != -1) 
			// 		return pastMove;

			// Attack
			if(shotOK && fire(2, me, liveBots, deadBots, bullets)) return pastShot;

			// If reached close enough to left edge, retreat
			if(me.getX() - (RADIUS * 35) <= BattleBotArena.LEFT_EDGE)
				xLock = true;

			// Move
			if(!xLock) return BattleBotArena.LEFT;
			if(xLock) {

				// If returned, commence phase 3
				if((me.getX() + (RADIUS*8)) >= BattleBotArena.RIGHT_EDGE) {

					xLock  = false;
					phase2 = false;
					phase3 = true;
					break;
				}

				return BattleBotArena.RIGHT;
			}
		}



		// Commence Phase 3
		while(phase3) {

			// // Dodge
			// if(evasion(3, me, shotOK, liveBots, deadBots, bullets)) 
			// 	if(avoid(me, liveBots, deadBots, bullets) != -1) 
			// 		return pastMove;

			// Attack
			if(shotOK && fire(3, me, liveBots, deadBots, bullets)) return pastShot;

			// If reached close enough to bottom edge, retreat
			if((me.getY() + (RADIUS*30)) >= BattleBotArena.BOTTOM_EDGE) 
				yLock = true;

			// Move
			if(!yLock) return BattleBotArena.DOWN;
			if(yLock) {

				// If returned, commence phase 2
				if((me.getY() - (RADIUS*5)) <= BattleBotArena.TOP_EDGE) {

					yLock  = false;
					phase2 = true;
					phase3 = false;
					break;
				}

				return BattleBotArena.UP;
			}

		}

		// Fail-safe emergency move
		if(shotOK && (Math.floor(Math.random()*(101)) > 98)) return pastShot;
		return pastMove;
    }



	

	// ACTIVE METHODS





	/**
	 * Called for attacking to set pastShot to the appropriate direction
	 * 
	 * @param phase		An int indiciating the current phase
	 * @param me		A BotInfo object with all publicly available info about this Bot
	 * @param liveBots	An array of BotInfo objects for the other Bots currently in play
	 * @param deadBots	An array of BotInfo objects for the dead Bots littering the arena
	 * @param bullets	An array of all Bullet objects currently in play
	 * 
	 * @return			Do I have to shoot?
	 */
	public boolean fire(int phase, BotInfo me, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {

		// Generate a number 0-100 
		// Random formula = Math.floor(Math.random()*(max-min+1)+min)
		int n = (int)Math.floor(Math.random()*(101));

		if(n >= 98) {

			switch(phase) {

				// Shot made during phase 1
				case 1:

					switch(pastShot) {

						case BattleBotArena.FIREDOWN:
							pastShot = BattleBotArena.FIRERIGHT;
							return true;
			
						case BattleBotArena.FIRERIGHT:
							pastShot = BattleBotArena.FIREDOWN;
							return true;
						
						default:
							pastShot = BattleBotArena.FIRERIGHT;
							return true;
					
					}

				// Shot made during phase 2
				case 2:

					// Moving left
					if(!xLock) {

						switch(pastShot) {
		
							case BattleBotArena.FIREDOWN:
								pastShot = BattleBotArena.FIRERIGHT;
								return true;
			
							case BattleBotArena.FIRERIGHT:
								pastShot = BattleBotArena.FIREDOWN;
								return true;
			
							default:
								pastShot = BattleBotArena.FIREDOWN;
								return true;
						
						}
					
					// Moving right
					} else if (xLock) {

						switch(pastShot) {
		
							case BattleBotArena.FIREDOWN:
								pastShot = BattleBotArena.FIRELEFT;
								return true;
			
							case BattleBotArena.FIRELEFT:
								pastShot = BattleBotArena.FIREDOWN;
								return true;
			
							default:
								pastShot = BattleBotArena.FIREDOWN;
								return true;
						
						}

					}

				// Shot made during phase 3
				case 3:

					// Moving down
					if(!yLock) {

						switch(pastShot) {
		
							case BattleBotArena.FIREUP:
								pastShot = BattleBotArena.FIRELEFT;
								return true;
			
							case BattleBotArena.FIRELEFT:
								pastShot = BattleBotArena.FIREUP;
								return true;
			
							default:
								pastShot = BattleBotArena.FIRELEFT;
								return true;
						
						}

					// Moving up
					} else if (yLock) {

						switch(pastShot) {
		
							case BattleBotArena.FIREDOWN:
								pastShot = BattleBotArena.FIRELEFT;
								return true;
			
							case BattleBotArena.FIRELEFT:
								pastShot = BattleBotArena.FIREDOWN;
								return true;
			
							default:
								pastShot = BattleBotArena.FIRELEFT;
								return true;
						
						}

					}

			}
		}

		// No shot was chosen
		return false;
	}



	/**
	 * Called for dodging to set dodge to the appropriate direction
	 * 
	 * @param phase		An int indiciating the current phase
	 * @param me		A BotInfo object with all publicly available info about this Bot
	 * @param shotOK	True if a FIRE move is currently allowed
	 * @param liveBots	An array of BotInfo objects for the other Bots currently in play
	 * @param deadBots	An array of BotInfo objects for the dead Bots littering the arena
	 * @param bullets	An array of all Bullet objects currently in play
	 * 
	 * @return			Do I have to dodge?
	 */
	public boolean evasion(int phase, BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {

		Bullet  bullet  = Rush.findClosestBullet(me, bullets);
		BotInfo liveBot = Rush.findClosestBot(me, liveBots);
		BotInfo deadBot = Rush.findClosestBot(me, deadBots);

		switch(phase) {

			// Phase 1
			case 1:



			// Phase 2
			case 2:

				// Horizontally approaching bullet
				if(Math.abs(bullet.getX() - me.getX()) <= RADIUS*2 && bullet.getXSpeed() != 0) {

					oldX = me.getX();
					oldY = me.getY();
					
					dodge = BattleBotArena.DOWN;
					return true;

				// Vertically approaching bullet
				} else if(Math.abs(bullet.getY() - me.getY()) <= RADIUS*2 && bullet.getYSpeed() != 0) {

					oldX = me.getX();
					oldY = me.getY();

					// Currently moving left
					if(!xLock) {

						dodge = BattleBotArena.RIGHT;
						return true;

					// Currently moving right
					} else if (xLock) {

						dodge = BattleBotArena.LEFT;
						return true;

					}

				}
			
			// Phase 3
			case 3:

				// Horizontally approaching bullet
				if(Math.abs(bullet.getX() - me.getX()) <= RADIUS*2 && bullet.getXSpeed() != 0) {

					// Currently moving down
					if(!yLock) {

						dodge = BattleBotArena.UP;
						return true;

					// Currently moving up
					} else if (yLock) {

						dodge = BattleBotArena.DOWN;
						return true;

					}

				// Vertically approaching bullet
				} else if(Math.abs(bullet.getY() - me.getY()) <= RADIUS*2 && bullet.getYSpeed() != 0) {

					oldX = me.getX();
					oldY = me.getY();
					
					dodge = BattleBotArena.LEFT;
					return true;

				}

		}

		// No need to dodge
		return false;
	}	



	/**
	 * Called for actuating the dodge
	 * 
	 * @param me		A BotInfo object with all publicly available info about this Bot
	 * @param liveBots	An array of BotInfo objects for the other Bots currently in play
	 * @param deadBots	An array of BotInfo objects for the dead Bots littering the arena
	 * @param bullets	An array of all Bullet objects currently in play
	 * 
	 * @return 			Move to make
	 */
	int avoid(BotInfo me, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {

		switch(dodge) {

			// Dodge up 
			case BattleBotArena.UP:

				while(Math.abs(me.getY() - oldY) <= RADIUS*2) {

					// Break if you would dodge to the edge
					if((me.getY() - BattleBotArena.TOP_EDGE) <= RADIUS*2) {

						dodge = 0;
						return -1;
					} 
					
					pastMove = BattleBotArena.UP;
					return pastMove;
				}

				if(Math.abs(me.getY() - oldY) > RADIUS) {

					pastMove = BattleBotArena.DOWN;
					return pastMove;

				} else {

					dodge = 0;
					return -1;

				}

			// Dodge down
			case BattleBotArena.DOWN:

				while(Math.abs(me.getY() - oldY) <= RADIUS*2) {

					// Break if you would dodge to the edge
					if((BattleBotArena.BOTTOM_EDGE - me.getY()) <= RADIUS*2) {

						dodge = 0;
						return -1;
					}

					pastMove = BattleBotArena.DOWN;
					return pastMove;
				}

				if(Math.abs(me.getY() - oldY) > RADIUS) {

					pastMove = BattleBotArena.UP;
					return pastMove;
				
				} else {

					dodge = 0;
					return -1;

				} 

			// Dodge left
			case BattleBotArena.LEFT:

				while(Math.abs(me.getX() - oldX) <= RADIUS*2) {

					// Break if you would dodge to the edge
					if((me.getX() - BattleBotArena.LEFT_EDGE) <= RADIUS*2) {

						dodge = 0;
						return -1;
					}

					pastMove = BattleBotArena.LEFT;
					return pastMove;
				}

				if(Math.abs(me.getX() - oldX) > RADIUS) {

					pastMove = BattleBotArena.RIGHT;
					return pastMove;
				
				} else {

					dodge = 0;
					return -1;

				}

			// Dodge right
			case BattleBotArena.RIGHT:

				while(Math.abs(me.getX() - oldX) <= RADIUS*2) {

					// Break if you would dodge to the edge
					if((BattleBotArena.RIGHT_EDGE - me.getX()) <= RADIUS*2) {

						dodge = 0;
						return -1;
					}

					pastMove = BattleBotArena.RIGHT;
					return pastMove;
				}

				if(Math.abs(me.getX() - oldX) > RADIUS) {

					pastMove = BattleBotArena.LEFT;
					return pastMove;
				
				} else {

					dodge = 0;
					return -1;

				}

		}

		// Unsuccessful dodge
		return -1;

	}





	// PASSIVE METHODS




	/**
	 * Called when it is time to draw the Bot. Your Bot should be (mostly)
	 * within a circle inscribed inside a square with top left coordinates
	 * <i>(x,y)</i> and a size of <i>RADIUS * 2</i>. If you are using an image,
	 * just put <i>null</i> for the ImageObserver - the arena has some special features
	 * to make sure your images are loaded before you will use them.
	 *
	 * @param g The Graphics object to draw yourself on.
	 * @param x The x location of the top left corner of the drawing area
	 * @param y The y location of the top left corner of the drawing area
	 */
    @Override
    public void draw(Graphics g, int x, int y) {

		g.drawImage(current, x, y, Bot.RADIUS*2, Bot.RADIUS*2, null);
	}

    @Override
    public String getName() {
        
        return "Megaman";
    }

    /**
	 * This method is called at every time step to find out what team you are
	 * currently on. Of course, there can only be one winner, but you can
	 * declare and change team allegiances throughout the match if you think
	 * anybody will care. Perhaps you can send coded broadcast message or
	 * invitation to other Bots to set up a temporary team...
	 *
	 * @return The Bot's current team name
	 */
    @Override
    public String getTeamName() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
	 * This is only called after you have requested a SEND_MESSAGE move (see
	 * the documentation for <i>getMove()</i>). However if you are already over
	 * your messaging cap, this method will not be called. Messages longer than
	 * 200 characters will be truncated by the arena before being broadcast, and
	 * messages will be further truncated to fit on the message area of the screen.
	 *
	 * @return The message you want to broadcast
	 */
    @Override
    public String outgoingMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
	 * This is called whenever the referee or a Bot sends a broadcast message.
	 *
	 * @param botNum The ID of the Bot who sent the message, or <i>BattleBotArena.SYSTEM_MSG</i> if the message is from the referee.
	 * @param msg The text of the message that was broadcast.
	 */
    @Override
    public void incomingMessage(int botNum, String msg) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String[] imageNames() {

        String[] images = {"MegamanFace.png"};
		return images;
    }

    /**
	 * Once the arena has loaded your images (see <i>imageNames()</i>), it
	 * calls this method to pass you the images it has loaded for you. Store
	 * them and use them in your draw method.<br><br>
	 *
	 * PLEASE resize your images in an
	 * image manipulation program. They should be squares of size RADIUS * 2 so
	 * that they don't take up much memory.<br><br>
	 *
	 * CAREFUL: If you got the file names wrong, the image array might be null
	 * or contain null elements.
	 *
	 * @param images The array of images (or null if there was a problem)
	 */
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

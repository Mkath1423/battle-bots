package bots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

/**
 * The RandBot is a very basic Bot that moves and shoots randomly. Sometimes it
 * overheats. It trash talks when it kills someone.
 *
 * @author Sam Scott
 * @version 1.0 (March 3, 2011)
 */
public class KernerBot extends Bot {

	/**
	 * Next message to send, or null if nothing to send.
	 */
	private String nextMessage = null;
	/**
	 * An array of trash talk messages.
	 */
	private String[] killMessages = { "Woohoo!!!", "In your face!", "Pwned", "Take that.", "Gotcha!", "Too easy.",
			"Hahahahahahahahahaha :-)" };
	/**
	 * Bot image
	 */
	Image current, up, down, right, left;
	/**
	 * My name (set when getName() first called)
	 */
	private String name = null;
	/**
	 * Counter for timing moves in different directions
	 */
	private int moveCount = 0;
	/**
	 * Next move to make
	 */
	private int move = BattleBotArena.UP;
	/**
	 * Counter to pause before sending a victory message
	 */
	private int msgCounter = 0;
	/**

	/**
	 * Return image names to load
	 */
	public String[] imageNames() {
		String[] paths = { "Wizard_up.png", "Wizard_down.png", "Wizard_right.png", "Wizard_left.png" };
		return paths;
	}

	/**
	 * Store the images loaded by the arena
	 */
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

	/**
	 * Generate a random direction, then stick with it for a random count between 30
	 * and 90 moves. Randomly take a shot when done each move.
	 */
	public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
		double deadAbove = 0, deadBelow = 0, deadRight = 0, deadLeft = 0;
		for (int i = 0; i < deadBots.length; i++) {
			double xDif = deadBots[i].getX() - me.getX();//if negative left else right
			double yDif = deadBots[i].getY() - me.getX();//if negative below else above
			if (xDif < 1 && xDif > 0) {
				if (yDif > 0) deadAbove = Math.min(yDif, deadAbove);
				else deadBelow = yDif;
			}
			if (yDif < 1 && yDif > 0) {
				if (xDif > 0) deadRight = xDif;
				else deadLeft = xDif;
			}


		}
		if (shotOK) {
			if (moveCount > 2) moveCount = 0;
			moveCount +=1;
			if (moveCount == 1) return BattleBotArena.FIRERIGHT;
			if (moveCount == 2) return BattleBotArena.FIREDOWN;
			if (moveCount == 3) return BattleBotArena.FIRELEFT;
		}

		return BattleBotArena.UP;
	}

	/**
	 * Decide whether we are overheating this round or not
	 */
	public void newRound() {
        return;
	}

	/**
	 * Send the message and then blank out the message string
	 */
	public String outgoingMessage() {
		String msg = nextMessage;
		nextMessage = null;
		return msg;
	}

	/**
	 * Construct and return my name
	 */
	public String getName() {
		if (name == null)
			name = "KernerBot";
		return name;
	}

	/**
	 * Team "Arena"
	 */
	public String getTeamName() {
		return "Arena";
	}

	/**
	 * Draws the bot at x, y
	 * 
	 * @param g The Graphics object to draw on
	 * @param x Left coord
	 * @param y Top coord
	 */
	public void draw(Graphics g, int x, int y) {
		if (current != null)
			g.drawImage(current, x, y, Bot.RADIUS * 2, Bot.RADIUS * 2, null);
		else {
			g.setColor(Color.lightGray);
			g.fillOval(x, y, Bot.RADIUS * 2, Bot.RADIUS * 2);
		}
	}

	/**
	 * If the message is announcing a kill for me, schedule a trash talk message.
	 * 
	 * @param botNum ID of sender
	 * @param msg    Text of incoming message
	 */
	public void incomingMessage(int botNum, String msg) {
		if (botNum == BattleBotArena.SYSTEM_MSG && msg.matches(".*destroyed by " + getName() + ".*")) {
			int msgNum = (int) (Math.random() * killMessages.length);
			nextMessage = killMessages[msgNum];
			msgCounter = (int) (Math.random() * 30 + 30);
		}
	}

}

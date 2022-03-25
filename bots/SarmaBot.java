/*
What went well?


*/
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
public class SarmaBot extends Bot {

    /**
     * Next message to send, or null if nothing to send.
     */
    private String nextMessage = null;
    /**
     * An array of trash talk messages.
     */
    private String[] killMessages = { "Woohoo!!!", "In your face!", "Suck it", "Take that.", "Gotcha!", "Too easy.",
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
    private int moveCount = 99;
    /**
     * Next move to make
     */
    private int move = BattleBotArena.UP;
    /**
     * Counter to pause before sending a victory message
     */
    private int msgCounter = 0;
    /**
     * Used to decide if this bot should overheat or not
     */
    // private int targetNum = (int) (Math.random() * BattleBotArena.NUM_BOTS);
    /**
     * The amount to sleep to simulate overheating because of excessive CPU usage.
     */
    // private int sleep = (int) (Math.random() * 5 + 1);
    /**
     * Set to True if we are trying to overheat
     */
    // private boolean overheat = false;

    private double closestBulletX = 0;
    private double closestBulletY = 0;
    private double deadBotX = 0;
    private double deadBotY = 0;
    private double liveBotX = 0;
    private double liveBotY = 0;
    private double mDist = 0;

    /**
     * Return image names to load
     */
    public String[] imageNames() {
        String[] paths = { "roomba_up.png", "roomba_down.png", "roomba_right.png", "roomba_left.png" };
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

        //Fix the mechanics the detection of positions are'nt working 
        BotHelper helper = new BotHelper();

        liveBotX = (helper.findClosestBot(me, liveBots).getX());
        liveBotY = (helper.findClosestBot(me, liveBots).getY());

        deadBotX = (helper.findClosestBot(me, deadBots).getX());
        deadBotY = (helper.findClosestBot(me, deadBots).getY());

        double xPosition = me.getX();
        double yPosition = me.getY();

        for (int i = 0; i < bullets.length; i++) {
            Bullet bullet = bullets[i];

            double xDiffBullet = me.getX() - bullet.getX();
            double yDiffBullet = me.getY() - bullet.getY();

            if (bullets.length > 0) {
                closestBulletX = (helper.findClosestBullet(me, bullets)).getX();
                closestBulletY = (helper.findClosestBullet(me, bullets)).getY();
                mDist = (BotHelper.manhattanDist(closestBulletX, closestBulletY, xPosition + Bot.RADIUS,
                        yPosition + Bot.RADIUS));
            }

            if (mDist < 30) {
                if ((xPosition + Bot.RADIUS) == closestBulletX) {
                    if (yDiffBullet > 0 && bullet.getYSpeed() > 0 || yDiffBullet < 0 && bullet.getYSpeed() < 0) {
                        return BattleBotArena.RIGHT;
                    }
                }

                else if ((yPosition + Bot.RADIUS) == closestBulletY) {
                    if (xDiffBullet > 0 && bullet.getXSpeed() > 0 || xDiffBullet < 0 && bullet.getXSpeed() < 0) {
                        return BattleBotArena.UP;
                    }
                }
            }
        }

        if (Math.abs(xPosition + Bot.RADIUS) == Math.abs(BattleBotArena.RIGHT_EDGE)) {
            return BattleBotArena.LEFT;
        } else if (Math.abs(xPosition + Bot.RADIUS) == Math.abs(BattleBotArena.LEFT_EDGE)) {
            return BattleBotArena.RIGHT;
        }
        if (Math.abs(yPosition + Bot.RADIUS) == Math.abs(BattleBotArena.TOP_EDGE)) {
            return BattleBotArena.DOWN;
        } else if (Math.abs(xPosition + Bot.RADIUS) == Math.abs(BattleBotArena.BOTTOM_EDGE)) {
            return BattleBotArena.UP;
        }

         if (((xPosition + Bot.RADIUS) == liveBotX + Bot.RADIUS) && liveBotY < yPosition) {
             return BattleBotArena.FIREDOWN;
         } else if (((xPosition + Bot.RADIUS) == liveBotX + Bot.RADIUS) && liveBotY > yPosition) {
             return BattleBotArena.FIREUP;
         } else if (((yPosition + Bot.RADIUS) == liveBotY + Bot.RADIUS) && liveBotX > xPosition) {
             return BattleBotArena.FIRERIGHT;
         } else if (((yPosition + Bot.RADIUS) == liveBotY + Bot.RADIUS) && liveBotX < xPosition) {
             return BattleBotArena.FIRELEFT;
         }

        if ((xPosition + Bot.RADIUS) == deadBotX && yPosition > deadBotY) {
            return BattleBotArena.DOWN;
        } else if ((xPosition + Bot.RADIUS) == deadBotX && yPosition< deadBotY) {
            return BattleBotArena.UP;
        } else if ((yPosition + Bot.RADIUS) == deadBotY && xPosition > deadBotX) {
            return BattleBotArena.LEFT;
        } else if ((yPosition + Bot.RADIUS) == deadBotY && xPosition < deadBotX) {
            return BattleBotArena.RIGHT;
        }

        // increase the move counter
        moveCount++;

        // Is it time to send a message?

        if (--msgCounter == 0) {
            move = BattleBotArena.SEND_MESSAGE;
            moveCount = 99;
        }
        // Time to choose a new move?

         else if (moveCount >= 30 + (int) Math.random() * 60) {
             moveCount = 0;
             int choice = (int) (Math.random() * 4);
             if (choice == 0) {
                 move = BattleBotArena.UP;
                 current = up;
             } else if (choice == 1) {
                 move = BattleBotArena.DOWN;
                 current = down;
             } else if (choice == 2) {
                 move = BattleBotArena.LEFT;
                 current = left;
             } else if (choice == 3) {
                 move = BattleBotArena.RIGHT;
                 current = right;
             } 
         }
        return move;

    }

    /**
     * Decide whether we are overheating this round or not
     */
    public void newRound() {

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
            name = "SarmaBot";
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
            g.setColor(Color.blue);
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
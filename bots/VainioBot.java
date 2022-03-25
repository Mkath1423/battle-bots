package bots;

import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class VainioBot extends Bot {
    
BotHelper helper = new BotHelper();
Image current, up, down, right, left;

private int moveCount = 99;
private int move = BattleBotArena.UP;

    @Override
    public void newRound() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
         if (moveCount >= 30 + (int) Math.random() * 60) {
			moveCount = 0;
			int choice = (int) (Math.random() * 8);
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
			} else if (choice == 4) {
				move = BattleBotArena.FIREUP;
				moveCount = 99; // make sure we choose a new move next time
				current = up;
			} else if (choice == 5) {
				move = BattleBotArena.FIREDOWN;
				moveCount = 99; // make sure we choose a new move next time
				current = down;
			} else if (choice == 6) {
				move = BattleBotArena.FIRELEFT;
				moveCount = 99; // make sure we choose a new move next time
				current = left;
			} else if (choice == 7) {
				move = BattleBotArena.FIRERIGHT;
				moveCount = 99; // make sure we choose a new move next time
				current = right;
			}
		}
		return move;
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        g.drawImage(current, x, y, Bot.RADIUS * 2, Bot.RADIUS * 2, null);
        
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "Vainio";
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
    String[] paths = { "UFO.png" };
		return paths;
    }

    @Override
    public void loadedImages(Image[] images) {
        // TODO Auto-generated method stub
        current = images[0];
        
    }
    
}

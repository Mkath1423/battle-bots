package bots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class KapurBot extends Bot{
    
    Image current, up, down, left, right;

    String botName = "Nauhar";

    private int move = BattleBotArena.UP;
   
    //Create a BotHelper for easy calculations
    BotHelper helper = new BotHelper();

    @Override
    public String[] imageNames() {
        String[] imageNameArray = { "pikachu_up.png", "pikachu_down.png", "pikachu_left.png", "pikachu_right.png" };
        return imageNameArray;
    }

    @Override
    public void loadedImages(Image[] images) {
        if (images != null) {
            for (int i=0; i<images.length; i++) {
                if (i==0) {
                    up = images[i];
                }
                if (i==1) {
                    down = images[i];
                }
                if (i==2) {
                    left = images[i];
                }
                if (i==3) {
                    right = images[i];
                }
            }
            current = up;
        }

    }

    @Override
    public void newRound() {
        current = up;
        move = BattleBotArena.UP;

        
        
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        // TODO Auto-generated method stub
        //Use the information that is passed in about other bots (their locations, etc) and bullets to decide what move to make
        //Eg. if a bullet is very close and moving towards this bot, move the bot out of the way
        //Eg. if another bot is close to this bot, get its position relative to this bot and shoot a bullet in the direction of the enemy bot
        BotInfo closestBot = helper.findClosestBot(me, liveBots);
        BotInfo closestTomb = helper.findClosestBot(me, deadBots);
        Bullet closestBullet = helper.findClosestBullet(me, bullets);
        if (closestBot.getX()-me.getX() >-100 && closestBot.getX() - me.getX() < 0 && Math.abs(closestBot.getY() - me.getY()) < RADIUS*2 ){
            current = left;
            move = BattleBotArena.FIRELEFT;
        }
        if (closestBot.getX()-me.getX()<100 && closestBot.getX()-me.getX()>0 && Math.abs(closestBot.getY() - me.getY()) < RADIUS*2 ) {
            current = right;
            move = BattleBotArena.FIRERIGHT;
        }
        if (closestBot.getY()-me.getY()>-100 && closestBot.getY()-me.getY()<0 && Math.abs(closestBot.getX() - me.getX()) < RADIUS*2) {
            current = up;
            move = BattleBotArena.FIREUP;
        }
        if (closestBot.getY()-me.getY()<100 && closestBot.getY()-me.getY()>0 && Math.abs(closestBot.getX() - me.getX()) < RADIUS * 2) {
            current = down;
            move = BattleBotArena.FIREDOWN;
        }
        //if bullet comes from the side
        if (closestBullet.getY()-me.getY() < RADIUS*2 && closestBullet.getY()-me.getY() >=RADIUS && Math.abs(closestBullet.getX()-me.getX())<=60  && closestBullet.getYSpeed() == 0) {
            current = up;
            move = BattleBotArena.UP;
        }
        if (closestBullet.getY() - me.getY() < RADIUS && closestBullet.getY() - me.getY() >= 0 && Math.abs(closestBullet.getX() - me.getX()) <= 60 && closestBullet.getYSpeed() == 0) {
            current = down;
            move = BattleBotArena.DOWN;
        }
        //if bullet comes from above or below
        if (closestBullet.getX()-me.getX()<RADIUS*2 && closestBullet.getX()-me.getX() >=RADIUS && Math.abs(closestBullet.getY()-me.getY())<=60 && closestBullet.getXSpeed() == 0) {
            current = left;
            move = BattleBotArena.LEFT;
        }
        if (closestBullet.getX()-me.getX() < RADIUS && closestBullet.getX()-me.getX()>=0 && Math.abs(closestBullet.getY()-me.getY()) <=60 && closestBullet.getXSpeed() == 0){
            current = right;
            move = BattleBotArena.RIGHT;
        }
        //If robot hits tombstone from the right
        if (me.getX()-closestTomb.getX() == RADIUS*2 && Math.abs(me.getY()-closestTomb.getY()) <= RADIUS*2) {
            current = right;
            move = BattleBotArena.RIGHT;
        }
        //If robot hits tombstone from the left
        if (closestTomb.getX()-me.getX() == RADIUS*2 && Math.abs(me.getY()-closestTomb.getY())<=RADIUS*2) {
            current = left;
            move = BattleBotArena.LEFT;
        }
        //If robot hits tombstone from above
        if (closestTomb.getY()-me.getY() == RADIUS*2 && Math.abs(closestTomb.getX()-me.getX())<=RADIUS*2) {
            current = up;
            move = BattleBotArena.UP;
        }
        //If robot hits tombstone from below
        if (me.getY()-closestTomb.getY()==RADIUS*2 && Math.abs(closestTomb.getX()-me.getX())<=RADIUS*2) {
            current = down;
            move = BattleBotArena.DOWN;
        }
        //If robot hits the top of the screen
        if (me.getY() == BattleBotArena.TOP_EDGE && closestTomb.getY()-me.getY()!= RADIUS*2) {
            current=  down;
            move = BattleBotArena.DOWN;
            
        }
        //If robot hits bottom of the screen
        if (me.getY() == BattleBotArena.BOTTOM_EDGE && me.getY()-closestTomb.getY()!=RADIUS*2) {
            current = up;
            move = BattleBotArena.UP;
        }
        //If robot hits the left edge
        if (me.getX() == BattleBotArena.LEFT_EDGE && closestTomb.getX()-me.getX()!=RADIUS*2) {
            current = right;
            move = BattleBotArena.RIGHT;
        }
        //If robot hits right edge
        if (BattleBotArena.RIGHT_EDGE-me.getX() == RADIUS*2 && me.getX()-closestTomb.getX() !=RADIUS*2) {
            current = left;
            move = BattleBotArena.LEFT;
        }
        
        return move;
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        if (current!=null) {
            g.drawImage(current, x, y, Bot.RADIUS*2, Bot.RADIUS*2, null);
        } else {
            g.setColor(Color.blue);
            g.fillOval(x, y, Bot.RADIUS*2, Bot.RADIUS*2);
        }
        
    }

    @Override
    public String getName() {
        return botName;
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

    
}

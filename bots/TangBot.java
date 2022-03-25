package bots;

import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class TangBot extends Bot {

    BotHelper helper = new BotHelper();
    boolean debug = false;
    Image current, up, down, left, right;

    @Override
    public void newRound() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        
        if(bullets.length > 0){

            Bullet blt = helper.findClosestBullet(me, bullets);
            double bltX = blt.getX();
            double bltY = blt.getY();
       
            if((Math.abs(me.getX() - bltX) < 45) && (bltY >= me.getY()) && (bltY <= me.getY() + 26)){
                
                if(me.getY() + 13 < bltY){
                    if(debug)System.out.println("DODGE UP");
                    current = up;
                    return BattleBotArena.UP;
                }
                else if (me.getY() + 13 >=  bltY){
                    if(debug)System.out.println("DODGE DOWN");
                    current = down;
                    return BattleBotArena.DOWN;
                }
            }

            else if((Math.abs(me.getY() - bltY) < 45) && (bltX >= me.getX()) && (bltX <= me.getX() + 26)){
                if(me.getX() + 13 < bltX){
                    if(debug)System.out.println("DODGE LEFT");
                    current = left;
                    return BattleBotArena.LEFT;
                }
                else if (me.getX() + 13 >=  bltX ){
                    if(debug)System.out.println("DODGE RIGHT");
                    current = right;
                    return BattleBotArena.RIGHT;
                }   
            }
        }

        //SHOOTING

        double botX = helper.findClosestBot(me,liveBots).getX();
        double botY = helper.findClosestBot(me,liveBots).getY();

        if(botX <= me.getX() && me.getX() <= botX + 5){

            if(botY > me.getY()){
                if(debug)System.out.println("FIRE DOWN");
                return BattleBotArena.FIREDOWN;
            }
            else if(botY < me.getY()){
                if(debug)System.out.println("FIRE UP");
                return BattleBotArena.FIREUP;
            }
        }

        else if(botY <= me.getY() && me.getY() <= botY+26){

            if(botX > me.getX()){
                if(debug)System.out.println("FIRE RIGHT");
                return BattleBotArena.FIRERIGHT;
            }
            else if(botX < me.getX()){
                if(debug)System.out.println("FIRE LEFT");
                return BattleBotArena.FIRELEFT;
            }
        }

        //MOVEMENT TRACKING

        if(Math.abs(me.getX() - botX) < Math.abs(me.getY() - botY)){

            if( (botX > me.getX()) && ( (botY > me.getY() + 26) || (botY + 26 < me.getY()) ) ){
                if(debug)System.out.println("RIGHT");
                current = right;
                return BattleBotArena.RIGHT;
            } 

            else if( (botX < me.getX() ) && ( (botY > me.getY() + 26) || (botY + 26 < me.getY()) ) ){
                if(debug)System.out.println("LEFT");
                current = left;
                return BattleBotArena.LEFT;
            } 
        }

        else if(Math.abs(me.getX() - botX) > Math.abs(me.getY() - botY)) {

            if( ( (botX + 26 < me.getX()) || (botX > me.getX() + 26) ) && (botY  < me.getY() ) ){
                if(debug)System.out.println("UP");
                current = up;
                return BattleBotArena.UP;
            }

            else if( ( (botX + 26 < me.getX()) || (botX > me.getX() + 26) ) && (botY  > me.getY() ) ){
                if(debug)System.out.println("DOWN");
                current = down;
                return BattleBotArena.DOWN;
            }
        }
        

        return BattleBotArena.FIREUP;
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        g.drawImage(current, x, y, Bot.RADIUS*2, Bot.RADIUS*2, null);
        
    }

    String name;
    @Override
    public String getName() {
        if (name == null)
			name = "ATANG";
		return name;
    }

    @Override
    public String getTeamName() {
        return "IMM?";
    }

    @Override
    public String outgoingMessage() {
        return "DIFFERENT";
    }

    @Override
    public void incomingMessage(int botNum, String msg) {
        // TODO Auto-generated method stub
    }

    @Override
    public String[] imageNames() {
        String[] images = {"gingerbread_up.png","gingerbread_down.png","gingerbread_left.png","gingerbread_right.png"};
        return images;
    }

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

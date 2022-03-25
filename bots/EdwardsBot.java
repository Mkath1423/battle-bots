package bots;

import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class EdwardsBot extends Bot {

    Image current, up, down, right, left;
    int count = 0;
    boolean dodging = false;
    private String name;
    boolean debug = false;
    BotHelper helper = new BotHelper();
    private double closeBotY = 0;
    private double closeBotX = 0;
    int myBullets = BattleBotArena.NUM_BULLETS;
    double closeBulletSpeedX = 0;
    double closeBulletSpeedY = 0;
    double closeBulletX = 0;
    double closeBulletY = 0;
    double bulletDistanceX = 0;
    double bulletDistanceY = 0;

    @Override

    public void newRound() {
        // TODO Auto-generated method stub
         count =0;
        
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        // TODO Auto-generated method stub
        closeBotY = ((helper.findClosestBot(me,liveBots)).getY());
        closeBotX = ((helper.findClosestBot(me,liveBots)).getX());
        count ++;

        double xPos = me.getX();
        double yPos = me.getY();

        double diffX = Math.abs(closeBotX - xPos);
        double diffY = Math.abs(closeBotY - yPos);


        if (bullets.length > 0){
            
            closeBulletSpeedX = ((helper.findClosestBullet(me,bullets)).getXSpeed());
            closeBulletSpeedY = ((helper.findClosestBullet(me,bullets)).getYSpeed());
            closeBulletX = (helper.findClosestBullet(me,bullets)).getX();
            closeBulletY = (helper.findClosestBullet(me,bullets)).getY();
            bulletDistanceX = Math.abs(helper.calcDisplacement(xPos,closeBulletX));
            bulletDistanceY = Math.abs(helper.calcDisplacement(yPos,closeBulletY));
    }

          

    //Dodging code
        if ((bulletDistanceX < 200) && (bulletDistanceY < 200)){
            if ((closeBulletY >= (yPos -40)) && (closeBulletY <= (yPos+40)) && (closeBulletX < xPos) && (closeBulletSpeedX>0)){
                dodging = true;
                if (debug)System.out.println("In Range");
                if ((closeBulletY > yPos) || (yPos >=650))  {
                    while(dodging){
                        if (debug)System.out.println("UP");
    
                            return BattleBotArena.UP;
                    }
                }

                if ((closeBulletY <= yPos) || (yPos <= 50)){
                    while(dodging){
                        if (debug)System.out.println("DOWN");
                            return BattleBotArena.DOWN;
                    }
                }
            }
            
            
            
            else if ((closeBulletY >= yPos -40) && (closeBulletY <= yPos+40) && (closeBulletX > xPos) && (closeBulletSpeedX<0)){
                dodging = true;
                if (debug)System.out.println("In Range");
                if ((closeBulletY >= yPos) || (yPos >=650)) {
                    while(dodging){
                    if (debug)System.out.println("UP");
                   
                    return BattleBotArena.UP;
                    }
                }

                if ((closeBulletY < yPos) || (yPos <= 50)){
                    while(dodging){
                    if (debug)System.out.println("DOWN");
                    
                    return BattleBotArena.DOWN;
                    }
                }

            }
           
            
            if ((closeBulletX >= xPos -40) && (closeBulletX <= xPos+40) && (closeBulletY < yPos) && (closeBulletSpeedY>0)){
                dodging = true;
                if (debug)System.out.println("In Range");
                if ((closeBulletX > xPos) || (xPos >= 950)){
                    while(dodging){
                    if (debug)System.out.println("left");
                   
                    return BattleBotArena.LEFT;
                    }
                }

                if ((closeBulletX <= xPos) || (xPos <= 50)){
                    while(dodging){
                    if (debug)System.out.println("RIGHT");
                    
                    return BattleBotArena.RIGHT;
                    }
                }

        }
            else if ((closeBulletX >= xPos -40) && (closeBulletX <= xPos+40) && (closeBulletY > yPos) && (closeBulletSpeedY<0)){
                dodging = true;
                if (debug)System.out.println("In Range");
                if ((closeBulletX >= xPos) || (xPos >= 950)){
                    while(dodging){
                    if (debug)System.out.println("left");
                   
                    return BattleBotArena.LEFT;
                    }
            }

                if ((closeBulletX < xPos) || (xPos <= 50)){
                    while(dodging){
                    if (debug)System.out.println("RIGHT");
                   
                    return BattleBotArena.RIGHT;
                    }
            }
        }
            
    }
        


    //Shoot at the nearest bot based on direction
    //else{
        if (((xPos + 20) > closeBotX) && ((xPos + 20) < (closeBotX + 30))) {
            if ((closeBotY > yPos) && (me.getLastMove()!=BattleBotArena.FIREDOWN)&& (count%5 == 0)) {
               
                return BattleBotArena.FIREDOWN;
                
            }   
            else if((closeBotY < yPos) && (me.getLastMove()!=BattleBotArena.FIREUP) && (count%5 == 0)){
                return BattleBotArena.FIREUP;
            }
        
        }
        
        else if (((yPos + 20) > closeBotY && ((yPos + 20) < (closeBotY + 30)))) {
            if ((closeBotX > xPos) && (me.getLastMove()!=BattleBotArena.FIRERIGHT) && (count%5 == 0)){
                return BattleBotArena.FIRERIGHT;
            }
            else if ((closeBotX < xPos) && (me.getLastMove()!=BattleBotArena.FIRELEFT) && (count%5 == 0)) {
                return BattleBotArena.FIRELEFT;
            }
            
        }
        //follow the nearest bot
        if (diffX < diffY){
            if (closeBotX > xPos){
            // if (debug)System.out.println("TRACKRIGHT");
                return BattleBotArena.RIGHT;
            }
            if (closeBotX < xPos){
                //if (debug)System.out.println("TRACKleft");
                return BattleBotArena.LEFT;
            }
    }

        else if (diffY < diffX){
            if (closeBotY > yPos){
                //if (debug)System.out.println("TRACKDOWN");
                return BattleBotArena.DOWN;
            }
            if (closeBotY < yPos){
                //if (debug)System.out.println("TRACKUP");
                return BattleBotArena.UP;
            }
    }


    

  
    return BattleBotArena.STAY;
    }
    

    @Override
    public void draw(Graphics g, int x, int y) {
        g.drawImage(current, x, y, Bot.RADIUS * 2, Bot.RADIUS * 2, null);
        
    }

    @Override
    public String getName() {
        name = "BestBot";
        return name;
    }

    @Override
    public String getTeamName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String outgoingMessage() {
        // TODO Auto-generated method stub
        return "Ur Tossing";
    }

    @Override
    public void incomingMessage(int botNum, String msg) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String[] imageNames() {
        String[] photos = { "pikachu_up.png", "pikachu_down.png", "pikachu_right.png", "pikachu_left.png" };
		return photos;
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
package bots;

import java.awt.Graphics;
import java.awt.Image;
import java.util.Arrays;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class WilliamBot extends Bot {

    BotHelper helper = new BotHelper();

    @Override
    public void newRound() {
        // TODO Auto-generated method stub
        
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {

        //模式
        boolean modeRun = false; //run away from a bullet
        boolean modeShoot = false; //shoot a bot while they are close
        boolean modeFollow = false; //follow a bot when there is a few bot survive

        //find closest bullet and bot
        Bullet closeBullet = helper.findClosestBullet(me, bullets);
        BotInfo closeBot = helper.findClosestBot(me, liveBots);

        //decide what to do
        //run first, shoot second
        while(true){
            //runs away from a close bullet
            if(helper.calcDistance(me.getX(), me.getY(), closeBullet.getX(), closeBullet.getY()) <= 10){
                //then this menas the bullet is too close
                modeRun = true;
                break; //run away this round
            }
            //try to shoot bot near by
            if(helper.calcDistance(me.getX(), me.getY(), closeBot.getX(), closeBot.getY()) <=5 ){
                //then that bot is close enoght to shoot
                modeShoot = true;
                break;
            }
            //if there is no bullet and bot close to my bot, try to find a target and follow it.
            else{
                modeFollow = true;
                break;
            }
        }

        //call method base on the situation
        if(modeRun){
            return(run(me, closeBullet));
        }
        if(modeShoot){
            return(shoot(me, closeBot));
        }
        if(modeFollow){
            System.out.println("Mode Follow move down"); //debug
            return BattleBotArena.DOWN; //debug
            //return(follow(me, closeBot));
        }
        return BattleBotArena.STAY;
    }

    //run away from specific buhllet
    public int run(BotInfo me, Bullet bullet){
        double distancesGoingUP;
        double distancesGoingRIGHT;
        double distancesGoingLEFT;
        double distancesGoingDOWN;

        distancesGoingUP = helper.calcDistance(me.getX(), me.getY()-1, bullet.getX(), bullet.getY());
        distancesGoingRIGHT = helper.calcDistance(me.getX()+1, me.getY(), bullet.getX(), bullet.getY());
        distancesGoingLEFT = helper.calcDistance(me.getX()-1, me.getY(), bullet.getX(), bullet.getY());
        distancesGoingDOWN = helper.calcDistance(me.getX(), me.getY()+1, bullet.getX(), bullet.getY());

        double[] distanceArray = new double[4];
        distanceArray[0] = distancesGoingUP;
        distanceArray[1] = distancesGoingRIGHT;
        distanceArray[2] = distancesGoingLEFT;
        distanceArray[3] = distancesGoingDOWN;
        Arrays.sort(distanceArray);

        if(distanceArray[3]==distancesGoingUP){
            return BattleBotArena.UP;
        }
        if(distanceArray[3]==distancesGoingRIGHT){
            return BattleBotArena.RIGHT;
        }
        if(distanceArray[3]==distancesGoingLEFT){
            return BattleBotArena.LEFT;
        }
        if(distanceArray[3]==distancesGoingDOWN){
            return BattleBotArena.DOWN;
        }
        else{
            System.out.println("error on escape from bullet");
            return BattleBotArena.DOWN;
        }
    }

    //shoot a specific bot
    public int shoot(BotInfo me, BotInfo bot){
        double x_displacement = helper.calcDisplacement(me.getX(), bot.getY());
        double y_displacement = helper.calcDisplacement(me.getY(), bot.getY());

        if(x_displacement >= -2 && x_displacement <= 2){ //说明目标在正上方或者正下方
            if(y_displacement >= 0){ //说明目标在正下方
                return(BattleBotArena.FIREDOWN);
            }
            if(y_displacement <= 0){//说明目标在正上方
                return(BattleBotArena.FIREUP);
            }
        }
        if(y_displacement >= -2 && y_displacement <=2){ //说明目标在正左边或者正右边
            if(x_displacement >= 0){ //说明目标在右面
                return(BattleBotArena.FIRERIGHT);
            }
            if(y_displacement <= 0){//说明目标在左面
                return(BattleBotArena.FIRELEFT);
            }
        }
        return(BattleBotArena.FIREUP); //debug
        //return(follow(me, bot));//如果不在正对的方向，说明目标在斜对面，无法直接射击，选择跟踪
    }

    //try to follow a bot
    public int follow(BotInfo me, BotInfo bot){
        double distancesGoingUP;
        double distancesGoingRIGHT;
        double distancesGoingLEFT;
        double distancesGoingDOWN;

        distancesGoingUP = helper.calcDistance(me.getX(), me.getY()-1, bot.getX(), bot.getY());
        distancesGoingRIGHT = helper.calcDistance(me.getX()+1, me.getY(), bot.getX(), bot.getY());
        distancesGoingLEFT = helper.calcDistance(me.getX()-1, me.getY(), bot.getX(), bot.getY());
        distancesGoingDOWN = helper.calcDistance(me.getX(), me.getY()+1, bot.getX(), bot.getY());

        double[] distanceArray = new double[4];
        distanceArray[0] = distancesGoingUP;
        distanceArray[1] = distancesGoingRIGHT;
        distanceArray[2] = distancesGoingLEFT;
        distanceArray[3] = distancesGoingDOWN;
        Arrays.sort(distanceArray);

        if(distanceArray[0]==distancesGoingUP){
            return BattleBotArena.UP;
        }
        if(distanceArray[0]==distancesGoingRIGHT){
            return BattleBotArena.RIGHT;
        }
        if(distanceArray[0]==distancesGoingLEFT){
            return BattleBotArena.LEFT;
        }
        if(distanceArray[0]==distancesGoingDOWN){
            return BattleBotArena.DOWN;
        }
        else{
            System.out.println("error on choosing closest way");
            return BattleBotArena.STAY;
        }

    }


//-------------------------------------------------------------
//-------------------------------------------------------------

    @Override
    public void draw(Graphics g, int x, int y) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
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

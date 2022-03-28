package bots;

import arena.BattleBotArena;
import arena.BotInfo;

public class BotTracker {
    BotHelper helper = new BotHelper();

    public String botName = "";
    public String botTeamName = "";

    public int cycleNumber = 1;

    public int lastMove = -1;

    public double startX = 0;
    public double startY = 0;

    public double currentX = 0;
    public double currentY = 0;

    public double lastX = 0;
    public double lastY = 0;

    public int bulletsDodged = 0;
    
    public int timesShot = 0;
    public int timesMoved = 0;

    public boolean isOverheated = false;

    public boolean isDead = false;

    public boolean isInitialized = false;

    public double SortMetric(BotInfo me, 
                           double w_proxy, 
                           double w_shooting, 
                           double w_stationary, 
                           double w_overheated,
                           double w_dodged){
        if(isDead) return 0d;
        return w_proxy * 1000/BotHelper.manhattanDist(currentX, currentY, me.getX(), me.getY()) + 
               w_shooting * -timesShot / cycleNumber + 
               w_stationary * -timesMoved / cycleNumber +
               w_overheated * (isOverheated ? 1 : 0) + 
               w_dodged * -(bulletsDodged - 5);
    }

    public double getXSpeed(){
        return currentX - lastX;
    }

    public double getYSpeed(){
        return currentY - lastY;
    }

    public Vector2 getSpeed(){
        return new Vector2(getXSpeed(), getYSpeed());
    }


    public BotTracker(){
        
    }

    public void init(BotInfo info){
        startX = info.getX();
        startY = info.getY();

        lastMove = info.getLastMove();
        botName = info.getName();
        botTeamName = info.getTeamName();
    }

    public void reset(){
        
        cycleNumber = 1;

        lastMove = -1;

        startX = 0;
        startY = 0;

        currentX = 0;
        currentY = 0;

        bulletsDodged = 0;
    
        timesShot = 0;
        timesMoved = 0;

        isOverheated = false;

        isDead = false;

        isInitialized = false;

    }


    public void updateTracker(BotInfo info){
        if(!isInitialized){
            init(info);
        }

        cycleNumber ++;

        if(info.isOverheated()){
            isOverheated = true;
            timesMoved = 0;
            timesShot = 0;
        }
        isDead = info.isDead();

        lastMove = info.getLastMove();

        lastX = currentX;
        lastY = currentY;

        currentX = info.getX();
        currentY = info.getY();

        switch(info.getLastMove()){
            case BattleBotArena.FIREDOWN:
                timesShot ++;
                break;

            case BattleBotArena.FIREUP:
                timesShot ++;
                break;

            case BattleBotArena.FIRELEFT:
                timesShot ++;
                break;

            case BattleBotArena.FIRERIGHT:
                timesShot ++;
                break;

            case BattleBotArena.UP:
                timesMoved ++;
                break;

            case BattleBotArena.LEFT:
                timesMoved ++;
                break;

            case BattleBotArena.DOWN:
                timesMoved ++;
                break;

            case BattleBotArena.RIGHT:
                timesMoved ++;
                break; 
        }
    }


}

package bots;

import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;


public class FahadBot extends Bot {

    BotHelper helper = new BotHelper();

    @Override
    public void newRound() {
        // TODO Auto-generated method stub
        
    

    //method to get my bots location
    //int x = BotInfo.FahadBot.getX();
    //int y = FahadBot.FahadBot.getY();

    //array to store moves
    //array to store moves(move[1,2,3,4])
    int [] move = new int[4];
    move [0]=  BattleBotArena.UP;
    move [1]=  BattleBotArena.DOWN;
    move [2]= BattleBotArena.LEFT;
    move [3]=BattleBotArena.RIGHT; 

    //array to store firing drections
    int fire[];
    fire = new int [4];
    fire [1]=  BattleBotArena.FIREUP;
    fire [2]=  BattleBotArena.FIREDOWN;
    fire [3]= BattleBotArena.FIRELEFT;
    fire [4]=BattleBotArena.FIRERIGHT;

    }
 




    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        // TODO Auto-generated method stub
        //adding a comment
        // //helper.findClosestBot(_me, _bots)
        // //double myX = FahadBot.getRadius();
        // for (int i = 0; i< bullets[2].length(); i++){
        //     int currnetBulletX = bullets[2][i].getX();
        //     if (currentBulletX = myX -  5)
                return BattleBotArena.UP;
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "nuha";
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
        String[] images = {"roomba_up.png","roomba_down.png","roomba_left.png","roomba_right.png"};
		return images;
    }

    @Override
    public void loadedImages(Image[] images) {
        // TODO Auto-generated method stub
        
    }
    
}

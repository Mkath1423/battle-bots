package bots;

import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class Jacky extends Bot{
    BotHelper botHelper = new BotHelper();

    Image up, down, left, right, current;

    int shootCounter; 
    int shootCountdown = 7;

    int diagonalMove = 0;

    @Override
    public void newRound() {
        shootCounter = 0;
    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        double myX = me.getX();
        double myY = me.getY();

        int hitboxRadius = Bot.RADIUS;
        int move = BattleBotArena.STAY;

        shootCounter--;

        double centreX = myX + hitboxRadius;
        double centreY = myY + hitboxRadius;

        int borderRadius = 50;

        //switch every time
        if (diagonalMove == 0) diagonalMove++;
        else if (diagonalMove == 1) diagonalMove = 0;
        
        // stay away from edges
        if (myX >= BattleBotArena.RIGHT_EDGE-borderRadius) {
            current = left;
            move = BattleBotArena.LEFT;
        }
        else if (myX <= BattleBotArena.LEFT_EDGE+borderRadius) {
            current = right;
            move = BattleBotArena.RIGHT;
        }
        else if (myY >= BattleBotArena.BOTTOM_EDGE-borderRadius) {
            current = up;
            move = BattleBotArena.UP;
        }
        else if (myY <= BattleBotArena.TOP_EDGE+borderRadius) {
            current = down;
            move = BattleBotArena.DOWN;
        }

        BotInfo closestPerson = botHelper.findClosestBot(me, liveBots);

        double personX = closestPerson.getX();
        double personY = closestPerson.getY();
        double personDistance = botHelper.calcDistance(myX, myY, personX, personY);

        double displacementX = Math.abs(myX - personX);
        double displacementY = Math.abs(myY - personY);

        int shootWindow = 5;

        // look for players
        if (liveBots.length != 0) {
            if (personDistance > 100) { // 100 away from me
                if (displacementX < displacementY) {
                    if (myY > personY) {
                        current = up;
                        move = BattleBotArena.UP;
                    }
                    else if (myY < personY) {
                        current = down;
                        move = BattleBotArena.DOWN;
                    }
                }
                else if (displacementX > displacementY) {
                    if (myX > personX) {
                        current = left;
                        move = BattleBotArena.LEFT;
                    }
                    else if (myX < personX) {
                        current = right;
                        move = BattleBotArena.RIGHT;
                    }
                }
            }

            // else if (personDistance < 100) { // line myself up
            //     if (displacementX <= displacementY) { // line horizontally if further vertically
            //         if (myX < personX) {
            //             current = right;
            //             move = BattleBotArena.RIGHT;
            //         }
            //         else if (myX > personX) {
            //             current = left;
            //             move = BattleBotArena.LEFT;
            //         }
            //     }
            //     else if (displacementY <= displacementX) {
            //         if (myY < personY) {
            //             current = down;
            //             move = BattleBotArena.DOWN;
            //         }
            //         else if (myY > personY) {
            //             current = up;
            //             move = BattleBotArena.UP;
            //         }
            //     }
            // }

            else if ((50 < personDistance) && (personDistance < 75)){ 
                if (shotOK && (shootCounter <= 0)) {
                    if ((myX-shootWindow < personX) && (personX < myX + (hitboxRadius*2) + shootWindow) && (myY > personY)) { // fire up
                        current = up;
                        move = BattleBotArena.FIREUP;
                    }
                    else if ((myX-shootWindow < personX) && (personX < myX + (hitboxRadius*2) + shootWindow) && (myY < personY)) { // fire down
                        current = down;
                        move = BattleBotArena.FIREDOWN;
                    }
                    else if ((myY-shootWindow < personY) && (personY < myY + (hitboxRadius*2) + shootWindow) && (myX < personX)) { // fire right
                        current = right;
                        move = BattleBotArena.FIRERIGHT;
                    }
                    else if ((myY-shootWindow < personY) && (personY < myY + (hitboxRadius*2) + shootWindow) && (myX > personX)) { // fire left
                        current = left;
                        move = BattleBotArena.FIRELEFT;
                    }
                }
            }

            else if (personDistance < 50) { // within 50 radius, back up
                if ((personX <= centreX) && (personY <= centreY)) { // top left quadrant
                    if (diagonalMove == 0) {
                        current = right;
                        move = BattleBotArena.RIGHT;
                    }
                    else if (diagonalMove == 1) {
                        current = down;
                        move = BattleBotArena.DOWN;
                    }
                }
                else if ((personX >= centreX) && (personY <= centreY)) { // top right quadrant
                    if (diagonalMove == 0) {
                        current = left;
                        move = BattleBotArena.LEFT;
                    }
                    else if (diagonalMove == 1) {
                        current = down;
                        move = BattleBotArena.DOWN;
                    }
                }
                else if ((personX <= centreX) && (personY >= centreY)) { // bottom left quadrant
                    if (diagonalMove == 0) {
                        current = right;
                        move = BattleBotArena.RIGHT;
                    }
                    else if (diagonalMove == 1) {
                        current = up;
                        move = BattleBotArena.UP;
                    }
                }
                else if ((personX >= centreX) && (personY >= centreY)) { // bottom right quadrant
                    if (diagonalMove == 0) {
                        current = left;
                        move = BattleBotArena.LEFT;
                    }
                    else if (diagonalMove == 1) {
                        current = up;
                        move = BattleBotArena.UP;
                    }
                }
            }
        }

        // move away from bodies
        if (deadBots.length != 0) {
            BotInfo closestBody = botHelper.findClosestBot(me, deadBots);
            
            double bodyX = closestBody.getX();
            double bodyY = closestBody.getY();
            double bodyDistance = botHelper.calcDistance(myX, myY, bodyX, bodyY);

            if (bodyDistance < 50) {
                if ((bodyX <= centreX) && (bodyY <= centreY)) { // top left quadrant
                    if (diagonalMove == 0) {
                        current = right;
                        move = BattleBotArena.RIGHT;
                    }
                    else if (diagonalMove == 1) {
                        current = down;
                        move = BattleBotArena.DOWN;
                    }
                }
                else if ((bodyX >= centreX) && (bodyY <= centreY)) { // top right quadrant
                    if (diagonalMove == 0) {
                        current = left;
                        move = BattleBotArena.LEFT;
                    }
                    else if (diagonalMove == 1) {
                        current = down;
                        move = BattleBotArena.DOWN;
                    }
                }
                else if ((bodyX <= centreX) && (bodyY >= centreY)) { // bottom left quadrant
                    if (diagonalMove == 0) {
                        current = right;
                        move = BattleBotArena.RIGHT;
                    }
                    else if (diagonalMove == 1) {
                        current = up;
                        move = BattleBotArena.UP;
                    }
                }
                else if ((bodyX >= centreX) && (bodyY >= centreY)) { // bottom right quadrant
                    if (diagonalMove == 0) {
                        current = left;
                        move = BattleBotArena.LEFT;
                    }
                    else if (diagonalMove == 1) {
                        current = up;
                        move = BattleBotArena.UP;
                    }
                }
            }
        }

        if ((personDistance < 35) && (shotOK)) {
            if ((myX-shootWindow < personX) && (personX < myX + (hitboxRadius*2) + shootWindow) && (myY > personY)) { // fire up
                current = up;
                move = BattleBotArena.FIREUP;
            }
            else if ((myX-shootWindow < personX) && (personX < myX + (hitboxRadius*2) + shootWindow) && (myY < personY)) { // fire down
                current = down;
                move = BattleBotArena.FIREDOWN;
            }
            else if ((myY-shootWindow < personY) && (personY < myY + (hitboxRadius*2) + shootWindow) && (myX < personX)) { // fire right
                current = right;
                move = BattleBotArena.FIRERIGHT;
            }
            else if ((myY-shootWindow < personY) && (personY < myY + (hitboxRadius*2) + shootWindow) && (myX > personX)) { // fire left
                current = left;
                move = BattleBotArena.FIRELEFT;
            }
        }

        // dodging 
        if (bullets.length != 0) {
            double bulletDistance, closestDist;
            Bullet closestBullet = bullets[0];
            closestDist = Math.abs(me.getX() - closestBullet.getX())+Math.abs(me.getY() - closestBullet.getY());

            for (int i = 1; i < bullets.length; i ++){ //find closest bullet that is going to hit me, not mine
                Bullet currentBullet = bullets[i];
                double bulletX = currentBullet.getX();
                double bulletY = currentBullet.getY();

                //approaching me
                boolean fromLeft = ( (bulletX < myX) && (currentBullet.getXSpeed() > 0) );
                boolean fromRight = ( (bulletX > myX) && (currentBullet.getXSpeed() < 0) );
                boolean fromTop = ( (bulletY < myY) && (currentBullet.getYSpeed() > 0) );
                boolean fromBottom = ( (bulletY > myY) && (currentBullet.getYSpeed() < 0) );

                if ( fromLeft || fromRight || fromBottom || fromTop ) {
                    bulletDistance = Math.abs(me.getX() - bullets[i].getX()) + Math.abs(me.getY() - bullets[i].getY());
                    if (bulletDistance < closestDist) {
                        closestBullet = bullets[i];
                        closestDist = bulletDistance;
                    }
                }
            }

            double bulletX = closestBullet.getX();
            double bulletY = closestBullet.getY();
            double distance = botHelper.calcDistance(myX, myY, bulletX, bulletY);

            if (distance < 250) { //bullet in 250 radius to me
                if ( ((bulletX < myX) && (closestBullet.getXSpeed() > 0)) || ((bulletX > myX) && (closestBullet.getXSpeed() < 0)) ) { // approaching horizontally
                    if ((myY-25 < bulletY) && (bulletY <= centreY)) {
                        current = down;
                        move = BattleBotArena.DOWN;
                    }
                    else if ( (centreY <= bulletY) && (bulletY < myY+(hitboxRadius*2)+25) ) { 
                        current = up;
                        move = BattleBotArena.UP;
                    }
                    if (shotOK && (shootCounter <= 0)) { // shoot back
                        if (bulletX < myX) {
                            current = left;
                            move = BattleBotArena.FIRELEFT;
                        }
                        else if (bulletX > myX) {
                            current = right;
                            move = BattleBotArena.FIRERIGHT;
                        }
                    }
                }

                if ( ((bulletY < myY) && (closestBullet.getYSpeed() > 0)) || ((bulletY > myY) && (closestBullet.getYSpeed() < 0)) ) { // approaching vertically
                    if ((myX-25 < bulletX) && (bulletX <= centreX)) {
                        current = right;
                        move = BattleBotArena.RIGHT;
                    }
                    else if ((centreX < bulletX) && (bulletX < myX+(hitboxRadius*2)+25)) { 
                        current = left;
                        move = BattleBotArena.LEFT;
                    }
                    if (shotOK && (shootCounter <= 0)) { // shoot back
                        if (bulletY < myY) {
                            current = up;
                            move = BattleBotArena.FIREUP;
                        }
                        else if (bulletY > myY) {
                            current = down;
                            move = BattleBotArena.FIREDOWN;
                        }
                    }
                }
            }
        }

        if (shootCounter <= 0) shootCounter = shootCountdown;

        // System.out.println(move);
        return move;
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        g.drawImage(current, x, y, Bot.RADIUS*2, Bot.RADIUS*2, null);
        
    }

    @Override
    public String getName() {
        return "Jacky";
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
        String[] images = {"roomba_up.png","roomba_down.png","roomba_left.png","roomba_right.png"};
		return images;

    }

    @Override
    public void loadedImages(Image[] images) {
        if (images != null)
		{
			current = up = images[0];
			down = images[1];
			left = images[2];
			right = images[3];
		}
        
    }




}

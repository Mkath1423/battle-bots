//What went well? It functioned & did something
// What didn't? It functioned poorly

package bots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;
import bots.BotHelper;

public class ShaunBot extends Bot {

    int counter;
    int action;
    int phaseCount;

    double top;
    double bottom;
    double left;
    double right;

    BotHelper helper = new BotHelper();

    @Override
    public void newRound() {
        // TODO Auto-generated method stub

        counter = 99;
        action = 0;
        phaseCount = 0;

        top = BattleBotArena.TOP_EDGE;
        bottom = BattleBotArena.BOTTOM_EDGE;
        left = BattleBotArena.LEFT_EDGE;
        right = BattleBotArena.RIGHT_EDGE;

    }

    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        // TODO Auto-generated method stub

        Bullet threat = helper.findClosestBullet(me, bullets);

        int moveChoice = (int) (Math.random() * 4) + 1;

        int backUpMoveChoice = (int) (Math.random() * 3) + 1;

        if (phaseCount <= 2) {

            counter++;

            if (counter >= 30 + (int) Math.random() * 60) {
                counter = 0;

                if (moveChoice == 1) {
                    if ((helper.calcDistance(me.getX(), me.getY(), me.getX(), top)) < 50) {
                        if (backUpMoveChoice == 1) {
                            action = BattleBotArena.DOWN;
                        } else if (backUpMoveChoice == 2) {
                            action = BattleBotArena.RIGHT;
                        } else {
                            action = BattleBotArena.LEFT;
                        }
                    } else {
                        action = BattleBotArena.UP;
                    }
                } else if (moveChoice == 2) {
                    if ((helper.calcDistance(me.getX(), me.getY(), me.getX(), bottom)) < 50) {
                        if (backUpMoveChoice == 1) {
                            action = BattleBotArena.UP;
                        } else if (backUpMoveChoice == 2) {
                            action = BattleBotArena.RIGHT;
                        } else {
                            action = BattleBotArena.LEFT;
                        }
                    } else {
                        action = BattleBotArena.DOWN;
                    }
                } else if (moveChoice == 3) {
                    if ((helper.calcDistance(me.getX(), me.getY(), left, me.getY())) < 50) {
                        if (backUpMoveChoice == 1) {
                            action = BattleBotArena.DOWN;
                        } else if (backUpMoveChoice == 2) {
                            action = BattleBotArena.RIGHT;
                        } else {
                            action = BattleBotArena.UP;
                        }
                    } else {
                        action = BattleBotArena.LEFT;
                    }
                } else if (moveChoice == 4) {
                    if ((helper.calcDistance(me.getX(), me.getY(), right, me.getY())) < 50) {
                        if (backUpMoveChoice == 1) {
                            action = BattleBotArena.DOWN;
                        } else if (backUpMoveChoice == 2) {
                            action = BattleBotArena.UP;
                        } else {
                            action = BattleBotArena.LEFT;
                        }
                    } else {
                        action = BattleBotArena.RIGHT;
                    }
                }
                phaseCount++;
                counter = 0;

            }

        }

        else if (phaseCount == 3) {
            action = BattleBotArena.STAY;
            phaseCount++;

        }

        else if (phaseCount > 3 && phaseCount <= 7) {
            if (moveChoice == 1) {
                action = BattleBotArena.FIREUP;
            } else if (moveChoice == 2) {
                action = BattleBotArena.FIREDOWN;
            } else if (moveChoice == 3) {
                action = BattleBotArena.FIRELEFT;
            } else if (moveChoice == 4) {
                action = BattleBotArena.FIRERIGHT;
            }
            phaseCount++;
        }

        else if (phaseCount == 8) {

            if (counter < 50 + (int) Math.random() * 100) {
                action = BattleBotArena.STAY;
                counter++;
            }

            else {
                counter = 0;
                phaseCount++;
            }

        }

        else {
            phaseCount = 0;
        }
        return action;

    }

    @Override
    public void draw(Graphics g, int x, int y) {
        // TODO Auto-generated method stub
        g.setColor(Color.green);
        g.fillRect(x + 2, y + 2, RADIUS * 2 - 4, RADIUS * 2 - 4);

    }

    @Override
    public String getName() {
        String name = "Shaun";
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
        String message = "TEST";
        return message;
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

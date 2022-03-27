package bots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

public class Dot {
    public Vector2 pos = new Vector2(13, 13);
    public Vector2 vel = Vector2.UnitVector(30);

    public Color c;

    public int radius = 5;

    private double last_time = System.currentTimeMillis();

    public Dot(Vector2 _pos, Vector2 _vel, int r){
        pos = _pos;
        vel = _vel;

        radius = r;
        c = new Color(randint(0, 255), randint(0, 255), randint(0, 255));
    }

    // https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
    private int randint(int Min, int Max) {
        return Min + (int) (Math.random() * ((Max - Min) + 1));
    }

    private double randrange(double Min, double Max) {
        return Min + (Math.random() * ((Max - Min) + 1));
    }

    public void move(double dt){
        pos.add(Vector2.scale(vel, dt));
    }

    private Color chooseColor(){
        return Color.getHSBColor((float)randrange(0, 1), 1, 1);
    }

    public void checkWallCollision(double wall_radius, double dt){
        if(wall_radius - radius < Math.sqrt(Math.pow(pos.x - wall_radius, 2) + Math.pow(pos.y - wall_radius, 2))){
            System.out.println(Math.sqrt(Math.pow(pos.x - wall_radius, 2) + Math.pow(pos.y - wall_radius, 2)));
            
            Vector2 n = new Vector2(pos.x - wall_radius, pos.y - wall_radius);

            double l = Vector2.length(n);
            n = Vector2.scale(n, 1/l);

            pos.sub(Vector2.scale(vel, dt));
            vel.reflect(n);

            c = chooseColor();
        }
    }

    public void collide(Dot other){
        double num = Vector2.dot(Vector2.sub(this.vel, other.vel), Vector2.sub(this.pos, other.pos));
        double div = Vector2.dot(Vector2.sub(this.pos, other.pos), Vector2.sub(this.pos, other.pos));

        this.vel = Vector2.sub(this.vel, Vector2.scale(Vector2.sub(this.pos, other.pos), num/div));

        c = chooseColor();
    }

    public void draw(Graphics g, int x, int y) {
        g.setColor(c);
        g.fillOval(x - 2 + (int)Math.round(pos.x), y - 2 + (int)Math.round(pos.y), radius*2, radius*2);

    }
}

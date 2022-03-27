package bots;

import java.util.Vector;

// https://noobtuts.com/java/vector2-class
public class Vector2
{              
    // Members
    public double x;
    public double y;

    public static Vector2 LEFT(){
        return new Vector2(-3, 0);
    }

    public static Vector2 RIGHT(){
        return new Vector2( 3, 0);
    }

    public static Vector2 UP(){
        return new Vector2(-3, 0);
    }

    public static Vector2 DOWN(){
        return new Vector2( 3, 0);
    }

    public static Vector2 STAY(){
        return new Vector2(0, 0);
    }
    

    public static Vector2 UnitVector(double degrees){
        return new Vector2(
            Math.cos(Math.toRadians(degrees)),
            Math.sin(Math.toRadians(degrees))
        );
    }
       
    // Constructors
    public Vector2() {
        this.x = 0.0f;
        this.y = 0.0f;
    }
       
    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 copy(){
        return new Vector2(x, y);
    }

    public String toString(){
        return String.format("(%s, %s)", x, y);
    }



    // MATH

    public static Vector2 add(Vector2 a, Vector2 b){
        return new Vector2(a.x + b.x, a.y + b.y);
    }


    public static Vector2 sub(Vector2 a, Vector2 b){
        return new Vector2(a.x - b.x, a.y - b.y);
    }

    public void add(Vector2 other){
        x += other.x;
        y += other.y;
    }

    public static Vector2 scale(Vector2 v, double s){
        return new Vector2(v.x*s, v.y*s);
    }

    public static double dot(Vector2 a, Vector2 b){
        return a.x * b.x + a.y * b.y;
    }

    public static double length(Vector2 a){
        return Math.sqrt(Math.pow(a.x, 2) + Math.pow(a.y, 2));
    }

    public void sub(Vector2 other){
        x -= other.x;
        y -= other.y;
    }
       
    public void reflect(Vector2 n){
        this.sub(Vector2.scale(n, 2 * Vector2.dot(this, n)));
    }

    // Compare two vectors
    public boolean equals(Vector2 other) {
        return (this.x == other.x && this.y == other.y);
    }
}

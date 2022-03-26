package bots;

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
    
       
    // Constructors
    public Vector2() {
        this.x = 0.0f;
        this.y = 0.0f;
    }
       
    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public String toString(){
        return String.format("(%s, %s)", x, y);
    }
       
    // Compare two vectors
    public boolean equals(Vector2 other) {
        return (this.x == other.x && this.y == other.y);
    }
}

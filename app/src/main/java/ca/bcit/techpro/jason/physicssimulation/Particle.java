package ca.bcit.techpro.jason.physicssimulation;

public class Particle {
    public double xPos;
    public double yPos;
    public double xVel;
    public double yVel;

    public Particle(double x, double y){
        xPos = x;
        yPos = y;
    }

    public static double getDistance(Particle p1, Particle p2){
        return Math.max(Math.sqrt(Math.pow(Math.abs(p1.xPos-p2.xPos),2)+Math.pow(Math.abs(p1.yPos-p2.yPos),2)),20);
    }

    public static double distSq(Particle p1, Particle p2){
        return Math.max(Math.pow(Math.abs(p1.xPos-p2.xPos),2)+Math.pow(Math.abs(p1.yPos-p2.yPos),2)+1,400);
    }

    public static double getAngle(Particle p1, Particle p2){
        return Math.atan2(p2.yPos - p1.yPos, p2.xPos - p1.xPos);
    }

    public void updatePos(){
        xPos += xVel;
        yPos += yVel;
    }
}
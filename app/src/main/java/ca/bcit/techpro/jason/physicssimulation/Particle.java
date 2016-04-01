package ca.bcit.techpro.jason.physicssimulation;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class Particle {
    static double G = 0.5;
    static int WIDTH;
    static int HEIGHT;
    public double xPos;
    public double yPos;
    public double xVel;
    public double yVel;
    public int mass;

    public Particle(double x, double y, int m){
        xPos = x;
        yPos = y;
        mass = m;
    }
    public Particle(double x, double y, double xv, double yv, int m){
        xPos = x;
        xVel = xv;
        yPos = y;
        yVel = yv;
        mass = m;
    }


    public static void updateVel(Particle p1, Particle p2){
        double distance = 5*Math.sqrt(Math.pow(p1.xPos-p2.xPos,2)+Math.pow(p1.yPos-p2.yPos,2));
            double accel = G / Math.pow(distance, 2);
            double angle = Math.atan2(p2.yPos - p1.yPos, p2.xPos - p1.xPos);
            double xAccel = accel * Math.cos(angle);
            double yAccel = accel * Math.sin(angle);
            p1.xVel += xAccel * p2.mass;
            p1.yVel += yAccel * p2.mass;
            p2.xVel -= xAccel * p1.mass;
            p2.yVel -= yAccel * p1.mass;
    }

    public static void merge(Particle p1, Particle p2) {
        if (p1.mass == p2.mass) {
            p1.xPos = (p1.xPos+p2.xPos)/2;
            p1.yPos = (p1.yPos+p2.yPos)/2;
        }
        else if (p1.mass < p2.mass) {
            p1.xPos = p2.xPos;
            p1.yPos = p2.yPos;
        }
        double xVel = p1.xVel*p1.mass+p2.xVel*p2.mass;
        double yVel = p1.yVel*p1.mass+p2.yVel*p2.mass;
        p1.mass += p2.mass;
        p1.xVel = xVel/p1.mass;
        p1.yVel = yVel/p1.mass;
    }

    public void updatePos(){
        yVel = Math.min(yVel,2);
        if (xPos > WIDTH)
            xVel = -Math.abs(xVel);
        else if (xPos < 0)
            xVel = Math.abs(xVel);
        xPos += xVel;

        if (yPos > HEIGHT)
            yVel = -Math.abs(yVel);
        else if (yPos < 0)
            yVel = Math.abs(yVel);
        yPos += yVel;
    }
}
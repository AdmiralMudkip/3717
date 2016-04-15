package ca.bcit.techpro.jason.physicssimulation;

import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class Particle {
    public double xPos, yPos, xVel, yVel;
    public int mass;

    public Particle(double x, double y, double xv, double yv, int m){
        xPos = x;
        xVel = xv;
        yPos = y;
        yVel = yv;
        mass = m;
    }

    // update both velocities, getting the distance between them with pythagoras, and then
    // calculating the acceleration and applying that to both the x and y components of both particles
    public static void updateVel(Particle p1, Particle p2){
        double distance = 5*Math.sqrt(Math.pow(p1.xPos-p2.xPos,2)+Math.pow(p1.yPos-p2.yPos,2));
        double accel = CanvasView.G / Math.pow(distance, 2);
        double angle = Math.atan2(p2.yPos - p1.yPos, p2.xPos - p1.xPos);
        double xAccel = accel * Math.cos(angle);
        double yAccel = accel * Math.sin(angle);

        p1.xVel += xAccel * p2.mass;
        p1.yVel += yAccel * p2.mass;
        p2.xVel -= xAccel * p1.mass;
        p2.yVel -= yAccel * p1.mass;
    }

    // merge particles, checking the size of them to properly simulate the conservation of momentum
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

    // update this particle's position, applying the velocity and updating it's x and y coords,
    // checking against the bounds of the screen
    public void updatePos(){
        yVel = Math.min(yVel,2);
        if (xPos > CanvasView.SCREEN_WIDTH)
            xVel = -Math.abs(xVel);
        else if (xPos < 0)
            xVel = Math.abs(xVel);
        xPos += xVel;

        if (yPos > CanvasView.SCREEN_HEIGHT)
            yVel = -Math.abs(yVel);
        else if (yPos < 0)
            yVel = Math.abs(yVel);
        yPos += yVel;
    }
}
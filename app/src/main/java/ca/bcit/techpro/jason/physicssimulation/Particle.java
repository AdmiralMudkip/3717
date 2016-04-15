package ca.bcit.techpro.jason.physicssimulation;

public class Particle {
    public double xPosition, yPosition, xVelocity, yVelocity;
    public boolean stationary;
    public int mass;

    public Particle(double x, double y, double xv, double yv, int m){
        xPosition = x;
        yPosition = y;
        xVelocity = xv;
        yVelocity = yv;
        mass = m;
    }

    // updates the velocity of two particles, getting the distance between them,
    // calculating the acceleration, and adding that to both particles'
    public static void updateVelocity(Particle p1, Particle p2){
        double distanceX = p2.xPosition - p1.xPosition;
        double distanceY = p2.yPosition - p1.yPosition;
        double distanceSquared = 14*(distanceX*distanceX + distanceY*distanceY);
        double distanceCubed = distanceSquared*Math.sqrt(distanceSquared);
        double xAcceleration = distanceX / distanceCubed;
        double yAcceleration = distanceY / distanceCubed;

        if (!p1.stationary) {
            p1.xVelocity += xAcceleration * p2.mass;
            p1.yVelocity += yAcceleration * p2.mass;
        }
        if (!p2.stationary) {
            p2.xVelocity -= xAcceleration * p1.mass;
            p2.yVelocity -= yAcceleration * p1.mass;
        }
    }

    // merge particles, checking the size of them to properly simulate the conservation of momentum
    // this function expects p2 to be set to null afterwards
    public static void merge(Particle p1, Particle p2) {
        if (p2.stationary)
            p1.stationary = true;

        //sets which particles' location is used based on which has the larger mass
        if (p1.mass == p2.mass) {
            p1.xPosition = (p1.xPosition + p2.xPosition) / 2;
            p1.yPosition = (p1.yPosition + p2.yPosition) / 2;
        } else if (p1.mass < p2.mass) {
            p1.xPosition = p2.xPosition;
            p1.yPosition = p2.yPosition;
        }

        //updates velocity based on particle masses and sets new mass
        double xVelocity = p1.xVelocity * p1.mass + p2.xVelocity * p2.mass;
        double yVelocity = p1.yVelocity * p1.mass + p2.yVelocity * p2.mass;
        p1.mass += p2.mass;
        p1.xVelocity = xVelocity / p1.mass;
        p1.yVelocity = yVelocity / p1.mass;
    }

    // update this particle's position, applying the velocity and updating it's x and y coords,
    // checking against the bounds of the screen
    public void updatePos(){
        if (!stationary) {
            //if out of bounds on X axis puts back inside and sets xVelocity appropriately
            if (xPosition > CanvasView.SCREEN_WIDTH)
                xVelocity = -Math.abs(xVelocity);
            else if (xPosition < 0)
                xVelocity = Math.abs(xVelocity);

            //if out of bounds on Y axis puts back inside and sets yVelocity appropriately
            if (yPosition > CanvasView.SCREEN_HEIGHT)
                yVelocity = -Math.abs(yVelocity);
            else if (yPosition < 0)
                yVelocity = Math.abs(yVelocity);

            //updates position based on velocity
            xPosition += xVelocity;
            yPosition += yVelocity;
        }
    }
}
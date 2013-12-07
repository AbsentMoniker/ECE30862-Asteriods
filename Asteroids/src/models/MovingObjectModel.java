package models;

import game.Asteroids;

public abstract class MovingObjectModel implements Updatable {
    // X and Y positions in points (pos[0] is X, pos[1] is Y)
	protected double pos[] = new double[2];
	// X and Y velocities in points/sec (vel[0] is X, vel[1] is Y)
	protected double vel[] = new double[2];
	// X and Y accelerations in points/sec^2 (acc[0] is X, acc[1] is Y)
	protected double acc[] = new double[2];
	// Rotational position in radians, where 0 means pointing up
	protected double rotPos = 0;
	// Rotational velocity in radians/sec, where positive is clockwise
	protected double rotVel = 0;
	// controls whether we should be counting time (e.g., false when paused)
	protected boolean playing = false;
	// nanosecond time when model was last updated
	protected long lastUpdate = 0;
    // circle radius for hit detection
	protected double hitRad = 0;
	// distance object has traveled in points
	protected double dist = 0;
	// lives
	protected int lives = 0;

	public MovingObjectModel(int x, int y, double angle, double vx, double vy, double vAngle){
		pos[0] = x;
		pos[1] = y;
		rotPos = angle;
		vel[0] = vx;
		vel[1] = vy;
		rotVel = vAngle;
	}
	
	public void update() {
		if (playing == false) {
			// we weren't updating, now we are; get the time and return
			playing = true;
			lastUpdate = System.nanoTime();
			return;
		}
		
		// we've already been updating for some time
		// seconds is time since last valid update
		double seconds = (System.nanoTime() - lastUpdate) / 1.0e9;
		lastUpdate = System.nanoTime();
		
		// update velocity and then position for both X, Y
		// bounds check and "warp" position if necessary
		for (int i = 0; i < pos.length; i++) {
			/*
			if (this instanceof AsteroidModel){
				System.out.println("position "+i+" updated by: "+vel[i]*seconds);
				System.out.println("vel["+i+"]: " + vel[i]+", seconds: "+seconds);
			}
			*/
			vel[i] += acc[i] * seconds;
			pos[i] += vel[i] * seconds;
			if (pos[i] < 0)
				pos[i] = pos[i] + Asteroids.getScreenDims()[i];
			else if (pos[i] > Asteroids.getScreenDims()[i])
				pos[i] = pos[i] - Asteroids.getScreenDims()[i];
		}
		
		// update rotational position
		rotPos += rotVel * seconds;
		rotPos %= 2 * Math.PI;
		
		// update distance traveled
		dist += Math.sqrt( Math.pow(vel[0] * seconds, 2) + Math.pow(vel[1] * seconds, 2) );
	}
	
	public final boolean collidesWith(MovingObjectModel obj) {
		double separationDistance = Math.sqrt( Math.pow(pos[0] - obj.pos[0], 2) + Math.pow(pos[1] - obj.pos[1], 2) );
		if (separationDistance < hitRad + obj.hitRad)
			return true;
		return false;
	}
	
	public final double[] getPosition() {
		return pos.clone();
	}
	
	public final double getOrientation() {
		return rotPos;
	}
	
	public final double[] getVelocity() {
		return vel.clone();
	}
	
	public final double getRotVel() {
		return rotVel;
	}
	
	public final void setPlaying(boolean newPlaying){
		playing = newPlaying;
	}
	
	// overridden by subclasses that want to
	public int getLives() {
		return lives;
	}
	
	public boolean decrementLives() {
		return true;
	}
	
	public String toString() {
		String retString;
		retString = String.format("Pos: (%.1f, %.1f)\n", pos[0], pos[1]);
		retString += String.format("Vel: (%.1f, %.1f)\n", vel[0], vel[1]);
		retString += String.format("RotPos: %.1f", rotPos);
		return retString;
	}
}

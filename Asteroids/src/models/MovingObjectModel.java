package models;

public abstract class MovingObjectModel implements Updatable {
    // X and Y positions in points (pos[0] is X, pos[1] is Y)
	// imaginary FOV is 100x100 points
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
	static protected boolean playing = false;
	// nanosecond time when model was last updated
	protected long lastUpdate = 0;
	
	public void update() {
		if (playing == false) {
			// we weren't updating, now we are; get the time and return
			playing = true;
			lastUpdate = System.nanoTime();
			return;
		}
		
		// we've already been updating for some time
		// seconds is time since last valid update
		double seconds = (System.nanoTime() - lastUpdate) / 1e9;
		lastUpdate = System.nanoTime();
		
		// update velocity and then position for both X, Y
		// bounds check and "warp" position if necessary
		for (int i = 0; i < pos.length; i++) {
			vel[i] += acc[i] * seconds;
			pos[i] += vel[i] * seconds;
			if (pos[i] < 0)
				pos[i] = pos[i] + 100;
			else if (pos[i] > 100)
				pos[i] = pos[i] - 100;
		}
		
		// update rotational position
		rotPos += rotVel * seconds;
		rotPos %= 2 * Math.PI;
	}
	
	public final double[] getPosition() {
		return pos.clone();
	}
	
	public final double getOrientation() {
		return rotPos;
	}
	public static final void setPlaying(boolean newPlaying){
		playing = newPlaying;
	}
	public String toString() {
		String retString;
		retString = String.format("Pos: (%.1f, %.1f)\n", pos[0], pos[1]);
		retString += String.format("Vel: (%.1f, %.1f)\n", vel[0], vel[1]);
		retString += String.format("RotPos: %.1f", rotPos);
		return retString;
	}
}

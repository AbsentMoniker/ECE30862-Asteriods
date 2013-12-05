package Models;

public class PlayerModel implements Updatable {
	// X and Y positions in points (pos[0] is X, pos[1] is Y)
	// imaginary FOV is 100x100 points
	private float pos[] = new float[2];
	// X and Y velocities in points/sec (vel[0] is X, vel[1] is Y)
	private float vel[] = new float[2];
	// controls whether we should be counting time (e.g., false when paused)
	private boolean playing = false;
	// nanosecond time when model was last updated
	private long lastUpdate = 0;
	
	private KeyChecker keyChecker;
	
	public PlayerModel() {
		keyChecker = new KeyChecker();
	}
	
	public void pauseModel() {
		playing = false;
	}
	
	public void update() {
		// TODO Auto-generated method stub
		if (playing == false) {
			// we weren't updating, now we are; get the time and return
			playing = true;
			lastUpdate = System.nanoTime();
			return;
		}
		
		// we've already been updating for some time
		// seconds is time since last valid update
		double seconds = (System.nanoTime() - lastUpdate) / 1e9;
		
	}

}

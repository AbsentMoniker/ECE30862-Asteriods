package models;

public class PlayerModel extends MovingObjectModel implements Updatable {
	// CONSTANTS
	// player acceleration constant, in points/s^2
	private final double shipAcceleration = 80;
	// player rotation constant, in rad/s
	private final double shipRotationSpeed = 2.5;
	// bullet relative velocity
	private final double bulletSpeed = 200;
	// player maximum speed
	private final double maxSpeed = 500;
	
	// VARIABLES
	// which player this is (0 or 1)
	private int player = 0;
	// our keychecker
	KeyChecker keyChecker;
	// how long it's been since a print was issued
	private long lastPrint = 0;

	// when last bullet was fired
	private long lastBullet = 0;
	// length of current burst
	private int burstLength = 0;
	// whether a bullet should be generated
	private boolean firingBullet = false;
	
	// number of remaining lives
	private int lives = 3;
	
	public PlayerModel(int x, int y, double angle, double vx, double vy, double vAngle, int numLives, int playerNum) {
		super(x, y, angle, vx, vy, vAngle);
		System.out.println("Player model " + playerNum + " instantiated");
		player = playerNum;
		keyChecker = KeyChecker.getInstance();
		hitRad = 20;
		lastBullet = System.nanoTime();
		lives = numLives;
		speedLimit = maxSpeed;
	}
	
	@Override
	public void update() {
		if (keyChecker.getPlayerAccelerating(player)) {
			acc[0] = -1 * Math.sin(rotPos) * shipAcceleration;
			acc[1] = Math.cos(rotPos) * shipAcceleration;
		} else {
			acc[0] = acc[1] = 0;
		}
		
		if (keyChecker.getPlayerDirection(player) == 'l') {
			rotVel = -shipRotationSpeed;
		} else if (keyChecker.getPlayerDirection(player) == 'r') {
			rotVel = shipRotationSpeed;
		} else {
			rotVel = 0;
		}
		
		if (keyChecker.getPlayerShooting(player)) {
			if (burstLength < 4) {
				firingBullet = true;
				burstLength++;
				lastBullet = System.nanoTime();
			}
		}

		if (System.nanoTime() - lastBullet > 0.2e9) {  // .2 seconds
			burstLength = 0;
		}
		
		super.update();
		
		// add slowdown effect
		// TODO add slowdown effect
		
		if (System.nanoTime() - lastPrint > 1e9) {
			//System.out.println(this);
			lastPrint = System.nanoTime();
		}
	}
	
	@Override
	public int getLives() {
		return lives;
	}
	
	@Override
	public boolean decrementLives() {
		if (lives > 0) {
			lives--;
			return true;
		}
		return false;
	}
	
	public boolean firesBullet() {
		if (firingBullet == true) {
			firingBullet = false;
			return true;
		}
		return false;
	}
	
	public double[] bulletPos() {
		double[] bPos = new double[2];
		bPos[1] = Math.cos(rotPos) * hitRad + pos[1];
		bPos[0] = -Math.sin(rotPos) * hitRad + pos[0];
		return bPos.clone();
	}
	
	public double[] bulletVel() {
		double[] bVel = new double[2];
		bVel[1] = Math.cos(rotPos) * bulletSpeed + vel[1];
		bVel[0] = -Math.sin(rotPos) * bulletSpeed + vel[0];
		return bVel.clone();
	}
	
	@Override
	public String toString() {
		String retString;
		retString = String.format("Player #%d\n", player);
		retString += super.toString();
		return retString;
	}
}

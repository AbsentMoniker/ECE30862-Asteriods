package models;

public class PlayerModel extends MovingObjectModel implements Updatable {
	// CONSTANTS
	// player acceleration constant, in points/s^2
	private final double shipAcceleration = 80;
	// player rotation constant, in rad/s
	private final double shipRotationSpeed = 2.5;
	
	// VARIABLES
	// which player this is (0 or 1)
	private int player = 0;
	// our keychecker
	KeyChecker keyChecker;
	// how long it's been since a print was issued
	private long lastPrint = 0;
	private long lastBullet = 0;
	
	private int burstLength = 0;
	private boolean firingBullet = false;
	
	public PlayerModel(int x, int y, double angle, double vx, double vy, double vAngle, int playerNum) {
		super(x, y, angle, vx, vy, vAngle);
		System.out.println("Player model " + playerNum + " instantiated");
		player = playerNum;
		keyChecker = KeyChecker.getInstance();
		hitRad = 10;
		lastBullet = System.nanoTime();
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
		} else {
			if (System.nanoTime() - lastBullet > 0.2e9) {  // .2 seconds
				burstLength = 0;
			}
		}
		
		super.update();
		
		if (System.nanoTime() - lastPrint > 1e9) {
			//System.out.println(this);
			lastPrint = System.nanoTime();
		}
		
		
	}
	
	public boolean firesBullet() {
		if (firingBullet == true) {
			firingBullet = false;
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		String retString;
		retString = String.format("Player #%d\n", player);
		retString += super.toString();
		return retString;
	}
}

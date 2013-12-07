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
	
	public PlayerModel(int x, int y, int angle, int vx, int vy, int vAngle, int playerNum) {
		super(x, y, angle, vx, vy, vAngle);
		System.out.println("Player model " + playerNum + " instantiated");
		player = playerNum;
		keyChecker = KeyChecker.getInstance();
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
			// TODO spawn bullets
		}
		
		super.update();
		
		if (System.nanoTime() - lastPrint > 1e9) {
			System.out.println(this);
			lastPrint = System.nanoTime();
		}
	}
	
	@Override
	public String toString() {
		String retString;
		retString = String.format("Player #%d\n", player);
		retString += super.toString();
		return retString;
	}
}

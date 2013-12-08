package models;

import game.Asteroids;;

public class PlayerModel extends MovingObjectModel implements Updatable {
	// CONSTANTS
	// player acceleration constant, in points/s^2
	private final double shipAcceleration = 100;
	// player rotation constant, in rad/s
	private final double shipRotationSpeed = 2.5;
	// bullet relative velocity
	private final double bulletSpeed = 230;
	// player maximum speed
	private final double maxSpeed = 500;
	// gravity scale factor
	private final double gravScale = 1000000;
	
	// VARIABLES
	// which player this is (0 or 1)
	private int player = 0;
	// our keychecker
	KeyChecker keyChecker;
	// how long it's been since a print was issued
	private long lastPrint = 0;
	// whether ship is executing burn (i.e. acceleration)
	private boolean isAccelerating = false;

	// when last bullet was fired
	private long lastBullet = 0;
	// length of current burst
	private int burstLength = 0;
	// whether a bullet should be generated
	private boolean firingBullet = false;
	
	// number of remaining lives
	private int lives = 3;
	
	// whether the grav object exists
	private boolean gravExists = false;
	
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
	
	public void setGravExists(boolean exists) {
		gravExists = exists;
	}
	
	public void resetToStart(boolean twoPlayer) {
		vel[0] = vel[1] = 0;
		rotPos = rotVel = 0;
		acc[0] = acc[1] = 0;
		if (twoPlayer == false) {
			pos[0] = Asteroids.getScreenWidth() / 2;
			pos[1] = Asteroids.getScreenHeight() / 2;
		} else if (player == 0) {
			pos[0] = Asteroids.getScreenWidth() / 4;
			pos[1] = Asteroids.getScreenHeight() / 2;
		} else if (player == 1) {
			pos[0] = (Asteroids.getScreenWidth() * 3) / 4;
			pos[1] = Asteroids.getScreenHeight() / 2;
		}
	}
	
	@Override
	public void update() {
		if (keyChecker.getPlayerAccelerating(player)) {
			acc[0] = -1 * Math.sin(rotPos) * shipAcceleration;
			acc[1] = Math.cos(rotPos) * shipAcceleration;
			isAccelerating = true;
		} else {
			acc[0] = acc[1] = 0;
			isAccelerating = false;
		}
		
		if (gravExists) {
			double gravXPos = Asteroids.getScreenDims()[0] / 2;
			double gravYPos = Asteroids.getScreenDims()[1] / 2;
			double distToGravX = pos[0] - gravXPos;
			double distToGravY = pos[1] - gravYPos;
			double distToGrav = Math.sqrt( Math.pow(distToGravX, 2) + 
					Math.pow(distToGravY, 2) );
			if (distToGrav < 1.0)
				distToGrav = 1.0;
			double gravAcc = gravScale / (Math.pow(distToGrav, 2) + 1);
			if (gravAcc > shipAcceleration)
				gravAcc = shipAcceleration * 0.9;
			double gravAccX = -gravAcc * (distToGravX / distToGrav);
			double gravAccY = -gravAcc * (distToGravY / distToGrav);
			
			acc[0] += gravAccX;
			acc[1] += gravAccY;
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
	
	public boolean isAccelerating() {
		return isAccelerating;
	}
	
	@Override
	public int getLives() {
		return lives;
	}
	
	@Override
	public boolean decrementLives() {
		if (lives == -1)
			return true;
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
		bPos[1] = Math.cos(rotPos) * hitRad * 2.5 + pos[1];
		bPos[0] = -Math.sin(rotPos) * hitRad * 2.5 + pos[0];
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

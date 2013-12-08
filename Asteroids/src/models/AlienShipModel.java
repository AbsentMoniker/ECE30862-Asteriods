package models;

public class AlienShipModel extends MovingObjectModel implements Updatable {
	private int lives = 2;
	
	private final double bulletSpeed = 230;
	
	public AlienShipModel(int x, int y, double angle, double vx, double vy,
			double vAngle) {
		super(x, y, angle, vx, vy, vAngle);
		hitRad = 20;
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
	
	public double[] bulletPos(double targetX, double targetY) {
		double[] bPos = new double[2];
		double diffX = targetX - pos[0];
		double diffY = targetY - pos[1];
		double diff = Math.sqrt( Math.pow(diffX, 2) + 
				Math.pow(diffY, 2) );
		bPos[1] = (diffY / diff) * hitRad * 2.5 + pos[1];
		bPos[0] = (diffX / diff) * hitRad * 2.5 + pos[0];
		return bPos.clone();
	}
	
	public double[] bulletVel(double targetX, double targetY) {
		double[] bVel = new double[2];
		double diffX = targetX - pos[0];
		double diffY = targetY - pos[1];
		double diff = Math.sqrt( Math.pow(diffX, 2) + 
				Math.pow(diffY, 2) );
		bVel[1] = (diffY / diff) * bulletSpeed + vel[1];
		bVel[0] = (diffX / diff) * bulletSpeed + vel[0];
		return bVel.clone();
	}
}

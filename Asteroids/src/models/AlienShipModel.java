package models;

public class AlienShipModel extends MovingObjectModel implements Updatable {
	private int lives = 3;
	
	public AlienShipModel(int x, int y, double angle, double vx, double vy,
			double vAngle) {
		super(x, y, angle, vx, vy, vAngle);
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
}

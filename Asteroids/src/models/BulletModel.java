package models;

public class BulletModel extends MovingObjectModel implements Updatable {

	public BulletModel(int x, int y, double angle, double vx, double vy, double vAngle) {
		super(x, y, 0, vx, vy, 0.0);
		hitRad = 0;
	}
}

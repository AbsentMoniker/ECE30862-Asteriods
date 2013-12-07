package models;

public class AsteroidModel extends MovingObjectModel implements Updatable {
	public AsteroidModel(int x, int y, int angle, int vx, int vy, int vAngle){
		super(x, y, angle, vx, vy, vAngle);
	}
}

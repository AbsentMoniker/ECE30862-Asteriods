package models;

public class RogueSpaceshipModel extends MovingObjectModel implements Updatable {
	
	public RogueSpaceshipModel(int x, int y, double angle, double vx, double vy, double vAngle){
		super(x,y,angle,vx,vy,vAngle);
	}
}

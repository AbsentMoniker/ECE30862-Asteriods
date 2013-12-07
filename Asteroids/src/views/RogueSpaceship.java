package views;

import models.RogueSpaceshipModel;
import models.Updatable;

public class RogueSpaceship extends BaseView implements Updatable {
	public RogueSpaceship(int x, int y, int angle, double vx, double vy, double vAngle){
		model = new RogueSpaceshipModel(x,y,angle,vx,vy,vAngle);
	}
}

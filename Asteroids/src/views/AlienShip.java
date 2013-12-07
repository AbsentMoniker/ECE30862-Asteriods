package views;

import models.AlienShipModel;
import models.Updatable;

public class AlienShip extends BaseView implements Updatable {
	public AlienShip(int x, int y, int angle, double vx, double vy, double vAngle){
		model = new AlienShipModel(x,y,angle,vx,vy,vAngle);
	}
}

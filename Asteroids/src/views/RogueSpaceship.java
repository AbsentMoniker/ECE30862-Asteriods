package views;

import java.awt.Color;
import java.awt.geom.GeneralPath;

import models.RogueSpaceshipModel;
import models.Updatable;

public class RogueSpaceship extends BaseView implements Updatable {
	public RogueSpaceship(int x, int y, int angle, double vx, double vy, double vAngle){
		model = new RogueSpaceshipModel(x,y,angle,vx,vy,vAngle);
		color = Color.red;
		shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 3);
		shape.moveTo(0, 20);
		shape.lineTo(10, -20);
		shape.lineTo(-10, -20);
		shape.closePath();
	}
}

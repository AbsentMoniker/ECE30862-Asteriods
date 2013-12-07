package views;

import java.awt.Color;
import java.awt.geom.GeneralPath;

import models.AlienShipModel;
import models.Updatable;

public class AlienShip extends BaseView implements Updatable {
	public AlienShip(int x, int y, int angle, double vx, double vy, double vAngle){
		model = new AlienShipModel(x,y,angle,vx,vy,vAngle);
		color = Color.green;
		shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD,10);
		shape.moveTo(-15, 0);
		shape.curveTo(-15.0*Math.sqrt(2),-15.0/2*Math.sqrt(2),15.0/2*Math.sqrt(2),-15.0*Math.sqrt(2),15,0);
		shape.curveTo(15.0/2*Math.sqrt(2), 4, -15.0/2*Math.sqrt(2), 4, -15, 0);
		shape.curveTo(-70.0/3, 2, -90.0/3, 6, -30, 10);
		shape.curveTo(-10*Math.sqrt(2), 20, 10*Math.sqrt(2), 20, 30, 10);
		shape.curveTo(90.0/3, 6, 70.0/3, 2, 15, 0);
		
	}
}

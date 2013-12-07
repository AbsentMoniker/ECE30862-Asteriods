package views;

import java.awt.Color;
import java.awt.geom.GeneralPath;
import models.BulletModel;

import models.Updatable;

public class Bullet extends BaseView implements Updatable {
	public Bullet(int x, int y, double angle, double vx, double vy, double vAngle, Color bulletColor){
		model = new BulletModel(x,y,angle,vx,vy,vAngle);
		color = bulletColor;
		shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);
		shape.moveTo(0,0);
		shape.lineTo(0, 1);
		shape.lineTo(1, 1);
		shape.lineTo(1, 0);
		shape.closePath();
	}
}

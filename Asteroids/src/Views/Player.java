package Views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import Models.PlayerModel;
import Models.Updatable;

public class Player implements Updatable {

	private PlayerModel model;
	private int x;
	private int y;
	private int angle;
	
	public Player(int startX, int startY){
		x = startX;
		y = startY;
		angle = 0;
		model = new PlayerModel();
	}
	@Override
	public void update() {
		model.update();
		x += 5;
		if (x > 1720)
			x = 0;
		angle += 1;
		if (angle >= 360)
			angle -= 360;
	}
	
	public void paint(Graphics2D g){
		g.setColor(Color.white);
		GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 3);
		shape.moveTo(x, y+20);
		shape.lineTo(x+10, y-20);
		shape.lineTo(x-10, y-20);
		shape.closePath();
		AffineTransform at = new AffineTransform();
		at.rotate(Math.toRadians(angle),x,y);
		shape.transform(at);
		g.draw(shape);
	}

}

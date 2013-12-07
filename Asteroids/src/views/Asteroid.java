package views;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.Random;

import models.AsteroidModel;
import models.Updatable;

public class Asteroid implements Updatable {
	private static final int RADIUS = 50;
	private static final int VERTICES = 30;
	AsteroidModel model;
	GeneralPath shape;
	int health = 2;
	
	public Asteroid(int startX, int startY, int startVX, int startVY, int startAngle, int startAngleVelocity){
		model = new AsteroidModel();
		shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD,VERTICES);
		Random rand = new Random();
		shape.moveTo(0, RADIUS+rand.nextInt(14)-6);
		for (int i = 1; i < VERTICES; i++){
			int currentRadius = RADIUS+rand.nextInt(14)-6;
			shape.lineTo(Math.sin(i/(float)VERTICES*2*Math.PI)*currentRadius, Math.cos(i/(float)VERTICES*2*Math.PI)*currentRadius);
		}
		shape.closePath();
		
	}
	@Override
	public void update() {
		model.update();
	}
	
	public void paint(Graphics2D g){
		double [] pos = model.getPosition();
		AffineTransform at = new AffineTransform();
		at.translate(pos[0], pos[1]);
		at.rotate(model.getOrientation(),pos[0],pos[1]);
		GeneralPath drawing = new GeneralPath(shape);
		drawing.transform(at);
		g.draw(drawing);
	}

}

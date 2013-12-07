package views;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.Random;

import models.AsteroidModel;
import models.Updatable;

public class Asteroid extends BaseView implements Updatable {
	private static final int RADIUS = 50;
	private static final int VERTICES = 30;
	int health = 2;
	
	public Asteroid(int x, int y, int angle, double vx, double vy, double vAngle){
		model = new AsteroidModel(x, y, angle, vx, vy, vAngle);
		shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD,VERTICES);
		color = Color.white;
		Random rand = new Random();
		shape.moveTo(0, RADIUS+rand.nextInt(14)-6);
		for (int i = 1; i < VERTICES; i++){
			int currentRadius = RADIUS+rand.nextInt(14)-6;
			shape.lineTo(Math.sin(i/(float)VERTICES*2*Math.PI)*currentRadius, Math.cos(i/(float)VERTICES*2*Math.PI)*currentRadius);
		}
		shape.closePath();
	}
}

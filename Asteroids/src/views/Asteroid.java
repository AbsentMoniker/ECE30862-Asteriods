package views;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.Random;

import models.AsteroidModel;
import models.Updatable;

public class Asteroid extends BaseView implements Updatable {
	private static final int RADIUS_LARGE = 50;
	private static final int RADIUS_SMALL = 20;
	private static final int VERTICES_LARGE = 30;
	private static final int VERTICES_SMALL = 20;
	
	private int type;
	
	public Asteroid(int x, int y, double angle, double vx, double vy, double vAngle, int type){
		this.type = type;
		int radius;
		int vertices;
		if (type == 0){
			radius = RADIUS_LARGE;
			vertices = VERTICES_LARGE;
		}else{
			radius = RADIUS_SMALL;
			vertices = VERTICES_SMALL;
		}
		model = new AsteroidModel(x, y, angle, vx, vy, vAngle);
		shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD,vertices);
		color = Color.white;
		Random rand = new Random();
		shape.moveTo(0, radius+rand.nextInt(14)-6);
		for (int i = 1; i < vertices; i++){
			int currentRadius = radius+rand.nextInt(14)-6;
			shape.lineTo(Math.sin(i/(float)vertices*2*Math.PI)*currentRadius, Math.cos(i/(float)vertices*2*Math.PI)*currentRadius);
		}
		shape.closePath();
	}
	
	public int getType(){
		return type;
	}
}

package views;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import models.MovingObjectModel;
import models.Updatable;

public abstract class BaseView implements Updatable{
	protected GeneralPath shape;
	protected MovingObjectModel model;
	protected Color color;
	
	@Override
	public void update() {
		model.update();
	}
	
	public void paint(Graphics2D g){
		g.setColor(color);
		double [] pos = model.getPosition();
		AffineTransform at = new AffineTransform();
		at.rotate(model.getOrientation(),0,0);
		GeneralPath drawing = new GeneralPath(shape);
		drawing.transform(at);
		at = new AffineTransform();
		at.translate(pos[0], pos[1]);
		drawing.transform(at);
		g.draw(drawing);
	}
	
	public void pause(){
		model.setPlaying(false);
	}
	
	public int getX(){
		return (int)model.getPosition()[0];
	}
	public int getY(){
		return (int)model.getPosition()[1];
	}
}

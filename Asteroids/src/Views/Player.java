package Views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import Models.PlayerModel;
import Models.Updatable;

public class Player implements Updatable {

	private PlayerModel model;
	private int x;
	private int y;
	
	public Player(int startX, int startY){
		x = startX;
		y = startY;
		model = new PlayerModel();
	}
	@Override
	public void update() {
		model.update();
		x += 20;
	}
	
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.white);
		GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 3);
		shape.moveTo(x, y+20);
		shape.lineTo(x+10, y-20);
		shape.lineTo(x-10, y-20);
		shape.closePath();
		g2.draw(shape);
		g2.dispose();
	}

}

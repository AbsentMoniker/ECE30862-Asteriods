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
	private int playerNo;
	
	public Player(int startX, int startY, int playerNo){
		model = new PlayerModel(playerNo);
		this.playerNo = playerNo;
	}
	@Override
	public void update() {
		model.update();
	}
	
	public void paint(Graphics2D g, int width, int height){
		if (playerNo == 0)
			g.setColor(Color.white);
		else
			g.setColor(Color.blue);
		GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 3);
		double [] pos = model.getPosition();
		pos[0]*= width/100;
		pos[1]*= height/100;
		shape.moveTo(pos[0], pos[1]+20);
		shape.lineTo(pos[0]+10, pos[1]-20);
		shape.lineTo(pos[0]-10, pos[1]-20);
		shape.closePath();
		AffineTransform at = new AffineTransform();
		at.rotate(model.getOrientation(),pos[0],pos[1]);
		shape.transform(at);
		g.draw(shape);
	}

}

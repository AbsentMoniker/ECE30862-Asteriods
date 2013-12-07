package views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import models.PlayerModel;
import models.Updatable;


public class Player extends BaseView implements Updatable {

	private int playerNo;
	
	public Player(int x, int y, double angle, double vx, double vy, double vAngle, int numLives, int playerNo){
		model = new PlayerModel(x, y, angle, vx, vy, vAngle, numLives, playerNo);
		this.playerNo = playerNo;
		if (playerNo == 0)
			color = Color.WHITE;
		else
			color = Color.blue;
		shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 3);
		shape.moveTo(0, 14);
		shape.lineTo(14, -14);
		shape.lineTo(0, -5);
		shape.lineTo(-14, -14);
		shape.closePath();
	}
}

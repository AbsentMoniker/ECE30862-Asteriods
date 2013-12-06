package Game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Paint;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Models.KeyChecker;
import Models.MovingObjectModel;
import Views.Player;

public class Asteroids{
	private Player player1;
	
	private int score = 0;
	private static boolean paused = false;
	private KeyChecker keyChecker;
	
	public Asteroids(){
		player1 = new Player(100,100);
		keyChecker = KeyChecker.getInstance();
	}
	public static void main(String [] argv){
		new Asteroids().start();
	}
	
	public void start(){
		DisplayMode displayMode = new DisplayMode(100,100,16,75);
		ScreenManager screen = new ScreenManager();
		try{
			
			screen.setFullScreen(displayMode);
			JFrame window = screen.getFullScreenWindow();
			MyCanvas canvas = new MyCanvas();
			window.getContentPane().add(canvas);
			canvas.setSize(window.getSize());
			canvas.createBufferStrategy(2);
			window.revalidate();
			while (true){
				if (paused){
					
				}else{
					player1.update();
				}
				canvas.update();
				try{
					Thread.sleep(20);
				}catch (InterruptedException ex){}
			}
		}finally{
			screen.restoreScreen();
		}
		
	}
	
	public static boolean isPaused(){
		return paused;
	}
	public static void togglePaused(){
		paused = !paused;
		if (paused)
			MovingObjectModel.setPlaying(false);
	}
	private class MyCanvas extends Canvas{
		
		
		public MyCanvas(){
			setBackground(Color.BLACK);
			setIgnoreRepaint(true);
		}
		
		
		public void update(){
			Graphics2D g = (Graphics2D)getBufferStrategy().getDrawGraphics();
			
			//Clear screen
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getWidth(), getHeight());
			
			paintItems(g);
			if (paused)
				paintPauseScreen(g);
			getBufferStrategy().show();
			Toolkit.getDefaultToolkit().sync();
			
			
			g.dispose();
		}
		
		public void paintItems(Graphics2D g){
			player1.paint(g, getWidth(), getHeight());
			g.setFont(new Font("Arial",Font.PLAIN,30));
			g.drawString(""+score, 40,40);
		}
		public void paintPauseScreen(Graphics2D g){
			Font font = new Font("Arial", Font.BOLD, 40);
			g.setFont(font);
			FontMetrics fm = g.getFontMetrics(font);
			g.drawString("PAUSED", (getWidth()-fm.stringWidth("PAUSED"))/2, 100);
			g.drawString("Continue", (getWidth()-fm.stringWidth("Continue"))/2, 300);
			g.drawString("Save", (getWidth()-fm.stringWidth("Save"))/2, 400);
			g.drawString("Options", (getWidth()-fm.stringWidth("Options"))/2, 500);
			g.drawString("Quit", (getWidth()-fm.stringWidth("Quit"))/2, 600);
		}
	}
	
	
}

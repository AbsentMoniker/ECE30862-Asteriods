package Game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Views.Player;

public class Asteroids{
	
	public static void main(String [] argv){
		Asteroids game = new Asteroids();
		
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
				canvas.update();
				try{
					Thread.sleep(20);
				}catch (InterruptedException ex){}
			}
		}finally{
			screen.restoreScreen();
		}
		
	}
	
	private static class MyCanvas extends Canvas{
		private Player player;
		
		public MyCanvas(){
			player = new Player(100,100);
			setBackground(Color.BLACK);
			setIgnoreRepaint(true);
		}
		
		
		public void update(){
			player.update();
			Graphics2D g = (Graphics2D)getBufferStrategy().getDrawGraphics();
			
			//Clear screen
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getWidth(), getHeight());
			
			paintItems(g);
			getBufferStrategy().show();
			Toolkit.getDefaultToolkit().sync();
			g.dispose();
			//repaint();
		}
		
		public void paintItems(Graphics2D g){
			player.paint(g, getWidth(), getHeight());
		}
		
		
		@Override
		public void paint(Graphics g){
			super.paint(g);
			Graphics2D g2 = (Graphics2D)g.create();
			player.paint(g2, getWidth(), getHeight());
			g2.dispose();
			g.dispose();
		}
	}
	
	
}

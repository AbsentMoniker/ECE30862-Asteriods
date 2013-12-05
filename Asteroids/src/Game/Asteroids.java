package Game;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

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
			Canvas canvas = new Canvas();
			window.getContentPane().add(canvas);
			window.revalidate();
			while (true){
				canvas.update();
				try{
					Thread.sleep(50);
				}catch (InterruptedException ex){}
			}
		}finally{
			screen.restoreScreen();
		}
		
	}
	
	private static class Canvas extends JPanel{
		private Player player;
		
		public Canvas(){
			player = new Player(100,100);
			this.setBackground(Color.BLACK);
		}
		
		
		public void update(){
			player.update();
			repaint();
		}
		
		@Override
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			player.paint(g);
			
		}
	}
	
	
}

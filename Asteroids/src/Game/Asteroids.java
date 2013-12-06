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
import java.awt.MouseInfo;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Models.KeyChecker;
import Models.MovingObjectModel;
import Views.Player;

public class Asteroids{
	private Player player1;
	
	private int score = 0;
	private static boolean paused = false;
	private boolean inOptions = false;
	private KeyChecker keyChecker;
	
	//options
	private boolean gravExists = false;
	private boolean gravVisible = false;
	private boolean unlimitedLives = false;
	private int numAsteroids;
	private int startingLevel = 1;
	
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
	private class MyCanvas extends Canvas implements MouseListener{
		private Font pauseFont;
		private FontMetrics fm;
		
		private Rectangle[] pauseTextAreas;
		private Rectangle[] optionsTextAreas;
		
		public MyCanvas(){
			setBackground(Color.BLACK);
			setIgnoreRepaint(true);
			addMouseListener(this);
			
			//Initialize font
			pauseFont = new Font("Arial", Font.BOLD, 40);
			
			pauseTextAreas = new Rectangle[5];
			optionsTextAreas = new Rectangle[7];
		}
		private void initClickAreas(){
			fm = getBufferStrategy().getDrawGraphics().getFontMetrics(pauseFont);
			int lineHeight = fm.getAscent()+fm.getDescent();
			//Pause Menu
			pauseTextAreas[0] = new Rectangle((getWidth()-fm.stringWidth("Continue"))/2, 250-lineHeight/2, fm.stringWidth("Continue"),lineHeight);
			pauseTextAreas[1] = new Rectangle((getWidth()-fm.stringWidth("Save"))/2, 350-lineHeight/2, fm.stringWidth("Save"),lineHeight);
			pauseTextAreas[2] = new Rectangle((getWidth()-fm.stringWidth("Open"))/2, 450-lineHeight/2, fm.stringWidth("Open"),lineHeight);
			pauseTextAreas[3] = new Rectangle((getWidth()-fm.stringWidth("Options"))/2, 550-lineHeight/2, fm.stringWidth("Options"),lineHeight);
			pauseTextAreas[4] = new Rectangle((getWidth()-fm.stringWidth("Quit"))/2, 650-lineHeight/2, fm.stringWidth("Quit"),lineHeight);
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
			g.setFont(pauseFont);
			if (fm == null){
				initClickAreas();
			}
			if (inOptions){
				int lineHeight = fm.getAscent()+fm.getDescent();
				g.drawString("OPTIONS", (getWidth()-fm.stringWidth("OPTIONS"))/2, 100);
				String current;
				
				//Use Gravity Object
				if (gravExists)
					current = "Use Gravity Object: Yes";
				else
					current = "Use Gravity Object: No";
				optionsTextAreas[0] = new Rectangle((getWidth()-fm.stringWidth(current))/2, 200-lineHeight/2, fm.stringWidth(current),lineHeight);
				if (optionsTextAreas[0].contains(MouseInfo.getPointerInfo().getLocation()))
					g.setColor(Color.RED);
				else
					g.setColor(Color.WHITE);
				g.drawString(current, (getWidth()-fm.stringWidth(current))/2, 200);
				
				//Gravity Object Visible
				if (gravVisible)
					current = "Gravity Object Visible: Yes";
				else
					current = "Gravity Object Visible: No";
				optionsTextAreas[1] = new Rectangle((getWidth()-fm.stringWidth(current))/2, 275-lineHeight/2, fm.stringWidth(current),lineHeight);
				if (!gravExists)
					g.setColor(Color.GRAY);
				else if (optionsTextAreas[1].contains(MouseInfo.getPointerInfo().getLocation()))
					g.setColor(Color.RED);
				else
					g.setColor(Color.WHITE);
				g.drawString(current, (getWidth()-fm.stringWidth(current))/2, 275);
				
				//Unlimited Lives
				if (unlimitedLives)
					current = "Unlimited Lives: Yes";
				else
					current = "Unlimited Lives: No";
				optionsTextAreas[2] = new Rectangle((getWidth()-fm.stringWidth(current))/2, 350-lineHeight/2, fm.stringWidth(current),lineHeight);
				if (optionsTextAreas[2].contains(MouseInfo.getPointerInfo().getLocation()))
					g.setColor(Color.RED);
				else
					g.setColor(Color.WHITE);
				g.drawString(current, (getWidth()-fm.stringWidth(current))/2, 350);
				
				//Number of Asteroids
				current = "Number of Asteroids: "+numAsteroids;
				optionsTextAreas[3] = new Rectangle((getWidth()-fm.stringWidth(current))/2, 425-lineHeight/2, fm.stringWidth(current),lineHeight);
				if (optionsTextAreas[3].contains(MouseInfo.getPointerInfo().getLocation()))
					g.setColor(Color.RED);
				else
					g.setColor(Color.WHITE);
				g.drawString(current, (getWidth()-fm.stringWidth(current))/2, 425);
				
				//Reset High Score Holder
				current = "Reset High Scores";
				optionsTextAreas[4] = new Rectangle((getWidth()-fm.stringWidth(current))/2, 500-lineHeight/2, fm.stringWidth(current),lineHeight);
				if (optionsTextAreas[4].contains(MouseInfo.getPointerInfo().getLocation()))
					g.setColor(Color.RED);
				else
					g.setColor(Color.WHITE);
				g.drawString(current, (getWidth()-fm.stringWidth(current))/2, 500);
				
				//Starting Level
				current = "Starting Level";
				optionsTextAreas[5] = new Rectangle((getWidth()-fm.stringWidth(current))/2, 575-lineHeight/2, fm.stringWidth(current),lineHeight);
				if (optionsTextAreas[5].contains(MouseInfo.getPointerInfo().getLocation()))
					g.setColor(Color.RED);
				else
					g.setColor(Color.WHITE);
				g.drawString(current, (getWidth()-fm.stringWidth(current))/2, 575);
				
				//Done
				current = "Done";
				optionsTextAreas[6] = new Rectangle((getWidth()-fm.stringWidth(current))/2, 650-lineHeight/2, fm.stringWidth(current),lineHeight);
				if (optionsTextAreas[6].contains(MouseInfo.getPointerInfo().getLocation()))
					g.setColor(Color.RED);
				else
					g.setColor(Color.WHITE);
				g.drawString(current, (getWidth()-fm.stringWidth(current))/2, 650);
			}else{
				g.drawString("PAUSED", (getWidth()-fm.stringWidth("PAUSED"))/2, 100);
				if (pauseTextAreas[0].contains(MouseInfo.getPointerInfo().getLocation()))
					g.setColor(Color.RED);
				else
					g.setColor(Color.WHITE);
				g.drawString("Continue", (getWidth()-fm.stringWidth("Continue"))/2, 250);
				if (pauseTextAreas[1].contains(MouseInfo.getPointerInfo().getLocation()))
					g.setColor(Color.RED);
				else
					g.setColor(Color.WHITE);
				g.drawString("Save", (getWidth()-fm.stringWidth("Save"))/2, 350);
				if (pauseTextAreas[2].contains(MouseInfo.getPointerInfo().getLocation()))
					g.setColor(Color.RED);
				else
					g.setColor(Color.WHITE);
				g.drawString("Open", (getWidth()-fm.stringWidth("Open"))/2, 450);
				if (pauseTextAreas[3].contains(MouseInfo.getPointerInfo().getLocation()))
					g.setColor(Color.RED);
				else
					g.setColor(Color.WHITE);
				g.drawString("Options", (getWidth()-fm.stringWidth("Options"))/2, 550);
				if (pauseTextAreas[4].contains(MouseInfo.getPointerInfo().getLocation()))
					g.setColor(Color.RED);
				else
					g.setColor(Color.WHITE);
				g.drawString("Quit", (getWidth()-fm.stringWidth("Quit"))/2, 650);
			}
		}
		
		public void mouseClicked(MouseEvent e){
			System.out.println("MOUSE CLICKED: ("+ e.getXOnScreen()+ ", "+e.getYOnScreen()+")");
			if (paused){
				System.out.println("Game is paused");
				if (inOptions){
					if (optionsTextAreas[0].contains(e.getLocationOnScreen())){//Grav Exists
						gravExists = !gravExists;
					}else if (optionsTextAreas[1].contains(e.getLocationOnScreen())){//Grav Visible
						gravVisible = !gravVisible;
					}else if (optionsTextAreas[2].contains(e.getLocationOnScreen())){//Unlimited Lives
						unlimitedLives = !unlimitedLives;
					}else if (optionsTextAreas[3].contains(e.getLocationOnScreen())){//Asteroid Number
						
					}else if (optionsTextAreas[4].contains(e.getLocationOnScreen())){//Reset high Score
						
					}else if (optionsTextAreas[5].contains(e.getLocationOnScreen())){//Start Level
						
					}else if (optionsTextAreas[6].contains(e.getLocationOnScreen())){//Done
						inOptions = false;
					}
				}else{
					System.out.println("In pause menu");
					if (pauseTextAreas[0].contains(e.getLocationOnScreen())){//Continue
						paused = false;
					}else if (pauseTextAreas[1].contains(e.getLocationOnScreen())){//Save
						System.out.println("SAVE CLICKED");
					}else if (pauseTextAreas[2].contains(e.getLocationOnScreen())){//Open
						System.out.println("OPEN CLICKED");
					}else if (pauseTextAreas[3].contains(e.getLocationOnScreen())){//Options
						inOptions = true;
					}else if (pauseTextAreas[4].contains(e.getLocationOnScreen())){//Quit
						System.exit(0);
					}
				}
			}
		}
		public void mouseEntered(MouseEvent e){
			
		}
		public void mouseExited(MouseEvent e){
			
		}
		public void mousePressed(MouseEvent e){
			
		}
		public void mouseReleased(MouseEvent e){
			
		}
	}
	
	
}

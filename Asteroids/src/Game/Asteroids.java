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
	//Game Objects
	private Player player1;
	private Player player2;
	
	//Game Status
	private int score1 = 0;
	private int score2 = 0;
	private static boolean paused = false;
	private boolean inOptions = false;
	private int level = 1;

	private KeyChecker keyChecker;
	
	private boolean inMainMenu = true;
	//options
	private boolean gravExists = false;
	private boolean gravVisible = false;
	private boolean unlimitedLives = false;
	private int numAsteroids = 1;
	private int startingLevel = 1;
	private boolean isSinglePlayer = true;
	
	private void gameInit(){
		if (isSinglePlayer){
			player1 = new Player(500,500, 0);
			player2 = null;
		}else{
			player1 = new Player(250, 500, 0);
			player2 = new Player(750, 500, 1);
		}
		keyChecker = KeyChecker.getInstance();
		paused = false;
		score1 = 0;
		score2 = 0;
		level = startingLevel;
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
			canvas.initClickAreas();
			window.revalidate();
			while (true){
				//Menu Loop
				while (inMainMenu){
					canvas.update();
				}
				//Game loop
				gameInit();
				while (!inMainMenu){
					if (paused){
					
					}else{
						player1.update();
						if (player2 != null)
							player2.update();
					}
					canvas.update();
					try{
						Thread.sleep(20);
					}catch (InterruptedException ex){}
				}
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
		private Font menuFont;
		private FontMetrics fm;
		
		private Rectangle[] mainMenuTextAreas;
		private Rectangle[] pauseTextAreas;
		private Rectangle[] optionsTextAreas;
		
		public MyCanvas(){
			setBackground(Color.BLACK);
			setIgnoreRepaint(true);
			addMouseListener(this);
			
			//Initialize font
			pauseFont = new Font("Arial", Font.BOLD, 40);
			menuFont = new Font("Arial", Font.BOLD, 40);
			
			mainMenuTextAreas = new Rectangle[5];
			pauseTextAreas = new Rectangle[5];
			optionsTextAreas = new Rectangle[10];
		}
		private void initClickAreas(){
			fm = getBufferStrategy().getDrawGraphics().getFontMetrics(pauseFont);
			int lineHeight = fm.getAscent()+fm.getDescent();
			//Main Menu
			mainMenuTextAreas[0] = new Rectangle((getWidth()-fm.stringWidth("Play"))/2, 250-lineHeight/2, fm.stringWidth("Play"), lineHeight);
			mainMenuTextAreas[1] = new Rectangle((getWidth()-fm.stringWidth("Load"))/2, 350-lineHeight/2, fm.stringWidth("Load"), lineHeight);
			mainMenuTextAreas[2] = new Rectangle((getWidth()-fm.stringWidth("Options"))/2, 450-lineHeight/2, fm.stringWidth("Options"), lineHeight);
			mainMenuTextAreas[3] = new Rectangle((getWidth()-fm.stringWidth("High Scores"))/2, 550-lineHeight/2, fm.stringWidth("High Scores"), lineHeight);
			mainMenuTextAreas[4] = new Rectangle((getWidth()-fm.stringWidth("Quit"))/2, 650-lineHeight/2, fm.stringWidth("Quit"), lineHeight);
			
			//Pause Menu
			pauseTextAreas[0] = new Rectangle((getWidth()-fm.stringWidth("Continue"))/2, 250-lineHeight/2, fm.stringWidth("Continue"),lineHeight);
			pauseTextAreas[1] = new Rectangle((getWidth()-fm.stringWidth("Save"))/2, 350-lineHeight/2, fm.stringWidth("Save"),lineHeight);
			pauseTextAreas[2] = new Rectangle((getWidth()-fm.stringWidth("Open"))/2, 450-lineHeight/2, fm.stringWidth("Open"),lineHeight);
			pauseTextAreas[3] = new Rectangle((getWidth()-fm.stringWidth("Options"))/2, 550-lineHeight/2, fm.stringWidth("Options"),lineHeight);
			pauseTextAreas[4] = new Rectangle((getWidth()-fm.stringWidth("Exit Game"))/2, 650-lineHeight/2, fm.stringWidth("Exit Game"),lineHeight);
		}
		
		public void update(){
			Graphics2D g = (Graphics2D)getBufferStrategy().getDrawGraphics();
			
			//Clear screen
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getWidth(), getHeight());
			if (inMainMenu)
				paintMainMenu(g);
			else{
				paintItems(g);
				if (paused)
					paintPauseScreen(g);
			}
			getBufferStrategy().show();
			Toolkit.getDefaultToolkit().sync();
			
			
			g.dispose();
		}
		
		public void paintItems(Graphics2D g){
			if (player1 == null)
				return;
			if (gravExists && gravVisible)
				drawGravObject(g);
			player1.paint(g, getWidth(), getHeight());
			if (player2 != null)
				player2.paint(g,  getWidth(), getHeight());
			g.setFont(new Font("Arial",Font.PLAIN,30));
			g.setColor(Color.WHITE);
			g.drawString(""+score1, 40,40);
			if (player2 != null){
				g.setColor(Color.BLUE);
				g.drawString(""+score2, getWidth()-fm.stringWidth(""+score2)-40, 40);
			}
		}
		private void drawGravObject(Graphics2D g){
			g.setColor(new Color(50,50,50));
			g.fillOval(getWidth()/2-70, getHeight()/2-62, 140, 124);
			g.setColor(new Color(100,100,100));
			g.fillOval(getWidth()/2-65, getHeight()/2-57, 130, 114);
			g.setColor(new Color(150,150,150));
			g.fillOval(getWidth()/2-60, getHeight()/2-52, 120, 104);
			g.setColor(new Color(200,200,200));
			g.fillOval(getWidth()/2-55, getHeight()/2-47, 110, 94);
			g.setColor(new Color(250,250,250));
			g.fillOval(getWidth()/2-50, getHeight()/2-42, 100, 84);
			g.setColor(new Color(200,200,200));
			g.fillOval(getWidth()/2-45, getHeight()/2-37, 90, 74);
			g.setColor(new Color(150,150,150));
			g.fillOval(getWidth()/2-40, getHeight()/2-32, 80, 64);
			g.setColor(new Color(100,100,100));
			g.fillOval(getWidth()/2-35, getHeight()/2-27, 70, 54);
			g.setColor(new Color(50,50,50));
			g.fillOval(getWidth()/2-30, getHeight()/2-22, 60, 44);
			g.setColor(Color.BLACK);
			g.fillOval(getWidth()/2-25, getHeight()/2-17, 50, 34);
			
			
			
			
			
		}
		private void paintPauseScreen(Graphics2D g){
			g.setFont(pauseFont);
			if (fm == null){
				initClickAreas();
			}
			if (inOptions)
				drawOptions(g);
			else
				drawPauseScreen(g);
		}
		private void paintMainMenu(Graphics2D g){
			g.setFont(menuFont);
			if (inOptions){
				drawOptions(g);
			}else{
				drawMenu(g);
			}
		}
		private void drawMenu(Graphics2D g){
			g.setColor(Color.WHITE);
			g.drawString("ASTEROIDS", (getWidth()-fm.stringWidth("ASTEROIDS"))/2, 100);
			if (mainMenuTextAreas[0].contains(MouseInfo.getPointerInfo().getLocation()))
				g.setColor(Color.RED);
			else
				g.setColor(Color.WHITE);
			g.drawString("Play", (getWidth()-fm.stringWidth("Play"))/2, 250);
			if (mainMenuTextAreas[1].contains(MouseInfo.getPointerInfo().getLocation()))
				g.setColor(Color.RED);
			else
				g.setColor(Color.WHITE);
			g.drawString("Load", (getWidth()-fm.stringWidth("Load"))/2, 350);
			if (mainMenuTextAreas[2].contains(MouseInfo.getPointerInfo().getLocation()))
				g.setColor(Color.RED);
			else
				g.setColor(Color.WHITE);
			g.drawString("Options", (getWidth()-fm.stringWidth("Options"))/2, 450);
			if (mainMenuTextAreas[3].contains(MouseInfo.getPointerInfo().getLocation()))
				g.setColor(Color.RED);
			else
				g.setColor(Color.WHITE);
			g.drawString("High Scores", (getWidth()-fm.stringWidth("High Scores"))/2, 550);
			if (mainMenuTextAreas[4].contains(MouseInfo.getPointerInfo().getLocation()))
				g.setColor(Color.RED);
			else
				g.setColor(Color.WHITE);
			g.drawString("Quit", (getWidth()-fm.stringWidth("Quit"))/2, 650);
		}
		private void drawPauseScreen(Graphics2D g){
			g.setColor(Color.WHITE);
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
			g.drawString("Exit Game", (getWidth()-fm.stringWidth("Exit Game"))/2, 650);
		}
		private void drawOptions(Graphics2D g){
			g.setColor(Color.WHITE);
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
			g.setColor(Color.WHITE);
			g.drawString(current, (getWidth()-fm.stringWidth(current))/2, 425);
			
			//Number of Asteroids +
			optionsTextAreas[3] = new Rectangle((getWidth()+fm.stringWidth(current))/2+20, 425-(fm.getAscent()-fm.getDescent())*5/4, fm.stringWidth("+"),lineHeight/2);
			if (optionsTextAreas[3].contains(MouseInfo.getPointerInfo().getLocation()))
				g.setColor(Color.RED);
			else
				g.setColor(Color.WHITE);
			g.drawString("+", (getWidth()+fm.stringWidth(current))/2+20, 425-(fm.getAscent()+fm.getDescent())/4);
			
			//Number of Asteroids -
			optionsTextAreas[4] = new Rectangle((getWidth()+fm.stringWidth(current))/2+20+(fm.stringWidth("+")-fm.stringWidth("-"))/4, 425-5, fm.stringWidth("-"),lineHeight/4);
			if (optionsTextAreas[4].contains(MouseInfo.getPointerInfo().getLocation()))
				g.setColor(Color.RED);
			else
				g.setColor(Color.WHITE);
			g.drawString("-", (getWidth()+fm.stringWidth(current))/2+20+(fm.stringWidth("+")-fm.stringWidth("-"))/4, 425+(fm.getAscent()+fm.getDescent())/4);
			
			//Reset High Score Holder
			current = "Reset High Scores";
			optionsTextAreas[5] = new Rectangle((getWidth()-fm.stringWidth(current))/2, 500-lineHeight/2, fm.stringWidth(current),lineHeight);
			if (optionsTextAreas[5].contains(MouseInfo.getPointerInfo().getLocation()))
				g.setColor(Color.RED);
			else
				g.setColor(Color.WHITE);
			g.drawString(current, (getWidth()-fm.stringWidth(current))/2, 500);
			
			//Starting Level
			g.setColor(Color.WHITE);
			current = "Starting Level: "+startingLevel;
			g.drawString(current, (getWidth()-fm.stringWidth(current))/2, 575);
			
			//Starting Level +
			optionsTextAreas[6] = new Rectangle((getWidth()+fm.stringWidth(current))/2+20, 575-(fm.getAscent()-fm.getDescent())*5/4, fm.stringWidth("+"),lineHeight/2);
			if (optionsTextAreas[6].contains(MouseInfo.getPointerInfo().getLocation()))
				g.setColor(Color.RED);
			else
				g.setColor(Color.WHITE);
			g.drawString("+", (getWidth()+fm.stringWidth(current))/2+20, 575-(fm.getAscent()+fm.getDescent())/4);
			
			//Starting Level -
			optionsTextAreas[7] = new Rectangle((getWidth()+fm.stringWidth(current))/2+20+(fm.stringWidth("+")-fm.stringWidth("-"))/4, 575-5, fm.stringWidth("-"),lineHeight/4);
			if (optionsTextAreas[7].contains(MouseInfo.getPointerInfo().getLocation()))
				g.setColor(Color.RED);
			else
				g.setColor(Color.WHITE);
			g.drawString("-", (getWidth()+fm.stringWidth(current))/2+20+(fm.stringWidth("+")-fm.stringWidth("-"))/4, 575+(fm.getAscent()+fm.getDescent())/4);
			//Mode
			if (isSinglePlayer)
				current = "Mode: Single Player";
			else
				current = "Mode: Mulitplayer";
			optionsTextAreas[8] = new Rectangle((getWidth()-fm.stringWidth(current))/2, 650-lineHeight/2, fm.stringWidth(current),lineHeight);
			if (optionsTextAreas[8].contains(MouseInfo.getPointerInfo().getLocation()))
				g.setColor(Color.RED);
			else
				g.setColor(Color.WHITE);
			g.drawString(current, (getWidth()-fm.stringWidth(current))/2, 650);
			//Done
			current = "Done";
			optionsTextAreas[9] = new Rectangle((getWidth()-fm.stringWidth(current))/2, 725-lineHeight/2, fm.stringWidth(current),lineHeight);
			if (optionsTextAreas[9].contains(MouseInfo.getPointerInfo().getLocation()))
				g.setColor(Color.RED);
			else
				g.setColor(Color.WHITE);
			g.drawString(current, (getWidth()-fm.stringWidth(current))/2, 725);
		}
		public void mouseClicked(MouseEvent e){
			if (inOptions){
				if (optionsTextAreas[0].contains(e.getLocationOnScreen())){//Grav Exists
					gravExists = !gravExists;
				}else if (optionsTextAreas[1].contains(e.getLocationOnScreen()) && gravExists){//Grav Visible
					gravVisible = !gravVisible;
				}else if (optionsTextAreas[2].contains(e.getLocationOnScreen())){//Unlimited Lives
					unlimitedLives = !unlimitedLives;
				}else if (optionsTextAreas[3].contains(e.getLocationOnScreen())){//Asteroid Number +
					numAsteroids++;
				}else if (optionsTextAreas[4].contains(e.getLocationOnScreen())){//Asteroid Number -
					numAsteroids = numAsteroids == 1 ? 1 : numAsteroids-1;
				}else if (optionsTextAreas[5].contains(e.getLocationOnScreen())){//Reset high Score
					
				}else if (optionsTextAreas[6].contains(e.getLocationOnScreen())){//Start Level +
					startingLevel++;
				}else if (optionsTextAreas[7].contains(e.getLocationOnScreen())){//Start Level -
					startingLevel = startingLevel == 1 ? 1 : startingLevel-1;
				}else if (optionsTextAreas[8].contains(e.getLocationOnScreen())){//Mode
					isSinglePlayer = !isSinglePlayer;
				}else if (optionsTextAreas[9].contains(e.getLocationOnScreen())){//Done
					inOptions = false;
				}
			}else if (inMainMenu){
				if (mainMenuTextAreas[0].contains(e.getLocationOnScreen())){//Play
					inMainMenu = false;
				}else if (mainMenuTextAreas[1].contains(e.getLocationOnScreen())){//Load
					
				}else if (mainMenuTextAreas[2].contains(e.getLocationOnScreen())){//Options
					inOptions = true;
				}else if (mainMenuTextAreas[3].contains(e.getLocationOnScreen())){//High Scores
					
				}else if (mainMenuTextAreas[4].contains(e.getLocationOnScreen())){//Quit
					System.exit(0);
				}
			}else if(paused){
				if (pauseTextAreas[0].contains(e.getLocationOnScreen())){//Continue
					paused = false;
				}else if (pauseTextAreas[1].contains(e.getLocationOnScreen())){//Save
					System.out.println("SAVE CLICKED");
				}else if (pauseTextAreas[2].contains(e.getLocationOnScreen())){//Open
					System.out.println("OPEN CLICKED");
				}else if (pauseTextAreas[3].contains(e.getLocationOnScreen())){//Options
					inOptions = true;
				}else if (pauseTextAreas[4].contains(e.getLocationOnScreen())){//Exit Game
					inMainMenu = true;
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

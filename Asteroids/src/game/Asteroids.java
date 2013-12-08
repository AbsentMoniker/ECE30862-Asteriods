package game;

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
import java.awt.geom.GeneralPath;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import views.AlienShip;
import views.Asteroid;
import views.Bullet;
import views.Player;
import views.RogueSpaceship;
import views.BaseView;
import models.AlienShipModel;
import models.KeyChecker;
import models.MovingObjectModel;
import models.PlayerModel;
import game.SoundUtil;


public class Asteroids{
	public static int screenWidth;
	public static int screenHeight;
	public static final String HIGHSCOREFILE = "scores.config";
	//Game Objects
	private Player player1;
	private Player player2;
	private ArrayList<Bullet> bullets;
	private ArrayList<Asteroid> asteroids;
	private RogueSpaceship rogueSpaceship;
	private AlienShip alienShip;
	private SoundUtil soundUtil;
	
	//Game Status
	private int score1 = 0;
	private int score2 = 0;
	private static boolean paused = false;
	private boolean p1Alive = true;
	private boolean p2Alive = true;

	private int level = 1;

	private KeyChecker keyChecker;
	
	private boolean inMainMenu = true;
	private boolean gameLoaded = false;
	private boolean inOptions = false;
	private boolean inHighScores = false;
	
	// tuning
	private final int asteroidSpeedScale = 20;
	
	//options
	private boolean gravExists = false;
	private boolean gravVisible = false;
	private boolean unlimitedLives = false;
	private int numAsteroids = 1;
	private int startingLevel = 1;
	private boolean isSinglePlayer = true;
	
	private ArrayList<Integer> highScores;
	private ArrayList<String> highScoreNames;
	
	private void gameInit(){
		score1 = 0;
		score2 = 0;
		level = startingLevel;
		if (unlimitedLives){
			if (isSinglePlayer){
				player1 = new Player(screenWidth/2,screenHeight/2, 0,0,0,0,-1,0);
				player2 = null;
			}else{
				player1 = new Player(screenWidth/4, screenHeight/2, 0,0,0,0,-1,0);
				player2 = new Player(3*screenWidth/4, screenHeight/2, 0,0,0,0,-1,1);
			}
		}else{
			if (isSinglePlayer){
				player1 = new Player(screenWidth/2,screenHeight/2, 0,0,0,0,3,0);
				player2 = null;
			}else{
				player1 = new Player(screenWidth/4, screenHeight/2, 0,0,0,0,3,0);
				player2 = new Player(3*screenWidth/4, screenHeight/2, 0,0,0,0,3,1);
			}
		}
		//rogueSpaceship = new RogueSpaceship(400,400,0,0,0,0);
		//alienShip = new AlienShip(600,600,0,0,0,0);
		initAsteroids();
		bullets = new ArrayList<Bullet>();
		paused = false;	
		
		soundUtil = new SoundUtil();
	}
	private boolean gameInitFromFile(File fin){
		DataInputStream file;
		try{
			file = new DataInputStream(new BufferedInputStream(new FileInputStream(fin)));
		}catch (FileNotFoundException ex){
			return false;
		}
		
		try{
			asteroids = new ArrayList<Asteroid>();
			bullets = new ArrayList<Bullet>();
			
			//write status
			score1 = file.readInt();
			score2 = file.readInt();
			level = file.readInt();
			
			//write options
			gravExists = file.readBoolean();
			gravVisible = file.readBoolean();
			unlimitedLives = file.readBoolean();
			numAsteroids = file.readInt();
			startingLevel = file.readInt();
			isSinglePlayer = file.readBoolean();
			
			//write players
			int newX = file.readInt();
			int newY = file.readInt();
			double newAngle = file.readDouble();
			double newVX = file.readDouble();
			double newVY = file.readDouble();
			double newVAngle = file.readDouble();
			int newLives = file.readInt();
			int status = file.readInt();
			player1 = new Player(newX,newY,newAngle,newVX,newVY,newVAngle, newLives,0);
			if (status == 0){
				player2 = null;
			}else{
				newX = file.readInt();
				newY = file.readInt();
				newAngle = file.readDouble();
				newVX = file.readDouble();
				newVY = file.readDouble();
				newVAngle = file.readDouble();
				newLives = file.readInt();
				player2 = new Player(newX,newY,newAngle,newVX,newVY,newVAngle,newLives,1);
			}
			status = file.readInt();
			for (int i = 0; i < status; i++){
				newX = file.readInt();
				newY = file.readInt();
				newAngle = file.readDouble();
				newVX = file.readDouble();
				newVY = file.readDouble();
				newVAngle = file.readDouble();
				Color newColor = new Color(file.readInt());
				int newShooterID = file.readInt();
				Bullet newBullet = new Bullet(newX,newY,newAngle,newVX,newVY,newVAngle,newColor, newShooterID);
				newBullet.setDist(file.readDouble());
				bullets.add(newBullet);
			}
			status = file.readInt();
			for (int i = 0; i < status; i++){
				newX = file.readInt();
				newY = file.readInt();
				newAngle = file.readDouble();
				newVX = file.readDouble();
				newVY = file.readDouble();
				newVAngle = file.readDouble();
				int newType = file.readInt();
				asteroids.add(new Asteroid(newX, newY, newAngle, newVX, newVY, newVAngle, newType));
			}
			status = file.readInt();
			if (status == 0){
				rogueSpaceship = null;
			}else{
				newX = file.readInt();
				newY = file.readInt();
				newAngle = file.readDouble();
				newVX = file.readDouble();
				newVY = file.readDouble();
				newVAngle = file.readDouble();
				rogueSpaceship = new RogueSpaceship(newX,newY,newAngle,newVX,newVY,newVAngle);
			}
			status = file.readInt();
			if (status == 0){
				alienShip = null;
			}else{
				newX = file.readInt();
				newY = file.readInt();
				newAngle = file.readInt();
				newVX = file.readDouble();
				newVY = file.readDouble();
				newVAngle = file.readDouble();
				newLives = file.readInt();
				alienShip = new AlienShip(newX,newY,newAngle,newVX,newVY,newVAngle);
			}
			file.close();
		}catch (IOException ex){
			return false;
		}
		return true;
	}
	private boolean saveGame(File fout){
		DataOutputStream file;
		try{
			file = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fout)));
		}catch(FileNotFoundException ex){
			return false;
		}
		try{
			//write status
			file.writeInt(score1);
			file.writeInt(score2);
			file.writeInt(level);
			
			//write options
			file.writeBoolean(gravExists);
			file.writeBoolean(gravVisible);
			file.writeBoolean(unlimitedLives);
			file.writeInt(numAsteroids);
			file.writeInt(startingLevel);
			file.writeBoolean(isSinglePlayer);
			
			//write players
			file.writeInt(player1.getX());
			file.writeInt(player1.getY());
			file.writeDouble(player1.getAngle());
			file.writeDouble(player1.getVX());
			file.writeDouble(player1.getVY());
			file.writeDouble(player1.getVAngle());
			file.writeInt(player1.getLives());
			if (player2 == null){
				file.writeInt(0);
			}else{
				file.writeInt(1);
				file.writeInt(player2.getX());
				file.writeInt(player2.getY());
				file.writeDouble(player2.getAngle());
				file.writeDouble(player2.getVX());
				file.writeDouble(player2.getVY());
				file.writeDouble(player2.getVAngle());
				file.writeInt(player2.getLives());
			}
			file.writeInt(bullets.size());
			for (Bullet bullet:bullets){
				file.writeInt(bullet.getX());
				file.writeInt(bullet.getY());
				file.writeDouble(bullet.getAngle());
				file.writeDouble(bullet.getVX());
				file.writeDouble(bullet.getVY());
				file.writeDouble(bullet.getVAngle());
				file.writeInt(bullet.getColor().getRGB());
				file.writeInt(bullet.getShooter());
				file.writeDouble(bullet.getDist());
			}
			file.writeInt(asteroids.size());
			for (Asteroid asteroid:asteroids){
				file.writeInt(asteroid.getX());
				file.writeInt(asteroid.getY());
				file.writeDouble(asteroid.getAngle());
				file.writeDouble(asteroid.getVX());
				file.writeDouble(asteroid.getVY());
				file.writeDouble(asteroid.getVAngle());
				file.writeInt(asteroid.getType());
			}
			if (rogueSpaceship == null){
				file.writeInt(0);
			}else{
				file.writeInt(1);
				file.writeInt(rogueSpaceship.getX());
				file.writeInt(rogueSpaceship.getY());
				file.writeDouble(rogueSpaceship.getAngle());
				file.writeDouble(rogueSpaceship.getVX());
				file.writeDouble(rogueSpaceship.getVY());
				file.writeDouble(rogueSpaceship.getVAngle());
			}
			if (alienShip == null){
				file.writeInt(0);
			}else{
				file.writeInt(1);
				file.writeInt(alienShip.getX());
				file.writeInt(alienShip.getY());
				file.writeDouble(alienShip.getAngle());
				file.writeDouble(alienShip.getVX());
				file.writeDouble(alienShip.getVY());
				file.writeDouble(alienShip.getVAngle());
				file.writeInt(alienShip.getLives());
			}
			
			file.close();
		}catch (IOException ex){
			return false;
		}
		return true;
	}
	public void exit(){
		saveHighScores();
		System.exit(0);
	}
	public void initAsteroids(){
		asteroids = new ArrayList<Asteroid>();
		for (int i = 0; i < level + 2; i++){
			int randX, randY;
			double distTo1 = 0, distTo2 = 0;
			if (player2 != null){
				do {
					randX = (int)(Math.random()*screenWidth);
					randY = (int)(Math.random()*screenHeight);
					distTo1 = Math.sqrt( Math.pow(randX - player1.getX(), 2) + Math.pow(randY - player1.getY(), 2) );
					distTo2 = Math.sqrt( Math.pow(randX - player2.getX(), 2) + Math.pow(randY - player2.getY(), 2) );
				} while (distTo1 < 75 && distTo2 < 75);
				
				asteroids.add(new Asteroid(randX,randY,0,
						(Math.random() - .5)*asteroidSpeedScale*level,
						(Math.random() - .5)*asteroidSpeedScale*level,
						Math.random() - 0.5,0));
			}else{
				do{
					randX = (int)(Math.random()*screenWidth);
				}while ((randX > player1.getX()-100)&&(randX < player1.getX()+100));
				do{
					randY = (int)(Math.random()*screenHeight);
				}while ((randY > player1.getY()-100)&&(randY < player1.getY()+100));
				do {
					randX = (int)(Math.random()*screenWidth);
					randY = (int)(Math.random()*screenHeight);
					distTo1 = Math.sqrt( Math.pow(randX - player1.getX(), 2) + Math.pow(randY - player1.getY(), 2) );
				} while (distTo1 < 75);
				
				asteroids.add(new Asteroid(randX,randY,0,
						(Math.random() - .5)*asteroidSpeedScale*level,
						(Math.random() - .5)*asteroidSpeedScale*level,
						Math.random() - 0.5,0));
			}
		}
	}
	public Asteroids(){
		keyChecker = KeyChecker.createInstance(this);
	}
	public static void main(String [] argv){
		new Asteroids().start();
	}
	public void loadHighScores(){
		highScores = new ArrayList<Integer>();
		highScoreNames = new ArrayList<String>();
		DataInputStream file;
		try{
			file = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(HIGHSCOREFILE))));
		}catch (FileNotFoundException ex){
			return;
		}
		
		try{
			int numScores = file.readInt();
			for (int i = 0; i < numScores; i++){
				highScoreNames.add(file.readUTF());
				highScores.add(file.readInt());
			}
			file.close();
		}catch (IOException ex){
			
		}
	}
	public void saveHighScores(){
		DataOutputStream file;
		try{
			file = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File(HIGHSCOREFILE),false)));
		}catch (FileNotFoundException ex){
			return;
		}
		try{
			file.writeInt(highScores.size());
			for (int i = 0; i < highScores.size(); i++){
				file.writeUTF(highScoreNames.get(i));
				file.writeInt(highScores.get(i));
			}
			file.close();
		}catch (IOException ex){
			
		}
	}
	public void addHighScore(){
		int player;
		int newScore;
		if (score1 >= score2){
			player = 1;
			newScore = score1;
		}else{
			player = 2;
			newScore = score2;
		}
		if ((highScores.size() == 10)&&(newScore < highScores.get(highScores.size()-1)))
			return;
		JPanel input = new JPanel();
		input.add(new JLabel("HIGH SCORE FOR PLAYER "+ player+"!!!"));
		input.add(new JLabel("Enter your name (only 5 letters will be used)"));
		JTextField nameEntry = new JTextField(5);
		input.add(nameEntry);
		String [] button = {"OK"};
		int result = JOptionPane.showOptionDialog(null,input,
				"HIGH SCORE", JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,null, button, button[0]);
		if (result == 0){
			String name = nameEntry.getText();
			if (name.length() > 5)
				name = name.substring(0,5);
			
			//Determine location of new high score relative to current high scores
			boolean added = false;
			for (int i = 0; i < highScores.size(); i++){
				if (newScore > highScores.get(i)){
					highScores.add(i, newScore);
					highScoreNames.add(i, name);
					added = true;
					break;
				}
			}
			if (!added){
				highScores.add(newScore);
				highScoreNames.add(name);
			}
			if (highScores.size() > 10){
				highScores.remove(10);
				highScoreNames.remove(10);
			}
		}
		
		//Check other player
		player = (player == 1) ? 2 : 1;
		newScore = (newScore == score1) ? score2 : score1;
		if ((player == 2)&&(player2 == null))
			return;
		if ((newScore < highScores.get(highScores.size()-1))&&(highScores.size() == 10))
			return;
		input = new JPanel();
		input.add(new JLabel("HIGH SCORE FOR PLAYER "+ player+"!!!"));
		input.add(new JLabel("Enter your name (only 5 letters will be used)"));
		nameEntry = new JTextField(5);
		input.add(nameEntry);
		result = JOptionPane.showOptionDialog(null,input,
				"HIGH SCORE", JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,null, button, button[0]);
		if (result == 0){
			String name = nameEntry.getText();
			if (name.length() > 5)
				name = name.substring(0,5);
			
			//Determine location of new high score relative to current high scores
			boolean added = false;
			for (int i = 0; i < highScores.size(); i++){
				if (newScore > highScores.get(i)){
					highScores.add(i, newScore);
					highScoreNames.add(i, name);
					added = true;
					break;
				}
			}
			if (!added){
				highScores.add(newScore);
				highScoreNames.add(name);
			}
			if (highScores.size() > 10){
				highScores.remove(10);
				highScoreNames.remove(10);
			}
		}
	}
	
	public void shootAtShips(BaseView v) {
		AlienShipModel m = (AlienShipModel)v.model;
		double tx, ty;
		
		if (player2 != null) {
		if (Math.random() > 0.5) {
			tx = player1.getX();
			ty = player1.getY();
		} else {
			tx = player2.getX();
			ty = player2.getY();
		}
		} else {
			tx = player1.getX();
			ty = player1.getY();
		}
		
		
		double[] bPos = m.bulletPos(tx, ty);
		double[] bVel = m.bulletVel(tx, ty);
		Bullet newBullet = new Bullet((int)bPos[0], (int)bPos[1], 0, bVel[0], bVel[1], 0, Color.white, 0);
		bullets.add(newBullet);
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
			
			loadHighScores();
			while (true){
				// sound timer
				int soundCounter = 0;
				final int shipSoundSpacing = 20; 
				//Menu Loop
				while (inMainMenu){
					canvas.update();
				}
				//Game loop
				if (gameLoaded)
					gameLoaded = false;
				else
					gameInit();
				int alienTime = 0;
				int rogueTime = 0;
				int alienShootTime = 0;
				while (!inMainMenu){
					if (paused){
					
					}else{
						updateObjects();
						soundCounter++;
						if (soundCounter == shipSoundSpacing) {
							PlayerModel p1Model = (PlayerModel)player1.model;
							PlayerModel p2Model;
							if (p1Model.isAccelerating() == true) {
								soundUtil.playHighBeep();
							} else {
								if (player2 != null) {
									p2Model = (PlayerModel)player2.model;
									if (p2Model.isAccelerating() == true) {
										soundUtil.playHighBeep();
									} else {
										soundUtil.playLowBeep();
									}
								} else {
									soundUtil.playLowBeep();
								}
							}
							soundCounter = 0;
						}
					}
					canvas.update();
					try{
						Thread.sleep(15);
					}catch (InterruptedException ex){}
					alienTime += 15;
					alienShootTime += 15;
					rogueTime += 15;
					if (alienTime >= 100){
						alienTime = 0;
						if (alienShip == null){
							alienShip = new AlienShip(0, (int)(getScreenHeight()*Math.random()), 0.0, Math.random()*level*20+50, 0.0,0.0);
						}
					}
					if (alienShip != null) {
						if (alienShootTime == 1005) {
							shootAtShips(alienShip);
						} else if (alienShootTime == 1275) {
							shootAtShips(alienShip);
						} else if (alienShootTime == 1500) {
							shootAtShips(alienShip);
						} else if (alienShootTime == 1770) {
							shootAtShips(alienShip);
						} else if (alienShootTime >= 1995) {
							alienShootTime = 0;
						}
					}
					if (rogueTime >= 20000){
						rogueTime = 0;
						if (rogueSpaceship == null)
							rogueSpaceship = new RogueSpaceship((int)(getScreenWidth()*Math.random()), 0, 0.0, 0.0, Math.random()*level*20+50, 0.0);
					}
				}
			}
		}finally{
			screen.restoreScreen();
		}
	}
	
	public void updateObjects(){
		PlayerModel p1Model = (PlayerModel)player1.model;
		PlayerModel p2Model = null;
		p1Model.setGravExists(gravExists);
		if (player2 != null) {
			p2Model = (PlayerModel)player2.model;
			p2Model.setGravExists(gravExists);
		}
		
		player1.update();
		if (player2 != null)
			player2.update();
		for (Asteroid asteroid:asteroids)
			asteroid.update();
		if (rogueSpaceship != null)
			rogueSpaceship.update();
		if (alienShip != null)
			alienShip.update();
		
		for (Iterator<Bullet> it = bullets.iterator(); it.hasNext();) {
			Bullet b = it.next();
			b.update();
			if (b.shouldDeconstruct()) {
				it.remove();
			}
		}
		// collidedWithCenter keeps track of whether *any* collisions have happened
		// with asteroids, to prevent spawning player into an asteroid
		boolean collidedWithCenter = false;
		for (Asteroid asteroid:asteroids) {
			MovingObjectModel astModel = asteroid.model;
			if (astModel.collidesWith(p1Model)) {
				collidedWithCenter = true;
				if (p1Alive == true) {
					soundUtil.playExplosion();
					// TODO player 1 has hit an asteroid
					//System.out.println("P1 hit an asteroid");
					if (p1Model.decrementLives() == true) {
						// still lives remaining
						p1Alive = false;
						p1Model.resetToStart(!isSinglePlayer);
					} else {
						addHighScore();
						inMainMenu = true;
						return;
					}
				}
			}
			if (player2 != null) {
				if (astModel.collidesWith(p2Model)) {
					collidedWithCenter = true;
					if (p2Alive == true) {
						soundUtil.playExplosion();
						// player 2 has hit an asteroid
						if (p2Model.decrementLives() == true) {
							p2Alive = false;
							p2Model.resetToStart(!isSinglePlayer);
						} else {
							addHighScore();
							inMainMenu = true;
							return;
						}
					}
				}
			}
		}
		
		// respawn p1 and p2 only if they won't be insta-squished
		if (p1Alive == false && collidedWithCenter == false) {
			p1Alive = true;
		}
		if (player2 != null && p2Alive == false && collidedWithCenter == false) {
			p2Alive = true;
		}
		
		// spawn bullet for P1
		
		if (p1Model.firesBullet() && p1Alive == true) {
			Bullet newBullet;
			double[] bPos = p1Model.bulletPos();
			double[] bVel = p1Model.bulletVel();
			newBullet = new Bullet((int)bPos[0], (int)bPos[1], 0, bVel[0], bVel[1], 0, Color.white, 0);
			bullets.add(newBullet);
		}
		

		if (player2 != null) {
			// spawn bullet for P2
			if (p2Model.firesBullet() && p2Alive == true) {
				Bullet newBullet;
				double[] bPos = p2Model.bulletPos();
				double[] bVel = p2Model.bulletVel();
				newBullet = new Bullet((int)bPos[0], (int)bPos[1], 0, bVel[0], bVel[1], 0, Color.white, 1);
				bullets.add(newBullet);
			}
		}
		
		// check collisions between bullets and, uhm, everything
		for (Iterator<Bullet> bIt = bullets.iterator(); bIt.hasNext();) {
			// check against asteroids
			Bullet b = bIt.next();
			MovingObjectModel bModel = b.model;
			boolean collided = false;
			int astX = 0, astY = 0, astType = 0;
			for (Iterator<Asteroid> aIt = asteroids.iterator(); aIt.hasNext();) {
				Asteroid ast = aIt.next();
				MovingObjectModel astModel = ast.model;
				if (astModel.collidesWith(bModel)) {
					soundUtil.playExplosion();
					if (b.getShooter() == 0) {
						score1 += 5;
					} else if (b.getShooter() == 1) {
						score2 += 5;
					}
					astX = ast.getX();
					astY = ast.getY();
					astType = ast.getType();
					bIt.remove();
					aIt.remove();
					collided = true;
					break;
				}
			}
			if (collided) {
				if (astType == 0) {
					for (int i = 0; i < 3; i++) {
						int newAstX = astX + (int)(Math.random() * 100 - 50);
						int newAstY = astY + (int)(Math.random() * 100 - 50);
						asteroids.add(new Asteroid(newAstX,newAstY,0,
								(Math.random() - .5)*asteroidSpeedScale*level,
								(Math.random() - .5)*asteroidSpeedScale*level,
								Math.random(),1));
					}
				}
				if (asteroids.isEmpty()) {
					score1 += level * 100;
					score2 += level * 100;
					level++;
					initAsteroids();
				}
				continue;
			}
			
			if (p1Model.collidesWith(bModel) && p1Alive) {
				// TODO bullet hit p1
				if (b.getShooter() == 1) {
					score2 += 100;
				}
				
				if (p1Model.decrementLives() == true) {
					p1Alive = false;
					p1Model.resetToStart(!isSinglePlayer);
				} else {
					addHighScore();
					inMainMenu = true;
					return;
				}
				continue;
			}
			
			if (player2 != null) {
				if (p2Model.collidesWith(bModel) && p2Alive) {
					// TODO bullet hit p2
					if (b.getShooter() == 0) {
						score1 += 100;
					}
					
					if (p2Model.decrementLives() == true) {
						p2Alive = false;
						p2Model.resetToStart(!isSinglePlayer);
					} else {
						addHighScore();
						inMainMenu = true;
						return;
					}
					continue;
				}
			}
			
			if (alienShip != null) {
				MovingObjectModel alienModel = alienShip.model;
				if (alienModel.collidesWith(bModel)) {
					// TODO bullet hit alien ship
					if (b.getShooter() == 0) {
						score1 += 100;
					} else if (b.getShooter() == 1) {
						score2 += 100;
					}
					boolean alienAlive = alienModel.decrementLives();
					if (alienAlive == false) {
						alienShip = null;
					}
					bIt.remove();
					continue;
				}
			}
			
			if (rogueSpaceship != null) {
				MovingObjectModel rogueModel = rogueSpaceship.model;
				if (rogueModel.collidesWith(bModel)) {
					// TODO bullet hit rogue ship
					rogueSpaceship = null;
					bIt.remove();
					continue;
				}
			}
		}
	}
	
	public static boolean isPaused(){
		return paused;
	}
	public void togglePaused(){
		paused = !paused;
		if (paused) {
			pauseObjects();
			numAsteroids = asteroids.size();
		}
	}
	public void pauseObjects(){
		if (player1 != null)
			player1.pause();
		if (player2 != null)
			player2.pause();
		for (Asteroid asteroid: asteroids)
			asteroid.pause();
		for (Bullet bullet: bullets)
			bullet.pause();
		if (alienShip != null)
			alienShip.pause();
		if (rogueSpaceship != null)
			rogueSpaceship.pause();
	}
	public static int getScreenWidth(){
		return screenWidth;
	}
	public static int getScreenHeight(){
		return screenHeight;
	}
	public static int []getScreenDims(){
		int []dims = new int[2];
		dims[0] = screenWidth;
		dims[1] = screenHeight;
		return dims;
	}
	private class MyCanvas extends Canvas implements MouseListener{
		private Font pauseFont;
		private Font menuFont;
		private FontMetrics fm;
		
		private Rectangle[] mainMenuTextAreas;
		private Rectangle[] pauseTextAreas;
		private Rectangle[] optionsTextAreas;
		private Rectangle highScoreTextArea;
		
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
			screenWidth = getWidth();
			screenHeight = getHeight();
			
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
		
			highScoreTextArea = new Rectangle((getWidth()-fm.stringWidth("Done"))/2, getHeight()-fm.getHeight()-20-lineHeight/2,fm.stringWidth("Done"), lineHeight);
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
			if (p1Alive == true)
				player1.paint(g);
			if (player2 != null && p2Alive == true)
				player2.paint(g);
			if (rogueSpaceship != null)
				rogueSpaceship.paint(g);
			if (alienShip != null)
				alienShip.paint(g);
			for (Bullet bullet:bullets)
				bullet.paint(g);
			for (int i = 0; i < asteroids.size(); i++)
				asteroids.get(i).paint(g);
			g.setFont(new Font("Arial",Font.PLAIN,30));
			g.setColor(Color.WHITE);
			g.drawString(""+score1, 40,40);
			if (player2 != null){
				g.setColor(Color.BLUE);
				g.drawString(""+score2, getWidth()-fm.stringWidth(""+score2)-40, 40);
			}
			drawLives(g);
		}
		private void drawLives(Graphics2D g){
			g.setColor(Color.white);
			if (player1.getLives() == -1){ //Infinite Lives
				g.drawString(""+((char) 8734), 40, 80);
			}else{
				for (int i = 0; i < player1.getLives(); i++){
					GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);
					shape.moveTo(20+30*i+0, 60-7);
					shape.lineTo(20+30*i+7, 60+7);
					shape.lineTo(20+30*i+0, 60+2);
					shape.lineTo(20+30*i-7, 60+7);
					shape.closePath();
					g.draw(shape);
				}
			}
			if (player2 != null){
				g.setColor(Color.blue);
				if (player2.getLives() == -1){
					g.drawString(""+((char)8734),getWidth()-fm.stringWidth(""+score2)-40,80);
				}else{
					int width = getWidth()-fm.stringWidth(""+score2)-60;
					for (int i = 0; i < player2.getLives(); i++){
						GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);
						shape.moveTo(width+30*i+0, 60-7);
						shape.lineTo(width+30*i+7, 60+7);
						shape.lineTo(width+30*i+0, 60+2);
						shape.lineTo(width+30*i-7, 60+7);
						shape.closePath();
						g.draw(shape);
					}
				}
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
			if (inHighScores){
				drawHighScores(g);
			}else if (inOptions){
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
		private void drawHighScores(Graphics2D g){
			g.setColor(Color.white);
			g.drawString("HIGH SCORES", (getWidth()-fm.stringWidth("HIGH SCORES"))/2, 100);
			if (highScores.size() == 0){
				g.drawString("No Scores Added!", (getWidth()-fm.stringWidth("No Scores Added!"))/2, getHeight()/2);
			}else{
				for (int i = 0; i < highScores.size();i++){
					g.drawString(""+(i+1)+") "+highScoreNames.get(i), getWidth()/4, 200+60*i);
					g.drawString(""+highScores.get(i), getWidth()*3/4, 200+60*i);
				}
			}
			if (highScoreTextArea.contains(MouseInfo.getPointerInfo().getLocation()))
				g.setColor(Color.RED);
			g.drawString("Done", (getWidth()-fm.stringWidth("Done"))/2, getHeight()-fm.getHeight()-20);
		}
		public void mouseClicked(MouseEvent e){
			if (inHighScores){
				if (highScoreTextArea.contains(e.getLocationOnScreen()))
					inHighScores = false;
			}else if (inOptions){
				if (optionsTextAreas[0].contains(e.getLocationOnScreen())){//Grav Exists
					gravExists = !gravExists;
				}else if (optionsTextAreas[1].contains(e.getLocationOnScreen()) && gravExists){//Grav Visible
					gravVisible = !gravVisible;
				}else if (optionsTextAreas[2].contains(e.getLocationOnScreen())){//Unlimited Lives
					unlimitedLives = !unlimitedLives;
				}else if (optionsTextAreas[3].contains(e.getLocationOnScreen())){//Asteroid Number +
					numAsteroids++;
					int randX, randY;
					double distTo1 = 0, distTo2 = 0;
					if (player2 != null){
						do {
							randX = (int)(Math.random()*screenWidth);
							randY = (int)(Math.random()*screenHeight);
							distTo1 = Math.sqrt( Math.pow(randX - player1.getX(), 2) + Math.pow(randY - player1.getY(), 2) );
							distTo2 = Math.sqrt( Math.pow(randX - player2.getX(), 2) + Math.pow(randY - player2.getY(), 2) );
						} while (distTo1 < 75 && distTo2 < 75);
						
						asteroids.add(new Asteroid(randX,randY,0,
								(Math.random() - .5)*asteroidSpeedScale*level,
								(Math.random() - .5)*asteroidSpeedScale*level,
								Math.random() - 0.5,0));
					}else{
						do{
							randX = (int)(Math.random()*screenWidth);
						}while ((randX > player1.getX()-100)&&(randX < player1.getX()+100));
						do{
							randY = (int)(Math.random()*screenHeight);
						}while ((randY > player1.getY()-100)&&(randY < player1.getY()+100));
						do {
							randX = (int)(Math.random()*screenWidth);
							randY = (int)(Math.random()*screenHeight);
							distTo1 = Math.sqrt( Math.pow(randX - player1.getX(), 2) + Math.pow(randY - player1.getY(), 2) );
						} while (distTo1 < 75);
						
						asteroids.add(new Asteroid(randX,randY,0,
								(Math.random() - .5)*asteroidSpeedScale*level,
								(Math.random() - .5)*asteroidSpeedScale*level,
								Math.random() - 0.5,0));
					}
				}else if (optionsTextAreas[4].contains(e.getLocationOnScreen())){//Asteroid Number -
					if (numAsteroids > 1) {
						numAsteroids--;
						Asteroid lastAst = asteroids.get(asteroids.size() - 1);
						asteroids.remove(lastAst);
					}
				}else if (optionsTextAreas[5].contains(e.getLocationOnScreen())){//Reset high Score
					highScoreNames = new ArrayList<String>();
					highScores = new ArrayList<Integer>();
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
					p1Alive = true;
					p2Alive = true;
				}else if (mainMenuTextAreas[1].contains(e.getLocationOnScreen())){//Load
					final JFileChooser fc = new JFileChooser(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
					fc.setFileFilter(new FileNameExtensionFilter("Saved Games (.asteroids)", "asteroids"));
					int returnVal = fc.showOpenDialog(this);
					if (returnVal == JFileChooser.APPROVE_OPTION){
						File openFile = fc.getSelectedFile();
						boolean result = gameInitFromFile(openFile);
						if (result){
							gameLoaded = true;
							inMainMenu = false;
						}
					}
					
				}else if (mainMenuTextAreas[2].contains(e.getLocationOnScreen())){//Options
					inOptions = true;
				}else if (mainMenuTextAreas[3].contains(e.getLocationOnScreen())){//High Scores
					inHighScores = true;
				}else if (mainMenuTextAreas[4].contains(e.getLocationOnScreen())){//Quit
					exit();
				}
			}else if(paused){
				if (pauseTextAreas[0].contains(e.getLocationOnScreen())){//Continue
					paused = false;
				}else if (pauseTextAreas[1].contains(e.getLocationOnScreen())){//Save
					final JFileChooser fc = new JFileChooser(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
					fc.setFileFilter(new FileNameExtensionFilter("Saved Games (.asteroids)", "asteroids"));
					int returnVal = fc.showSaveDialog(this);
					if (returnVal == JFileChooser.APPROVE_OPTION){
						File saveFile = fc.getSelectedFile();
						if (!saveFile.getAbsolutePath().endsWith(".asteroids"))
							saveFile = new File(saveFile + ".asteroids");
						saveGame(saveFile);
					}
				}else if (pauseTextAreas[2].contains(e.getLocationOnScreen())){//Open
					final JFileChooser fc = new JFileChooser(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
					fc.setFileFilter(new FileNameExtensionFilter("Saved Games (.asteroids)", "asteroids"));
					int returnVal = fc.showOpenDialog(this);
					if (returnVal == JFileChooser.APPROVE_OPTION){
						File openFile = fc.getSelectedFile();
						gameInitFromFile(openFile);
					}
				}else if (pauseTextAreas[3].contains(e.getLocationOnScreen())){//Options
					inOptions = true;
				}else if (pauseTextAreas[4].contains(e.getLocationOnScreen())){//Exit Game
					addHighScore();
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

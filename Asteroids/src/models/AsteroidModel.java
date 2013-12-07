package models;

public class AsteroidModel extends MovingObjectModel implements Updatable {
	
	public AsteroidModel(int x, int y, int angle, double vx, double vy, double vAngle){
		super(x, y, angle, vx, vy, vAngle);
		acc[0] = 0;
		acc[1] = 0;
	}
	
	@Override
	public void update(){
		System.out.println("Asteroid: "+ pos[0] + ", "+ pos[1]);
		super.update();
		
	}
}

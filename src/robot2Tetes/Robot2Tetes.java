package robot2Tetes;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import interfaces.RobotInterface;

public class Robot2Tetes implements RobotInterface {
    
    private static Robot2Tetes instance;
    
    public static Robot2Tetes getInstance() {
        if (instance == null) {
            instance = new Robot2Tetes();
        }
        return instance;
    }
    
    private static final int DIST_COEFF = 25;
    private static final float ANGLE_COEFF = 1.030f;
    
    private DifferentialPilot pilot;
    private UltrasonicSensor usLeft;
    private UltrasonicSensor usRight;
    private LightSensor lsG;
	private LightSensor lsD;
	private int right;
	private int left;
    
    public Robot2Tetes() {
        pilot = new DifferentialPilot(56,160,Motor.B, Motor.C);
        usLeft = new UltrasonicSensor(SensorPort.S4);
        usRight = new UltrasonicSensor(SensorPort.S1);
        lsG = new LightSensor(SensorPort.S3);
        lsD = new LightSensor(SensorPort.S2);   
        pilot.setTravelSpeed(250);
        pilot.setRotateSpeed(250);
    }
    
    private void align(){
         int Left = usLeft.getDistance();
         int Right = usRight.getDistance();
    	 if (Left < 30 && Right < 30){
    		 float angle = (Left - Right);
	         if (Math.abs(angle) > 2 && angle < 6){
	    		  pilot.rotate(-angle);
	    	 }
	         if (angle < 6){
		    	 if (Left < 10 ||Right < 10){
		    		 int dist = Math.min(Left, Right);
		    		 pilot.travel((13 - dist)*3,false);
		    	 }
		    	 
		    	 if (Left > 15 || Right < 15){
		    		 float dist = Math.max(Left, Right);
		    		 pilot.travel((13 - dist)*3,false);
		    	 }
	         }
    	 }
    	 pilot.stop();
    }
   
    public void avanceUneCase() {
    	pilot.travel(-16 * DIST_COEFF,true);
    	left =0;
    	right=0;
		while(pilot.isMoving()){ 
				if (lsD.getLightValue() >= 47 && lsG.getLightValue() < 47 && right == 0){
					left++;
				}
				if(lsD.getLightValue() < 47 && lsG.getLightValue() >= 47 && left == 0 ){
					right++;
				}
		}
		if (left != 0){
			pilot.rotate(-left/10,false);
		}
		if (right != 0){
			pilot.rotate(right/10,false);
		}
		pilot.stop();
    	align();
    }
        
    @Override
    public void tourneAGauche() {
        pilot.rotate(90.0f * ANGLE_COEFF);
        align();
    }
    
    @Override
    public void tourneADroite() {
        pilot.rotate(-90.0f * ANGLE_COEFF);
        align();
    }
    
    @Override
    public void tourneDemiTour() {
        pilot.rotate(180.0f * ANGLE_COEFF);
    	align();
    }
    
    @Override
    public boolean regardeToutDroit() {
        int Left = usLeft.getDistance() ;
        int right = usRight.getDistance();
        return (Left < 30 && right < 30);
    }
    
    @Override
    public boolean regardeAGauche() {
    	boolean res;
        tourneAGauche();
        res = regardeToutDroit();
        tourneADroite();
        return res;
    }
    
    @Override
    public boolean regardeADroite() {
    	boolean res;
        tourneADroite();
        res = regardeToutDroit();
        tourneAGauche();
        return res;
    }

}

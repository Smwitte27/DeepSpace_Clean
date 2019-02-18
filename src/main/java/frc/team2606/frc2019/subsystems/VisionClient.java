package frc.team2606.frc2019.subsystems;

public class VisionClient extends Subsystem{
    
    // Program Status
    private boolean connected;
    private boolean running;

    // Target Status
    private boolean target;
    private double targetAngle;
    private double targetDistance;

    public VisionClient() {

    }

    public void startReceiving() {

    }

	@Override
	public boolean checkSystem() {
		return false;
	}

	@Override
	public void outputTelemetry() {
		
	}

	@Override
	public void stop() {
		
	} 
}
package frc.team2606.frc2019.subsystems;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import frc.team2606.frc2019.loops.Loop;

public class VisionClient extends Subsystem {
    
    // Program Status
    private boolean connected;
    private boolean running;
    private int rPIPort;

    // Target Status
    private boolean target;
    private double targetAngle;
    private double targetDistance;
    private long _lastTimestamp = 0;
    private byte[] _lastTrackingImage = null;

    // Socket Programming
    private ServerSocket _serverSocket = null;
    private Socket _clientSocket = null;
    private DataInputStream _in = null;
    private DataOutputStream _out = null;

    public VisionClient(int RPIPort) {
        rPIPort = RPIPort;
    }

    private final Loop loop = new Loop() {

        @Override
        public void onStart(double timestamp) {

        }

        @Override
        public void onLoop(double timestamp) {

        }

        @Override
        public void onStop(double timestamp) {

        }

    };

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
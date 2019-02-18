package frc.team2606.frc2019.subsystems;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import frc.team2606.frc2019.Constants;

public class VisionClient extends Subsystem implements Runnable {
    
    final boolean PRINT_DEBUG = false;

    // Program Status
    private boolean connected;
    private boolean running;

    // Target Status
    private boolean target;
    private double targetAngle;
    private double targetDistance;
    private long lastTimestamp = 0;
    private byte[] lastTrackingImage = null;

    // Socket Programming
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    public VisionClient() {
    }

    public boolean isConnected() {
        synchronized (this) {
            return connected;
        }
    }

    public boolean hasVisionTarget() {
        synchronized (this) {
            return target;
        }
    }

    public double targetAngle() {
        synchronized (this) {
            return targetAngle;
        }
    }

    public double targetDistance() {
        synchronized (this) {
            return targetDistance;
        }
    }

    public long lastTimestamp() {
        synchronized (this) {
            return lastTimestamp;
        }
    }

    public byte[] lastTrackingImage() {
        synchronized (this) {
            return lastTrackingImage;
        }
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

    public void run() {
        while (running) {
            try {

                boolean isConnected;
                synchronized (this) {
                    isConnected = connected;
                }

                if (!isConnected) {
                    listenForClient();
                }

                processMessages();

            } catch (Exception ex) {
                if (PRINT_DEBUG) {
                    System.out.println("Unhandled exception in VisionTrackingClient. " + ex.getMessage());
                }
            }
        }
    }

    protected void listenForClient() throws java.io.IOException {
        if (PRINT_DEBUG) {
            System.out.println(String.format("Initializing Socket on port %d.", Constants.RPI_PORT));
        }

        serverSocket = new ServerSocket(Constants.RPI_PORT, 50, InetAddress.getByAddress(new byte[] { 0x00, 0x00, 0x00, 0x00 }));
        serverSocket.setSoTimeout(10000);

        if (PRINT_DEBUG) {
            System.out.println("Waiting for connection.....");
        }

        clientSocket = serverSocket.accept();

        if (PRINT_DEBUG) {
            System.out.println("Client connected, Waiting for messages.....");
        }

        in = new DataInputStream(clientSocket.getInputStream());
        out = new DataOutputStream(clientSocket.getOutputStream());

        synchronized (this) {
            connected = true;
        }
    }

    protected void processMessages() throws java.io.IOException {

        try {
            while (running) {
                int command = in.readInt();

                switch (command) {
                case 1:
                    processMessageType1();
                    break;

                case 2:
                    processMessageType2();
                    break;

                case 3:
                    processMessageType3();
                    break;

                default:
                    if (PRINT_DEBUG) {
                        System.out.println(String.format("Unknown message type id %d", command));
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception occurred while reading. " + ex.getMessage());
        } finally {
            in.close();
            out.close();
            clientSocket.close();

            synchronized (this) {
                target = false;
                connected = false;
            }
        }
    }
    
    protected void processMessageType1() throws java.io.IOException {
        double angle = in.readDouble(); // 8 Bytes
        double distance = in.readDouble(); // 8 Bytes
        short hour = in.readShort(); // 2 bytes
        short min = in.readShort(); // 2 bytes
        short second = in.readShort(); // 2 bytes
        int ms = in.readInt(); // 4 Bytes

        // Calculate timestamp in ms.
        long currentTimestamp = hour * 60 * 60 * 1000 + min * 60 * 1000 + second * 1000 + ms;

        // Determine Time since last target
        if (lastTimestamp == 0) {
            lastTimestamp = currentTimestamp;
        }
        long timeSinceLastTargetInMs = currentTimestamp - lastTimestamp;

        synchronized (this) {
            lastTimestamp = currentTimestamp;
            targetAngle = angle;
            targetDistance = distance;
            target = true;
        }

        if (PRINT_DEBUG) {
            System.out.println(String.format("Vision Target: Angle %f, Distance %f, Timestamp %d, Last Target = %d ms",
                    angle, distance, currentTimestamp, timeSinceLastTargetInMs));
        }
    }

    protected void processMessageType2() {
        if (PRINT_DEBUG) {
            System.out.println("No Vision Target");
        }
        synchronized (this) {
            target = false;
        }
    }

    protected void processMessageType3() throws java.io.IOException {
        int imageSize = in.readInt(); // 4 Bytes
        synchronized (this) {
            lastTrackingImage = new byte[imageSize];
            in.read(lastTrackingImage);
        }

        if (PRINT_DEBUG) {
            System.out.println(String.format("Got Vision Image. %d Bytes", imageSize));
        }
    }

}
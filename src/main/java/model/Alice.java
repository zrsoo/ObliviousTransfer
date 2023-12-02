package model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Alice extends ObliviousTransferEntity implements Runnable{
    private static final String serverAddress = "localhost";
    private static final String ALICE = "ALICE: ";

    Socket socket;

    public Alice(int port, int base, int prime)
    {
        super(port, base, prime);
    }

    @Override
    public void run() {
        this.openClientSocket();
    }

    private void openClientSocket()
    {
        try {
            socket = new Socket(serverAddress, port);
            System.out.println(ALICE + "Connected to Bob");

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (Exception e)
        {
            System.out.println(ALICE + "Error when connecting to server: " + e.getMessage());
        }
    }

    public void performObliviousTransfer() {
        send("Yoyoyo Bobski");
    }
}

package model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Bob extends ObliviousTransferEntity implements Runnable{
    private static final String BOB = "BOB: ";

    ServerSocket serverSocket;


    public Bob(int port, int base, int prime)
    {
        super(port, base, prime);
    }

    @Override
    public void run() {
        this.openServerSocket();
    }

    private void openServerSocket()
    {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println(BOB + "Bob is listening, waiting for Alice...");

            Socket aliceSocket = serverSocket.accept();
            System.out.println(BOB + "Alice connected");

            out = new PrintWriter(aliceSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(aliceSocket.getInputStream()));
        }
        catch (Exception e)
        {
            System.out.println(BOB + "Error when opening server: " + e.getMessage());
        }
    }

    public void performObliviousTransfer() {
        String message = receive();
        System.out.println(BOB + "Received: " + message);
    }
}

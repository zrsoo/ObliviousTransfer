package model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Bob implements Runnable{
    private static final String BOB = "BOB: ";
    private final int port;

    public Bob(int port)
    {
        this.port = port;
    }

    @Override
    public void run() {
        this.performObliviousTransfer(port);
    }

    private void performObliviousTransfer(int port) {
        // Open communication channel
        try(ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println(BOB + "Bob is listening, waiting for Alice...");

            try (Socket aliceSocket = serverSocket.accept();
                 PrintWriter out = new PrintWriter(aliceSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(aliceSocket.getInputStream()))) {
                System.out.println(BOB + "Alice connected");

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println(BOB + "Alice says: " + inputLine);
                    out.println("Heya");
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(BOB + "Error on Bob's side: " + e.getMessage());
        }
    }
}

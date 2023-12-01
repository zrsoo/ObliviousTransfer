package model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class Alice implements Runnable{
    private static final String serverAddress = "localhost";
    private static final String ALICE = "ALICE: ";
    private final int port;

    public Alice(int port)
    {
        this.port = port;
    }

    @Override
    public void run() {
        this.performObliviousTransfer(port);
    }

    private void performObliviousTransfer(int port) {
        try(Socket socket = new Socket(serverAddress, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            System.out.println(ALICE + "Connected to Bob");

            // Send data to Bob
            String input = "Yoyoyo Bobsky";
            out.println(input);

            String response = in.readLine();
            System.out.println(ALICE + "Received from Bob: " + response);
        }
        catch (Exception e) {
            System.out.println(ALICE + "Error on Alice's side: " + Arrays.toString(e.getStackTrace()));
        }
    }
}

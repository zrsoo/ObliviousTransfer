package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static java.lang.System.exit;

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
        wait(2000);
        this.performObliviousTransfer();
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
        sendA();
        String B = receiveB();
        closeSocket();
    }

    private void sendA()
    {
        String A = Integer.toString(modularExponentiation(base, exponent, prime));
        send(A);
        System.out.println(ALICE + "Sent A = " + base + " ^ " + exponent + " mod " + prime + " = " + A);
    }

    private String receiveB()
    {
        String B = receive();
        System.out.println(ALICE + "Received B = " + B);
        return B;
    }

    private void closeSocket()
    {
        try{
            socket.close();
        }
        catch (IOException e)
        {
            System.out.println(ALICE + "Error when closing socket");
        }
    }
}

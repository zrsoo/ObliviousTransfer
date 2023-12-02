package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Bob extends ObliviousTransferEntity implements Runnable{
    private static final String BOB = "BOB: ";

    private final int decisionBit;

    ServerSocket serverSocket;


    public Bob(int port, int base, int prime)
    {
        super(port, base, prime);

        double prob = Math.random();
        if(prob > 0.5)
            decisionBit = 0;
        else
            decisionBit = 1;
    }

    @Override
    public void run() {
        this.openServerSocket();
        this.performObliviousTransfer();
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
        String A = receiveA();
        String B = computeB(A);
        sendB(B);
        closeSocket();
    }

    private String receiveA() {
        String A = receive();
        A = A.strip();
        System.out.println(BOB + "Received A = " + A);
        return A;
    }

    private String computeB(String A)
    {
        System.out.println(BOB + "Computing B");

        int intA = Integer.parseInt(A);
        int B;
        int gb = modularExponentiation(base, exponent, prime);

        System.out.println(BOB + "Computed g ^ b mod prime = " + base + " ^ " + exponent + " mod " + prime +
                " = " + gb);

        if(decisionBit == 0)
        {
            B = gb;
            System.out.println(BOB + "Decision bit is 0 so B = g ^ b mod prime = " + B);
        }
        else
        {
            // Ensure to stay within mod prime
            B = (intA * gb) % prime;
            System.out.println(BOB + "Decision bit is 1 so B = " + "A * g ^ b mod prime = "
                    + A + " * " + gb + " mod " + prime + " = " + B);
        }

        return Integer.toString(B);
    }

    private void sendB(String B)
    {
        send(B);
        System.out.println(BOB + "Sent B = " + B);
    }

    private void closeSocket()
    {
        try{
            serverSocket.close();
        }
        catch (IOException e)
        {
            System.out.println(BOB + "Error when closing socket");
        }
    }
}

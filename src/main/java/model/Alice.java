package model;

import encrypt.EncryptUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
        String A = sendA();
        String B = receiveB();

        ArrayList<String> keyList = computeKeys(A, B);

        closeSocket();
    }

    private String sendA()
    {
        String A = Integer.toString(modularExponentiation(base, exponent, prime));
        send(A);
        System.out.println(ALICE + "Sent A = " + base + " ^ " + exponent + " mod " + prime + " = " + A);
        return A;
    }

    private String receiveB()
    {
        String B = receive();
        B = B.strip();
        System.out.println(ALICE + "Received B = " + B);
        return B;
    }

    // TODO (B/A)^a often comes as 0 because B is too small, fix
    private ArrayList<String> computeKeys(String A, String B)
    {
        int intA = Integer.parseInt(A);
        int intB = Integer.parseInt(B);

        String k0val = Integer.toString(modularExponentiation(intB, exponent, prime));
        String k1val = Integer.toString(modularExponentiation(intB / intA, exponent, prime));

        String k0 = EncryptUtils.digest(k0val, algorithm);
        String k1 = EncryptUtils.digest(k1val, algorithm);

        System.out.println(ALICE + "Hashed B^a mod p = " + B + " ^ " + exponent + " mod " + prime +
                " = " + k0val + " to:\n" + k0);
        System.out.println(ALICE + "Hashed (B/A)^a mod p = (" + B + " / " + A + ") ^ " + exponent + " mod " + prime +
                " = " + k1val + " to:\n" + k1);

        ArrayList<String> keyList = new ArrayList<>();
        keyList.add(k0);
        keyList.add(k1);

        return keyList;
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

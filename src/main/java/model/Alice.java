package model;

import encrypt.EncryptUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Alice extends ObliviousTransferEntity implements Runnable{
    private static final String serverAddress = "localhost";
    private static final String ALICE = "ALICE: ";

    private String M0;
    private String M1;

    Socket socket;

    public Alice(int port, int base, int prime)
    {
        super(port, base, prime);
        System.out.println(ALICE + "Computed secret integer a = " + exponent);
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
        sendEncryptedMessages(keyList);

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

    private ArrayList<String> computeKeys(String A, String B)
    {
        int intB = Integer.parseInt(B);

        String k0val = Integer.toString(modularExponentiation(intB, exponent, prime));
        String k1val = computeK1Val(A, B);

        String k0 = EncryptUtils.digest(k0val);
        String k1 = EncryptUtils.digest(k1val);

        System.out.println(ALICE + "Hashed B^a mod p = " + B + " ^ " + exponent + " mod " + prime +
                " = " + k0val + " to:" + k0);
        System.out.println(ALICE + "Hashed (B/A)^a mod p = (" + B + " / " + A + ") ^ " + exponent + " mod " + prime +
                " = " + k1val + " to:" + k1);

        ArrayList<String> keyList = new ArrayList<>();
        keyList.add(k0);
        keyList.add(k1);

        return keyList;
    }

    private void sendEncryptedMessages(ArrayList<String> keyList)
    {
        String k0 = keyList.get(0);
        String k1 = keyList.get(1);

        EncryptUtils encryptUtils = new EncryptUtils();

        String M0k0 = encryptUtils.encrypt(M0, k0);
        String M1k1 = encryptUtils.encrypt(M1, k1);

        send(M0k0);
        send(M1k1);

        System.out.println(ALICE + "Sent encrypted messages to Bob");
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

    private String computeK1Val(String A, String B)
    {
        String invDiv = modularInverseDivision(A, B);
        int invDivVal = Integer.parseInt(invDiv);
        return Integer.toString(modularExponentiation(invDivVal, exponent, prime));
    }

    public void setM0(String m0) {
        M0 = m0;
    }

    public void setM1(String m1) {
        M1 = m1;
    }
}

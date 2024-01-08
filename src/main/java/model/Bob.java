package model;

import encrypt.EncryptUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Bob extends ObliviousTransferEntity implements Runnable{
    private static final String BOB = "BOB: ";

    private final int decisionBit;

    ServerSocket serverSocket;

    public Bob(int port, int base, int prime)
    {
        super(port, base, prime);

        System.out.println(BOB + "Computed secret integer b = " + exponent);

        double prob = Math.random();
        if(prob > 0.5)
            decisionBit = 0;
        else
            decisionBit = 1;
    }

    @Override
    public void run() {
        this.openServerSocket();
        this.exchangeKeys();
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
        String hashedKey = computeKey(A);
        ArrayList<String> encryptedMessages = receiveEncryptedMessages();
        decryptMessages(encryptedMessages, hashedKey);
        closeSocket();
    }

    private String receiveA() {
        String A = receiveAuthenticated();
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
        sendAuthenticated(B);
        System.out.println(BOB + "Sent B = " + B);
    }

    private ArrayList<String> receiveEncryptedMessages()
    {
        String M0k0 = receiveAuthenticated();
        String M1k1 = receiveAuthenticated();

        System.out.println(BOB + "Received encrypted messages");
        System.out.println(BOB + "First message: " + M0k0);
        System.out.println(BOB + "Second message: " + M1k1);

        ArrayList<String> encryptedMessages = new ArrayList<>();

        encryptedMessages.add(M0k0);
        encryptedMessages.add(M1k1);

        return encryptedMessages;
    }

    private String computeKey(String A)
    {
        int intA = Integer.parseInt(A);
        int key = modularExponentiation(intA, exponent, prime);

        String hashedKey = EncryptUtils.digest(Integer.toString(key));
        System.out.println(BOB + "Hashing A ^ b mod prime = " + A + " ^ " + exponent + " mod " + prime + " = " + key);
        System.out.println(BOB + "Computed hashed key kR = " + hashedKey);

        return hashedKey;
    }

    private void decryptMessages(ArrayList<String> encryptedMessages, String hashedKey)
    {
        String M0k0 = encryptedMessages.get(0);
        String M1k1 = encryptedMessages.get(1);

        EncryptUtils encryptUtils = new EncryptUtils();

        String M0k0Decrypted = encryptUtils.decrypt(M0k0, hashedKey);
        String M1k1Decrypted = encryptUtils.decrypt(M1k1, hashedKey);

        System.out.println(BOB + "Decrypted first message as: " + M0k0Decrypted);
        System.out.println(BOB + "Decrypted second message as: " + M1k1Decrypted);
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

    @Override
    protected void exchangeKeys()
    {
        System.out.println(BOB + "Exchanging public key with Alice");
        otherPublicKey = receiveKey();
        System.out.println(BOB + "Received key");
        sendKey();
        System.out.println(BOB + "Sent key");
    }
}

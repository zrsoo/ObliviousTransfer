package model;

import encrypt.EncryptUtils;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Random;

import static java.lang.System.exit;

public abstract class ObliviousTransferEntity {
    protected final int port;
    protected final int base;
    protected final int prime;
    protected final int exponent;

    PrintWriter out;
    BufferedReader in;

    protected final String END_OF_MESSAGE = "$";

    PrivateKey privateKey;
    PublicKey publicKey;

    public ObliviousTransferEntity(int port, int base, int prime) {
        this.port = port;
        this.base = base;
        this.prime = prime;

        Random random = new Random();
        this.exponent = random.ints(0, prime).findFirst().getAsInt();

        // Generate public/private key pairs
        KeyPair pair = EncryptUtils.generateKeyPair();
        privateKey = pair.getPrivate();
        publicKey = pair.getPublic();
    }

    protected int modularExponentiation(int base, int exponent, int modulo)
    {
        long x = 1;
        long y = base;

        while(exponent > 0) {
            if(exponent % 2 == 1) {
                x = (x * y) % modulo;
            }

            y = (y * y) % modulo;

            exponent /= 2;
        }

        return (int) x % modulo;
    }

    protected String modularInverseDivision(String A, String B)
    {
        BigInteger bigA = new BigInteger(A);
        BigInteger bigB = new BigInteger(B);
        BigInteger bigP = new BigInteger(Integer.toString(prime));

        // Compute A inverse mod p
        BigInteger inverseA = bigA.modInverse(bigP);

        // Compute B * (A inverse) mod p
        BigInteger result = bigB.multiply(inverseA).mod(bigP);

        return Integer.toString(result.intValue());
    }

    protected String receive()
    {
        StringBuilder message = new StringBuilder();
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if(inputLine.equals(END_OF_MESSAGE))
                    break;
                message.append(inputLine).append("\n");
            }
        }
        catch (Exception e) {
            System.out.println("Error when receiving: " + e.getMessage());
        }
        return message.toString();
    }

    protected void send(String message)
    {
        try{
            out.println(message);
            out.println(END_OF_MESSAGE);
        }
        catch (Exception e)
        {
            System.out.println("Error when sending: " + e.getMessage());
        }
    }

    protected void wait(int miliseconds)
    {
        try{
            Thread.sleep(miliseconds);
        }
        catch (InterruptedException ex)
        {
            System.out.println("Could not wait");
            exit(0);
        }
    }

    protected static String publicKeyToString(PublicKey key)
    {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    protected static PublicKey stringToPublicKey(String key)
    {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e)
        {
            System.out.println("Error when initializing key factory, no such algorithm");
            return  null;
        }
    }

    protected PublicKey receiveKey()
    {
        String keyString = receive();
        keyString = keyString.strip();
        return stringToPublicKey(keyString);
    }

    protected void sendKey()
    {
        String keyString = publicKeyToString(this.publicKey);
        send(keyString);
    }

    protected abstract void exchangeKeys();
}

// TODO serialize PublicKey to String before sending to Bob -> send it from Alice to Bob
// TODO deserialize back to PublicKey object on Bob's side
// TODO also do this the other way around
// TODO on both ends, along with any data sent, also send the digital signature (encrypted hash of sent data)
// TODO on both ends, when receiving data, check that the computed hash of the public data matches the decrypted digital signature

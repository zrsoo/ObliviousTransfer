package model;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Random;

import static java.lang.System.exit;

public class ObliviousTransferEntity {
    protected final int port;
    protected final int base;
    protected final int prime;
    protected final int exponent;

    PrintWriter out;
    BufferedReader in;

    protected final String END_OF_MESSAGE = "$";

    public ObliviousTransferEntity(int port, int base, int prime) {
        this.port = port;
        this.base = base;
        this.prime = prime;

        Random random = new Random();
        this.exponent = random.ints(0, prime).findFirst().getAsInt();
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
}

// TODO serialize PublicKey to String before sending to Bob -> send it from Alice to Bob
// TODO deserialize back to PublicKey object on Bob's side
// TODO also do this the other way around
// TODO on both ends, along with any data sent, also send the digital signature (encrypted hash of sent data)
// TODO on both ends, when receiving data, check that the computed hash of the public data matches the decrypted digital signature

package model;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Random;

public class ObliviousTransferEntity {
    protected final int port;
    protected final int base;
    protected final int prime;
    protected final int exponent;

    PrintWriter out;
    BufferedReader in;

    public ObliviousTransferEntity(int port, int base, int prime) {
        this.port = port;
        this.base = base;
        this.prime = prime;

        Random random = new Random();
        this.exponent = random.ints(0, prime).findFirst().getAsInt();
    }

    // TODO to be tested
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

    protected String receive()
    {
        StringBuilder message = new StringBuilder();
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
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
        }
        catch (Exception e)
        {
            System.out.println("Error when sending: " + e.getMessage());
        }
    }
}

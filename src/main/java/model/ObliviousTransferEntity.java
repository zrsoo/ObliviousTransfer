package model;

import java.util.Random;

public class ObliviousTransferEntity {
    protected final int port;
    protected final int base;
    protected final int prime;
    protected final int exponent;

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
}

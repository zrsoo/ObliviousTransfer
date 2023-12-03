import model.Alice;
import model.Bob;

import static java.lang.System.exit;

public class Controller {
    private static final int port = 8080;

    public static void main(String[] args)
    {
        System.out.println("Salut");

        int base = 3;
        int prime = 36697;

        System.out.println("Using BASE: " + base + "\nUsing PRIME: " + prime);

        Bob bob = new Bob(port, base, prime);
        Alice alice = new Alice(port, base, prime);

        alice.setM0("Bob te iubesc esti dragostea mea hai te pup");
        alice.setM1("Iesi dreaq Bobule");

        // Start bob and alice in different threads
        new Thread(bob).start();
        new Thread(alice).start();
    }
}

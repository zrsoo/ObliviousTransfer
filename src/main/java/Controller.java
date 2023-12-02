import model.Alice;
import model.Bob;

import static java.lang.System.exit;

public class Controller {
    private static final int port = 8080;

    public static void main(String[] args)
    {
        System.out.println("Salut");

        int base = 3;
        int prime = 89;

        System.out.println("Using BASE: " + base + "\nUsing PRIME: " + prime);

        Bob bob = new Bob(port, base, prime);
        Alice alice = new Alice(port, base, prime);

        // Start bob and alice in different threads
        new Thread(bob).start();
        new Thread(alice).start();
    }
}

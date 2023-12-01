import model.Alice;
import model.Bob;

public class Controller {
    private static final int port = 8080;

    public static void main(String[] args)
    {
        System.out.println("Salut");

        Bob bob = new Bob(port);
        Alice alice = new Alice(port);

        // Start bob and alice in different threads
        new Thread(bob).start();
        new Thread(alice).start();
    }
}

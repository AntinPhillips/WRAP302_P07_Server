import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Antin on 10/9/2017.
 */
public class Player
{
    public Socket socket;
    public BufferedReader in;
    public PrintWriter out;
    public String name;
    public int brains;
    public int shotguns;

    public Player(Socket socket, BufferedReader in, PrintWriter out, String name)
    {
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.name = name;
        brains = 0;
        shotguns = 0;
    }
}

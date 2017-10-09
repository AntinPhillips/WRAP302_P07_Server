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

    public Player(Socket socket)
    {
        this.socket = socket;

        try
        {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

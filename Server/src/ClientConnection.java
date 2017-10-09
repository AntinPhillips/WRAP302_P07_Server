import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Antin on 10/9/2017.
 */
public class ClientConnection extends Thread
{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientConnection(Socket socket)
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

    @Override
    public void run()
    {
        try
        {
            String inLine = in.readLine();
            switch (inLine)
            {
                case Main.JOIN_GAME:
                {
                    String roomCode = in.readLine();
                    Game game = Main.getRoom(roomCode);
                    if (game != null)
                    {
                        if (!game.isFull())
                        {
                            Player player = new Player(socket);
                            out.println(Main.JOIN_SUCCESSFUL);
                            game.addPlayer(player);
                        } else
                        {
                            out.println(Main.ROOM_FULL);
                        }
                    } else
                    {
                        out.println(Main.INVALID_ROOM_CODE);
                    }
                    break;
                }

                case Main.CREATE_GAME:
                {
                    String roomCode = Main.generateRoomCode();
                    int maxPlayers = Integer.parseInt(in.readLine());
                    Player player = new Player(socket);

                    Game game = new Game(roomCode, maxPlayers);
                    out.println(Main.JOIN_SUCCESSFUL);
                    game.addPlayer(player);
                    game.start();
                    break;
                }
                default:
                {
                    socket.close();
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

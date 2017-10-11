import javax.xml.transform.sax.SAXTransformerFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Antin on 10/9/2017.
 */
public class Main
{
    //define some connection constants
    public static final String JOIN_GAME = "1";
    public static final String CREATE_GAME = "2";
    public static final String INVALID_ROOM_CODE = "3";
    public static final String ROOM_FULL = "4";
    public static final String JOIN_SUCCESSFUL = "5";
    public static final String START_GAME = "6";
    public static final String NEW_PLAYER = "7";
    public static final String GAME_STARTING = "8";
    public static final String PLAYER_READY = "9";
    public static final String ROLL = "10";
    public static final String END_TURN = "11";
    public static final String DICE_VALUES = "12";
    public static final String GAME_OVER = "13";
    public static final String DISCONNECT = "14";
    public static final String SAME_TURN = "14";

    private static ArrayList<Game> games;

    public static void main(String[] args)
    {
        games = new ArrayList<>();

        try
        {
            ServerSocket serverSocket = new ServerSocket(123);

            while (true)
            {
                Socket socket = serverSocket.accept();
                new ClientConnection(socket).start();
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static String generateRoomCode()
    {
        String code = "";

        while (code.equals("") || roomCodeInUse(code))
        {
            code = "";
            Random rand = new Random();

            for (int i = 0; i < 4; i++)
                code += (char) (rand.nextInt(26) + 65);
        }

        return code;
    }

    private static boolean roomCodeInUse(String roomCode)
    {
        for (Game game : games)
            if (game.roomCode.equals(roomCode))
                return true;

        return false;
    }

    public static Game getRoom(String roomCode)
    {
        for (Game game : games)
            if (game.roomCode.equals(roomCode))
                if (!game.inSession)
                {
                    return game;
                }
                else
                    return null;

        return null;
    }

    public static void addRoom(Game game)
    {
        games.add(game);
    }

    public static void removeGame(Game game)
    {
        games.remove(game);
    }
}

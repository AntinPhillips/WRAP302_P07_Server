import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Antin on 10/9/2017.
 */
public class Game extends Thread
{
    public String roomCode;
    public boolean inSession;
    private ArrayList<Player> players;
    int maxPlayers;

    public Game(String roomCode, int maxPlayers)
    {
        this.roomCode = roomCode;
        this.maxPlayers = maxPlayers;
        inSession = false;
        players = new ArrayList<>();
    }

    public void addPlayer(Player player)
    {
        //notify the other players of the join
        for (int i = 0; i < players.size(); i++)
        {
            players.get(i).out.println(Main.NEW_PLAYER);
            players.get(i).out.println(player.socket.getInetAddress().toString());
        }

        //tell the new player about all the other players
        //how many are there? and there names
        player.out.println(players.size());
        for (int i = 0; i < players.size(); i++)
            player.out.println(players.get(i).socket.getInetAddress().toString());

        players.add(player);
    }

    public boolean isFull()
    {
        return players.size() >= maxPlayers;
    }

    @Override
    public void run()
    {
        try
        {
            String inLine = "";
            while (!inLine.equals(Main.START_GAME))
                inLine = players.get(0).in.readLine();

            //start the game
            inSession = true;
            for (Player player : players)
                player.out.println(Main.GAME_STARTING);

            //wait for all players to be ready
            for (Player player : players)
                player.in.readLine(); //all players *SHOULD* send back Main.PLAYER_READY - but we're not really going to check for that

            System.out.println("GAME: " + roomCode + " SUCCESSFULLY STARTED!");

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

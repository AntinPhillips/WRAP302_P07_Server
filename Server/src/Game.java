import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Antin on 10/9/2017.
 */
public class Game extends Thread
{
    public String roomCode;
    public boolean inSession;
    private ArrayList<Player> players;
    private int maxPlayers;
    private ArrayList<Die> diceUnplayed;
    private ArrayList<Die> dicePlayed;
    private ArrayList<Die> diceInPlay;
    private int shotguns;
    private int brains;

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
            players.get(i).out.println(player.name);
        }

        //tell the new player about all the other players
        //how many are there? and their names
        player.out.println(players.size());
        for (int i = 0; i < players.size(); i++)
            player.out.println(players.get(i).name);

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
            System.out.println("Waiting to start...");
            String inLine = "";
            while (!inLine.equals(Main.START_GAME)) //to do: check that there's enough players!
            {
                System.out.println("Awaiting line....");
                inLine = players.get(0).in.readLine();
                System.out.println("inLine:" + inLine);
            }

            System.out.println("Ready to start!");

            //start the game
            inSession = true;
            for (Player player : players)
                player.out.println(Main.GAME_STARTING);

            //wait for all players to be ready
            for (Player player : players)
                player.in.readLine(); //all players *SHOULD* send back Main.PLAYER_READY - but we're not really going to check for that

            System.out.println("GAME: " + roomCode + " SUCCESSFULLY STARTED!");
            System.out.println("THIS IS AN ACHIEVEMENT OK! SHUSH!");

            //start playing the game!
            initialiseDice();

            //broadcast all the players so that everyone has the same order
            //num players:
            for (Player player : players)
                player.out.println(players.size());

            //that players position in the list
            for (int i = 0; i < players.size(); i++)
                players.get(i).out.println(i);

            //the player names
            for (Player player : players)
                for (Player curPlayer : players)
                    player.out.println(curPlayer.name);

            int curPlayerPos = 0;
            Player curPlayer = players.get(curPlayerPos);
            boolean go = true;
            while (go)
            {
                //broadcast current player
                for (Player player : players)
                    player.out.println(curPlayerPos);

                inLine = curPlayer.in.readLine();
                if (inLine.equals(Main.ROLL))
                {
                    roll();

                    //broadcast dice values
                    for (Player player : players)
                    {
                        player.out.println(Main.DICE_VALUES);
                        for (int i = 0; i < 3; i++)
                        {
                            player.out.println(diceInPlay.get(i).type);
                            player.out.println(diceInPlay.get(i).value);
                        }
                    }

                    //check result
                    if (shotguns >= 3)
                    {
                        //broadcast that turn is over
                        for (Player player : players)
                            player.out.println(Main.END_TURN);

                        brains = 0;
                        shotguns = 0;
                        curPlayerPos++;
                        curPlayerPos %= players.size();
                        curPlayer = players.get(curPlayerPos);

                    } else if (brains + curPlayer.brains >= 13)
                    {
                        //broadcast that game was won and by who
                        for (Player player : players)
                        {
                            player.out.println(Main.GAME_OVER);
                            player.out.println(curPlayerPos);
                        }

                        go = false;
                    } else if (diceUnplayed.size() == 0 && diceInPlay.size() == 0)
                    {
                        //do something
                        //should never happen - if this point is reached, the player should already have 13 brains at least
                        System.out.println("Umm..? You sure?");
                        go = false;
                    } else
                    {
                        //broadcast that it's the same player's turn
                        for (Player player : players)
                            player.out.println(Main.SAME_TURN);
                    }

                } else if (inLine.equals(Main.END_TURN))
                {
                    curPlayer.brains += brains;
                    brains = 0;
                    shotguns = 0;
                    curPlayerPos++;
                    curPlayerPos %= players.size();
                    curPlayer = players.get(curPlayerPos);

                    //broadcast that turn is over
                    for (Player player : players)
                        player.out.println(Main.END_TURN);
                } else
                {
                    //then we've got a bit of a problem
                }
            }

            for (Player player : players)
                player.out.println(Main.DISCONNECT);

            for (Player player : players)
                player.socket.close();

            Main.removeGame(this);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void initialiseDice()
    {
        diceUnplayed = new ArrayList<>();
        dicePlayed = new ArrayList<>();
        diceInPlay = new ArrayList<>();

        for (int i = 0; i < 6; i++)
            diceUnplayed.add(new Die(0));

        for (int i = 0; i < 4; i++)
            diceUnplayed.add(new Die(1));

        for (int i = 0; i < 3; i++)
            diceUnplayed.add(new Die(2));
    }

    private void roll()
    {
        //add dice to roll
        while (diceInPlay.size() < 3 && diceUnplayed.size() > 0)
        {
            Random random = new Random();
            int num = random.nextInt(diceUnplayed.size());
            diceInPlay.add(diceUnplayed.remove(num));
        }

        //roll the dice
        for (Die die : diceInPlay)
            die.roll();

        ArrayList<Die> toRemove = new ArrayList<>();

        //check what was rolled
        for (Die die : diceInPlay)
        {
            if (die.value == 0)
            {
                brains++;
                toRemove.add(die);
            } else if (die.value == 2)
            {
                shotguns++;
                toRemove.add(die);
            }
        }

        dicePlayed.addAll(toRemove);
        diceInPlay.removeAll(toRemove);
    }
}

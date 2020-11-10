package uk.coolguys;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static uk.coolguys.Player.HAND_SIZE;

/**
 * Describe a table pls
 */
public class Table {

    /** The instance of our table since it's there's only one table */
    private static Table instance;

    /**
     * Begins a new table by generating the player instances in the main thread and
     * stores the available cards.
     * If a {@link Table} has already began, throws a {@link IllegalStateException}.
     *
     * @param cards List of available cards
     * @param nPlayers The quantity of players
     * @return The table instance
     */
    public static Table begin(List<Integer> cards, int nPlayers) {
        if(instance != null)
            throw new IllegalStateException("Table has already began!");

        return instance = new Table(cards, nPlayers);
    }

    /**
     * Returns the instance of the current {@link Table} or
     * throws {@link IllegalStateException} if there are table.
     *
     * @return The table instance
     */
    public static Table getInstance() {
        if(instance == null)
            throw new IllegalStateException("There's no table available!");

        return instance;
    }

    /** The pick of cards */
    private final List<Integer> pick;

    /** The players */
    private final List<Player> players;

    /**
     * Creates a new instance of table with specified pick at the beginning and
     * specified amount of player.
     * This constructor verifies if every conditions are match to play and if not, throws
     * an exception.
     *
     * @param pick Pick
     * @param nPlayer Player count
     */
    Table(List<Integer> pick, int nPlayer) {
        if(pick.size() < nPlayer*8)
            throw new IllegalStateException("The pick is not big enough for the amount of players!");

        this.pick = pick;
        this.players = new ArrayList<>(nPlayer);
        for(int i = 0; i < nPlayer; i++)
            this.players.add(new Player());
    }

    /**
     * Distributes cards to each players with each card distributed by a new thread.
     *
     * @throws InterruptedException In case if the thread is interrupted.
     */
    public Table distribute() throws InterruptedException {
        AtomicInteger playerIdSupplier  = new AtomicInteger();

        while(!pick.isEmpty()) {
            Thread thread = new Thread(() -> {
                int playerId = playerIdSupplier.getAndUpdate(i -> players.size() < i + 2 ? 0 : i + 1);

                Player player = players.get(playerId);
                if(player.getCurrentHand().size() < HAND_SIZE)
                    player.addToHand(pick.get(0));
                else
                    player.addToPick(pick.get(0));

                pick.remove(0);
            });
            thread.start();

            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (thread) {
                thread.wait();
            }
        }
        System.out.println(players);
        return this;
    }

    /**
     * Returns the global pick of the {@link Table}, this pick should be empty
     * once {@link this#distribute()} is ran.
     *
     * @return The list of pick
     */
    public List<Integer> getPick() {
        return pick;
    }

    /**
     * Returns the list of players around the table.
     *
     * @return The players.
     */
    public List<Player> getPlayers() {
        return players;
    }
}

package uk.coolguys;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;

import static uk.coolguys.Player.HAND_SIZE;

public class Table {

    /**
     * The instance of our table; there can only be one table playing at one time.
     */
    private static Table instance;

    /** Boolean declared to determine whether the game has a winner later. */
    private Boolean hasWinner = false;

    /** Int used to store the index/id of the winning player */
    private int winningPlayer;

    /**
     * Begins a new table by generating the player instances in the main thread and
     * stores the available cards. If a {@link Table} has already began, throws an
     * {@link IllegalStateException}.
     *
     * @param cards    List of available cards
     * @param nPlayers The quantity of players
     * @return The table instance
     */
    public static Table begin(List<Integer> cards, int nPlayers) {
        if (instance != null)
            throw new IllegalStateException("Table has already began!");

        return instance = new Table(cards, nPlayers);
    }

    /**
     * Returns the instance of the current {@link Table} or throws
     * {@link IllegalStateException} if there is already a table.
     *
     * @return The table instance
     */
    public static Table getInstance() {
        if (instance == null)
            throw new IllegalStateException("There's no table available!");

        return instance;
    }

    /** The pick of cards (pick refers to the piles of cards between players) */
    private final List<Integer> pick;

    /** The players */
    private final List<Player> players;

    /**
     * Creates a new instance of table with specified pick at the beginning and
     * specified amount of player. This constructor verifies that all conditions are
     * met. Ff not, throws an exception.
     *
     * @param pick    Pick
     * @param nPlayer Player count
     */
    Table(List<Integer> pick, int nPlayer) {
        if (pick.size() < nPlayer * 8)
            throw new IllegalStateException("The pick is not big enough for the amount of players!");

        this.pick = pick;
        this.players = new ArrayList<>(nPlayer);
        for (int i = 0; i < nPlayer; i++)
            this.players.add(new Player());
    }

    /**
     * Distributes cards to each players with each card distributed by a new thread.
     * Checks if any of the integers are negative
     * 
     * @throws IllegalArgumentException if any input numbers are negative.
     * @throws InterruptedException     In case the thread is interrupted.
     */
    public Table distribute() throws InterruptedException {
        AtomicInteger playerIdSupplier = new AtomicInteger();

        ListIterator<Integer> iterator = pick.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next() < 0)
                throw new IllegalArgumentException("One of the numbers in the file was not non-negative.");
        }

        while (!pick.isEmpty()) {
            Thread thread = new Thread(() -> {
                int playerId = playerIdSupplier.getAndUpdate(i -> players.size() < i + 2 ? 0 : i + 1);

                Player player = players.get(playerId);
                if (player.getCurrentHand().size() < HAND_SIZE)
                    player.addToHand(pick.get(0));
                else
                    player.addToPick(pick.get(0));

                pick.remove(0);
            });
            thread.start();

            synchronized (thread) {
                thread.wait();
            }
        }
        System.out.println(players);
        return this;
    }

    public Table playGame() throws InterruptedException {

        for (int i = 0; i < players.size(); i++) {
            Player winChecker = players.get(i);
            if (winChecker.getCurrentHand().stream().distinct().count() <= 1) {
                hasWinner = true;
                winningPlayer = winChecker.getPlayerNumber();
                break;
            }
        }

        while (hasWinner == false) {

            players.stream().map(p -> new Thread(() -> {

                AtomicInteger playerNr = new AtomicInteger(p.getPlayerNumber());
                int pileId = playerNr.updateAndGet(i -> players.size() < i + 2 ? 0 : i + 1);

                /**
                 * Creates a new file if necessary. If file already exist it does not overwrite
                 * it.
                 */
                try {
                    File myObj = new File("player" + p.getPlayerNumber() + ".txt");
                    if (!myObj.exists()) {
                        myObj.createNewFile();
                        System.out.println("File created:" + p.getPlayerNumber() + ".");
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred in the creation/writing of the file for player"
                            + p.getPlayerNumber() + ".");
                    e.printStackTrace();
                }

                /**
                 * Gets a card from next player's personalPick, adds it to p's currentHand.
                 */
                p.getCurrentHand().add(players.get(pileId).getPersonalPick().get(0));

                /**
                 * Writes to the text file the logs of the previous action.
                 */
                try (FileWriter fw = new FileWriter("player" + p.getPlayerNumber() + ".txt", true);
                        BufferedWriter bw = new BufferedWriter(fw);
                        PrintWriter out = new PrintWriter(bw)) {
                    out.println("player " + p.getPlayerNumber() + " draws a "
                            + players.get(pileId).getPersonalPick().get(0) + " from deck " + pileId + ".");
                    bw.close();
                    fw.close();
                    out.close();
                } catch (IOException e) {
                    System.out.println(
                            "An error occurred in the writing of the file for player" + p.getPlayerNumber() + ".");
                    e.printStackTrace();
                }

                /**
                 * Removes the card from the personalPick. This is the card picking turn.
                 */
                players.get(pileId).getPersonalPick().remove(0);

                /**
                 * Adds card the first non-priority card from p's current hand to their personal
                 * pick. Removes this card. from p's current hand. This is the card discarding
                 * turn.
                 */

                ListIterator<Integer> iterator = p.getCurrentHand().listIterator();
                while (iterator.hasNext()) {
                    if (iterator.next() != p.getPlayerNumber()) {
                        p.getPersonalPick().add(p.getCurrentHand().get(iterator.nextIndex() - 1));

                        /**
                         * Writes to the text file the logs of the previous action.
                         */
                        try (FileWriter fw = new FileWriter("player" + p.getPlayerNumber() + ".txt", true);
                                BufferedWriter bw = new BufferedWriter(fw);
                                PrintWriter out = new PrintWriter(bw)) {
                            out.println("player " + p.getPlayerNumber() + " discards a "
                                    + players.get(pileId).getPersonalPick().get(0) + " from deck " + pileId + ".");
                            bw.close();
                            fw.close();
                            out.close();
                        } catch (IOException e) {
                            System.out.println("An error occurred in the writing of the file for player"
                                    + p.getPlayerNumber() + ".");
                            e.printStackTrace();
                        }

                        p.getCurrentHand().remove(iterator.nextIndex() - 1);
                        break;
                    }
                }

                /**
                 * Checks if there are more than 1 unique values in p's hand. If not, the player
                 * has won and hasWinner is set to true.
                 */
                if (p.getCurrentHand().stream().distinct().count() <= 1) {
                    hasWinner = true;
                    winningPlayer = p.getPlayerNumber();
                }

            })).forEach(Thread::run);

            System.out.println("A turn has elapsed. Current cards in each pile: ");
            players.forEach(i -> System.out.println("Card pile " + i.getPlayerNumber() + ": " + i.getPersonalPick()));
        }

        /**
         * Writing to every file which player won
         */
        for (int i = 0; i < players.size(); i++) {
            try (FileWriter fw = new FileWriter("player" + i + ".txt", true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw)) {
                out.println("Player " + winningPlayer + " has won.");
                out.println("Player " + winningPlayer + " hand: " + players.get(winningPlayer).getCurrentHand() + ".");
                bw.close();
                fw.close();
                out.close();
            } catch (IOException e) {
                System.out.println("An error occured while writing to the file.");
            }
        }

        System.out.println("Player " + winningPlayer + " has won");

        return this;
    }

    /**
     * Returns the global pick of the {@link Table}, this pick should be empty once
     * {@link this#distribute()} is ran.
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

package uk.coolguys;

import java.util.ArrayList;
import java.util.List;

/**
 * Note that pick throughout this class refers to the pile of cards between
 * players, each of which has been attached to a Player. A perosnal pick is a
 * specific player's pick.
 */

public class Player {

    /** A constant that defines the amount of cards in a hand */
    public static int HAND_SIZE = 4;

    /** The current hand of the player, shouldn't exceed {@link this#HAND_SIZE} */
    private final List<Integer> currentHand = new ArrayList<>(HAND_SIZE);

    /**
     * The current personal pick of the player, shouldn't exceed
     * {@link this#HAND_SIZE}
     */
    private final List<Integer> personalPick = new ArrayList<>(HAND_SIZE);

    /**
     * Returns the player number based on {@link Table#getPlayers()} using the
     * {@link List#indexOf(Object)} method.
     *
     * @return This player's number.
     */
    public int getPlayerNumber() {
        return Table.getInstance().getPlayers().indexOf(this);
    }

    /**
     * Adds specified card to the hand of the player (if the hand does not exceed
     * {@link this#HAND_SIZE}).
     *
     * @param card Card's number
     */
    public Player addToHand(Integer card) {
        if (currentHand.size() < HAND_SIZE)
            currentHand.add(card);

        return this;
    }

    /**
     * Adds specified card to the player's pick.
     *
     * @param card Card number
     */
    public Player addToPick(Integer card) {
        personalPick.add(card);
        return this;
    }

    /**
     * Returns the player's current hand.
     *
     * @return The player's current hand.
     */
    public List<Integer> getCurrentHand() {
        return currentHand;
    }

    /**
     * Returns the player's personal pick.
     *
     * @return The personal pick.
     */
    public List<Integer> getPersonalPick() {
        return personalPick;
    }

    @Override
    public String toString() {
        return "Player{" + "currentHand=" + currentHand + ", personalPick=" + personalPick + '}';
    }

}
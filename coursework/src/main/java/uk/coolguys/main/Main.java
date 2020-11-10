package uk.coolguys.main;

import uk.coolguys.Table;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import static uk.coolguys.utils.PrintUtils.println;

public class Main {

    public static void main(String[] args) throws Exception {
        final Scanner scanner = new Scanner(System.in);

        // Asks for the amount of players
        println("Please enter the number of players: ");
        int nPlayers = scanner.nextInt();

        // Asks for the card deck
        println("Please enter the path to the pick (card deck):");
        File pickFile = new File(scanner.next());
        if (!pickFile.exists() || !pickFile.canRead())
            throw new FileNotFoundException("The path to the pick seems incorrect!");

        List<Integer> cards = Files.readAllLines(Paths.get(pickFile.toURI())).stream().map(Integer::parseInt)
                .collect(Collectors.toList());

        // Récupérer cartes & joueurs
        Table.begin(cards, nPlayers).distribute().playGame();
    }

}

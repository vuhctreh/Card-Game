The rules of the game are as follows: 
Each player is distributed 4 cards from a deck of cards. The
remaining cards are then distributed evenly throughout n piles
where n is the number of players such that each pile has 4 cards
each. Players then discard a card into the pile to their right
and pick a card from a pile to their left. This is considered a
turn. Each player attempts to keep cards matching their player
number (e.g. Player 1 keeps 1s, player 2 keeps 2s, etc...). 
A player wins the game upon gathering 4 cards of the same number 
in their hand.

To run the card game, it is preferable to place the input file
inside the same directory as the jar when running.
Otherwise, use the full file path. Input the number of players 
and file path when prompted. 

Exceptions are handeled if anything but postive integers are input. 
As such, the stack trace and or custom error messages may be 
displayed if letters/negative integers/ other illegal arguments 
are passed through the file.

Note that players will start from 0 with player 0 retaining
cards numbered 0, player 1 retaining cards numbered 1, and
so on so forth.

Player logs will be generated in the same directory as the jar.

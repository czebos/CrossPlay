
# CrossPlay Rules

# Objective:
  Get one gull to the other side of the board
	Before the opponent does
	
  Prevent your opponent from having any moves
  
# Movement:
  A gull(each piece on the board), may move
	  forward once or twice depending on circumstance.
	  
  A gull may take another gull diagonally forward of
	  the opposite color
	  
  A gull may move left or right if it is on its 
    backmost row
    
# Rules:
  White and Black take turns making one move.
	Black starts with the first move.
	
  On the very first move of an individual gull,
    the gull may move forward twice. After the gull
    makes any type of move, it may not move forward 2.
    
  When a gull is taken, it is moved to its backmost row
	  in the column it was in, and it may move like it is its first 
    move again.
    
  If a gull is taken and the square that it is supposed to be 
    moved to is taken, that gull is removed from the board.
    
# User Guide - Cross Play
By: Conrad Zborowski

# Settings
Before the game, the user may choose which players will play the game by clicking the radio buttons on the side

Player 1 and Player 2 may choose from Player, Win AI, and Losing AI

Player gives the opportunity for a human to play the game

Win AI is a MonteCarlo Tree Search AI that plays to win the game

Losing AI is a MonteCarlo Tree Search AI that plays to lose the game

If an AI was selected, the difficulty is determined by the slider

The Slider determines the number of iterations that the computer will simulate

The start button starts the game; the start button may be used to start over the game with different players, but the difficulty of AI will remain the same

# Game Play
A computer will think, then automatically play its move when it is its turn

For a player to move a piece, the player must first select the piece that it wishes to move, then the space that the player wishes to move it

If no piece is selected when a space is clicked, no move will happen. The piece selected is denoted by the red square around it

Similarly, if the move is illegal, the move will not be made

If a piece can take another, then clicking on the piece will not select it, but take it

When a piece is taken it is moved back to the backmost row, unless it is occupied

When a piece makes a legal move, it should animate to the clicked square 

# Special Buttons
At any point, a human player may ask for a hint, and the computer will calculate a very good move for the human. Click the button to get the hint

At any point, the user may click the quit button to exit the game

At any point, the user may click the undo button as many times it would like to undo their most recent move and the opponents most recent move

**********************************************************************************************************
# README Individual Project- CrossPlay

DESIGN CHOICES:

App: This is the class that starts the program and sets up the stage and displays it

PaneOrganizer: This is the Top-Level class. It sets up the game based on the buttons clicked,
and creates the visuals for the GUI. This class also handles button and slider interaction.

Game: This is the class that deals with visuals of the board, where the pieces are located, and 
handles the turn logistics. This class contains both of the Players and the logicBoard. It gives 
the LogicBoard to the players.

LogicBoard: This class keeps track of all the logistics of the board and its moves. It also acts
as the nodes in the MonteCarlo simulation. It keeps track of all the statistics in the MonteCarlo
Simulation. It keeps track of available moves, the actual board, and the win state of the game.

Piece: This class deals with everything related to the gulls themselves. It keeps track of information
about the gull and moves the gull to the appropriate square when called. It also creates it visually.
These are contained within an Array in the LogicBoard class.  

HumanPlayer: This class handles a human player moves. This inherits the player interface, and it is one of
the classes that can play the game. It is contained within the Game class. It handles the clicks
and the calling the movement of the pieces.

SmartAI: This class handles the MonteCarlo Tree Search AI. It inherits the player interface, and its another
one of the classed that can play the game. It is contained within the Game class. This is the premise of the
MonteCarlo Tree Search.

Step 1: Choose Node
The search starts with the Root node, and it gets the best UCT of the children. Once it gets the best of
UCT value of those children, it repeats the process with its children until it gets to a node with no children.
The UCT value is a formula that balances out whether a node should explore a very promising node, or explore
a new one.

Step 2: Expansion:
Once it gets to a node with no children, it creates the children of the node. So, the equivalent is playing out
each possbile move and adding the new board states to the children

Step 3: Simulation
Choose one of the children of the node arbitarily. From that node choose a random move to play out. Repeat this
process until it results in a win/loss. Return the result

Step 4: Back propagation
Once the win or loss is returned, report the statistcs. For all of the subsequent parents of the child node
and the child node, add one playout and a win if the simulation was a win.

Move: This class is soley responsible for keeping track of variables about each move. It keeps track
of Piece, X and Y coordinates where the piece will move to, and the X and Y coordinatees of where 
it came from.

UndoStack: This keeps track of board states and puts them on the stack. This is used to undo moves in the order
that they were put on the stack. 

INTERACTION: 
There are no key interaction for the program. The slider at the beginning
dictates the number of iteration in the computer. To move a piece as a human, click
the piece you want to move, then the square you want to move it to. The buttons on the right
determine the players in the next game. Start starts the game. Undo undoes the last two moves.
Hint gives a good next move, and exit exits the game.

KNOWN BUGS:
There are no known bugs for CrossPlay. If the iterations are too
high for the SmartAI, the program might freeze.
**********************************************************************************************************

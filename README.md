# swap-color

Swap Color is a tiny video game made by Thibault Morvan, programming student.
It is a clone of "Flood It", upgraded with multiplayer.

The project is written in Object Oriented Java, please feel free to comment if you find anything gross in the code.
I will welcome critics and avises with open arms !

LOGS :

+ 26/05/2016 : Game is now entirely playable once :
  Everything works on the "Model" and "Controler" parts of the game, still a few problems on the "View". Loss of connexion is properly handled on both sides of the sockets.
  * Next step : Game can only be played once and have to be shutdown before starting a new game 
    - Handle ServerGame shutdown on game's end
    - Detect open ports instead of always using 2345
    - fix the "concurrent use of ArrayList" for once.
    - also : fix view.
  
  => Last straight line before 1.0 !

+ 12/04/2016 : Creation of the Git Repository :
  The game can already be played in multiplayer, mostly.
  Can launch the game, create or join a game, and play until the end.
  * Known criticals bugs :
    - Victory conditions are not implemented yet.
    - application have to be closed before starting a new game (problem with the dynamic array in the Observable interface of GameWindow.).
    - Server doesn't handle client disconnexion yet.


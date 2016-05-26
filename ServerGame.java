import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedInputStream;

public class ServerGame implements Runnable {
	
	private boolean DEBUG = true;

	private int boardWidth ,boardHeight , nbColor, nbPlayer, nbDeadPlayer = 0, winner = -1, nbBomb;
	private GameBoard board;
	private boolean[] isAlive;
	
	private String[] order = new String[5];
	private Socket[] clientSocket = null;
	private PrintWriter[] writer = null; 
	private BufferedInputStream[] reader = null;
	
//////////////////////////////CONSTRUCTOR////////////////////////////////////
	public ServerGame(int nbPlayer, int nbColor, int height, int width){
		//Preparation des connections
		this.nbPlayer = nbPlayer;
		clientSocket = new Socket[nbPlayer];
		writer = new PrintWriter[nbPlayer];
		reader = new BufferedInputStream[nbPlayer];
		isAlive = new boolean[nbPlayer];
		
		//Preparation du terrain
		this.nbColor = nbColor;
		boardHeight = height;
		boardWidth = width;
		//Bomb a modifier lorsque ce sera parametrable par le joueur.
		nbBomb = 0; //(int) ((height * width) / 16); //En moyenne une bombe tous les lots de 4*4 cases.

		//Initialisation du terrain de jeu
		board = new GameBoard(boardHeight, boardWidth);
		board.initialiseBoard(nbColor, nbPlayer, nbBomb);
		
		if(DEBUG) System.err.println("ServerGame > Construction achevée : hauteur : "+ boardHeight+" largeur : "+boardWidth);
	}
	
////////////////////////////RUNNING////////////////////////////////////////
	public void run(){
		
		
		//Préparation aux connections
		ServerSocket gameServSocket;
		boolean open = false;

		int listeningPort;
		InetAddress listeningAddress;
		
		
		 /*****************************************
		  ******* CREATION DES CONNECTIONS ********
		  ****************************************/

		//if(DEBUG) System.err.println(board.printedBoard());

		 
		 try{

			//ouverture du serveur
			listeningPort = 2345;
			listeningAddress = InetAddress.getLocalHost();	
			gameServSocket  = new ServerSocket(listeningPort, nbPlayer, listeningAddress);
			if(DEBUG) System.err.println("ServerGame > informations de connections envoyées au Main");
			
			//informe le Main pour qu'il puisse créer le joueur 1
			Main.setServerAddress(listeningAddress.getHostAddress());
			Main.setServerPort(Integer.toString(listeningPort));
			
			
			if(DEBUG) System.err.println("ServerGame> En attente de connections sur l'adresse " + listeningAddress + ", Sur le port " + listeningPort);
			
		  	
			for(int i = 0; i < nbPlayer; ++i){
				
				open = true;
				
				while(open){
				
					//Attends une connection
					clientSocket[i] = gameServSocket.accept();
				
					//Si un joueur se connecte, sors de la boucle
					if(clientSocket[i] != null) {
						open = false;
						if(DEBUG) System.err.println("ServerGame > Connexion reçu du joueur" + (i+1));
					}
				
				}
				//Paramètre la nouvelle connection
				reader[i] = new BufferedInputStream(clientSocket[i].getInputStream());
				writer[i] = new PrintWriter(clientSocket[i].getOutputStream());
				isAlive[i] = true;
				
				//fourni son playerID au joueur qui viens de se connecter, avec le nombre de couleur de la partie, la hauteur et la largeur
				writer[i].write("INITIALISE|"+ (i+1)+"|"+ nbColor+"|"+ boardHeight+"|"+boardWidth+"|");
				writer[i].flush();
			}
			
			this.playGame();
			
		 	}catch(UnknownHostException e){
		 		e.printStackTrace();
		 	}catch(IOException e){
		 		e.printStackTrace();
		 	}
		 
		 //gestion du game over
		 
	}
	
	public void playGame(){
		

		//int turnCount = 0;
		int currentPlayer = 0; //Debute a zero pour que la première incrementation fasse commencer au joueur 1
		int newColor;
		String playerInput;
		String[] playerInstruction = new String[5];
		
		//Début des tours de jeu
		while(winner <= 0){
			
			//Initialise le tour
			newColor = -1;
			playerInput = "";
			
			++currentPlayer;
			if(currentPlayer > nbPlayer) currentPlayer = 1;
			
			if(isAlive[currentPlayer - 1]){ //Verifie que le joueur est encore connecté, sinon saute le tour

				order[0] = "TURN";
				order[1] = Integer.toString(currentPlayer);
				order[2] = "A vous de jouer";
				order[3] = "En attente du joueur" + currentPlayer;
				order[4] = board.toString();
				
				//Donne les instructions du tour + la vue
				this.broadCast(order);
				
				if(DEBUG) System.err.println("ServerGame > order sent : "+ order[0]+" for player "+ order[1]);
				if(DEBUG) System.err.println(board.printedBoard());
				
				//Attends la réponse du joueur concerné
				while(playerInput.equals("")){

					try{
						writer[currentPlayer - 1].write("ping");
						writer[currentPlayer - 1].flush();
						playerInput = read(reader[currentPlayer - 1]);
						playerInstruction = this.stringToArray(playerInput);
						
					}catch(IOException e){ //Exception levée en cas de perte de connexion du joueur 
						
						if(DEBUG) System.err.println("ServerGame > Error : " + e.getMessage() );
						
						
						if(nbPlayer - nbDeadPlayer >= 3){
														
							//s'il reste au moins 3 joueurs, tue le joueur.
							this.board.killPlayer(currentPlayer);
							isAlive[currentPlayer - 1] = false;
							++nbDeadPlayer;

							if(DEBUG) System.err.println("ServerGame > player killed : " + currentPlayer );

							
						} else { //S'il ne restait que 2 joueurs, détermine le vainqueur par forfait.

							if(DEBUG) System.err.println("ServerGame > end game, winner is " + winner );
							
							isAlive[currentPlayer - 1] = false;
							for(int i = 0; i < nbPlayer; ++i){
								if(isAlive[i]) winner = i + 1;
							}
							
						}
						
						playerInput = "DEAD";
					}
				}
				
				//lorsqu'une instruction est reçue, joue le coup demandé
				if(playerInstruction[0].equals("PLAY")){
					try{
						newColor = Integer.parseInt(playerInstruction[1]);
					}catch(NumberFormatException e){
						e.printStackTrace();
					}
					
					board.playColor(newColor, currentPlayer);
					if(DEBUG) System.err.println("GameServer > joueur " + currentPlayer + " joue la couleur " + newColor);
				}
				
				//Si aucun vainqueur par forfait, vérifie la présence d'un vainqueur normal.
				if(winner <= 0){
					winner = board.winnerID();
				}
			}
			
			
		}
		//quand un joueur est determiné vainqueur, sort de la boucle de jeu
		order[0] = "END";
		order[1] = Integer.toString( winner);
		order[2] = "Félicitations, vous avez gagné la partie !";
		order[3] = "Le joueur " + winner + " a gagné la partie";
		order[4] = board.toString();
		
		this.broadCast(order);
		if(DEBUG) System.err.println("ServerGame > order sent : "+ order[0]);
	}
	
	
	/**********************************************
	 * METHODES DE COMMUNICATION AVEC LES CLIENTS *
	 **********************************************/
	
	//Methode pour récupérer les données envoyées par le joueur 2
	private String read(BufferedInputStream input) throws IOException{      
	      String response = "";
	      int stream;
	      byte[] b = new byte[4096];
	      stream = input.read(b);
	      response = new String(b, 0, stream);
	      return response;
	}
	
	//Methode pour preparer l'instruction pour l'envois au serveur en une chaine de format "*|*|*|*|*|"
	private String arrayToString (String[] instructions){
		
		String result ="";
		
		if(instructions.length == 5){
			for(int i = 0; i < instructions.length; ++i){
				result += instructions[i] + "|";
			}
		} else {
			System.err.println("ServerGame.arrayToString > la taille de l'array est incorrecte, la fonction retourne une chaine vide.");
		}
		
		return result;
	}
	
	//Recupere une chaine de format "*|*|*|*|*|" et la retranscrit en tableau de chaines utilisables
	private String[] stringToArray (String instructions){
		
		String result[];
		
		result = instructions.split("\\|");
		
		return result;
	}

	//Methode pour envoyer le meme message a tous les joueurs connectés.
	private void broadCast(String[] order){
		String message = this.arrayToString(order);
		
		for(int i = 0; i < nbPlayer; ++i){
			if(isAlive[i]){
				writer[i].write(message);
				writer[i].flush();
			}
		}
	}
}

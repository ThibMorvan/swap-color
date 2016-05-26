import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;
import java.io.Serializable;

public class GameBoard {

	private boolean DEBUG = true;
	
	//Attributs
	private int[][][] board;
	private int nbPlayer, nbColor;

	/***********************
	 **** CONSTRUCTORS *****
	 **********************/
	
	public GameBoard(int height, int width) {
		
		board = new int[height][width][3];
	}
	
	/********************************************
	 ******** Methodes d'initialisation *********
	 *******************************************/
	
	//Méthode publique pour initialiser le tableau
	public void initialiseBoard(int nbColor, int nbPlayers, int nbBomb){
		//Place les couleurs
		this.nbColor = nbColor;
		colorBoard();
		//Place les joueurs et défini leurs territoire
		this.nbPlayer = nbPlayers;
		initialisePlayers();
		//Place les bombes s'il y en a
		if (nbBomb > 0) mineBoard(nbBomb);
	}
	
	//Rempli le tableau de couleurs aléatoires
	private void colorBoard() {
		
		if(DEBUG) System.err.println("GameBoard >> Debut de ColorBoard");
		
		Random dice = new Random();
		
		for (int i = 0; i < board.length; ++i) {
			for (int j = 0; j < board[i].length; ++j) {
				if(isInIndex(i,j)){
					board[i][j][0] = dice.nextInt(nbColor);
				}
			}
		}
		if(DEBUG) System.err.println("GameBoard >> Fin de ColorBoard");
	}
	
	// /!\ //Rempli le tableau d'appartenance à 0 puis place les premières cases de chaque joueur
	private void initialisePlayers(){

		if(DEBUG) System.err.println("GameBoard >> Debut de InitialisePlayers");
			
		for (int i = 0; i < board.length; ++i) {
			for (int j = 0; j < board[i].length; ++j) {
				if(isInIndex(i,j)){
					board[i][j][1] = 0;
				}
			}
		}
		
		/// /!\GESTION DES ERREURS ET CAS PARTICULIERS A FAIRE, REDONDANCE DU JOUEUR 1 
		switch(nbPlayer){
			case 1 :
				board[0][0][1] = 1;
				break;
			case 2 :
				board[0][0][1] = 1;
				board[board.length - 1][board[0].length - 1][1] = 2; 
				break;
			case 4 :
				board[0][0][1] = 1;
				board[0][board[0].length - 1][1] = 2; 
				board[board.length - 1][board[0].length - 1][1] = 3; 
				board[board.length - 1][0][1] = 4;
				break;
			default :
				System.err.println("GameBoard.initialisePlayer() > Nombre de joueurs invalide");
				break;
		}
		
		for (int i = 1; i <= nbPlayer; ++i){
			floodBoard(i);
		}
		
	}
	
	//positionne aléatoirement certain nombre de bombes sur tout le terrain non occupé par des joueurs.
	private void mineBoard(int nbBomb){
		
		Random dice = new Random();
		int row,col,bombType;
		int bombVariety = 2;
		
		while(nbBomb > 0){
			
			//Choisi une position au hasard dans le terrain
			row = dice.nextInt(board.length - 1);
			col = dice.nextInt(board[0].length -1);
			//Choisi un type de bombe au hasard parmi la collection existante. ==> A MODIFIER SI PARAMETRABLE A LA CREATION DU PLATEAU.
			bombType = dice.nextInt(bombVariety) + 1;
			
			//Si la case est libre et n'appartien a personne, pose une bombe
			if(board[row][col][1] == 0 && board[row][col][2] == 0 ) { 
				board[row][col][2] = bombType; 
				nbBomb -= 1;
			}
			
		}
		
	}

	/****************************************
	 * METHODES D'INTERACTION AVEC LE JOUEUR*
	 ****************************************/
	
	//change les couleurs des cases appartenant a un joueur puis étend le territoire via la methode floodBoard()
	public void playColor(int colorPlayed, int player){
		
		if(DEBUG) System.err.println("GameBoard > Joueur " + player + " joue la couleur " + colorPlayed);
		
		for(int i = 0; i < board.length; ++i){
			for(int j = 0; j < board[i].length; ++j){
				if(isInIndex(i,j)){
					
					if(board[i][j][1] == player){
						board[i][j][0] = colorPlayed;
					}
				}
			}
		}
		floodBoard(player);
	}
		
	//Méthode privée pour étendre la propriété d'un joueur après qu'il ai joué.
	//Parcours le tableau en verifiant la couleur d'une case et l'appartenance des cases adjacentes
	//Répète jusqu'à ce qu'il n'y ai plus de modification.
	private void floodBoard(int player){

		if(DEBUG) System.err.println("GameBoard >> Debut de FloodBoard pour le joueur " + player);
		
		int colorPlayed = -1;
		boolean isFlooding = true;
		
		//Determine la couleur du joueur visé
		for(int i = 0; i < board.length; ++i){
			for(int j = 0; j < board[i].length; ++j){
				if(isInIndex(i,j)){
					//Dès que la couleur est trouvée, sors enregistre et sors des boucles.
					if(board[i][j][1] == player){
						colorPlayed = board[i][j][0];
						j = board[i].length; //sors de la boucle colonnes
					}
				}
			}
			if(colorPlayed >= 0) i = board.length; //Si couleur trouvée, sors de la boucle lignes
		}
		
		if(colorPlayed == -1) isFlooding = false; //Cas particulier des joueurs "morts"
				
		while(isFlooding){
			
			isFlooding = false; //Determine s'il reste des modifications a faire.
			
			for(int i = 0; i < board.length; ++i){
				for(int j = 0; j < board[i].length; ++j){
					if(isInIndex(i,j)){
						
						//Traitement des cases concernées
						if(board[i][j][1] == 0 && board[i][j][0] == colorPlayed){
							if((isInIndex(i+1,j) && board[i+1][j][1] == player) ||
									(isInIndex(i-1,j) && board[i-1][j][1] == player) || 
										(isInIndex(i,j+1) && board[i][j+1][1] == player) || 
											(isInIndex(i,j-1) && board[i][j-1][1] == player)){
								
								board[i][j][1] = player;
								isFlooding = true; //Si au moins une case modifiée, refait un passage sur tout le tableau
								//Detecte et active les bombes
								if(board[i][j][2] != 0){
									switch(board[i][j][2]) {
									case 1 :
										detonateBombA(i,j);
										break;
									case 2 :
										detonateBombB(i,j);
										break;
									default :
										System.err.println("GameBoard.floodBoard() > Type de bombe inconnue : " + board[i][j][2]);
										break;
									}
								}
								
							}
						}
					}
				}
			}
		}
		
		
	}
	
	//Methode a n'utiliser que s'il y a au moins 3 joueurs : 
	//supprime le joueur passé en paramètre puis redistribue aleatoirement ses cases
	public void killPlayer(int player){
		
		Random dice = new Random();
		
		if(DEBUG) System.err.println("GameBoard> Start Killing player " + player);
		
		for(int i = 0; i < board.length; ++i){
			for(int j = 0; j < board[i].length; ++j){
				if(board[i][j][1] == player) {
					board[i][j][1] = 0; //Efface la présence du joueur
					board[i][j][0] = dice.nextInt(nbColor); //remélange les couleurs
				}
			}
		}
		
		for(int i = 1; i <= nbPlayer; ++i){
			this.floodBoard(i); //Redistribue le terrain du joueur pour être cohérent
		}
	}
	
	// /!\ //Vérifie s'il y a un vainqueur et si oui lequel. Si retourne -1 : pas de vainqueur. devra retourner 0 pour ex aequo
	public int winnerID(){
		
		int winnerID = -1;
		int[] playerScore = new int[nbPlayer];
		int winningScore = 0;
		
		//Initialise les scores
		for(int i : playerScore) playerScore[i] = 0;
		
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if(isInIndex(i,j) && board[i][j][1] == 0 ){
					return -1; //Si il reste au moins une case qui n'appartien a personne, la partie n'est pas finie. Retourne 0.
				} else {
					++playerScore[board[i][j][1]]; //Compte les cases pour chaque joueur
				}
			}
		}
		
		//Compare au score minimum pour gagner
		///// /!\ A TESTER ET AMELIORER POUR CAS PARTICULIERS COMME LE EX AEQUO
		for(int i = 0; i < playerScore.length; i++){
			if (playerScore[i] >= winningScore){
				winnerID = i;
				winningScore = playerScore[i];
			}
		}
		
		return winnerID;
		
	}
	
	/**************************************
	 ** METHODES D'ACTIVATION DES BOMBES **
	 *************************************/
	
	//Bombe A : Transforme toutes les cases autour en cases du joueur qui l'a déclenché
	private void detonateBombA(int row, int col){
		
		if(DEBUG) System.err.println("GameBoard > Bombe A explose : " + row + ", "+ col);
		
		//Initialise les variables utiles
		int radius = 1;
		int player = board[row][col][1];
		int color = board[row][col][0];
		
		board[row][col][2] = 0; //Supprime la bombe
		
		//Applique l'effet autour de la bombe
		for(int i = (row - radius); i <= row + radius; ++i){
			for (int j = col - radius; j <= col + radius; ++j){
				if(isInIndex(i,j)) {
					board[i][j][1] = player;
					board[i][j][0] = color;
					if(DEBUG) System.err.print("["+i+", "+j+"]");
				}
			}
		}
		floodBoard(player);
	}
	
	//Bombe B : Redistribue aleatoirement les couleurs des cases autour 
	private void detonateBombB(int row, int col){

		if(DEBUG) System.err.print("GameBoard > Bombe B explose : " + row + ", "+ col + ". Cases affectées : ");
		
		//initialise les variables utiles
		int radius = 1;
		int currentPlayer = board[row][col][1];
		Random dice = new Random();
		
		board[row][col][2] = 0; //Supprime la bombe
		
		//Applique l'effet dans le rayon fixé.
		for(int i = (row - radius); i <= row + radius; ++i){
			for (int j = col - radius; j <= col + radius; ++j){
				if(isInIndex(i,j)) {
					board[i][j][1] = 0;
					board[i][j][0] = dice.nextInt(nbColor);
					if(DEBUG) System.err.print("["+i+", "+j+"]");
				}
			}
		}
		
		//Redistribue les cases qui doivent l'être, en finissant par le joueur qui a activé la bombe pour que ce soit un vrai malus.
		for (int i = 1; i <= nbPlayer; ++i){
			if(i != currentPlayer) floodBoard(i);
		}
		floodBoard(currentPlayer);
	}
	
	
	//Methode privée pour vérifier qu'on ne sort pas de l'index des tableaux (securité)
	private boolean isInIndex(int testI, int testJ){
		
		return ((testI >= 0 && testI < board.length) && (testJ >= 0 && testJ < board[testI].length));
	}
	
	/***************************************
	 * Methodes de representation graphique*
	 ***************************************/
		
	//Retourne l'etat actuel du tableau sous la forme d'une ligne de char. de format "c j b,c j b,c j b-c j b,c j b,c j b-c j b,c j b,c j b"
	public String toString(){
		
		String printedBoard= "";
		
			for (int i = 0; i < board.length; i++) {


				for (int j = 0; j < board[i].length; j++) {
					
					for(int k = 0; k < board[i][j].length; ++k){
						//triplet "couleur joueur bombe" délimitées par des espaces.
						printedBoard += board[i][j][k]; 
						
						if (k + 1 != board[i][j].length){
							printedBoard += " ";
						}
					}
					//colonnes délimitées par des virgules.
					if (j + 1 != board[i].length){
						printedBoard += ",";
					}
				}
				//lignes délimitées par des tirets.
				if (i + 1 != board.length){
					printedBoard += "-";
				}
			}
		
		return printedBoard;
	}

	//Methode pour envoyer le tableau. Ne dois être utilisé que pour l'affichage des clients. 
	//Ne dois etre utilisé que pour le DEBUG mode.
	public String printedBoard(){
		String printedBoard= "";
		
		printedBoard += "+";
		for (int i = 0; i < board[0].length - 1; i++) {
			printedBoard += "--";
		}
		printedBoard += "-+\n";

		for (int i = 0; i < board.length; i++) {

			printedBoard += "|";

			for (int j = 0; j < board[i].length; j++) {
				
				if(board[i][j][2]==0){
					printedBoard += board[i][j][0] + "|";
				} else if (board[i][j][2]==1){
					printedBoard += "A" + "|";
				} else if (board[i][j][2]==2){
					printedBoard += "B" + "|";
				}
			}

			printedBoard += "\n";
		}

		printedBoard += "+";
		for (int i = 0; i < board[0].length - 1; i++) {
			printedBoard += "--";
		}
		printedBoard += "-+\n";
		
		
		return printedBoard;
	}
	
}

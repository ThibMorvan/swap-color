
import java.util.ArrayList;

import javax.swing.JFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics;

public class GameWindow extends JFrame implements Observable, Observer {

	private static boolean DEBUG = true;
	
	//Variable de communication entre observer/observables
	private ArrayList<Observer> obsList = new  ArrayList<Observer>();
	private String[] output = new String[5];
	
	private GamePanel gamePan;
	//Variable de references. A supprimer si inutile, sinon decommenter.
	private String nbPlayer,nbColor,boardHeight,boardWidth;
	
	public GameWindow(){

		/////////////////////Initialise window/////////////////////////
		this.setTitle("Flood It Graphical Tests");
		
		this.setSize(800, 600);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		//////////////////////////////////////////////////////////////

		this.mainMenu(true);
		
	}
	
	public void goToPage(String index){
		switch(index){
			case "0" :
				this.mainMenu(false);
				break;
			case "1" :
				this.createMenu();
				break;
			case "2" :
				this.joinMenu();
				break;
			case "3" :
				output[0]="QUIT";
				this.updateObserver();
				break;
			case "4" :
				this.gameMenu();
				break;
			default :
				break;
		}
	}
	
	//Envoie la requete de creation de serveur au Main.
	public void tryCreate(String nbPlayer,String nbColor,String boardHeight,String boardWidth){
		
		this.nbPlayer = nbPlayer;
		this.nbColor = nbColor;
		this.boardHeight = boardHeight;
		this.boardWidth = boardWidth;
		
		output[0] = "CREATE";
		output[1] = nbPlayer;
		output[2] = nbColor;
		output[3] = boardHeight;
		output[4] = boardWidth;
		this.updateObserver();
		
		if(DEBUG) System.err.println("GameWindow> fait une demande de creation de partie : height = "+output[3]+" width = "+output[4]);
		
	}
	
	//Envoie la requete de creation de client au Main.
	public void tryJoin(String adress,String port){
		
		output[0] = "JOIN";
		output[1] = adress;
		output[2] = port;
		this.updateObserver();
		
		if(DEBUG) System.err.println("GameWindow> fait une demande pour rejoindre une partie");
		
	}
	
	public void playMove(String color){
		
		output[0] = "PLAY";
		output[1] = color;
		this.updateObserver();
		
		if(DEBUG) System.err.println("GameWindow> joue la couleur "+color);
	}
	
	/********************************
	 ** GESTION DES PAGES DE MENUS **
	 ********************************/
	private  void mainMenu(boolean init){
		
		if(DEBUG) System.err.println("GameWindow> Fonction MainMenu() lancée");
		
		MainMenuPanel mainMenuPan = new MainMenuPanel();
		this.setContentPane(mainMenuPan);
		this.setVisible(true);
		
		//Réinitialise les observers
		this.delObserver();
		if(!init) Main.ObserveWindow();
		
	}
	
	private void createMenu(){

		CreationPanel creationPan = new CreationPanel();
		//window.getContentPane().removeAll();
		this.setContentPane(creationPan);
		this.setVisible(true);
	}

	private void joinMenu(){

		JoinPanel joinPan = new JoinPanel();
		//window.getContentPane().removeAll();
		this.setContentPane(joinPan);
		this.setVisible(true);
	}

	private void gameMenu(){
		
		if(DEBUG) System.err.println("GameWindow> jeu lancé");

		int parsedNbColor = 0;
		int parsedHeight = 0;
		int parsedWidth = 0;
		
		try{
			parsedNbColor = Integer.parseInt(nbColor);
			parsedHeight = Integer.parseInt(boardHeight);
			parsedWidth = Integer.parseInt(boardWidth);
		} catch (NumberFormatException e){
			e.printStackTrace();
		}
				
		gamePan = new GamePanel(parsedNbColor, parsedHeight, parsedWidth);
		this.setContentPane(gamePan);
		this.setVisible(true);
	}
	
	/**********************************************
	************ OBSERVABLE/OBSERVER **************
	**********************************************/
	
	public void addObserver(Observer obs){
		this.obsList.add(obs);
	}
	public void delObserver(){
		obsList = new ArrayList<Observer>();
	}
	
	//Envois les instructions vers Main et Client.
	public void updateObserver(){
		//s'il n'y a qu'un seul objet dans la liste, ce DOIT être le main. 
		//Traitement spécifique ajouté pour effectuer un Join ou Create sans ramasser un "java.util.ConcurrentModificationException"
		if (obsList.size() == 1){
			obsList.get(0).update(output);
		} else {
			for(Observer obs : obsList){
				obs.update(output);

				if(DEBUG) System.err.println("GameWindow> Output sent : " + output[0]);
			}
		}
	}
	
	//Instructions en provenance du Client
	public void update(String[] order){
		switch(order[0]){
		case "INITIALISE" :
			nbColor = order[2];
			boardHeight = order[3];
			boardWidth = order[4];

			this.goToPage("4");

			this.gamePan.setInfo(order[1]);
			
			break;
		case "GO" :
			//Recupère les infos a afficher et attends le tour du joueur Eventuellement reactive les ColorButtons.
			this.gamePan.setInfo(order[1]);
			this.gamePan.showBoard(organiseBoard(order[2]));
			this.gamePan.repaint();
			this.setVisible(true);
			break;
		case "WAIT" :
			//Recupère les informations a afficher et attends l'instruction GO. Eventuellement desactive les ColorButtons.
			break;
		case "OVER" :
			//Recupère les informations et termine la partie => désactive les ColorButtons.
			break;
		default :
			//Drop and Chill.
			break;
		}
	}

	//Methode pour fournir un nouveau board au GamePanel. Fourni sous forme de String avec : 
	// format du board attendu : "c j b,c j b,c j b-c j b,c j b,c j b-c j b,c j b,c j b"
	public String[][][] organiseBoard(String board){
		
		int height = 0;
		int width = 0;
		
		try{
			height = Integer.parseInt(boardHeight);
			width = Integer.parseInt(boardWidth);
		} catch(NumberFormatException e){
			e.printStackTrace();
		}
		
		if(DEBUG) System.err.println(height + " " + width + " " + board);
		
		String[][][] result = new String[height][width][3];
		String[][] tmpCol = new String[height][width];
		String[] tmpRow = new String[height];
		
		tmpRow = board.split("-");
		
		for(int i = 0; i < tmpRow.length; ++i){
			tmpCol[i] = tmpRow[i].split(",");
		}
		
		for(int i = 0; i < tmpRow.length; ++i){
			for(int j = 0; j < tmpCol[i].length; ++j){
				result[i][j] = tmpCol[i][j].split(" ");
			}
		}
		
		return result;
	}
}



/*****************************************************
***************ACTION LISTENERS***********************
******************************************************/

class MenuListener implements ActionListener {
	
	String index = "";
	
	public MenuListener(int i ){
		index = Integer.toString(i);
	}
	
	public void actionPerformed(ActionEvent e){
		Main.window.goToPage(index);
	}
}

class GameMoveListener implements ActionListener {
	
	String color;
	
	public GameMoveListener(String color){
		this.color = color;
	}
	
	public void actionPerformed(ActionEvent e){
		Main.window.playMove(color);
	}
}

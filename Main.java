
public class Main {
	
	private static boolean DEBUG = false;
	
	private static String[] input = new String[5];
	public static GameWindow window;
	private static String serverAdress;
	private static String serverPort;
	
	public static void main(String[] args){
		
		
		/////////////////Initialisation//////////////////////
		
		window = new GameWindow();
		input[0]="";
		
		ObserveWindow();
		
		/////////////////////////////////////////////////////
		
		
			
	}
	
	//OBSERVER => Observe les changements de page => recupère toutes les infos dans Input puis lis Input[0] pour savoir quoi faire.
	//Fonction a part pour être appelé a chaque retour au menu principal pour supprimer les observer ClientGame sur window.
	public static void ObserveWindow(){
		window.addObserver(new Observer(){
			public void update(String arguments[]){
				for(int i = 0; i < 5; ++i){
					input[i] = arguments[i];
				}
				manageInput();
			}
		});
	}
	
/*********************************************************
********* TRAITEMENT DES UPDATES DE GAMEWINDOW ***********
*********************************************************/
	
	public static void manageInput(){
		
		if(DEBUG) System.err.println("Main> Input received : " + input[0]);
		
		switch (input[0]){
		case "CREATE" : 
			//Demande de creation de partie => récupère les arguments 1 à 5 pour créer la partie
			int[] parsedInput = new int[4];
			try{
				for(int i = 0; i < 4; ++i){
					parsedInput[i] = Integer.parseInt(input[i+1]);
				}
			} catch(NumberFormatException e){
				e.printStackTrace();
			}
			//0 = nbplayer, 1 = nbcolor, 2 = height, 3 = width
			createGame(parsedInput[0],parsedInput[1],parsedInput[2],parsedInput[3]);
			break;
		case "JOIN" : 
			//Demande de connexion a un serveur => input[1] = addresse du serveur, input[2] = port de connexion
			joinGame(input[1], input[2]);
			break;
		case "QUIT" :
			window.dispose();
			System.exit(0);
			break;
		default : 
			//Wait for input
			break;
			
		}
	}
	
	//Cree un thread avec le serveur puis tente de s'y connecter en utilisant la methode joinGame.
	public static void createGame(int nbPlayer, int nbColor, int height, int width){
		
		if(DEBUG) System.err.println("Main> creation de jeu a " + nbPlayer + " joueurs, "+ nbColor + " couleurs, de hauteur"+ height+ " et de largeur "+ width );
		
		Thread t_server = new Thread(new ServerGame(nbPlayer, nbColor, height, width));
		t_server.start();
		
		boolean waiting = true;
		
		while(waiting){
			if(serverAdress != null && serverPort != null) {
				joinGame(serverAdress, serverPort);
				waiting = false;
			} 
		}
	}
	
	//Convertie le Port de connexion en entier et tente de créer un client connecté au serveur, puis créé la liaison Client/Window
	public static void joinGame(String address, String port){
		
		int parsedPort = 0;
		
		try{
			parsedPort = Integer.parseInt(port);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		

		ClientGame client /*= new ClientGame(address, parsedPort)*/;
		Thread t_client = new Thread(client = new ClientGame(address, parsedPort));
		t_client.start();
		client.addObserver(window);
		window.addObserver(client);
		
		if(DEBUG) System.err.println("Main> tentative de connexion a l'addresse "+address+" sur le port "+port);
	}
	
	//Setter pour récupérer le port et l'adresse du serveur
	public static void setServerAddress(String IPAddress) { serverAdress = IPAddress; }
	public static void setServerPort(String port) {serverPort = port; }
	
}

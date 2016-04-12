import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedInputStream;

public class ClientGame implements Runnable, Observable, Observer {
	
	private boolean DEBUG = true;
	
	private ArrayList<Observer> obsList = new  ArrayList<Observer>();
	private String[] instruction = new String[5];

	private String address = "";
	private int port;
	private String playerID = "";
	
	private Socket clientSocket = null;
	private PrintWriter writer = null;
	private BufferedInputStream reader = null;
	
	public ClientGame(String tryAddress, int tryPort){
		address = tryAddress;
		port = tryPort;
		if(DEBUG)	System.err.println("ClientGame > Construction finie ");
	}
	
	
	
	public void run(){
		
		//Dès la creation, tente de se connecter au serveur et parametre la connexion
		try{
			clientSocket = new Socket(InetAddress.getByName(address), port);
			
		if(DEBUG)	System.err.println("ClientGame > Connexion au serveur réussie ");
			
			reader = new BufferedInputStream(clientSocket.getInputStream());
			writer = new PrintWriter(clientSocket.getOutputStream());
			
		} catch(UnknownHostException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		boolean isRunning = true;
		String serverInput = "";
		String[] serverOrder = new String[5];
		
		while(isRunning){
			
			serverInput = "";
			
			//Attend un message en provenance du serveur et le retranscrit en formulaire d'ordre.
			while(serverInput.equals("")){
				try{
					if(reader.available() > 0){
						serverInput = read();
					}
				}catch(IOException e){
					e.printStackTrace();
				}
			}
			
			serverOrder = stringToArray(serverInput);
			
			//Traite la réponse reçue
			switch(serverOrder[0]){
			
			//Premier ordre, recu une seule fois, donne au client son identifiant de joueur.
			//Recupere la valeur, puis fait passer le message au gameWindow 
			case "INITIALISE" :
				playerID = serverOrder[1];
				
				instruction[0] = "INITIALISE";
				instruction[1] = "Vous êtes le joueur " + playerID;
				instruction[2] = serverOrder[2];
				instruction[3] = serverOrder[3];
				instruction[4] = serverOrder[4];
				this.updateObserver();
				
				//Informe le joueur qu'il est connecté et que son numero est playerID
				if(DEBUG) System.err.println("ClientGame > playerID reçu de la part du serveur : "+playerID);
				break;
			
			//Ordre diffusé en broadcast, donne l'etat du plateau et annonce a qui c'est le tour.
			// serverOdrder[1] = joueur qui dois jouer; [2] = informations du joueur qui dois jouer; [3] Informations des autres joueurs; [4] = terrain de jeu. 
			case "TURN" :
				
				//Reordonne les instructions pour n'envoyer que le necessaire au GameWindow
				if(serverOrder[1].equals(playerID)){
					instruction[0] = "GO";
					instruction[1] = serverOrder[2];
					instruction[2] = serverOrder[4];
					this.updateObserver();
				} else {
					instruction[0] = "WAIT";
					instruction[1] = serverOrder[3];
					instruction[2] = serverOrder[4];
					this.updateObserver();
				}
				
				break;
			case "END" : 
				instruction[0] = "OVER";
				if(serverOrder[1].equals(playerID)){
					instruction[1] = serverOrder[2];
					instruction[2] = serverOrder[4];
					this.updateObserver();
				} else {
					instruction[1] = serverOrder[3];
					instruction[2] = serverOrder[4];
					this.updateObserver();
				}
				
			default :
				if(DEBUG) System.err.println("ClientGame > Commande non reconnue");
				break;
			}
		}
		
		//Fin du jeu, affiche le résultat final et la victoire
		try{
			serverInput = read();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		//System.out.println(serverInput);
		
		//Fermeture de la connexion
		writer = null;
		reader = null;
		try{
			clientSocket.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	/*********************************************
	 * METHODES DE COMMUNICATION AVEC LE SERVEUR *
	 *********************************************/
	
	private String read() throws IOException{      
	      String response = "";
	      int stream;
	      byte[] b = new byte[4096];
	      stream = reader.read(b);
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

	/******************************************
	 ** IMPLEMENTATION DU PATTERN OBSERVABLE **
	 ******************************************/
	
	public void addObserver(Observer obs){
		this.obsList.add(obs);
	}
	public void delObserver(){
		obsList = new ArrayList<Observer>();
	}
	public void updateObserver(){
		for(Observer obs : obsList){
			obs.update(instruction);

			if(DEBUG) System.err.println("CLientGame> Output sent : " + instruction[0]);
		}
	}

	public void update(String[] order){
		//Si l'instruction est "play", le client rajoute l'ID du joueur et demande au serveur de jouer son tour.
		if(order[0].equals("PLAY")){
			order[2] = playerID;
			this.writer.write(arrayToString(order));
			this.writer.flush();
		}
	}
	
}

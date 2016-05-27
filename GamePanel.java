import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JSlider;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Graphics;

import javax.swing.JTextField;

public class GamePanel extends JPanel {

	private boolean DEBUG = false;
	
	private String[][][] arrayBoard;
	private JPanel informationPan = new JPanel();
	private JPanel titlePan = new JPanel();
	private JPanel buttonPan = new JPanel();
	private BoardPanel boardPan;
	
	JLabel info = new JLabel("ICI LES INFORMATIONS");
	
	public GamePanel(int nbColor, int height, int width){
		
		//Creation des differents éléments
		JLabel title = new JLabel("PARTIE EN COURS");

		JButton exit = new JButton("Exit");
		exit.addActionListener(new MenuListener(0));
		
		//Créé l'espace des boutons de jeu
		buttonPan.setBackground(Color.lightGray);
		
		//Préparametre les boutons
		Color[] colorShelf = {Color.cyan,Color.magenta,Color.green,Color.orange,Color.yellow,Color.red};
		Dimension colorButtonSize = new Dimension(20,20);
		JButton[] colorButton = new JButton[nbColor];
		
		//Créé les boutons fonctionnels
		for(int i = 0; i < colorButton.length; ++i){
			colorButton[i] = new JButton();
			colorButton[i].setPreferredSize(colorButtonSize);
			colorButton[i].setBackground(colorShelf[i]);
			colorButton[i].addActionListener(new GameMoveListener(Integer.toString(i)));
			
			buttonPan.add(colorButton[i]);
		}
		
		buttonPan.add(exit);
		
		//Creation du panneau d'informations
		informationPan.setBackground(Color.lightGray);
		informationPan.add(info);

		//Creation du panneau de titre
		titlePan.setBackground(Color.lightGray);
		titlePan.add(title);
		
		arrayBoard = new String[10][10][3];
		
		boardPan = new BoardPanel(arrayBoard);
		
		//Assemblage
		this.setBackground(Color.gray);

		this.setLayout(new BorderLayout());
		
		this.add(boardPan, BorderLayout.CENTER);
		this.add(titlePan, BorderLayout.NORTH);		
		this.add(buttonPan, BorderLayout.EAST);
		this.add(informationPan, BorderLayout.SOUTH);
		
	}
	
	public void setInfo(String news){
		this.info.setText(news);
	}
	
	//transmission du tableau au boardPan
	public void setBoard(String[][][] board){
		
		this.arrayBoard = board;
		
		boardPan.setBoardContent(board);
		
		/*if(DEBUG){
			System.err.println("GamePanel.setBoard > informations recues : ");
			
			for(int i = 0; i < board.length; ++i){
				for(int j = 0; j < board[i].length; ++j){
					System.err.print(board[i][j][0]+ " " );
				}
				System.err.println("");
			}
			
		} */
	}
	
	
}

class BoardPanel extends JPanel{
	
	private boolean DEBUG = false;
	
	private String[][][] board;
	private int cellSize;
	private Dimension panelSize;
	
	public BoardPanel(String[][][] board){

		this.board = board;
		panelSize = new Dimension(600,400);
		this.setPreferredSize(panelSize);
		
		if(DEBUG) System.err.println("BoardPanel> panelSize = " + panelSize);

		if (board.length >= board[0].length){
			cellSize = 20;
		} else {
			cellSize = 20;
		}
		
		if(DEBUG) System.err.println("BoardPanel> boardLength = " + board.length + " cellsize = " + cellSize);
		
	} 

	public void setBoardContent(String[][][] board){
		
		this.board = board;
		
	}
	
	public void paintComponent(Graphics g){
		
		g.setColor(Color.darkGray);
		g.fillRect(0, 0, 800, 600);
		
		if(board[0][0][0] != null){
			for(int i = 0; i < board.length; ++i){
				for(int j = 0; j < board[i].length; ++j ){
				
					//Choix de la couleur selon le numero de la case
					// ==> A voir si peut être rendu parametrable
					
					switch(board[i][j][0]){
					case "0" :
						g.setColor(Color.cyan);
						break;
					case "1" :
						g.setColor(Color.magenta);
						break;
					case "2" :
						g.setColor(Color.green);
						break;
					case "3" :
						g.setColor(Color.orange);
						break;
					case "4" :
						g.setColor(Color.yellow);
						break;
					case "5" :
						g.setColor(Color.red);
						break;
					default :
						g.setColor(Color.black);
						break;
					}
				
					g.fillRect(j*(cellSize + 1), i*(cellSize + 1), cellSize, cellSize);
				}
			}
		} else {
			if(DEBUG) System.err.println("BoardPanel> board pas encore reçu");
		}
	}
}
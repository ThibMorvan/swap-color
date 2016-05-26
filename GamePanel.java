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
	
	private BoardPanel boardPan;
	private String[][][] arrayBoard;
	private JPanel informationPan = new JPanel();
	JLabel info = new JLabel("ICI LES INFORMATIONS");
	
	public GamePanel(int nbColor, int height, int width){
		
		//Creation des differents éléments
		JLabel title = new JLabel("PARTIE EN COURS");

		JButton exit = new JButton("Exit");
		exit.addActionListener(new MenuListener(0));
		
		//Créé l'espace des boutons de jeu
		JPanel buttonPan = new JPanel();
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
		
		informationPan.setBackground(Color.lightGray);
		informationPan.add(info);
		
		//Assemblage
		this.setBackground(Color.gray);

		this.setLayout(new BorderLayout());
		
		this.add(title, BorderLayout.NORTH);		
		this.add(buttonPan, BorderLayout.EAST);
		this.add(informationPan, BorderLayout.SOUTH);
		
		
		
	}
	
	public void setInfo(String news){
		this.info.setText(news);
	}

	//Methode d'affichage graphique du tableau de jeu
	public void showBoard(String[][][] board){
		
		this.arrayBoard = board;
		
		Dimension boardSize = new Dimension(400,400);
		int cellSize = boardSize.height/arrayBoard.length;
		
		boardPan = new BoardPanel(arrayBoard,cellSize, getGraphics());
		
		boardPan.setPreferredSize(boardSize);
		
		this.add(boardPan, BorderLayout.CENTER);
		
		if(DEBUG){
			System.err.println("GamePanel.showBoard > informations recues");

			for(int i = 0; i < board.length; ++i){
				for(int j = 0; j < board[i].length; ++j){
					System.err.print(board[i][j][0]+ " " );
				}
				System.err.println("");
			}
			
		}
	}
	
	
}

class BoardPanel extends JPanel{
	
	private String[][][] board;
	private int cellSize;
	
	public BoardPanel(String[][][] board,int cellSize, Graphics g){

		this.board = board;
		this.cellSize = cellSize;
		
		this.setBackground(Color.darkGray);
		
	} 
	
	public void paintComponent(Graphics g){
		
		this.setBackground(Color.darkGray);
		
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
	}
}
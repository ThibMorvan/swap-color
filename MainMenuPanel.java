import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.BoxLayout;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Component;

public class MainMenuPanel extends JPanel {
	
	private boolean DEBUG = false;
	
	public MainMenuPanel(){

		//Creation des différents composants
		JPanel pan_buttons = new JPanel();

		JLabel lab_title = new JLabel("FLOOD IT!");
		
		//Assemblage des composants
		
		//bibliotheque de noms de boutons
		String[] nameShelf = {"Créer Partie", "Rejoindre", "Quitter"};
		
		//Paramétrage de la taille d'un bouton
		Dimension buttonSize = new Dimension(200,70);
		
		//Déclaration des boutons dans un array
		JButton[] menuButton = new JButton[3];
		
		//Initialisation de chaque bouton : couleur, emplacement, taille, action effectuée, ajout dans le panel 
		for(int i = 0; i < menuButton.length; ++i){
			
			menuButton[i] = new JButton(nameShelf[i]);
			menuButton[i].setLocation(300, (150+90*i));
			menuButton[i].setPreferredSize(buttonSize);
			
			menuButton[i].addActionListener(new MenuListener(i+1));
			menuButton[i].setAlignmentY(Component.CENTER_ALIGNMENT);
			pan_buttons.add(menuButton[i]);
		}
		
		pan_buttons.setBackground(Color.gray);
		//Dimension buttons_pan_dim = new Dimension(200, 500);
		//pan_buttons.setPreferredSize(buttons_pan_dim);

		//Assemblage final
		this.setBackground(Color.gray);
		
		this.setLayout(new BorderLayout());
		this.add(lab_title, BorderLayout.NORTH);
		this.add(pan_buttons, BorderLayout.CENTER);
		
		if(DEBUG) System.err.println("MainMenuPanel> constructor fini");
		
	}

}


import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JSlider;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreationPanel extends JPanel {

	public CreationPanel(){
		
		//Creation des differents éléments
		JLabel title = new JLabel("CREATION DE PARTIE");
		
		JLabel labPlayer = new JLabel("Nombre de joueurs");
		String[] playerChoice = {"1", "2", "4"};
		JComboBox choosePlayer = new JComboBox(playerChoice);
		choosePlayer.setSelectedIndex(0);

		JLabel labColor = new JLabel("Nombre de couleurs");
		String[] colorChoice = {"3", "4", "5", "6"};
		JComboBox chooseColor = new JComboBox(colorChoice);
		chooseColor.setSelectedIndex(0);

		JLabel labHeight = new JLabel("Hauteur du terrain");
		JSlider height = new JSlider();
		height.setMaximum(25);
		height.setMinimum(10);
		height.setPaintTicks(true);
		height.setPaintLabels(true);
		height.setMinorTickSpacing(1);
		height.setMajorTickSpacing(5);
		height.setBackground(Color.gray);

		JLabel labWidth = new JLabel("Largeur du terrain");
		JSlider width = new JSlider();
		width.setMaximum(25);
		width.setMinimum(10);
		width.setPaintTicks(true);
		width.setPaintLabels(true);
		width.setMinorTickSpacing(1);
		width.setMajorTickSpacing(5);
		width.setBackground(Color.gray);

		//Cree un serveur ==> un listener va a la classe main pour créer un objet serveur
		JButton validate = new JButton("VALIDER");
		//validate.addActionListener(new MenuListener(4));
		validate.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String nbPlayer = choosePlayer.getSelectedItem().toString();
				String nbColor = chooseColor.getSelectedItem().toString();
				String boardHeight = Integer.toString( height.getValue());
				String boardWidth = Integer.toString( width.getValue());
				Main.window.tryCreate(nbPlayer, nbColor,boardHeight,boardWidth);
			}
		});
		//Reviens sur le main menu ==> menu listener
		JButton exit = new JButton("Exit");
		exit.addActionListener(new MenuListener(0));
		
		//Assemblage
		this.setBackground(Color.gray);
		
		this.add(title);
		this.add(labPlayer);
		this.add(choosePlayer);
		this.add(labColor);
		this.add(chooseColor);
		this.add(labHeight);
		this.add(height);
		this.add(labWidth);
		this.add(width);
		this.add(validate);
		this.add(exit);
		
	}
}
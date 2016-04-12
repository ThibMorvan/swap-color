import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JSlider;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class JoinPanel extends JPanel {

	public JoinPanel(){
		
		//Creation des differents éléments
		JLabel title = new JLabel("REJOINDRE UNE PARTIE");

		JLabel labAddress = new JLabel("Adresse du serveur");
		String address = "";
		JTextField chooseAddress = new JTextField();
		Dimension dimAdress = new Dimension(100,20);
		chooseAddress.setPreferredSize(dimAdress);

		JLabel labPort = new JLabel("Port de connexion");
		String port = "";
		JTextField choosePort = new JTextField("2345");
		Dimension dimPort = new Dimension(40,20);
		choosePort.setPreferredSize(dimPort);
				
		JButton validate = new JButton("VALIDER");
		//validate.addActionListener(new MenuListener(4));
		validate.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.window.tryJoin(chooseAddress.getText(), choosePort.getText());
			}
		});
		
		JButton exit = new JButton("Exit");
		exit.addActionListener(new MenuListener(0));
		
		//Assemblage
		this.setBackground(Color.gray);

		this.add(title);
		this.add(labAddress);
		this.add(chooseAddress);
		this.add(labPort);
		this.add(choosePort);
		this.add(validate);
		this.add(exit);
		
		
		
	}
	
}

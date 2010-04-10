package graph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GraphMap extends JDialog{
	private JPanel canvasp;
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	String imagename = "Images/MAPJPEG.jpg";
	ImageIcon ii = new ImageIcon(imagename);
	
	public GraphMap(JFrame frame){  
		super(frame, true);

		setTitle("Graph to show all the Towns in Kampala and their locations");
		JLabel jLabel7 = new JLabel(" ", ii, JLabel.CENTER);	
		jLabel7.setBounds (0, 0, (screen.width * 3 / 5), (screen.height * 7 / 10));

		canvasp = new JPanel();
		canvasp.setLayout(null);
		canvasp.add("Center", jLabel7);				   

		setLayout(new BorderLayout());

		setSize((screen.width * 3/5), (screen.height * 15/21 ));
		setLocation((screen.width / 12), (screen.height /12));
		getContentPane().add(canvasp, BorderLayout.CENTER);
		setVisible(true);
	}

}

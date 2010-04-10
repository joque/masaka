package graph;

import files.MainFrame;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class NewClientToRegister extends JDialog implements ActionListener{
	
	  private JPanel jpShow = new JPanel();
	  private JButton btnSet;
	  private JTextField txtName;
	  private JLabel lName;
	  private int X,Y;
	  private CompGeomTest former;
	  Dimension screen = 	Toolkit.getDefaultToolkit().getScreenSize();
	  private MainFrame main;
	  
	NewClientToRegister(String titttle, JFrame OwnerForm, final kim2 canvasClose, final CompGeomTest toClose, int x, int y) {
		super(OwnerForm, true);
		main = (MainFrame) OwnerForm;
		setTitle(titttle);

		jpShow.setLayout(null);
		this.X = x;
		this.Y = y;

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {

				setVisible(false);
				canvasClose.setVisible(false);
				toClose.setVisible(false);

				toClose.dispose();
				dispose();

			}
		});

		former = toClose;

		lName = new JLabel("Enter Client Name");
		lName.setBounds(15, 10, 110, 20);
		jpShow.add(lName);

		txtName = new JTextField();
		txtName.setHorizontalAlignment(JTextField.LEFT);
		txtName.setBounds(125, 10, 120, 20);
		jpShow.add(txtName);

		btnSet = new JButton("Save");
		btnSet.setBounds(250, 10, 75, 20);
		btnSet.addActionListener(this);
		jpShow.add(btnSet);

		getContentPane().add(jpShow);
		setSize(350, 75);
		setResizable(false);
		setLocation(x + 100, y + 70);
		setVisible(true);

	}

	public void actionPerformed(ActionEvent ae) {
		Object obj = ae.getSource();

		if (obj == this.btnSet) {	
	    		if(this.txtName.getText()==""||txtName.getText()==null||txtName.getText().length()==0){	
	    			JOptionPane.showMessageDialog(null,"We cant have a Client with no Name !!!","Fill in the Name please",JOptionPane.INFORMATION_MESSAGE);
		         	return;
		         }

		try{         
      boolean append = true;
      FileWriter fileOpen = new FileWriter( "graph/Clien.obj", append );
      PrintWriter cOut = new PrintWriter(new BufferedWriter( fileOpen ));
      
      cOut.println( "'C'"+" '"+this.txtName.getText()+"' "+this.X+" "+this.Y);
      cOut.close();
      
      this.main.kim1.reloadRecord(false);
		setVisible(false);
		former.setVisible(false);
		former.dispose();
		dispose();
		
		}catch(IOException x){System.out.println(x.getMessage());}
	        }
	}

}
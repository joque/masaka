package files;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;

import graph.dialogOrders;

public class MainFrame extends JFrame implements ActionListener {
	
	DefaultTableModel dtmCustomer;
	private JButton delete, Place;
	public JDesktopPane desktop = new JDesktopPane();
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	private JTabbedPane tabbedPane = new JTabbedPane();
	final static String tab1header = " Information Structure ";
	Container contentPane = getContentPane();
	public OrdersMade kim3;
	public TownOrder to;
	public Problems pros;
	public ClientList kim1;
	public VehiclesAvailable kim2;
	public Vector<Vector> jouneyGotWithThisGraph;
	public String forLableDisplay = "Total Jouney Cost = ";
	
	public MainFrame() {
		super("A Dynamic Routing System");
		setIconImage(getToolkit().getImage("Images/stop.gif"));
		setSize(screen);
		
		jouneyGotWithThisGraph = new Vector<Vector>();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				dispose(); 
				System.exit(0); 
			//	 quitApp();

			}
		});

		setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getWidth()) / 2,(Toolkit.getDefaultToolkit().getScreenSize().height - getHeight()) / 2);

		desktop.putClientProperty("JDesktopPane.dragMode", "outline");

		getContentPane().add(desktop, BorderLayout.CENTER);

		setuptab1();
	
	}

	public void setuptab1() {
		JPanel jpShow = new JPanel();
		jpShow = new JPanel() {
			public Dimension getPreferredSize() {
				Dimension size2 = new Dimension();
				size2 = screen;
				return size2;
			}
		};
		jpShow.setBorder(BorderFactory.createRaisedBevelBorder());
		jpShow.setLayout(null);

		Place = new JButton("Make Order");
		Place.setBounds((screen.width*1/3), (screen.height*1/3), 110, 40);
		Place.addActionListener(this);
		jpShow.add(Place);
		
		delete = new JButton("Delete Order");
		delete.setBounds((screen.width*1/3), (screen.height*2/5), 110 , 40);
		delete.addActionListener(this);
		jpShow.add(delete);

		kim1 = new ClientList("CarBrands", this, screen);
		jpShow.add(kim1);

		pros = new Problems("CarBrands", this, screen);
		jpShow.add(pros);
		
		to = new TownOrder("CarBrands", this, screen);
		jpShow.add(to);
		
		kim2 = new VehiclesAvailable("CarBrands", this, screen);
		jpShow.add(kim2);

		kim3 = new OrdersMade("CarBrands", this, screen);
		jpShow.add(kim3);

		desktop.putClientProperty("JDesktopPane.dragMode", "outline");
		getContentPane().add(desktop, BorderLayout.CENTER);

		getContentPane().add(jpShow);

		tabbedPane.addTab(tab1header, jpShow);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		setVisible(true);

	}

	private boolean openChildWindow(String title) {
		JInternalFrame[] childs = desktop.getAllFrames();
		for (int i = 0; i < childs.length; i++) {
			if (childs[i].getTitle().equalsIgnoreCase(title)) {
				childs[i].show();
				return true;
			}
		}
		return false;
	}

	private void quitApp() {
		try {
			// Show a Confirmation Dialog.
			int reply = JOptionPane.showConfirmDialog(this,
					"Do u really want to quit this Dynamic Routing System",
					"System - Exit", JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE);
					
		if (reply == JOptionPane.YES_OPTION) {			
	
		if(this.kim3.rowDataCollections.size() != 0){
		
   int theCheapestGraphCost = Integer.MAX_VALUE;
   
   String directn = "";
   String carDirection = "";
   
   
   for(int i =0;i<this.jouneyGotWithThisGraph.size();i++){
   		if(theCheapestGraphCost > Integer.parseInt(this.jouneyGotWithThisGraph.get(i).get(2).toString())){
   			theCheapestGraphCost = Integer.parseInt(this.jouneyGotWithThisGraph.get(i).get(2).toString());	
   			directn = this.jouneyGotWithThisGraph.get(i).get(0).toString();
   			carDirection = this.jouneyGotWithThisGraph.get(i).get(1).toString();
   		}
	
	}
   

		  	try{
		  		Properties props = new Properties();
     
		  		props.store(new FileOutputStream("graph/Client.obj"),null);
		  	}catch(IOException e){System.out.println("kim    "+e.getMessage());}
     	
     	
     	try {
     	  	FileWriter fileOpen = new FileWriter( "graph/Client.obj", true );
      		PrintWriter cOut = new PrintWriter(new BufferedWriter( fileOpen ));
      		
      	      cOut.println( "'Direction'"+" '"+directn+"' '"+carDirection+"' "+theCheapestGraphCost);

      
          for(int z=0;z<this.kim3.rowDataCollections.size();z++){
     	 
     	      cOut.println( "'order'"+" '"+this.kim3.rowDataCollections.get(z).get(0).toString()+"' "+this.kim3.rowDataCollections.get(z).get(1).toString()+" '"+this.kim3.rowDataCollections.get(z).get(2)+"'");
     	 
      		}
     
      		cOut.close();
      
      		}catch(IOException xp){System.out.println(xp.getMessage());}
     		
			}

				setVisible(false); 
				dispose(); 
				System.exit(0); 
				
			} else if (reply == JOptionPane.NO_OPTION) {

				//return;
				setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			}
		} catch (Exception ex) { }
	}

	public static void main(String args[]) {
		MainFrame student = new MainFrame();
	}

	public void actionPerformed(ActionEvent ae) {
		Object obj = ae.getSource();

	  if (obj == Place) {
			JDialog viewCell = new dialogOrders (this.screen, null, true, "Enter the client Order",this, kim1.tbCustomer.getValueAt(kim1.tbCustomer.getSelectedRow(),0).toString(), kim1.tbCustomer.getValueAt(kim1.tbCustomer.getSelectedRow(),1).toString());
			try{
			desktop.add( viewCell );
			}catch(IllegalArgumentException e){}
		}
		
		if (obj == delete) {
			try{
			
				OrdersMade.deleteRecord(OrdersMade.tbCustomer.getValueAt(OrdersMade.tbCustomer.getSelectedRow(),0).toString(), Integer.parseInt(OrdersMade.tbCustomer.getValueAt(OrdersMade.tbCustomer.getSelectedRow(),1).toString()), OrdersMade.tbCustomer.getValueAt(OrdersMade.tbCustomer.getSelectedRow(),2).toString());
			
				}catch(Exception E){JOptionPane.showMessageDialog(null,"No Records have been selected for deleting, Please Select one first","No Record Selected",JOptionPane.INFORMATION_MESSAGE);}
		}

	}
    
}

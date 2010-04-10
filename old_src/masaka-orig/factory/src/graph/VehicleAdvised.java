package graph;

import files.MainFrame;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class VehicleAdvised extends JDialog implements ActionListener{
	private ClientsBefore myGraph;
	private Dimension screen;
	private JButton btnSet;
	private JPanel jpShow = new JPanel();
	private MainFrame main;
	private JLabel jlabel;
	private  Vector<String> vehiclesUsed;
	private	GridBagLayout gridbag = new GridBagLayout();
	private GridBagConstraints c = new GridBagConstraints();
	private ButtonGroup bg2 = new ButtonGroup();
	private JRadioButton[] vehiclesThatUsedTheTown;
	
	public VehicleAdvised(Dimension screen, ClientsBefore thisGraph, JFrame OwnerForm, Vector<String> vehicles, Vector<JouneyDetails> collectionOfWholeJouney,String townWanted) {
		super(OwnerForm, true);
		this.myGraph = thisGraph;
		this.main = (MainFrame) OwnerForm;
		this.screen = screen;
		setTitle("This Jouney Details");
		this.vehiclesUsed = vehicles;

		jpShow.setLayout(null);

		jpShow.setLayout(gridbag);	
	   	c.fill = GridBagConstraints.HORIZONTAL;

		if((vehicles.size()==0)||(vehicles == null)){
		jlabel = new JLabel("Please select a valid Town where atleast a vehicle must have passed");
		}
		if(vehicles.size() >1){
			vehiclesThatUsedTheTown = new JRadioButton[vehicles.size()];
			jlabel = new JLabel("Please select the Vehicle of focus from above");
			
			for(int i=0;i<vehicles.size();i++){
				vehiclesThatUsedTheTown[i] = new JRadioButton(vehicles.get(i).toString(),false);
					c.fill = GridBagConstraints.WEST;		
					c.gridx = i;       						     
		        	c.gridy = 1; 
					bg2.add(vehiclesThatUsedTheTown[i]);      						
		        	gridbag.setConstraints(vehiclesThatUsedTheTown[i], c);				
		        	jpShow.add(vehiclesThatUsedTheTown[i]);
		        	vehiclesThatUsedTheTown[i].addActionListener(this);
			}		
		}
		
		if(vehicles.size() == 1){
			for(int i=0;i<collectionOfWholeJouney.size();i++){
				if(collectionOfWholeJouney.get(i).carName.contentEquals(vehicles.get(0).toString()) == true){
					for(int j=0;j<collectionOfWholeJouney.get(i).allPlacesVisitedWithLoadStillCarriedOnTruck.size();j++){
		
						if(collectionOfWholeJouney.get(i).allPlacesVisitedWithLoadStillCarriedOnTruck.get(j).startsWith(townWanted)){
						getBestCarToUse(Integer.parseInt(collectionOfWholeJouney.get(i).allPlacesVisitedWithLoadStillCarriedOnTruck.get(j).replace(townWanted+"-", "")));
						break;
						}
					}
					break;
				}
			}
			
		}
		//else{
		c.fill = GridBagConstraints.WEST;				
		c.gridx = 0;       						     
    	c.gridy = 2;       						
    	gridbag.setConstraints(jlabel, c);				
    	jpShow.add(jlabel);
		//}
		
		btnSet = new JButton("Ok");
		c.fill = GridBagConstraints.WEST;				
		c.gridx = 0;       						     
    	c.gridy = 3;       						
    	gridbag.setConstraints(btnSet, c);
		btnSet.addActionListener(this);
		jpShow.add(btnSet);
		
		getContentPane().add(jpShow);
		setSize((screen.width * 2/5), (screen.height/8)+10);
		setResizable(false);
		setLocation(30, (screen.height*6/8));
		setVisible(true);

	}

	public void actionPerformed(ActionEvent ae) {

		Object obj = ae.getSource();

		if (obj == btnSet) {
			if((this.vehiclesUsed.size() == 1)||(this.vehiclesUsed.size() == 0)){
				dispose();
				return;
			}

		/*	if (this.clientOrder == false) {
					int bestCarCapacity = Integer.MAX_VALUE;
					String bestCarNeeded = "";
					for (int i = 0; i < this.main.kim2.rowDataCollect.size(); i++) {
						if ((Integer.parseInt(this.main.kim2.rowDataCollect.get(i).get(1).toString()) <= bestCarCapacity)&& (Integer.parseInt(this.main.kim2.rowDataCollect.get(i).get(1).toString()) >= Integer.parseInt(txtName.getText()))) {
							bestCarCapacity = Integer.parseInt(this.main.kim2.rowDataCollect.get(i).get(1).toString());
							bestCarNeeded = this.main.kim2.rowDataCollect.get(i).get(0).toString();

						}
					}

					JOptionPane.showMessageDialog(null,"Its advisable to go with a " + bestCarNeeded,"Selection of car to take",JOptionPane.INFORMATION_MESSAGE);
					dispose();
					this.myGraph.paintRouteAfterUncertainities(this.myGraph.F,null, this.myGraph.nodeWhereIam);
				}   */
			}
		}

	private void getBestCarToUse(int orderLeftOnTruck) {
		int bestCarCapacity = Integer.MAX_VALUE;
		String bestCarNeeded = "";
		for (int i = 0; i < this.main.kim2.rowDataCollect.size(); i++) {
			if ((Integer.parseInt(this.main.kim2.rowDataCollect.get(i).get(1).toString()) <= bestCarCapacity)&& (Integer.parseInt(this.main.kim2.rowDataCollect.get(i).get(1).toString()) >= orderLeftOnTruck)) {
				bestCarCapacity = Integer.parseInt(this.main.kim2.rowDataCollect.get(i).get(1).toString());
				bestCarNeeded = this.main.kim2.rowDataCollect.get(i).get(0).toString();

			}
		}
		jlabel = new JLabel("Its advisable to go with a " + bestCarNeeded);

		//JOptionPane.showMessageDialog(null,"Its advisable to go with a " + bestCarNeeded,"Selection of car to take",JOptionPane.INFORMATION_MESSAGE);
		this.myGraph.paintRouteAfterUncertainities(this.myGraph.F,null, this.myGraph.nodeWhereIam);
	}

}

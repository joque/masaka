package graph;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DialogJouney extends JDialog{
	
	  private JPanel jpShow = new JPanel();
	  private JLabel joneyCost, daLabels[];
	  Dimension screen = 	Toolkit.getDefaultToolkit().getScreenSize();
	  public JFrame now;
	
	public DialogJouney(int xLocation, int yLocation, String titttle,JFrame OwnerForm, Vector<JouneyDetails> theJouneyVehicle){
	
		super(OwnerForm,true);
		this.now = OwnerForm;
		setTitle(titttle);		
       jpShow.setLayout( null );
       
        daLabels = new JLabel[theJouneyVehicle.size()];
        int totalCost = 0;
        
        for(int i =0;i<daLabels.length;i++){
        	
        	        String nodeNames = "";
                 /* 	for(int j=0;j<theJouneyVehicle.get(i).theNodesSupplied.size();j++){
                   		nodeNames = nodeNames + theJouneyVehicle.get(i).theNodesSupplied.get(j).name +" b="+(theJouneyVehicle.get(i).totalCapacityCarriedRightNow - theJouneyVehicle.get(i).theNodesSupplied.get(j).OrderWanted)+ " ; ";
                   		theJouneyVehicle.get(i).totalCapacityCarriedRightNow = (theJouneyVehicle.get(i).totalCapacityCarriedRightNow - theJouneyVehicle.get(i).theNodesSupplied.get(j).OrderWanted);
                  	}  */
          	for(int j=0;j<theJouneyVehicle.get(i).allPlacesToVisit.size();j++){
           		nodeNames = nodeNames + theJouneyVehicle.get(i).allPlacesToVisit.get(j) + " ; ";
          	}  
          	
          	daLabels[i] = new JLabel(theJouneyVehicle.get(i).carName+"     "+nodeNames+"     km ="+theJouneyVehicle.get(i).NoOfKmCovered+"    cost ="+theJouneyVehicle.get(i).jouneyCost);
           	daLabels[i].setBounds (10, ((25*i)+10), screen.width, 15);
    		jpShow.add(daLabels[i]);
    		
    	totalCost = totalCost + theJouneyVehicle.get(i).jouneyCost;	
	       
       }
       
            joneyCost = new JLabel("Total Jouney Cost = "+totalCost);
           	joneyCost.setBounds (10, ((25*(daLabels.length))+10), 200, 25);
    		jpShow.add(joneyCost);
       
       getContentPane().add( jpShow );
       setSize((screen.width * 10 / 25), (screen.height * 2/5));
	   setResizable(true);
	   setLocation(xLocation, yLocation);
       setVisible( true );
       
	}	
			
}

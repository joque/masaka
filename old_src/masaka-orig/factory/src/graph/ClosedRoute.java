package graph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import files.MainFrame;

public class ClosedRoute extends JDialog implements ActionListener {

	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	private JPanel canvasp;
	public MainFrame main;
	private JButton submit;
    private JDesktopPane desktop = new JDesktopPane();
	private Vector<JouneyDetails> collectionIfUsedThisDirection;
	private JComboBox restOfVehicles[], selectedroad;
	ComboBoxListener cbListener = new ComboBoxListener();
	private String driver="sun.jdbc.odbc.JdbcOdbcDriver";
	String url="jdbc:odbc:moses";
	private Vector<Vector> vehiclesAvailable;
	private ResultSet rs;
	private Vector<Node> townsAll;
	private kimDijkstra pathToNode;
	private Vector<Vector> bestVehicleToHelpBreakDown;
	private String damagedCar;
	private int indexOfDamagedCarTown;
	private int totalNow[], totalremaining;
	private JouneyDetails brokenCar = null;
	public Vector<Node> newColectionOfTownsToBeSupplied;
	private Node F = new Node();
	private String damagedVehicleTownName;
	private UnCertainities uncertainities;
	private boolean firstTime = false;
	private int[] indexOfTownWhereCarIs;
	
	public ClosedRoute(String damagedVehicle, int damagedVehicleTown,  String damagedVehicleTownName, Vector<JouneyDetails> wholeRoute, JFrame OwnerForm){
		super(OwnerForm, true);
		setTitle( "The rest of the vehicles that are in transit");
		collectionIfUsedThisDirection = wholeRoute;
		canvasp = new JPanel();
		canvasp.setLayout(null);
		this.main = (MainFrame)OwnerForm;
		this.vehiclesAvailable = new Vector<Vector>();
		townsAll = new Vector<Node>();
		bestVehicleToHelpBreakDown = new  Vector<Vector>();
		damagedCar = damagedVehicle;
		this.damagedVehicleTownName = damagedVehicleTownName;
		indexOfDamagedCarTown = damagedVehicleTown;
		totalNow = new int[collectionIfUsedThisDirection.size()];
		F.name ="DIPO";
		F.x = 453103;
		F.y = 35156;

		
			try {
 				
 				Class.forName(driver);
 		   		Connection connection=DriverManager.getConnection(url);
 		   		Statement st = connection.createStatement();
 		   		rs = st.executeQuery("select * from Vehicles order by Capacity asc");
 		   		
 		   		while (rs.next()) {
 		   			Vector<String> rowDataSingle = new Vector<String>();
 		   			rowDataSingle.addElement(rs.getString("VehicleName"));
 		   			rowDataSingle.addElement(rs.getString("Capacity"));
 		   			rowDataSingle.addElement(rs.getString("ChargePerDay"));
 		   			rowDataSingle.addElement(rs.getString("ChargesPerKm"));

 		   			this.vehiclesAvailable.addElement(rowDataSingle);
 		   		}
	       			connection.close();

	       		} catch (Exception ex) {System.out.println(ex);	}
	       		
	  		  try {			  					
					Class.forName(driver);					
					Connection connection=DriverManager.getConnection(url);
			   		Statement statement = connection.createStatement();	
					String query = "select * from kla_towns order by OBJECTID Asc";
					ResultSet rs = statement.executeQuery(query);
						
					while (rs.next()) {
						Node town = new Node();
						town.name = rs.getString("NAME");
						town.x = (int)(Double.parseDouble(rs.getString("X_COORD")));
						town.y = (int)(Double.parseDouble(rs.getString("Y_COORD")));

						townsAll.addElement(town);

					}

					rs.close();
					statement.close();
					connection.close();

			  }catch(Exception ex){System.out.println(ex.getMessage());}
	       		
			  computeFromFactory(damagedVehicle, damagedVehicleTown, wholeRoute);
			  
       	JLabel[] carname = new JLabel[collectionIfUsedThisDirection.size()];
       	restOfVehicles = new JComboBox[collectionIfUsedThisDirection.size()];
		
        for(int i =0; i< collectionIfUsedThisDirection.size(); i++){
        	//if(! damagedVehicle.contentEquals(collectionIfUsedThisDirection.get(i).carName)){
        		String tab = "\t";
            		restOfVehicles[i] = new JComboBox();
            		carname[i] = new JLabel(collectionIfUsedThisDirection.get(i).carName+" Jouney");

        			for(int j=0;j<collectionIfUsedThisDirection.get(i).allPlacesToVisit.size();j++){
        				restOfVehicles[i].addItem(collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j)+tab);
        				tab = tab + "\t";
        			}
        			
        			restOfVehicles[i].addItemListener(cbListener);	
        		
        	//}
        }
        
        int id = 0; //for setting boundaries right
        for(int j =0; j< collectionIfUsedThisDirection.size(); j++){
        	if(! damagedVehicle.contentEquals(collectionIfUsedThisDirection.get(j).carName)){
         		carname[j].setBounds (20+(210 * (id)),  10, 100, 25);
        		canvasp.add(carname[j]);
        		restOfVehicles[j].setBounds (120+(200 * (id)), 10, 100, 20);
        		canvasp.add(restOfVehicles[j]);
        		id++;
        	}
        }
		
		submit = new JButton ("Submit");
		submit.setBounds ((250 * (wholeRoute.size()-1))*2/5, 40, 100, 20);
		submit.addActionListener (this);
		canvasp.add("Center", submit);
        
		setLayout(new BorderLayout());
		getContentPane().add(canvasp, BorderLayout.CENTER);

		setSize((250 * (wholeRoute.size()-1)), 105);
		setLocation((screen.width /5), (screen.height * 6/10));
		setResizable(false);
		getContentPane().add(canvasp, BorderLayout.CENTER);
		
		setVisible(true);

}
	
	public ClosedRoute( int[] idOfTownWhereCarIs, Vector<JouneyDetails> vehiclesJouneys, JFrame OwnerForm, UnCertainities uncertainitiesss, int locationWidth, int locationHeight){
		super(OwnerForm, true);
		setTitle( "Select the road that has been closed" );
		collectionIfUsedThisDirection = vehiclesJouneys;
		this.uncertainities = uncertainitiesss;
		canvasp = new JPanel();
		canvasp.setLayout(null);
		this.main = (MainFrame)OwnerForm;
		bestVehicleToHelpBreakDown = new  Vector<Vector>();
		this.vehiclesAvailable = new Vector<Vector>();
		townsAll = new Vector<Node>();
		totalNow = new int[collectionIfUsedThisDirection.size()];
		indexOfTownWhereCarIs = idOfTownWhereCarIs;
		
    	selectedroad = new JComboBox();
		try {	
			Class.forName(driver);
	   		Connection connection=DriverManager.getConnection(url);
	   		Statement st = connection.createStatement();

				rs = st.executeQuery("select * from roads");
				while (rs.next()) {
					selectedroad.addItem(rs.getString("start")+" - "+rs.getString("stop"));
				}
							
			connection.close();
		} catch (Exception ex) {System.out.println(ex);	}
           
       	JLabel carname = new JLabel("Closed road");
       	carname.setBounds (10, 10, 100, 25);
		canvasp.add(carname);
		
		selectedroad.addItemListener(cbListener);	
		selectedroad.setBounds (120, 10, 250, 30);
		canvasp.add(selectedroad);
        
		F.name ="DIPO";
		F.x = 453103;
		F.y = 35156;
	
			try {
 				
 				Class.forName(driver);
 		   		Connection connection=DriverManager.getConnection(url);
 		   		Statement st = connection.createStatement();
 		   		rs = st.executeQuery("select * from Vehicles order by Capacity asc");
 		   		
 		   		while (rs.next()) {
 		   			Vector<String> rowDataSingle = new Vector<String>();
 		   			rowDataSingle.addElement(rs.getString("VehicleName"));
 		   			rowDataSingle.addElement(rs.getString("Capacity"));
 		   			rowDataSingle.addElement(rs.getString("ChargePerDay"));
 		   			rowDataSingle.addElement(rs.getString("ChargesPerKm"));

 		   			this.vehiclesAvailable.addElement(rowDataSingle);
 		   		}
	       			connection.close();

	       		} catch (Exception ex) {System.out.println(ex);	}
	       		
	  		  try {			  					
					Class.forName(driver);					
					Connection connection=DriverManager.getConnection(url);
			   		Statement statement = connection.createStatement();	
					String query = "select * from kla_towns order by OBJECTID Asc";
					ResultSet rs = statement.executeQuery(query);
						
					while (rs.next()) {
						Node town = new Node();
						town.name = rs.getString("NAME");
						town.x = (int)(Double.parseDouble(rs.getString("X_COORD")));
						town.y = (int)(Double.parseDouble(rs.getString("Y_COORD")));

						townsAll.addElement(town);

					}

					rs.close();
					statement.close();
					connection.close();

			  }catch(Exception ex){System.out.println(ex.getMessage());}
        
		setLayout(new BorderLayout());
		getContentPane().add(canvasp, BorderLayout.CENTER);

		setSize(400, 80);
		setLocation( locationWidth, locationHeight);
		setResizable(false);
		getContentPane().add(canvasp, BorderLayout.CENTER);
		
		setVisible(true);

}

	public void actionPerformed (ActionEvent ae) {

	        Object obj = ae.getSource();
	         
	         if (obj == submit){
	     	    for(int i =0; i< (collectionIfUsedThisDirection.size()); i++){
	     	    	if(!(this.restOfVehicles[i].getSelectedItem().toString().trim().contentEquals("DIPO"))){
	        			 if(!(this.damagedCar.contentEquals(collectionIfUsedThisDirection.get(i).carName))){	
	     	    			totalNow[i] = collectionIfUsedThisDirection.get(i).totalCapacityCarriedByThisVehicle;
	    	        		
   	        			 for(int j=0;j<collectionIfUsedThisDirection.get(i).allPlacesToVisit.size();j++){

   	        				 for(int k=0; k<collectionIfUsedThisDirection.get(i).theNodesSupplied.size(); k++){
    	   	     	                   			
    	       		     			if((collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j).toString().contentEquals(collectionIfUsedThisDirection.get(i).theNodesSupplied.get(k).name))&&(collectionIfUsedThisDirection.get(i).theNodesSupplied.get(k).alreadySuppliedAndBalanceDeducted == false)){
    	                   				totalNow[i] = (totalNow[i] - collectionIfUsedThisDirection.get(i).theNodesSupplied.get(k).OrderWanted);
   	       		     					collectionIfUsedThisDirection.get(i).theNodesSupplied.get(k).alreadySuppliedAndBalanceDeducted = true;

    	       		     			}
  	                   			}
  	                   		
	     	        			if((j ==this.restOfVehicles[i].getSelectedIndex())&&(this.restOfVehicles[i].getSelectedItem().toString().trim().contentEquals(collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j)))){
 	     	        				break;
  	                   			}
   	        			 	}
	    	        			
	     	    		}
	     	    	}
	     	    }
	     	    
	     	   for(int i =0; i< (collectionIfUsedThisDirection.size()); i++){
       			 	if(!(this.damagedCar.contentEquals(collectionIfUsedThisDirection.get(i).carName))){
       			 		newColectionOfTownsToBeSupplied = new Vector<Node>();
       			 		int totalDistanceFromWhereIam = 0;
       			 		String routeToCover ="";
       			 		
       			 		for(int d=0; d<this.brokenCar.theNodesSupplied.size(); d++){
       			 			if(this.brokenCar.theNodesSupplied.get(d).alreadySuppliedAndBalanceDeducted == false){
       			 				newColectionOfTownsToBeSupplied.add(this.brokenCar.theNodesSupplied.get(d));
       			 			}
       			 		}
     	       			
       			 		for (int p = 0; p < this.vehiclesAvailable.size(); p++) {
         	       			if(vehiclesAvailable.get(p).get(0).toString().contentEquals(collectionIfUsedThisDirection.get(i).carName)){
         	       				if((Integer.parseInt(vehiclesAvailable.get(p).get(1).toString())) >= totalremaining){
         	       					//capacity is greater than remaining balance of broken vehicle
         	       						if(((Integer.parseInt(vehiclesAvailable.get(p).get(1).toString())) - totalNow[i])>= totalremaining){
         	       							//straight away go to help
         	       						setBestVehicleToHelpBreakDown("", i, routeToCover, totalDistanceFromWhereIam, p);
         	              			 		
         	       						}else{
         	       							//first off load then think of helping
         	   	    	        		
         	         	        			 for(int j =this.restOfVehicles[i].getSelectedIndex();j<collectionIfUsedThisDirection.get(i).allPlacesToVisit.size();j++){
         	         	        				routeToCover = routeToCover + collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j)+ " > ";
         	         	        				 for(int k=0; k<collectionIfUsedThisDirection.get(i).theNodesSupplied.size(); k++){
         	          	   	     	                   			
         	          	       		     			if((collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j).toString().contentEquals(collectionIfUsedThisDirection.get(i).theNodesSupplied.get(k).name))&&(collectionIfUsedThisDirection.get(i).theNodesSupplied.get(k).alreadySuppliedAndBalanceDeducted == false)){
         	          	                   				totalNow[i] = (totalNow[i] - collectionIfUsedThisDirection.get(i).theNodesSupplied.get(k).OrderWanted);
         	         	       		     					collectionIfUsedThisDirection.get(i).theNodesSupplied.get(k).alreadySuppliedAndBalanceDeducted = true;

         	          	       		     			}
         	        	                   		}
         	        	                   		
         	      	     	        			if(((Integer.parseInt(vehiclesAvailable.get(p).get(1).toString())) - totalNow[i])>= totalremaining){
         	       	     	        				break;
         	        	                   			}
         	         	        			 	}
         	         	        			 
         	         	        			 setBestVehicleToHelpBreakDown("", i, routeToCover, totalDistanceFromWhereIam, p);
         	       						}

         	       				}else{
         	       					//capacity is less than remaining balance of broken vehicle

         	       				}
         	       			}
         	       		}  
       			 	}
	     	   }

	     	   int theCheapestJouneyCost = Integer.MAX_VALUE;
	     	   String car = "";
	     	   String carRoute ="";
	     	   int totalDist =0;

	     	   for(int y=0;y<this.bestVehicleToHelpBreakDown.size();y++){
	     		   System.out.println( this.bestVehicleToHelpBreakDown.get(y).get(0).toString()+"    "+this.bestVehicleToHelpBreakDown.get(y).get(3).toString()+"   Km = "+Integer.parseInt(this.bestVehicleToHelpBreakDown.get(y).get(1).toString())+"    Cost = "+Integer.parseInt(this.bestVehicleToHelpBreakDown.get(y).get(2).toString()));
	     	   		if(theCheapestJouneyCost > Integer.parseInt(this.bestVehicleToHelpBreakDown.get(y).get(2).toString())){
	     	   			theCheapestJouneyCost = Integer.parseInt(this.bestVehicleToHelpBreakDown.get(y).get(2).toString());	
	     	   			car = this.bestVehicleToHelpBreakDown.get(y).get(0).toString();
	     	   			carRoute = this.bestVehicleToHelpBreakDown.get(y).get(3).toString();
	     	   			totalDist = Integer.parseInt(this.bestVehicleToHelpBreakDown.get(y).get(1).toString());
	     	   		} 
	     	   }
	     	   
            	JOptionPane.showMessageDialog(null, carRoute+"    Km = "+totalDist+"   Cost = "+theCheapestJouneyCost , car +" Journey",JOptionPane.INFORMATION_MESSAGE);

	     	   
		}
	}
	
	private void setBestVehicleToHelpBreakDown(String townWhereIamAfterExtraServing, int i, String routeToCover, int totalDistanceFromWhereIam, int p){
 		for(int d=0; d<collectionIfUsedThisDirection.get(i).theNodesSupplied.size(); d++){
			if(collectionIfUsedThisDirection.get(i).theNodesSupplied.get(d).alreadySuppliedAndBalanceDeducted == false){
				newColectionOfTownsToBeSupplied.add(collectionIfUsedThisDirection.get(i).theNodesSupplied.get(d));
			}
		}
 		
 		Node start = null;
 		if(townWhereIamAfterExtraServing.contentEquals("")){
 			start = townNode(this.uncertainities.jCombobox2[i].getSelectedItem().toString().trim());
 		}else{
 			start = townNode(townWhereIamAfterExtraServing);
 		}
		Node end = townNode(this.uncertainities.jCombobox2[this.selectedroad.getSelectedIndex()].getSelectedItem().toString().trim()); 
		pathToNode = new kimDijkstra( start, null ,end);
		Node ifUsedThisPath = pathToNode.getDestinationNode(end);
		for(int g=0;g<ifUsedThisPath.nodeNames.size();g++){
			try{
			if(ifUsedThisPath.nodeNames.get(g).contentEquals(ifUsedThisPath.nodeNames.get(g+1))){
				
			}else{
				routeToCover = routeToCover + ifUsedThisPath.nodeNames.get(g)+" > ";
			}
			}catch(ArrayIndexOutOfBoundsException e){
				routeToCover = routeToCover + ifUsedThisPath.nodeNames.get(g)+" > ";
			}		
		}
		for(int g=0;g<ifUsedThisPath.nodeNames.size();g++){
			for(int m=0;m<newColectionOfTownsToBeSupplied.size();m++){
				if((ifUsedThisPath.nodeNames.get(g).contentEquals(newColectionOfTownsToBeSupplied.get(m).name))&&(!ifUsedThisPath.nodeNames.get(g).contentEquals(this.F.name))){
					if((totalNow[i] - newColectionOfTownsToBeSupplied.get(m).OrderWanted)>= 0){
						newColectionOfTownsToBeSupplied.get(m).alreadySuppliedAndBalanceDeducted = true;
						totalNow[i] = totalNow[i] - newColectionOfTownsToBeSupplied.get(m).OrderWanted;
					}
				}				
			}
		}
		totalDistanceFromWhereIam = totalDistanceFromWhereIam + ifUsedThisPath.shortestDist;
		Vector<Vector> ordersToBeSupplied = new Vector<Vector>();
		Vector thisTownWhereIam = new Vector(); 
		thisTownWhereIam.add(this.uncertainities.jCombobox2[this.selectedroad.getSelectedIndex()].getSelectedItem().toString().trim());
		thisTownWhereIam.add(0);
		ordersToBeSupplied.addElement(thisTownWhereIam);   // basically having it as the first element
		int totalCarriedRightNowOnVehicle = 0;
		for(int y=0;y<newColectionOfTownsToBeSupplied.size(); y++){
			if(newColectionOfTownsToBeSupplied.get(y).alreadySuppliedAndBalanceDeducted == false){
				Vector thisTown = new Vector();
				thisTown.add(newColectionOfTownsToBeSupplied.get(y).name);
				thisTown.add(newColectionOfTownsToBeSupplied.get(y).OrderWanted);
				totalCarriedRightNowOnVehicle = totalCarriedRightNowOnVehicle + newColectionOfTownsToBeSupplied.get(y).OrderWanted;
				
				ordersToBeSupplied.addElement(thisTown);
			}
		}
		
		ClientsBefore cheapestWay = new ClientsBefore(ordersToBeSupplied, end, collectionIfUsedThisDirection.get(i).carName, (Integer.parseInt(vehiclesAvailable.get(p).get(3).toString())), totalDistanceFromWhereIam , totalCarriedRightNowOnVehicle);
		for(int o=0;o<cheapestWay.thisJouneyHelping.allPlacesToVisit.size();o++){
		routeToCover = routeToCover + cheapestWay.thisJouneyHelping.allPlacesToVisit.get(o)+" > ";
	}
	
    
    Vector thiscar = new Vector();
    thiscar.add(cheapestWay.thisJouneyHelping.carName);  // the car name
    thiscar.add(cheapestWay.thisJouneyHelping.NoOfKmCovered);  // total dist covered
    thiscar.add(cheapestWay.thisJouneyHelping.jouneyCost);  // cost of journey
    thiscar.add(routeToCover);  //  route

    this.bestVehicleToHelpBreakDown.addElement(thiscar);
		
	}
	
	private void computeFromFactory(String carname, int townId, Vector<JouneyDetails> wholeRoute){
			
		int totalDistanceCoveredByThisVehicle = 0;
	   	String carneeded = "";
    	String routeGot = "";
	   	int parDay = 0;
		int parKm = 0;

	    for(int i =0; i< wholeRoute.size(); i++){
	        if(carname.contentEquals(wholeRoute.get(i).carName)){	
	        	totalremaining = wholeRoute.get(i).totalCapacityCarriedByThisVehicle;

	        			for(int j=0;j<townId;j++){
	                   		for(int k=0; k<wholeRoute.get(i).theNodesSupplied.size(); k++){
	                   			if((wholeRoute.get(i).allPlacesToVisit.get(j).toString().contentEquals(wholeRoute.get(i).theNodesSupplied.get(k).name))&&(wholeRoute.get(i).theNodesSupplied.get(k).alreadySuppliedAndBalanceDeducted == false)){
	                   				totalremaining = (totalremaining - wholeRoute.get(i).theNodesSupplied.get(k).OrderWanted);
	                   				wholeRoute.get(i).theNodesSupplied.get(k).alreadySuppliedAndBalanceDeducted = true;
	                   			}
	                   		}
	        			}
	        			
	        		this.brokenCar = new JouneyDetails(wholeRoute.get(i).allPlacesVisitedWithLoadStillCarriedOnTruck, wholeRoute.get(i).totalCapacityCarriedByThisVehicleRightNow, wholeRoute.get(i).totalCapacityCarriedByThisVehicle, wholeRoute.get(i).carName, wholeRoute.get(i).NoOfKmCovered, wholeRoute.get(i).theNodesSupplied, wholeRoute.get(i).direction, wholeRoute.get(i).jouneyCost, wholeRoute.get(i).allPlacesToVisit);
	        		
  		   		int bestcarcapacity = Integer.MAX_VALUE;
     	       		
     	       		for (int p = 0; p < this.vehiclesAvailable.size(); p++) {
     	       			if ((Integer.parseInt(this.vehiclesAvailable.get(p).get(1).toString()) <= bestcarcapacity)&& (Integer.parseInt(this.vehiclesAvailable.get(p).get(1).toString()) >= totalremaining)) {
     	       				bestcarcapacity = Integer.parseInt(this.vehiclesAvailable.get(p).get(1).toString());
     	       				carneeded = this.vehiclesAvailable.get(p).get(0).toString();
     	     		   		parDay = Integer.parseInt(this.vehiclesAvailable.get(p).get(2).toString());
     	     		   		parKm = Integer.parseInt(this.vehiclesAvailable.get(p).get(3).toString());
     	       			}
     				}
     	       		
     	       		Node whereIam = townNode(wholeRoute.get(i).allPlacesToVisit.get(townId).toString());
     	       		Node DIPO = townNode("DIPO");
     	       		
     				pathToNode = new kimDijkstra( DIPO, null ,whereIam);
     				Node forPathToFollow = pathToNode.getDestinationNode(whereIam);
     				totalDistanceCoveredByThisVehicle = totalDistanceCoveredByThisVehicle + forPathToFollow.shortestDist;
     				for(int r=0;r<forPathToFollow.nodeNames.size();r++){
     					try {
     						if(forPathToFollow.nodeNames.get(r-1).toString().contentEquals(forPathToFollow.nodeNames.get(r).toString())){
     						
     						}else {
     							routeGot = routeGot + forPathToFollow.nodeNames.get(r).toString()+" > ";
     						}
     					}catch(ArrayIndexOutOfBoundsException e){
							routeGot = routeGot + forPathToFollow.nodeNames.get(r).toString()+" > ";
     					}
     				}
     				
       			 for(int k=0; k<wholeRoute.get(i).theNodesSupplied.size(); k++){
	                   			
  	       		     			if((wholeRoute.get(i).theNodesSupplied.get(k).alreadySuppliedAndBalanceDeducted == false)){
	                   	     	                   				
	                   				kimDijkstra path = new kimDijkstra( whereIam, null ,wholeRoute.get(i).theNodesSupplied.get(k));
	                   				whereIam = wholeRoute.get(i).theNodesSupplied.get(k);
		    	     				forPathToFollow = path.getDestinationNode(whereIam);
		    	     				for(int r=0;r<forPathToFollow.nodeNames.size();r++){
		    	     					try {
		    	     						if(forPathToFollow.nodeNames.get(r-1).toString().contentEquals(forPathToFollow.nodeNames.get(r).toString())){
		    	     						
		    	     						}else {
		    	     							routeGot = routeGot + forPathToFollow.nodeNames.get(r).toString()+" > ";
		    	     						}
		    	     					}catch(ArrayIndexOutOfBoundsException e){
	    	     							routeGot = routeGot + forPathToFollow.nodeNames.get(r).toString()+" > ";
		    	     					}
		    	     				}
		    	//     				wholeRoute.get(i).theNodesSupplied.get(k).alreadySuppliedAndBalanceDeducted = true;
		    	     				totalDistanceCoveredByThisVehicle = totalDistanceCoveredByThisVehicle + forPathToFollow.shortestDist;
	                   			}
	               }
       			 
    			 
    			 //returning to the DIPO
     				kimDijkstra path = new kimDijkstra( whereIam, null,wholeRoute.get(i).theNodesSupplied.get(0));
           				whereIam = wholeRoute.get(i).theNodesSupplied.get(0);
	     				forPathToFollow = path.getDestinationNode(whereIam);
	     				for(int r=0;r<forPathToFollow.nodeNames.size();r++){
	     					try {
	     						if(forPathToFollow.nodeNames.get(r-1).toString().contentEquals(forPathToFollow.nodeNames.get(r).toString())){
	     						
	     						}else {
	     							routeGot = routeGot + forPathToFollow.nodeNames.get(r).toString()+" > ";
	     						}
	     					}catch(ArrayIndexOutOfBoundsException e){
     							routeGot = routeGot + forPathToFollow.nodeNames.get(r).toString()+" > ";
	     					}
	     				}
	     				totalDistanceCoveredByThisVehicle = totalDistanceCoveredByThisVehicle + forPathToFollow.shortestDist;
	    	}

	     }
        
        Vector thiscar = new Vector();
        thiscar.add(carneeded);  // the car name
        thiscar.add(totalDistanceCoveredByThisVehicle);  // total dist covered
        thiscar.add(parDay + (totalDistanceCoveredByThisVehicle * parKm));  // cost of journey
        thiscar.add(routeGot);  //  route
   
        this.bestVehicleToHelpBreakDown.addElement(thiscar);
        
	}
	
	private Node townNode(String townName){
		Node nodeWhereIam = null;
		  
	        for(int i=0;i<this.townsAll.size();i++){
	        	if(townName.contentEquals(this.townsAll.get(i).name)){
	        		nodeWhereIam = this.townsAll.get(i);
	        		break;
	        	}
	        }
	        
	        return nodeWhereIam;		
	}
	
	class ComboBoxListener implements ItemListener  {
		
  		public void itemStateChanged(ItemEvent e) {				
  			String sr = (String)e.getItem();
	
  				if ((e.getSource().equals(selectedroad))&&(firstTime == false)){
	
  		     			Vector toBeRemovedRoad = new Vector ();
  		     			String[] closedRoadConnect = selectedroad.getSelectedItem().toString().split(" - ");
  		     			for(int i =0; i<closedRoadConnect.length; i++){
  		     				toBeRemovedRoad.addElement(closedRoadConnect[i].toString());
  		     			}
  		     			
  		     			boolean[] thisVehicleMeetsClosedRoad = new boolean[collectionIfUsedThisDirection.size()];  		     			

  		     	        for(int i =0; i< collectionIfUsedThisDirection.size(); i++){
  		     	        	
  		     	        	thisVehicleMeetsClosedRoad[i] = false;
  		     	       		String routeGot = "";
  		     	       		boolean beginingFactory = false;

  	    	        		 if(!(collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(indexOfTownWhereCarIs[i]).toString().contentEquals("DIPO"))){
  	    	        			 
  	    	        			 for(int id = indexOfTownWhereCarIs[i]; id<collectionIfUsedThisDirection.get(i).allPlacesToVisit.size(); id++){
  	    	        				try{
  	    	        				 if(((collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(id).contentEquals(toBeRemovedRoad.get(0).toString()))&&(collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(1+id).contentEquals(toBeRemovedRoad.get(1).toString()))) ||
  	    	        						((collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(id).contentEquals(toBeRemovedRoad.get(1).toString()))&&(collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(1+id).contentEquals(toBeRemovedRoad.get(0).toString())))){
  	    	        					thisVehicleMeetsClosedRoad[i] = true;
  	    	        					break;
  	    	        				 }
  	    	        				}catch(ArrayIndexOutOfBoundsException eo){}
  	    	        			 }
  	    	        			 
  	    	        			 if( thisVehicleMeetsClosedRoad[i] == true){
  		    	     	       		Node whereIam = townNode(collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(indexOfTownWhereCarIs[i]).toString().trim());
  		    	     	       		
  	    	        			 for(int j=0;j<collectionIfUsedThisDirection.get(i).allPlacesToVisit.size();j++){
  		     	                   		for(int k=0; k<collectionIfUsedThisDirection.get(i).theNodesSupplied.size(); k++){
  		       	   	     	                   			
  		       	       		     			if((collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j).toString().contentEquals(collectionIfUsedThisDirection.get(i).theNodesSupplied.get(k).name))&&(collectionIfUsedThisDirection.get(i).theNodesSupplied.get(k).alreadySuppliedAndBalanceDeducted == false)){
  		       	       		     				if(collectionIfUsedThisDirection.get(i).theNodesSupplied.get(k).name.toString().contentEquals("DIPO")){
  		       	       		     					if(beginingFactory == false){
  		       	       		     						collectionIfUsedThisDirection.get(i).theNodesSupplied.get(k).alreadySuppliedAndBalanceDeducted = true;
  		       	       		     						beginingFactory = true;
  		       	       		     					}else{
  		       	       		     						
  		       	       		     					}  
  		       	       		     				}else{
  		       	       		     					collectionIfUsedThisDirection.get(i).theNodesSupplied.get(k).alreadySuppliedAndBalanceDeducted = true;
  		       	       		     				}
  		     	                   			}
  		     	                   		}
  		     	                   		
  	    	     	        			if(j == indexOfTownWhereCarIs[i]){
  	    	     	        				Vector<String> townToStay = new Vector<String>();

  	    	        	        			 for(int jnext=0; jnext<j; jnext++){
	     		    	   		     	  		townToStay.addElement(collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(jnext));
  	    	        	        			 }
  	    	        	        			collectionIfUsedThisDirection.get(i).allPlacesToVisit = townToStay;
  	    	        	        			 break;
  		     	                   		}
  		     	        			}  
  	    	        			 

  	    	        			 for(int k=0; k<collectionIfUsedThisDirection.get(i).theNodesSupplied.size(); k++){
  		     	                   			
  		       	       		     			if((collectionIfUsedThisDirection.get(i).theNodesSupplied.get(k).alreadySuppliedAndBalanceDeducted == false)){
  		     	                   	     	                   				
  		     	                   				kimDijkstra path = new kimDijkstra( whereIam, toBeRemovedRoad ,collectionIfUsedThisDirection.get(i).theNodesSupplied.get(k));
  		     	                   				whereIam = collectionIfUsedThisDirection.get(i).theNodesSupplied.get(k);
  		     		    	     				Node forPathToFollow = path.getDestinationNode(whereIam);
  		     		    	     				for(int r=0;r<forPathToFollow.nodeNames.size();r++){
  		     		    	     					try {
  		     		    	     						if(forPathToFollow.nodeNames.get(r-1).toString().contentEquals(forPathToFollow.nodeNames.get(r).toString())){
  		     		    	     						
  		     		    	     						}else {
  		     		    	     							routeGot = routeGot + forPathToFollow.nodeNames.get(r).toString()+" > ";
  		     		    	     						}
  		     		    	     					}catch(ArrayIndexOutOfBoundsException ep){
  		     	    	     							routeGot = routeGot + forPathToFollow.nodeNames.get(r).toString()+" > ";
		     		    	     						//collectionIfUsedThisDirection.get(i).allPlacesToVisit.addElement(forPathToFollow.nodeNames.get(r).toString());
  		     		    	     					}
  		     		    	     				}
  		     		    	     				collectionIfUsedThisDirection.get(i).theNodesSupplied.get(k).alreadySupplied = true;

  		     	                   			}
  		     	                   		}
  	    	        			 
  	    	        			 //returning to the DIPO
  	                 				kimDijkstra path = new kimDijkstra( whereIam, toBeRemovedRoad ,collectionIfUsedThisDirection.get(i).theNodesSupplied.get(0));
  		                   				whereIam = collectionIfUsedThisDirection.get(i).theNodesSupplied.get(0);
  			    	     				Node forPathToFollow = path.getDestinationNode(whereIam);
  			    	     				for(int r=0;r<forPathToFollow.nodeNames.size();r++){
  			    	     					try {
  			    	     						if(forPathToFollow.nodeNames.get(r-1).toString().contentEquals(forPathToFollow.nodeNames.get(r).toString())){
  			    	     						
  			    	     						}else {
  			    	     							routeGot = routeGot + forPathToFollow.nodeNames.get(r).toString()+" > ";
	     		    	     						//collectionIfUsedThisDirection.get(i).allPlacesToVisit.addElement(forPathToFollow.nodeNames.get(r).toString());
  			    	     						}
  			    	     					}catch(ArrayIndexOutOfBoundsException ep){
  		    	     							routeGot = routeGot + forPathToFollow.nodeNames.get(r).toString()+" > ";
   		    	     							//collectionIfUsedThisDirection.get(i).allPlacesToVisit.addElement(forPathToFollow.nodeNames.get(r).toString());
  			    	     					}
  			    	     				}
  			    	     				
  			    	     				String[] wam = routeGot.split(" > ");
  			    	     				for(int rNew=0;rNew<wam.length;rNew++){
		    	     						collectionIfUsedThisDirection.get(i).allPlacesToVisit.addElement(wam[rNew]);		
  			    	     				}
  			    	     				
  		     			            	JOptionPane.showMessageDialog(null, routeGot, collectionIfUsedThisDirection.get(i).carName +" Journey",JOptionPane.INFORMATION_MESSAGE);
  	    	        			 }
  	    	        		 }
  	    	        		 
  		     	        }
					
 		     	    firstTime = true;
  				}    
	    }
	}

}

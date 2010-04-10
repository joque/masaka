package graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Timer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import files.BrokenDownCar;
import files.MainFrame;
import files.TimeCounter;

public class UnCertainities extends JDialog implements ActionListener, Runnable {
	
	private Vector<Vector> vehiclesAvailable;
	private ResultSet rs;
	private JPanel canvasp;
	private String driver = "sun.jdbc.odbc.JdbcOdbcDriver";
	String url = "jdbc:odbc:moses";
	public boolean chargesConditions = true;
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	public MainFrame main;
	private JButton btnAdd, closure, btnVarietions, send;
	private Vector<Node> townsAll;
	public Vector<JouneyDetails> collectionIfUsedThisDirection;
	public JComboBox jCombobox2[], carDamaged, selectedDamagedCar, selectedroad;
    private JDesktopPane desktop = new JDesktopPane();
	ComboBoxListener cbListener = new ComboBoxListener();
	private int labelCount = 0;
	private boolean thisClosedRoadOrCarDamaged, hasBrokenDownAndBestGot = false;
	private String Direction ="", carDirection ="";
	private int distanceFromFile = 0;
	private Vector<Vector> townsWhoOrdered;
	private JouneyDetails brokenCarNew;
    private JScrollPane jspTable;
	private JTextArea display ;
	private int carSelected, indexTownSelected;
	private String townName = "";
	private JLabel lblClock, brokenCar;
	private Thread updateClockMain;
	private int[] idOfTownWhereCarIs;
	private TimeCounter timeObject;
	private Timer timeSeconds;
	private ImageIcon imgBrokenCarSho, imgBrokenCarHid;
	private boolean isFinished = true, showimage = true;
	private BrokenDownCar brokenCarDetails = null;
	private int totalNow[], totalremaining;
	public Vector<Node> newColectionOfTownsToBeSupplied;
	private Vector<Vector> bestVehicleToHelpBreakDown;
	private kimDijkstra pathToNode;
	private Node F = new Node();
	
	public UnCertainities(boolean condition, boolean closedRoadOrCarDamaged, JFrame OwnerForm, MainFrame me){
		super(OwnerForm, true);
		this.main = me;
		setTitle( "The Jouney that gave the cheapest cost");
		this.thisClosedRoadOrCarDamaged = closedRoadOrCarDamaged;
		bestVehicleToHelpBreakDown = new  Vector<Vector>();
		townsWhoOrdered = new Vector<Vector>();
		canvasp = new JPanel();
		canvasp.setLayout(null);
		imgBrokenCarSho = new ImageIcon("Images/stop.gif");
		imgBrokenCarHid = new ImageIcon("Images/stoping.gif");
		
		F.name ="DIPO";
		F.x = 453103;
		F.y = 35156;
		
		lblClock = new JLabel("Time: 00:00");
		lblClock.setBounds((screen.width*17/25), 2,100,25);
		lblClock.setForeground( new Color(255,0,0));
		lblClock.setHorizontalAlignment(SwingConstants.RIGHT);
		canvasp.add(lblClock);
		
		brokenCar = new JLabel();
		brokenCar.setBounds((screen.width*13/25), (screen.height*13/25),200,45);
		brokenCar.setHorizontalAlignment(SwingConstants.LEFT);
		canvasp.add(brokenCar);
		
	    timeSeconds = new Timer();
		timeObject = new TimeCounter();		
		timeSeconds.schedule(timeObject, 0, 1*1000);
	
		updateClockMain = new Thread(this, "clock");
		updateClockMain.start();
		
		try {
			InputStream is = new FileInputStream("C:/savedInfo.obj");

			inputSavedStaff(is);
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found.");
		} catch (IOException e) {
			System.err.println("Cannot access file.");
		}
		
		if(closedRoadOrCarDamaged == true){
			graphWorkCarDamaged(20,20,condition,closedRoadOrCarDamaged, this.carDirection, this.Direction);
			closure = new JButton ("Road Closure");
		}
		else{
			graphWorkClosedRoad(20,20,condition,closedRoadOrCarDamaged, this.carDirection, this.Direction);
			closure = new JButton ("Submit");

		}
		
		closure.setBounds ((screen.width*4/25), (screen.height*35/50), 120, 25);
		closure.addActionListener (this);
		canvasp.add("Center", closure);
		
        btnAdd = new JButton ("Veiw Graph");
        btnAdd.setBounds ((screen.width*7/25), (screen.height*35/50), 120, 25);
        btnAdd.addActionListener (this);
		canvasp.add("Center", btnAdd);
		
        btnVarietions = new JButton ("Veiw Varietions");
        btnVarietions.setBounds ((screen.width*10/25), (screen.height*35/50), 130, 25);
        btnVarietions.addActionListener (this);
		canvasp.add("Center", btnVarietions);
		
        send = new JButton ("Send Vehicles");
        send.setBounds ((screen.width*13/25)+10, (screen.height*35/50), 130, 25);
        send.addActionListener (this);
		canvasp.add("Center", send);
        
		setLayout(new BorderLayout());
		getContentPane().add(canvasp, BorderLayout.CENTER);

		setSize((screen.width * 4 / 5), (screen.height * 4/5));
		setLocation((screen.width / 12), (screen.height / 22));
		setResizable(false);
		getContentPane().add(canvasp, BorderLayout.CENTER);
		
		setVisible(true);

}
	
	public void run(){
		
		Thread myThread = updateClockMain;
		
		while(myThread == updateClockMain){
			
			if (!isFinished){
				lblClock.setText("Time : " + getTime());
						try{
					myThread.sleep(1000);
					//System.out.println("kim   after..." + getTime());
				}catch (InterruptedException e){}
				
				if( getTime().toString().contentEquals("01:00")){
	     	       brokenCarDetails = new BrokenDownCar(this.collectionIfUsedThisDirection);
				}
				
				if(brokenCarDetails != null){
					if(showimage == true){
						if((! brokenCarDetails.getBrokenCarDetails().get(1).toString().contentEquals(""))||(brokenCarDetails.getBrokenCarDetails().get(1).toString().length() > 0)){
							brokenCar.setIcon(imgBrokenCarSho);
							brokenCar.setText(brokenCarDetails.getBrokenCarDetails().get(1).toString()+"    Has got a Break Down");
							showimage = false;
							if(hasBrokenDownAndBestGot == false){
								computeFromFactory(brokenCarDetails.getBrokenCarDetails().get(1).toString(), this.idOfTownWhereCarIs[Integer.parseInt(brokenCarDetails.getBrokenCarDetails().get(0).toString())], collectionIfUsedThisDirection);
								bestRouteForbreakdown(brokenCarDetails.getBrokenCarDetails());
								hasBrokenDownAndBestGot = true;
							}
						}
					}else{
						brokenCar.setIcon(imgBrokenCarHid);
						brokenCar.setText(" ");
						showimage = true;
					}
				}

  				for(int i =0; i< collectionIfUsedThisDirection.size(); i++){
  					if(collectionIfUsedThisDirection.get(i).isSuspended == false){
  					collectionIfUsedThisDirection.get(i).startCount();

  					}else{
  		     			idOfTownWhereCarIs[i] = idOfTownWhereCarIs[i] + 1;
  						keepingTrackOfCarAndItsTown(i, idOfTownWhereCarIs[i]);
  						try{
  							collectionIfUsedThisDirection.get(i).changingFromTownToTown(i, collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(idOfTownWhereCarIs[i]).toString(), collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(idOfTownWhereCarIs[i]+1).toString());
  							collectionIfUsedThisDirection.get(i).resume();
  						}catch(ArrayIndexOutOfBoundsException e){
  							collectionIfUsedThisDirection.get(i).stop();
  						}
  					}
  				}   
			}		
		}
		
	}
	
	public String getTime(){
		return timeObject.getTimeFormat(1);
	}
		
	private void graphWorkCarDamaged(int locationWidth, int locationHeight, boolean condition,boolean closedRoadOrCarDamaged, String vehicleDirection, String direction) {

		vehiclesAvailable = new Vector<Vector>();

		try {
			
			Class.forName(driver);
	   		Connection connection=DriverManager.getConnection(url);
	   		Statement st = connection.createStatement();

			if (vehicleDirection.contentEquals("vehiclesAscending")) {
				rs = st.executeQuery("select * from Vehicles order by Capacity Asc");
			} else if (vehicleDirection.contentEquals("vehiclesDescending")) {
				rs = st.executeQuery("select * from Vehicles order by Capacity Desc");
			}

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

		ClientsBefore c = new ClientsBefore(false, locationWidth, locationHeight, condition, closedRoadOrCarDamaged,this.main, direction, (screen.width * 4 / 5), (screen.height * 4 / 5), this.townsWhoOrdered, this.vehiclesAvailable, vehicleDirection );

        int totalCost = 0;
        this.collectionIfUsedThisDirection = c.collectionIfUsedThisDirection;
        idOfTownWhereCarIs = new int[collectionIfUsedThisDirection.size()];
        
        for(int i =0; i< collectionIfUsedThisDirection.size(); i++){
        	String nodeNames = "";
 
        	         for(int j=0;j<c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.size();j++){
        	         	try{
        	         		if( c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j).contentEquals(c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j+1))){
        	         			for(int p=0; p<c.collectionIfUsedThisDirection.get(i).theNodesSupplied.size(); p++){
        	         				if(c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j).contentEquals(c.collectionIfUsedThisDirection.get(i).theNodesSupplied.get(p).name)){
        	         				nodeNames = nodeNames + c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j) + " ( "+  c.collectionIfUsedThisDirection.get(i).theNodesSupplied.get(p).OrderWanted +" ) ; ";
        	         				}
        	         			}
        	         		}else{
        	 	       				if( c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j).contentEquals(c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j-1))){
        	 	        			
        	 	       				}else{
        	 	       					nodeNames = nodeNames + c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j) + " ; ";
        	 	       				}
        	 	        	}
        	         	}catch(ArrayIndexOutOfBoundsException e){
        	 					nodeNames = nodeNames + c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j) + " ; ";
        	         	}
        	          }
        	
        	String wholeString = collectionIfUsedThisDirection.get(i).carName+"     "+nodeNames+"     km ="+collectionIfUsedThisDirection.get(i).NoOfKmCovered+"    cost ="+collectionIfUsedThisDirection.get(i).jouneyCost;
        	int labelsForThisCar = ((wholeString).length()/120);
        	if(((wholeString).length()%120) != 0){
        		labelsForThisCar = labelsForThisCar + 1;
        	}
        	
    		JLabel[] daLabels = new JLabel[labelsForThisCar];

        	for(int k =0; k<labelsForThisCar; k++){
        		try{
        			daLabels[k] = new JLabel(wholeString.substring(120*k, 120*(k+1)));
        			daLabels[k].setBounds(locationWidth, locationHeight + (30*(labelCount + k)), 700, 25);
        			canvasp.add(daLabels[k]);
        		}catch(StringIndexOutOfBoundsException e){
        			daLabels[k] = new JLabel(wholeString.substring(120*k,(120*k)+(wholeString.length()%120)));
        			daLabels[k].setBounds(locationWidth, locationHeight + (30*(labelCount + k)), 700, 25);
        			canvasp.add(daLabels[k]);
        		}

        	}
        	
        labelCount	= labelCount + labelsForThisCar;
    	totalCost = totalCost + collectionIfUsedThisDirection.get(i).jouneyCost;	
	       
       }
       
        	JLabel joneyCost = new JLabel("Total Jouney Cost = "+totalCost);
        	joneyCost.setBounds (locationWidth,  locationHeight + (30*(labelCount + 1)), (screen.width * 10 / 25), 30);
           	canvasp.add(joneyCost);
           	
        	JLabel townselect = new JLabel("Tracking Jouney Progress in relation to Towns arrived in");
        	townselect.setBounds (locationWidth,  locationHeight + (30*(labelCount + 3)), 350, 25);
           	canvasp.add(townselect);
           	
	      	display = new JTextArea();
	        jspTable = new JScrollPane( display );
	        jspTable.setBounds (locationWidth,  locationHeight + (30*(labelCount + 4)), 400, 200);
	        canvasp.add( jspTable );
           	
     /*   	JLabel closedroad = new JLabel("Select the Town where a given Vehicle is");
        	closedroad.setBounds (locationWidth,  locationHeight + (30*(labelCount + 3)), 300, 25);
           	canvasp.add(closedroad);
    		
           	JLabel[] carname = new JLabel[collectionIfUsedThisDirection.size()];
           	jCombobox2 = new JComboBox[collectionIfUsedThisDirection.size()];
           	
            for(int i =0; i< collectionIfUsedThisDirection.size(); i++){
            	jCombobox2[i] = new JComboBox();
            	carname[i] = new JLabel(collectionIfUsedThisDirection.get(i).carName+" Jouney");
            	carname[i].setBounds (locationWidth,  locationHeight + (30*(labelCount + 4 + i)), 100, 25);
               	canvasp.add(carname[i]);
               	
               		String tab = "\t";
        			for(int j=0;j<collectionIfUsedThisDirection.get(i).allPlacesToVisit.size();j++){
        				jCombobox2[i].addItem(collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j)+tab);
        	           	tab = tab + "\t";
        			}
        			
        		jCombobox2[i].addItemListener(cbListener);	
        		jCombobox2[i].setBounds (locationWidth+100,  locationHeight + (30*(labelCount + 4 + i)), 200, 20);
        		canvasp.add(jCombobox2[i]);
           	
            }   */
       	
        labelCount	= labelCount + 4 + collectionIfUsedThisDirection.size();

		getContentPane().add(canvasp, BorderLayout.CENTER);
		//this.vehiclesAvailable.removeAllElements();

	}

	public void actionPerformed (ActionEvent ae) {

	        Object obj = ae.getSource();
	        
	         if (obj == btnAdd){this.main.kim1.graphDisplay();}
	         
	         if (obj == send){
	     			isFinished = false;
	     			timeObject.resetTime();
	     			
	     	        for(int i =0; i< collectionIfUsedThisDirection.size(); i++){ 	        	
		     			idOfTownWhereCarIs[i] = 0;
  						keepingTrackOfCarAndItsTown(i, idOfTownWhereCarIs[i]);
  						collectionIfUsedThisDirection.get(i).changingFromTownToTown(i, collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(idOfTownWhereCarIs[i]).toString(), collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(idOfTownWhereCarIs[i]+1).toString());
	     	        }
	     			
	     	        for(int i =0; i< collectionIfUsedThisDirection.size(); i++){
	     	        	collectionIfUsedThisDirection.get(i).isSuspended = false;
	     	        	collectionIfUsedThisDirection.get(i).start();
	     	        }
	     	        
	     			send.setEnabled(false);

				}
	         
	         if (obj == btnVarietions){
	        	 JDialog viewCell = new	dialogGraphs(false,false,this.main, this.main, true, townsWhoOrdered);
	        	 try{
	        		 desktop.add( viewCell );
	        	 }catch(IllegalArgumentException e){}
	         }
	         
	         if (obj == closure){
					JDialog viewCell = new ClosedRoute(this.idOfTownWhereCarIs, this.collectionIfUsedThisDirection, (JFrame)main, this, this.screen.width/3,  this.screen.height/2);
					try{
						main.desktop.add( viewCell );
					}catch(IllegalArgumentException ex){}

			}

	}
	
	private Node townNode(String townName){
		townsAll = new Vector<Node>();
		Node nodeWhereIam = null;
		
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
		  
	        for(int i=0;i<this.townsAll.size();i++){
	        	if(townName.contentEquals(this.townsAll.get(i).name)){
	        		nodeWhereIam = this.townsAll.get(i);
	        		break;
	        	}
	        }
	        
	        return nodeWhereIam;		
	}
	
	private void bestRouteForbreakdown(Vector BrokenCar){
		totalNow = new int[collectionIfUsedThisDirection.size()];
		
     	    for(int i =0; i< (collectionIfUsedThisDirection.size()); i++){
     	    	if(!(collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(idOfTownWhereCarIs[i]).toString().contentEquals("DIPO"))){
       			 if(!(BrokenCar.get(1).toString().contentEquals(collectionIfUsedThisDirection.get(i).carName))){	
    	    			totalNow[i] = collectionIfUsedThisDirection.get(i).totalCapacityCarriedByThisVehicle;
   	        		
	        			 for(int j=0;j<collectionIfUsedThisDirection.get(i).allPlacesToVisit.size();j++){

	        				 for(int k=0; k<collectionIfUsedThisDirection.get(i).theNodesSupplied.size(); k++){
	   	     	                   			
	       		     			if((collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j).toString().contentEquals(collectionIfUsedThisDirection.get(i).theNodesSupplied.get(k).name))&&(collectionIfUsedThisDirection.get(i).theNodesSupplied.get(k).alreadySuppliedAndBalanceDeducted == false)){
	                   				totalNow[i] = (totalNow[i] - collectionIfUsedThisDirection.get(i).theNodesSupplied.get(k).OrderWanted);
	       		     					collectionIfUsedThisDirection.get(i).theNodesSupplied.get(k).alreadySuppliedAndBalanceDeducted = true;

	       		     			}
	                   			}
	                   		
	        				 	if(j ==this.idOfTownWhereCarIs[i]){
	        				 		break;
	                   			}
	        			 	}
   	        			
    	    		}
    	    	}
     	    	
     	    }
     	    
     	   for(int i =0; i< (collectionIfUsedThisDirection.size()); i++){

  			 if(!(BrokenCar.get(1).toString().contentEquals(collectionIfUsedThisDirection.get(i).carName))){	
   			 		newColectionOfTownsToBeSupplied = new Vector<Node>();
   			 		int totalDistanceFromWhereIam = 0;
   			 		String routeToCover ="";
   			 		
   			 		for(int stillToServe=0; stillToServe<collectionIfUsedThisDirection.get(i).theNodesSupplied.size(); stillToServe++){
   		     			
   			 			if(collectionIfUsedThisDirection.get(i).theNodesSupplied.get(stillToServe).alreadySuppliedAndBalanceDeducted == false){
   			 				newColectionOfTownsToBeSupplied.add(collectionIfUsedThisDirection.get(i).theNodesSupplied.get(stillToServe));
   		     			}
   			 		}
   			 		
   			 		for(int d=0; d<this.brokenCarNew.theNodesSupplied.size(); d++){

   			 			if(this.brokenCarNew.theNodesSupplied.get(d).alreadySuppliedAndBalanceDeducted == false){
   			 				newColectionOfTownsToBeSupplied.add(this.brokenCarNew.theNodesSupplied.get(d));
   			 			}
   			 		}
 	       		//	System.out.println("car  "+ collectionIfUsedThisDirection.get(i).carName +"   towns to b supplied   "+newColectionOfTownsToBeSupplied.size());
   			 		for (int p = 0; p < this.vehiclesAvailable.size(); p++) {

     	       			if(vehiclesAvailable.get(p).get(0).toString().contentEquals(collectionIfUsedThisDirection.get(i).carName)){
     	       				if((Integer.parseInt(vehiclesAvailable.get(p).get(1).toString())) >= totalremaining){
     	       					//capacity is greater than remaining balance of broken vehicle
     	       						if(((Integer.parseInt(vehiclesAvailable.get(p).get(1).toString())) - totalNow[i])>= totalremaining){
     	       							//straight away go to help
     	       						setBestVehicleToHelpBreakDown(BrokenCar, "", i, routeToCover, totalDistanceFromWhereIam, p);
	
     	       						}else{
     	       							//first off load then think of helping
     	   	    	        		String whereIam = "";
     	         	        			 for(int j =this.idOfTownWhereCarIs[i];j<collectionIfUsedThisDirection.get(i).allPlacesToVisit.size();j++){
     	         	        				routeToCover = routeToCover + collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j)+ " > ";
     	         	        				whereIam = collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j);
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
     	         	        			 setBestVehicleToHelpBreakDown(BrokenCar, whereIam, i, routeToCover, totalDistanceFromWhereIam, p);
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
				
				String[] wam = carRoute.split(" > ");
					if((car.contentEquals(this.bestVehicleToHelpBreakDown.get(0).get(0).toString()))&&(wam[0].toString().contentEquals("DIPO"))){
						Vector<String> townsNew = new Vector<String>();
						for(int rNew=0;rNew<wam.length;rNew++){
							townsNew.add(wam[rNew]);
						}
						this.brokenCarNew = new JouneyDetails(car, townsNew);
		        		this.brokenCarNew.start();
					}else{
			     	    for(int i =0; i< (collectionIfUsedThisDirection.size()); i++){
			     	    	if((collectionIfUsedThisDirection.get(i).carName.contentEquals(car.toString()))){
	     	        				Vector<String> townToStay = new Vector<String>();

 	        	        			 for(int jnext=0; jnext<idOfTownWhereCarIs[i]; jnext++){
		    	   		     	  		townToStay.addElement(collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(jnext));
 	        	        			 }
 	        	        			collectionIfUsedThisDirection.get(i).allPlacesToVisit = townToStay;
 	        	        			
								for(int rNew=0;rNew<wam.length;rNew++){
				    				collectionIfUsedThisDirection.get(i).allPlacesToVisit.addElement(wam[rNew]);
								}
			     	    	}
			     	    }					
					}
     	   
        	JOptionPane.showMessageDialog(null, carRoute+"    Km = "+totalDist+"   Cost = "+theCheapestJouneyCost , car +" Journey",JOptionPane.INFORMATION_MESSAGE);

     	   
	}
	
	private void graphWorkClosedRoad(int locationWidth, int locationHeight, boolean condition,boolean closedRoadOrCarDamaged, String vehicleDirection, String direction) {

		vehiclesAvailable = new Vector<Vector>();

		try {
			
			Class.forName(driver);
	   		Connection connection=DriverManager.getConnection(url);
	   		Statement st = connection.createStatement();

			if (vehicleDirection.contentEquals("vehiclesAscending")) {
				rs = st.executeQuery("select * from Vehicles order by Capacity Asc");
			} else if (vehicleDirection.contentEquals("vehiclesDescending")) {
				rs = st.executeQuery("select * from Vehicles order by Capacity Desc");
			}

			try {
				while (rs.next()) {
					Vector<String> rowDataSingle = new Vector<String>();
					rowDataSingle.addElement(rs.getString("VehicleName"));
					rowDataSingle.addElement(rs.getString("Capacity"));
					rowDataSingle.addElement(rs.getString("ChargePerDay"));
					rowDataSingle.addElement(rs.getString("ChargesPerKm"));

					this.vehiclesAvailable.addElement(rowDataSingle);
				}

			//	rs.close();
			//	st.close();
			} catch (Exception e) {System.out.println(e);}
			
			connection.close();

		} catch (Exception ex) {System.out.println(ex);	}

		ClientsBefore c = new ClientsBefore(false, locationWidth, locationHeight, condition, closedRoadOrCarDamaged,this.main, direction, (screen.width * 4 / 5), (screen.height * 4 / 5), this.townsWhoOrdered,	this.vehiclesAvailable, vehicleDirection );

        int totalCost = 0;
        this.collectionIfUsedThisDirection = c.collectionIfUsedThisDirection;

        for(int i =0; i< collectionIfUsedThisDirection.size(); i++){
        	
        	        String nodeNames = "";
 
        	         for(int j=0;j<c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.size();j++){
        	         	try{
        	         		if( c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j).contentEquals(c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j+1))){
        	         			for(int p=0; p<c.collectionIfUsedThisDirection.get(i).theNodesSupplied.size(); p++){
        	         				if(c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j).contentEquals(c.collectionIfUsedThisDirection.get(i).theNodesSupplied.get(p).name)){
        	         				nodeNames = nodeNames + c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j) + " ( "+  c.collectionIfUsedThisDirection.get(i).theNodesSupplied.get(p).OrderWanted +" ) ; ";
        	         				}
        	         			}
        	         		}else{
        	 	       				if( c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j).contentEquals(c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j-1))){
        	 	        			
        	 	       				}else{
        	 	       					nodeNames = nodeNames + c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j) + " ; ";
        	 	       				}
        	 	        	}
        	         	}catch(ArrayIndexOutOfBoundsException e){
        	 					nodeNames = nodeNames + c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j) + " ; ";
        	         	}
        	          }
        	
        	String wholeString = collectionIfUsedThisDirection.get(i).carName+"     "+nodeNames+"     km ="+collectionIfUsedThisDirection.get(i).NoOfKmCovered+"    cost ="+collectionIfUsedThisDirection.get(i).jouneyCost;
        	int labelsForThisCar = ((wholeString).length()/120);
        	if(((wholeString).length()%120) != 0){
        		labelsForThisCar = labelsForThisCar + 1;
        	}
        	
    		JLabel[] daLabels = new JLabel[labelsForThisCar];

        	for(int k =0; k<labelsForThisCar; k++){
        		try{
        			daLabels[k] = new JLabel(wholeString.substring(120*k, 120*(k+1)));
        			daLabels[k].setBounds(locationWidth, locationHeight + (30*(labelCount + k)), 700, 25);
        			canvasp.add(daLabels[k]);
        		}catch(StringIndexOutOfBoundsException e){
        			daLabels[k] = new JLabel(wholeString.substring(120*k,(120*k)+(wholeString.length()%120)));
        			daLabels[k].setBounds(locationWidth, locationHeight + (30*(labelCount + k)), 700, 25);
        			canvasp.add(daLabels[k]);
        		}

        	}
        	
        labelCount	= labelCount + labelsForThisCar;
    	totalCost = totalCost + collectionIfUsedThisDirection.get(i).jouneyCost;	
	       
       }
       
        	JLabel joneyCost = new JLabel("Total Jouney Cost = "+totalCost);
        	joneyCost.setBounds (locationWidth,  locationHeight + (30*(labelCount + 1)), (screen.width * 10 / 25), 30);
           	canvasp.add(joneyCost);
		
    	JLabel townselect = new JLabel("Select the town where you are right now");
    	townselect.setBounds (locationWidth + 400,  locationHeight + (30*(labelCount + 3)), 300, 25);
       	canvasp.add(townselect);
       	
    	JLabel closedroad = new JLabel("Select the road that has been closed");
    	closedroad.setBounds (locationWidth,  locationHeight + (30*(labelCount + 3)), 300, 25);
       	canvasp.add(closedroad);
       	
    	JLabel closedroadselected = new JLabel("Closed road");
    	closedroadselected.setBounds (locationWidth,  locationHeight + (30*(labelCount + 4)), 100, 25);
       	canvasp.add(closedroadselected);
       	
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
		
		selectedroad.addItemListener(cbListener);	
		selectedroad.setBounds (locationWidth+85,  locationHeight + (30*(labelCount + 4)), 250, 30);
		canvasp.add(selectedroad);
       	
       	JLabel[] carname = new JLabel[collectionIfUsedThisDirection.size()];
       	jCombobox2 = new JComboBox[collectionIfUsedThisDirection.size()];
		
        for(int i =0; i< collectionIfUsedThisDirection.size(); i++){
        	jCombobox2[i] = new JComboBox();
        	carname[i] = new JLabel(collectionIfUsedThisDirection.get(i).carName+" Jouney");
        	carname[i].setBounds (locationWidth + 400,  locationHeight + (30*(labelCount + 4 + i)), 100, 25);
           	canvasp.add(carname[i]);
           	
           		String tab = "\t";
    			for(int j=0;j<collectionIfUsedThisDirection.get(i).allPlacesToVisit.size();j++){
    				jCombobox2[i].addItem(collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j)+tab);
    	           	tab = tab + "\t";
    			}
    			
    		jCombobox2[i].addItemListener(cbListener);	
    		jCombobox2[i].setBounds (locationWidth+500,  locationHeight + (30*(labelCount + 4 + i)), 200, 20);
    		canvasp.add(jCombobox2[i]);
       	
        }
       	
        labelCount	= labelCount + 4 + collectionIfUsedThisDirection.size();

		getContentPane().add(canvasp, BorderLayout.CENTER);
		this.vehiclesAvailable.removeAllElements();

	}

	class ComboBoxListener implements ItemListener  {

  		public void itemStateChanged(ItemEvent e) {				
  			String sr = (String)e.getItem();

  			if(thisClosedRoadOrCarDamaged == true){
  				for(int i =0; i< collectionIfUsedThisDirection.size(); i++){

  					if (e.getSource().equals(jCombobox2[i])){
  						townWhereVehicleIs(jCombobox2[i].getSelectedItem().toString().trim(), jCombobox2[i].getSelectedIndex(), i);
  						break;
  					}               	           	
  				}
  			}
 
	    }
	}

	private void inputSavedStaff(InputStream is) throws IOException {
		String s;

		Reader r = new BufferedReader(new InputStreamReader(is));
		StreamTokenizer st = new StreamTokenizer(r);
		st.commentChar('#');

		boolean b = true;

		try {

			while (b) {
				st.nextToken();
				s = st.sval;

				if (s.equals("Direction")) {
					st.nextToken();
					this.Direction = st.sval;
					st.nextToken();
					this.carDirection = st.sval;
					st.nextToken();
					this.distanceFromFile = (int) st.nval;

				} else if (s.equals("order")) {
					Vector order = new Vector();
					st.nextToken();
					order.add(st.sval);
					st.nextToken();
					order.add((int) st.nval);

					this.townsWhoOrdered.addElement(order);
				}

			}

		} catch (NullPointerException x) {}

	}

	private void townWhereVehicleIs(String town, int selectedTownIndex, int vehicleSelected){
		if((carSelected == vehicleSelected)&&(selectedTownIndex == indexTownSelected)&&(town.contentEquals(townName))){
			
		}else{
			try{
				display.append( "\n" +collectionIfUsedThisDirection.get(vehicleSelected).carName +"  is now at  "+  jCombobox2[vehicleSelected].getSelectedItem().toString().trim() + "  heading next to  " +  jCombobox2[vehicleSelected].getItemAt(jCombobox2[vehicleSelected].getSelectedIndex()+1).toString().trim());
				carSelected = vehicleSelected;
				indexTownSelected = selectedTownIndex;
				townName = town;
			}catch(NullPointerException e){
				display.append( "\n" +collectionIfUsedThisDirection.get(vehicleSelected).carName +"  is now at  "+  jCombobox2[vehicleSelected].getSelectedItem().toString().trim());
				carSelected = vehicleSelected;
				indexTownSelected = selectedTownIndex;
				townName = town;
			}
		}
	}

	private void keepingTrackOfCarAndItsTown(int vehicleNo, int townId){
		//for(int j=0;j<collectionIfUsedThisDirection.get(vehicleNo).allPlacesToVisit.size();j++){
			try{
				display.append( "\n" +collectionIfUsedThisDirection.get(vehicleNo).carName +"  is now at  "+  collectionIfUsedThisDirection.get(vehicleNo).allPlacesToVisit.get(townId).toString() + "  heading next to  " +  collectionIfUsedThisDirection.get(vehicleNo).allPlacesToVisit.get(townId+1).toString());
			}catch(ArrayIndexOutOfBoundsException e){
				try{
					display.append( "\n" +collectionIfUsedThisDirection.get(vehicleNo).carName +"  is now back to  "+  collectionIfUsedThisDirection.get(vehicleNo).allPlacesToVisit.get(townId).toString());
				}catch(ArrayIndexOutOfBoundsException ee){}
			}

		//}
	}
	
	private void setBestVehicleToHelpBreakDown(Vector BrokenCar, String townWhereIamAfterExtraServing, int i, String routeToCover, int totalDistanceFromWhereIam, int p){
 		for(int d=0; d<collectionIfUsedThisDirection.get(i).theNodesSupplied.size(); d++){
			if(collectionIfUsedThisDirection.get(i).theNodesSupplied.get(d).alreadySuppliedAndBalanceDeducted == false){
				newColectionOfTownsToBeSupplied.add(collectionIfUsedThisDirection.get(i).theNodesSupplied.get(d));
			}
		}
 		
 		Node start = null;
 		if(townWhereIamAfterExtraServing.contentEquals("")){
 			start = townNode(this.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(this.idOfTownWhereCarIs[i]).toString().trim());
 		}else{
 			start = townNode(townWhereIamAfterExtraServing);
 		}
		Node end = townNode(this.collectionIfUsedThisDirection.get(Integer.parseInt(BrokenCar.get(0).toString())).allPlacesToVisit.get(this.idOfTownWhereCarIs[Integer.parseInt(BrokenCar.get(0).toString())]).toString()); 
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
		thisTownWhereIam.add(this.collectionIfUsedThisDirection.get(Integer.parseInt(BrokenCar.get(0).toString())).allPlacesToVisit.get(this.idOfTownWhereCarIs[Integer.parseInt(BrokenCar.get(0).toString())]).toString());
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
	        		
	        		wholeRoute.get(i).stop();
	        		this.brokenCarNew = new JouneyDetails(wholeRoute.get(i).allPlacesVisitedWithLoadStillCarriedOnTruck, wholeRoute.get(i).totalCapacityCarriedByThisVehicleRightNow, wholeRoute.get(i).totalCapacityCarriedByThisVehicle, wholeRoute.get(i).carName, wholeRoute.get(i).NoOfKmCovered, wholeRoute.get(i).theNodesSupplied, wholeRoute.get(i).direction, wholeRoute.get(i).jouneyCost, wholeRoute.get(i).allPlacesToVisit);
	        		
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
		
}

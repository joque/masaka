package graph;

import files.MainFrame;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Event;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class ClientsBefore extends Canvas {

	int width, height;
	private Color[] kimColors = {Color.blue, Color.cyan, Color.green, Color.orange, Color.yellow, Color.black, Color.red};
	public Vector<Node> clientsAll;
	public Vector<Vector> clientsRoads;  
	public Vector<Node> clientsWithOrders;
	public Vector<Node> townsAll;
	private kimDijkstra pathToNode;
	private boolean firstTime = false;
	public Node F = new Node();
	private Vector<Vector> theGroupOfVehiclesNeeded;
	private int carNo = 0;
	private Vector<Vector> vehiclesAvailable; 
	private String Direction;
	public Vector<JouneyDetails> collectionIfUsedThisDirection = new Vector<JouneyDetails>();
	public MainFrame main;
	public String vehicleDirection;
	private Node[] vectorWhoseTotalDistWillBePopulated;
	private boolean uncertainities, jouneyProblem;
	private boolean clickFirst = false, clickSecond = false, nowRepaint = false, graphSmallOrBig;
	private int x1,y1,x2,y2,x3,y3;
	private Node nodeGotForShortestDist;
	public Node nodeWhereIam, roadClosedToThisNode, nodeWhereIHaveToDropNext;
	public int totalCapacityCarriedByThisVehicle =0;
	private static Connection connection;
	private static final String driver="sun.jdbc.odbc.JdbcOdbcDriver";
	String url="jdbc:odbc:moses";
	private int locationHeight;
	private int locationWidth;
	public JouneyDetails thisJouneyHelping;
	
	public ClientsBefore( Vector<Vector> orders, Node firstOne, String carName, int kmCost, int kmFromWhereIamToBreakDown, int totalNow) {
		clientsAll = new Vector<Node>();
		townsAll = new Vector<Node>();
		clientsRoads = new Vector<Vector>();
		clientsWithOrders = new Vector<Node>();
		
		F.name ="DIPO";
		F.x = 453103;
		F.y = 35156;
		
		input_graphNeighbours();
		
		computeAngles(orders,firstOne);
		
		sort(this.clientsWithOrders);
		
		thisJouneyHelping = new JouneyDetails();
		thisJouneyHelping.carName = carName;
		thisJouneyHelping.totalCapacityCarriedByThisVehicleRightNow = totalNow;
     
		for(int j=0;j<this.clientsWithOrders.size();j++){
			thisJouneyHelping.theNodesSupplied.addElement(clientsWithOrders.get(j));
		}
		thisJouneyHelping.theNodesSupplied.addElement(this.F);

		Node nodeToBeFollowed = populatesTotalDistanceIfFollowedAnyNode(this.clientsWithOrders, true, firstOne);
		
		try{
			thisJouneyHelping.NoOfKmCovered = nodeToBeFollowed.totalDistanceIfFollowedThisNode + kmFromWhereIamToBreakDown;
		}catch(NullPointerException e){}		
		thisJouneyHelping.jouneyCost = ((thisJouneyHelping.NoOfKmCovered + kmFromWhereIamToBreakDown) * kmCost);
					
		paintTheRoadsToBeTaken(thisJouneyHelping, null, null, this.clientsWithOrders, firstOne, nodeToBeFollowed, this.kimColors[1], (2*1)+1, true);	

	}
	
	public ClientsBefore(boolean smallOrBigGraph, int locationWidth ,int locationHeight, boolean condition,boolean closedRoadOrCarDamaged, MainFrame me, String direction, int cw, int ch, Vector<Vector> orders, Vector<Vector> vehiclesList, String vehicleDirect) {
		this.main = me;
		this.uncertainities = condition;
		this.jouneyProblem  = closedRoadOrCarDamaged;
		this.setSize(width = cw, height = ch);
		this.setLocation(locationWidth,locationHeight);
		this.graphSmallOrBig = smallOrBigGraph;
		this.locationWidth = locationWidth;
		this.locationHeight = locationHeight;
		clientsAll = new Vector<Node>();
		townsAll = new Vector<Node>();
		clientsRoads = new Vector<Vector>();
		clientsWithOrders = new Vector<Node>();
		theGroupOfVehiclesNeeded = new Vector<Vector>();
		this.vehiclesAvailable = vehiclesList;	
		//this.forJouneyDetails = forJouneyCollection;
		this.Direction = direction;
		this.vehicleDirection = vehicleDirect;
			
			F.name ="DIPO";
			F.x = 453103;
			F.y = 35156;

			input_graphNeighbours();
		
		computeAngles(orders,F);
		
		sort(this.clientsWithOrders);
		
		for(int i=0;i<this.clientsWithOrders.size();i++){
			
			if(this.clientsWithOrders.get(i).alreadySupplied != true){
				setTheGroupOfVehiclesNeeded(this.carNo);

			}
		}
		
		borrowedFromPaint(false);
		
		setBackground(Color.white);
		
	}
	
	private Node populatesTotalDistanceIfFollowedAnyNode(Vector<Node> nodesToSupply, boolean helpingJouney, Node startNodeNotFactory){
	
		this.vectorWhoseTotalDistWillBePopulated = new Node[nodesToSupply.size()];
		
		for(int i=0;i<nodesToSupply.size();i++){
			this.vectorWhoseTotalDistWillBePopulated[i] = nodesToSupply.get(i);
		}

		if(helpingJouney == false){
	
			for(int i=1;i<(nodesToSupply.size()-1);i++){
				Vector<Node> whatWillBeRemovedFromInProcess = new Vector<Node>();
				for(int j=0;j<this.vectorWhoseTotalDistWillBePopulated.length;j++){
					whatWillBeRemovedFromInProcess.addElement(this.vectorWhoseTotalDistWillBePopulated[j]);
				}
			
				recussivelyFindDistIfThisNodeUsed(nodesToSupply.get(0), vectorWhoseTotalDistWillBePopulated[i], whatWillBeRemovedFromInProcess, vectorWhoseTotalDistWillBePopulated[i], helpingJouney);
			}
		}else{
			
			for(int i=0;i<(nodesToSupply.size());i++){
				Vector<Node> whatWillBeRemovedFromInProcess = new Vector<Node>();
				for(int j=0;j<this.vectorWhoseTotalDistWillBePopulated.length;j++){
					whatWillBeRemovedFromInProcess.addElement(this.vectorWhoseTotalDistWillBePopulated[j]);
				}
			
				recussivelyFindDistIfThisNodeUsed(startNodeNotFactory, vectorWhoseTotalDistWillBePopulated[i], whatWillBeRemovedFromInProcess, vectorWhoseTotalDistWillBePopulated[i], helpingJouney);
			}
			
		}
		 	
		int pathThatWillRequireSmallestDistance = Integer.MAX_VALUE;
		int routeToBeFollowed = 0;

		for(int i=1;i<(this.vectorWhoseTotalDistWillBePopulated.length-1);i++){
		 		if(pathThatWillRequireSmallestDistance > this.vectorWhoseTotalDistWillBePopulated[i].totalDistanceIfFollowedThisNode){
		 			pathThatWillRequireSmallestDistance = this.vectorWhoseTotalDistWillBePopulated[i].totalDistanceIfFollowedThisNode;
		 			
		 			routeToBeFollowed = i;	
		 		}
		 		
		 	}
		 	
			try{
		 	return this.vectorWhoseTotalDistWillBePopulated[routeToBeFollowed];
			}catch(ArrayIndexOutOfBoundsException e){return null;}
	}
	
	private void recussivelyFindDistIfThisNodeUsed(Node start, Node end, Vector<Node> toTravelThru, Node innitialNode, boolean helpingJouney){
			pathToNode = new kimDijkstra( start, null ,end);

			Vector<Node> nodeToCheck = toTravelThru;
			Node ifUsedThisPath = pathToNode.getDestinationNode(end);

			nodeToCheck.remove(nodeToCheck.get(0));
						
			for(int i=0;i<ifUsedThisPath.nodeNames.size();i++){
				for(int j=0;j<toTravelThru.size();j++){
					if((ifUsedThisPath.nodeNames.get(i).contentEquals(toTravelThru.get(j).name))&&(!ifUsedThisPath.nodeNames.get(i).contentEquals(this.F.name))){
						nodeToCheck.remove(toTravelThru.get(j));
					}				
				}
			}
			
			innitialNode.totalDistanceIfFollowedThisNode += ifUsedThisPath.shortestDist;
		
		if(nodeToCheck.size() != 0){

			recussivelyFindDistIfThisNodeUsed(end, nodeToCheck.get(0), nodeToCheck, innitialNode, helpingJouney);	
		}
		
		if(helpingJouney == true){
			pathToNode = new kimDijkstra( end, null ,this.F);
			Node toFactory = pathToNode.getDestinationNode(this.F);
			innitialNode.totalDistanceIfFollowedThisNode += toFactory.shortestDist;
		}
			
	}
	
	private void setTheGroupOfVehiclesNeeded(int theNo){
		int NoOfCar = theNo;
		for(int i=0;i<this.vehiclesAvailable.size();i++){
			Vector car = new Vector();
			 Vector<Node> carNodes = clientsToBeSuppliedByThisCar(this.clientsWithOrders, Integer.parseInt(vehiclesAvailable.get(i).get(1).toString()));
			if(carNodes.size() > 2){

				car.addElement(carNodes);
				car.addElement(this.vehiclesAvailable.get(i).get(0).toString());  //name
				car.addElement(NoOfCar);
				car.addElement(this.vehiclesAvailable.get(i).get(2).toString());  //the parDay
				car.addElement(this.vehiclesAvailable.get(i).get(3).toString()); //the parKm
				car.addElement(this.totalCapacityCarriedByThisVehicle);
				
				NoOfCar++;
				this.totalCapacityCarriedByThisVehicle = 0;
				
				this.theGroupOfVehiclesNeeded.addElement(car);
						
			}
		
		}
		
		this.carNo = NoOfCar;
	}
	
	private Vector<Node> clientsToBeSuppliedByThisCar(Vector<Node> clientsToBeSupplied,int vehicleCapacity){
			
				int orderSoFar = 0;
				Vector<Node> listThisCarIsToSupply = new Vector<Node>();
				
				listThisCarIsToSupply.addElement(this.F);
				
			if(this.Direction.contentEquals("clockwise")){	
				for(int j=0;j<clientsToBeSupplied.size();j++){
						
						if((orderSoFar < vehicleCapacity )&&((orderSoFar + clientsToBeSupplied.get(j).OrderWanted)<=vehicleCapacity) &&( clientsToBeSupplied.get(j).alreadySupplied==false)){
							orderSoFar = orderSoFar + clientsToBeSupplied.get(j).OrderWanted;
							clientsToBeSupplied.get(j).alreadySupplied = true;
							listThisCarIsToSupply.addElement(clientsToBeSupplied.get(j));
					
						}
				
				}
						}
		else if(this.Direction.contentEquals("anticlockwise")){
		for(int j=(clientsToBeSupplied.size()-1); j >=0; j--){

						if((orderSoFar < vehicleCapacity )&&((orderSoFar + clientsToBeSupplied.get(j).OrderWanted)<=vehicleCapacity) &&( clientsToBeSupplied.get(j).alreadySupplied==false)){
							orderSoFar = orderSoFar + clientsToBeSupplied.get(j).OrderWanted;
							clientsToBeSupplied.get(j).alreadySupplied = true;
							listThisCarIsToSupply.addElement(clientsToBeSupplied.get(j));
					
						}
		}
		}
				this.totalCapacityCarriedByThisVehicle = orderSoFar;
				listThisCarIsToSupply.addElement(this.F);
			
		
		return listThisCarIsToSupply;
	}

	private void paintNode(Graphics g, Node n, FontMetrics fm, boolean bigOrSmallGraph) {
		
		int x,y;
		if(bigOrSmallGraph == true){
			 x = n.x ;
			 y = n.y;
		}else{
			x = n.x *49/81;
			y = n.y*61/116;
		}
		int w = fm.stringWidth(n.name) + 10;
		int h = fm.getHeight() + 4;
		n.w = w;
		n.h = h;

		//g.setColor(Color.black);
		//g.drawRect(x - w / 2, y - h / 2, w, h);
		
		g.setColor(getBackground());
		g.fillRect(x - w / 2 + 1, y - h / 2 + 1, w - 1, h - 1);

		g.setColor(Color.black);
		g.drawString(n.name, x - (w - 10) / 2, (y - (h - 4) / 2)+ fm.getAscent());
		
		g.setColor(Color.red);
		for(int i=0;i<this.clientsWithOrders.size();i++){
			if((this.clientsWithOrders.get(i).x == n.x)&&(this.clientsWithOrders.get(i).y==n.y)&&(this.clientsWithOrders.get(i).name.contentEquals(n.name)))	
				g.drawString(String.valueOf(this.clientsWithOrders.get(i).OrderWanted),((x - (w - 10) / 2)-3), ((y - (h - 4) / 2)+ fm.getAscent()+10));

		}
		
		g.setColor(getBackground());
		
	}

	public void paintRoads(Graphics g, Vector road, FontMetrics fm, boolean bigOrSmallGraph) {
	
		Node node1 = null;
		Node node2 = null;
		
		for (int i = 0; i < this.clientsAll.size(); i++){
			
			if(this.clientsAll.get(i).name.contentEquals(road.get(0).toString())){
				node1 = this.clientsAll.get(i);
			}
				
			if(this.clientsAll.get(i).name.contentEquals(road.get(1).toString())){
				node2 = this.clientsAll.get(i);
			}

		}
		g.setColor(Color.lightGray);
		
		if(bigOrSmallGraph == false){
		g.drawRect(((int)((node1.x+node2.x)*49/162)), ((int)((node1.y + node2.y)*61/232)), 10, 4);
		g.drawLine(node1.x *49/81,node1.y*61/116,node2.x*49/81,node2.y*61/116);
		g.drawString(road.get(2).toString(), ((int)((node1.x+node2.x)*49/162)) , ((int)((node1.y + node2.y)*61/232)));
		}else{
			g.drawRect(((int)((node1.x+node2.x)/2)), ((int)((node1.y + node2.y)/2)), 10, 4);
			g.drawLine(node1.x ,node1.y ,node2.x ,node2.y);
			g.drawString(road.get(2).toString(), ((int)((node1.x+node2.x)/2)) , ((int)((node1.y + node2.y)/2)));
		}
	}

	public void paint(Graphics g) {
		FontMetrics fm = g.getFontMetrics();
	
	/*	for (int i = 0; i < this.clientsRoads.size(); i++){
			paintRoads(g,this.clientsRoads.get(i), fm, this.graphSmallOrBig);
		}
		
		for (int i = 0; i < this.clientsAll.size(); i++){
			paintNode(g,this.clientsAll.get(i), fm, this.graphSmallOrBig);
		}   */

		if(this.nowRepaint == false){			
		
		int totalCostOfJouneysToBeMade = 0;
		
		for(int i=0; i<this.theGroupOfVehiclesNeeded.size();i++){
				Vector<Node> theGotVector = (Vector<Node>)this.theGroupOfVehiclesNeeded.get(i).get(0);
				Vector<Node> extractedCollect = new Vector<Node>();
				JouneyDetails thisJouney = new JouneyDetails();
				//thisJouney.theNodesSupplied = theGotVector;
				thisJouney.carName = this.theGroupOfVehiclesNeeded.get(i).get(1).toString();
				thisJouney.direction = this.Direction;
				thisJouney.totalCapacityCarriedByThisVehicle = Integer.parseInt(this.theGroupOfVehiclesNeeded.get(i).get(5).toString());
				thisJouney.totalCapacityCarriedByThisVehicleRightNow = Integer.parseInt(this.theGroupOfVehiclesNeeded.get(i).get(5).toString());

			for(int j=0;j<theGotVector.size();j++){
				extractedCollect.addElement(theGotVector.get(j));
				thisJouney.theNodesSupplied.addElement(theGotVector.get(j));
			}
			
			int carN = Integer.parseInt(this.theGroupOfVehiclesNeeded.get(i).get(2).toString());

			Node nodeToBeFollowed = populatesTotalDistanceIfFollowedAnyNode(extractedCollect, false, null);
			
	try{
				thisJouney.NoOfKmCovered = nodeToBeFollowed.totalDistanceIfFollowedThisNode;
	}catch(NullPointerException e){}		
				thisJouney.jouneyCost = (thisJouney.NoOfKmCovered * Integer.parseInt(this.theGroupOfVehiclesNeeded.get(i).get(4).toString())) + Integer.parseInt(this.theGroupOfVehiclesNeeded.get(i).get(3).toString());
				
				totalCostOfJouneysToBeMade = totalCostOfJouneysToBeMade + thisJouney.jouneyCost;
				
			paintTheRoadsToBeTaken(thisJouney, g, fm, theGotVector, this.F, nodeToBeFollowed, this.kimColors[carN], (2*carN)+1, false);	

			this.collectionIfUsedThisDirection.addElement(thisJouney);
			
		}
			
			Vector<String> thisJoney = new Vector<String>();
			thisJoney.add(this.Direction);
			thisJoney.add(this.vehicleDirection);
			thisJoney.add(String.valueOf(totalCostOfJouneysToBeMade));
		
		this.main.jouneyGotWithThisGraph.addElement(thisJoney);
		
		if(this.uncertainities == true){
			//JOptionPane.showMessageDialog(null,"Locate the Client/Place where you are for assistance !!!","Please locate the Client/Place where you are",JOptionPane.INFORMATION_MESSAGE);
		}
		
		}else if(this.nowRepaint == true){
		
			paintTheRoadToTwoNodes(g, this.nodeGotForShortestDist, Color.red, 3, this.graphSmallOrBig);
				
		}
		
		
	/*	JDialog viewQty = new DialogJouney(this.locationWidth, this.locationHeight, this.vehicleDirection,this.main, this.collectionIfUsedThisDirection);
		try{
			this.main.desktop.add( viewQty );
		}catch(IllegalArgumentException ex){}   */
		
	}
	
	private void borrowedFromPaint(boolean helpingJouney){	
	
		if(this.nowRepaint == false){			
			
			int totalCostOfJouneysToBeMade = 0;
			
			for(int i=0; i<this.theGroupOfVehiclesNeeded.size();i++){
					Vector<Node> theGotVector = (Vector<Node>)this.theGroupOfVehiclesNeeded.get(i).get(0);
					Vector<Node> extractedCollect = new Vector<Node>();
					JouneyDetails thisJouney = new JouneyDetails();
					//thisJouney.theNodesSupplied = theGotVector;
					thisJouney.carName = this.theGroupOfVehiclesNeeded.get(i).get(1).toString();
					thisJouney.direction = this.Direction;
					thisJouney.totalCapacityCarriedByThisVehicle = Integer.parseInt(this.theGroupOfVehiclesNeeded.get(i).get(5).toString());
					thisJouney.totalCapacityCarriedByThisVehicleRightNow = Integer.parseInt(this.theGroupOfVehiclesNeeded.get(i).get(5).toString());
			       
				for(int j=0;j<theGotVector.size();j++){
					extractedCollect.addElement(theGotVector.get(j));
					thisJouney.theNodesSupplied.addElement(theGotVector.get(j));
				}
				
				int carN = Integer.parseInt(this.theGroupOfVehiclesNeeded.get(i).get(2).toString());

				Node nodeToBeFollowed = populatesTotalDistanceIfFollowedAnyNode(extractedCollect, false, null);
				
		try{
					thisJouney.NoOfKmCovered = nodeToBeFollowed.totalDistanceIfFollowedThisNode;
		}catch(NullPointerException e){}		
					thisJouney.jouneyCost = (thisJouney.NoOfKmCovered * Integer.parseInt(this.theGroupOfVehiclesNeeded.get(i).get(4).toString())) + Integer.parseInt(this.theGroupOfVehiclesNeeded.get(i).get(3).toString());
					
					totalCostOfJouneysToBeMade = totalCostOfJouneysToBeMade + thisJouney.jouneyCost;
					
				paintTheRoadsToBeTaken(thisJouney, null, null, theGotVector, this.F, nodeToBeFollowed, this.kimColors[carN], (2*carN)+1, helpingJouney);	

				this.collectionIfUsedThisDirection.addElement(thisJouney);
				
			}
				
				Vector<String> thisJoney = new Vector<String>();
				thisJoney.add(this.Direction);
				thisJoney.add(this.vehicleDirection);
				thisJoney.add(String.valueOf(totalCostOfJouneysToBeMade));
			
			this.main.jouneyGotWithThisGraph.addElement(thisJoney);
			
		}
	}
	
	private void paintTheRoadsToBeTaken(JouneyDetails thisJouney, Graphics g, FontMetrics fm, Vector<Node> routeOwners, Node start, Node end, Color withThisColor, int carNo, boolean helpingJouney){
	
		Vector<Node> allThePlacesToGoTo = routeOwners;
		pathToNode = new kimDijkstra( start, null, end);
		Node whereIAmCurrently = pathToNode.getDestinationNode(end);
		thisJouney.totalCapacityCarriedByThisVehicleRightNow = (thisJouney.totalCapacityCarriedByThisVehicleRightNow - end.OrderWanted);
  		 
		paintTheRoadToTwoNodes(g, whereIAmCurrently, withThisColor, carNo, this.graphSmallOrBig);
		
		allThePlacesToGoTo.remove(allThePlacesToGoTo.get(0));//i dont kno
			for(int i=0;i<whereIAmCurrently.nodeNames.size();i++){
				try{
				if(whereIAmCurrently.nodeNames.get(i).contentEquals(whereIAmCurrently.nodeNames.get(i+1))){
					
				}else{
					thisJouney.allPlacesToVisit.add(whereIAmCurrently.nodeNames.get(i));
					thisJouney.allPlacesVisitedWithLoadStillCarriedOnTruck.add(whereIAmCurrently.nodeNames.get(i)+"-"+(end.OrderWanted + thisJouney.totalCapacityCarriedByThisVehicleRightNow));
				}
				}catch(ArrayIndexOutOfBoundsException e){
					thisJouney.allPlacesToVisit.add(whereIAmCurrently.nodeNames.get(i));
						if(end.name.contentEquals(whereIAmCurrently.nodeNames.get(i))){
						thisJouney.allPlacesVisitedWithLoadStillCarriedOnTruck.add(whereIAmCurrently.nodeNames.get(i)+"-"+thisJouney.totalCapacityCarriedByThisVehicleRightNow);
						}
				}
				
				for(int j=0;j<allThePlacesToGoTo.size();j++){
					if((whereIAmCurrently.nodeNames.get(i).contentEquals(routeOwners.get(j).name))&&(!whereIAmCurrently.nodeNames.get(i).contentEquals(this.F.name))){
						allThePlacesToGoTo.remove(routeOwners.get(j));
					}				
				}			
			}
			
		if(allThePlacesToGoTo.size() != 0){
			paintTheRoadsToBeTaken(thisJouney, g, fm, allThePlacesToGoTo, end, allThePlacesToGoTo.get(0), withThisColor, carNo, helpingJouney);	
		}
		
		if((helpingJouney == true)&&(this.firstTime == false)){
			pathToNode = new kimDijkstra( end, null ,this.F);
			whereIAmCurrently = pathToNode.getDestinationNode(this.F);
			for(int i=0;i<whereIAmCurrently.nodeNames.size();i++){
				try{
				if(whereIAmCurrently.nodeNames.get(i).contentEquals(whereIAmCurrently.nodeNames.get(i+1))){
					
				}else{
					thisJouney.allPlacesToVisit.add(whereIAmCurrently.nodeNames.get(i));
					thisJouney.allPlacesVisitedWithLoadStillCarriedOnTruck.add(whereIAmCurrently.nodeNames.get(i)+"-"+(end.OrderWanted + thisJouney.totalCapacityCarriedByThisVehicleRightNow));
				}
				}catch(ArrayIndexOutOfBoundsException e){
					thisJouney.allPlacesToVisit.add(whereIAmCurrently.nodeNames.get(i));
						if(end.name.contentEquals(whereIAmCurrently.nodeNames.get(i))){
						thisJouney.allPlacesVisitedWithLoadStillCarriedOnTruck.add(whereIAmCurrently.nodeNames.get(i)+"-"+thisJouney.totalCapacityCarriedByThisVehicleRightNow);
						}
				}		
			}
			firstTime = true;
		}
		
	}
	
	private void paintTheRoadToTwoNodes(Graphics g, Node upToHere, Color thisColor, int carNo, boolean bigOrSmallGraph){
		
	//	g.setColor(thisColor);

		for(int i=0;i<(upToHere.nodeNames.size() - 1);i++){
			String name1 = upToHere.nodeNames.get(i);
			String name2 = upToHere.nodeNames.get(i+1);
			Node node1 = null;
			Node node2 =null;
			int x1=0,y1=0,x2=0,y2=0;
			
			for(int j=0;j<this.clientsAll.size();j++){
				if(name1.contentEquals(this.clientsAll.get(j).name)){
					node1 = this.clientsAll.get(j);
				}
				
				if(name2.contentEquals(this.clientsAll.get(j).name)){
					node2 = this.clientsAll.get(j);
				}
				
			}
			
			if(bigOrSmallGraph == true){
				 x1 = node1.x ;
				 y1 = node1.y;
				 x2 = node2.x;
				 y2 = node2.y;
			}else{
				x1 = node1.x *49/81;
				y1 = node1.y*61/116;
				x2 = node2.x *49/81;
				y2 = node2.y*61/116;
			}

		//	g.drawLine(x1, y1+carNo+2, x2, y2+carNo+2);		
		}
	}

	private void sort(Vector<Node> a) {
		sort(a, 0, (a.size()-1));
	    }

	public void sort(Vector<Node> a, int lo0, int hi0){
		int lo = lo0;
		int hi = hi0;

		if (lo >= hi) {
		    return;
		}
	        else if( lo == hi - 1 ) {
	            if (a.get(lo).angle > a.get(hi).angle) {
	                Node T = a.get(lo);
	                a.setElementAt(a.get(hi), lo);//.elementAt(lo) = a[hi];
	               a.setElementAt(T,hi);// = T;
	            }
	            return;
		}

		Node pivot = a.get((lo + hi) / 2);
	        a.setElementAt(a.get(hi),((lo + hi) / 2));
	        a.setElementAt(pivot,hi);

	        while( lo < hi ) {

	            while (a.get(lo).angle <= pivot.angle && lo < hi) {
			lo++;
		    }

		    while (pivot.angle <= a.get(hi).angle && lo < hi ) {
			hi--;
		    }

	            if( lo < hi ) {
	               Node T = a.get(lo);
	                a.setElementAt(a.get(hi),lo);
	                a.setElementAt(T,hi);
	           
	            }

		}

	        a.setElementAt(a.get(hi),hi0);
	        a.setElementAt(pivot,hi);

		sort(a, lo0, lo-1);
		sort(a, hi+1, hi0);
	    }

	private boolean findIfAlreadyExists(Vector<Vector> towns, String townName){
		boolean value = false;
		for(int j=0;j<towns.size();j++){
			if(townName.contentEquals(towns.get(j).get(2).toString())){
				value = true;
				break;
			}
		}
		
		return value;
	}
	
	private void computeAngles(Vector<Vector> contentsOfOrders,Node ffrom){
				
		for (int i=0;i<contentsOfOrders.size();i++){
			
			for(int j=0;j<this.clientsAll.size();j++){
				
				if(contentsOfOrders.get(i).get(0).toString().contentEquals(clientsAll.get(j).name)){
					Node thisCLnt = new Node();
					thisCLnt.name = clientsAll.get(j).name;
					thisCLnt.x = clientsAll.get(j).x;
					thisCLnt.y = clientsAll.get(j).y;
					thisCLnt.OrderWanted = Integer.parseInt(contentsOfOrders.get(i).get(1).toString());
					thisCLnt.angle = setAngle(clientsAll.get(j),ffrom);
					
					this.clientsWithOrders.addElement(thisCLnt);

				}
			}
			
		}
		
	}
	
	private double setAngle(Node c, Node DIPO){
		int dx = c.x - DIPO.x;
		int dy = c.y - DIPO.y;
		double angle = 0.0;
		
		if(dx>0 && dy>0 || dx<0 && dy>0)
		 	angle = Math.toDegrees(Math.atan2(dy,dx));

		if(dx<0 && dy<0 || dx>0 && dy<0)
		 	angle =(360+ Math.toDegrees(Math.atan2(dy,dx)));

		if(dx==0 && dy>0)
		 	angle = 90.00;
		if(dx==0 && dy<0)
		 	angle = 270.00;
		if(dx<0 && dy==0)
		 	angle = 180.00;
		if(dx==0 && dy==0)
		 	angle = 0.00;	
		
		return angle;
	}

	private void input_graphNeighbours(){
		
		  try
		  {			  					
				Class.forName(driver);					
		   		connection=DriverManager.getConnection(url);
		   		Statement statement = connection.createStatement();	
				String query = "select * from roads order by id Asc";
				ResultSet rs = statement.executeQuery(query);
					
				while (rs.next()) {
					Vector<String> road = new Vector<String>();
					road.addElement(rs.getString("start"));
					road.addElement(rs.getString("stop"));
					road.addElement(rs.getString("Length_km"));

					clientsRoads.addElement(road);
				}

				rs.close();
				statement.close();
				connection.close();

		  }catch(Exception ex){
		  		System.out.println(ex.getMessage());
		  }

		  
		  try
		  {			  					
				Class.forName(driver);					
		   		connection=DriverManager.getConnection(url);
		   		Statement statement = connection.createStatement();	
				String query = "select * from kla_towns order by OBJECTID Asc";
				ResultSet rs = statement.executeQuery(query);
					
				while (rs.next()) {
					Node town = new Node();
					town.name = rs.getString("NAME");
					town.x = (int)(Double.parseDouble(rs.getString("X_COORD")));
					town.y = (int)(Double.parseDouble(rs.getString("Y_COORD")));

					townsAll.addElement(town);
					clientsAll.addElement(town);

				}

				rs.close();
				statement.close();
				connection.close();

		  }catch(Exception ex){
		  		System.out.println(ex.getMessage());
		  }

	}

	private boolean isAlreadyIn(Vector<String> vehiclesInHere, String thisTownName){
		boolean yesIsAlreadyIn = false;
		for(int p=0; p<vehiclesInHere.size(); p++){
			if(thisTownName.contentEquals(vehiclesInHere.get(p).toString()) == true){
				yesIsAlreadyIn = true;
				break;
			}
		}

		return yesIsAlreadyIn;
	}
	
	public boolean mouseDown(Event e, int x, int y) {
	
		if(this.uncertainities == false){
		JDialog viewQty = new DialogJouney(this.locationWidth, this.locationHeight, this.vehicleDirection,this.main, this.collectionIfUsedThisDirection);
		try{
		this.main.desktop.add( viewQty );
		}catch(IllegalArgumentException ex){}
		}   
		
	/*	if((this.uncertainities == true)&&(this.jouneyProblem == true)){
			if(this.clickFirst == false){
				this.x1 = x; this.y1 = y;
				String amInThisTown = getLocatedInThisTown(x,y);
    	        Vector<String> vehiclesThatParsedInThisTown = new Vector<String>();
    	        
    	        for(int i=0;i<this.clientsAll.size();i++){
    	        	if(amInThisTown.contentEquals(this.clientsAll.get(i).name)){
    	        		this.nodeWhereIam = this.clientsAll.get(i);
    	        	}
    	        }

		        for(int i =0;i<this.collectionIfUsedThisDirection.size();i++){	        	
		        	for(int j=0;j<this.collectionIfUsedThisDirection.get(i).allPlacesToVisit.size();j++){
				
		        		if(amInThisTown.contentEquals(this.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j))){
		        			if(vehiclesThatParsedInThisTown.size() == 0){
			        			vehiclesThatParsedInThisTown.addElement(this.collectionIfUsedThisDirection.get(i).carName);
		        			}else{
		        				if(isAlreadyIn(vehiclesThatParsedInThisTown, this.collectionIfUsedThisDirection.get(i).carName) == false){
				        			vehiclesThatParsedInThisTown.addElement(this.collectionIfUsedThisDirection.get(i).carName);
		        				}
		        			}
		        		}
		        	}  
		        }
		        
		        JDialog viewCell = new VehicleAdvised((new Dimension (1024,768)),this, this.main, vehiclesThatParsedInThisTown, collectionIfUsedThisDirection, amInThisTown);
			
			}
		}       */
		
		
		if((this.uncertainities == true)&&(this.jouneyProblem == false)){
			if(this.clickFirst == false){
				this.x1 = x; this.y1 = y;
				this.clickFirst = true;
				JOptionPane.showMessageDialog(null,"Please now click where the road connects to !!","The road that is not acccessible",JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			else if((this.clickFirst == true)&&(this.clickSecond == false)){
				this.x2 = x; this.y2 = y;
				this.clickSecond = true;
				JOptionPane.showMessageDialog(null,"Now we need to Know the Client you were heading to next !!!","The client to be supplied next",JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			
			this.x3 = x;  this.y3 = y;
			
				for(int i =0; i<this.clientsAll.size();i++){
					int rangeInX = (this.clientsAll.get(i).name.length() + 10)/2;
					int rangeInY = (10 + 4)/2;
					
					if(((this.clientsAll.get(i).x - rangeInX) <= x1)&&(x1 <= (this.clientsAll.get(i).x + rangeInX))){
						if(((this.clientsAll.get(i).y - rangeInY) <= y1)&&(y1 <= (this.clientsAll.get(i).y + rangeInY))){
							this.nodeWhereIam = this.clientsAll.get(i);
						}
					}
					
					if(((this.clientsAll.get(i).x - rangeInX) <= x2)&&(x2 <= (this.clientsAll.get(i).x + rangeInX))){
						if(((this.clientsAll.get(i).y - rangeInY) <= y2)&&(y2 <= (this.clientsAll.get(i).y + rangeInY))){
							this.roadClosedToThisNode = this.clientsAll.get(i);
						}
					}
					
				}
				
				Vector closedRoad = new Vector();
				closedRoad.add(this.nodeWhereIam.name);
				closedRoad.add(this.roadClosedToThisNode.name);
							
			int NoOfTheseGuysInHere = this.collectionIfUsedThisDirection.size();  //get the last entered which is ofcause this jouney
				
				for(int i =0; i<this.collectionIfUsedThisDirection.get(NoOfTheseGuysInHere-1).theNodesSupplied.size();i++){
					int rangeInX = (this.collectionIfUsedThisDirection.get(NoOfTheseGuysInHere-1).theNodesSupplied.get(i).name.length() + 10)/2;
					int rangeInY = (10 + 4)/2;
					
					if(((this.collectionIfUsedThisDirection.get(NoOfTheseGuysInHere-1).theNodesSupplied.get(i).x - rangeInX) <= x3)&&(x3 <= (this.collectionIfUsedThisDirection.get(NoOfTheseGuysInHere-1).theNodesSupplied.get(i).x + rangeInX))){
						if(((this.collectionIfUsedThisDirection.get(NoOfTheseGuysInHere-1).theNodesSupplied.get(i).y - rangeInY) <= y3)&&(y3 <= (this.collectionIfUsedThisDirection.get(NoOfTheseGuysInHere-1).theNodesSupplied.get(i).y + rangeInY))){
							this.nodeWhereIHaveToDropNext = this.collectionIfUsedThisDirection.get(NoOfTheseGuysInHere-1).theNodesSupplied.get(i);
						}
					}
				}	
			
			paintRouteAfterUncertainities(this.nodeWhereIam, closedRoad, this.nodeWhereIHaveToDropNext);
				
		}		
		
		return true;
	}
		
	public void paintRouteAfterUncertainities(Node start, Vector toBeRemoved, Node end){
		pathToNode = new kimDijkstra( start, toBeRemoved, end);
		this.nodeGotForShortestDist = pathToNode.getDestinationNode(end);
		this.nowRepaint = true;
		this.repaint();

	}

}

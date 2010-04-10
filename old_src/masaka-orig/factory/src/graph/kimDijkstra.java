package graph;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;
   
 public class kimDijkstra {
   	
   	private Vector<Vector> clientRoads, townsAll;
   	private Vector<Node> clientsAll;
   	private Vector<Node> settledNodes;
   	private Vector<Node> unSettledNodes;
   	private Node wantedPathNode ;
	private static Connection connection;
	private static final String driver="sun.jdbc.odbc.JdbcOdbcDriver";
	String url="jdbc:odbc:moses";

   // Dijkstra's algorithm to find shortest path from s to all other nodes
     public kimDijkstra( Node s, Vector toBeRemoved, Node destination) {
      	
      	clientRoads = new Vector<Vector>();
      	clientsAll = new Vector<Node>();
      	settledNodes = new Vector<Node>();
      	unSettledNodes = new Vector<Node>();
		townsAll = new Vector<Vector>();
      	
      	this.wantedPathNode = destination;

			input_graphNeighbours();
         
         try{
         
         if((toBeRemoved != null)||(toBeRemoved.size() != 0)){
         	for(int i =0; i<this.clientRoads.size();i++){
         		
         		if(((toBeRemoved.get(0).toString()).contentEquals(this.clientRoads.get(i).get(0).toString()))&&((toBeRemoved.get(1).toString()).contentEquals(this.clientRoads.get(i).get(1).toString()))){
         			this.clientRoads.removeElement(this.clientRoads.get(i));
         			break;
         		}
         		
         		if(((toBeRemoved.get(1).toString()).contentEquals(this.clientRoads.get(i).get(0).toString()))&&((toBeRemoved.get(0).toString()).contentEquals(this.clientRoads.get(i).get(1).toString()))){
         			this.clientRoads.removeElement(this.clientRoads.get(i));
         			break;
         		}
         	}
      
         }
         
         }catch(NullPointerException en){   }
         
         
         Node startNode = null;
         for (int i=0; i<clientsAll.size(); i++){
         	if((s.x == clientsAll.get(i).x) && (s.y == clientsAll.get(i).y) && (s.name.contentEquals(clientsAll.get(i).name))){
         		startNode = clientsAll.get(i);
         	}
           clientsAll.get(i).shortestDist = Integer.MAX_VALUE;
           clientsAll.get(i).nodeNames = new Vector<String>();
         }
        
        startNode.shortestDist = 0; int i=0;
        
        unSettledNodes.addElement(startNode);
        
        while((unSettledNodes.size() != 0)&&(unSettledNodes != null)){
        	
        	Node u = extractMinimum(unSettledNodes);
    	
        	settledNodes.addElement(u);
       				  	     	
        	relaxNeighbours(u);
        	
        try{
        	if((this.wantedPathNode.x == u.x)&&(this.wantedPathNode.y == u.y)&&(this.wantedPathNode.name.contentEquals(u.name))){
        		break;
        	}
    	}catch(NullPointerException e){}		
        	
        	}
        	
	}
     
  	 private Node findNode(String itsName){
  	 	Node theNode  = null;
  	 	for(int i=0;i<this.clientsAll.size();i++){
  	 		if(itsName.contentEquals(this.clientsAll.get(i).name)){
  	 			theNode = this.clientsAll.get(i);
  	 			break;
  	 		}
  	 	}
  	 	
  	 	return theNode;
  	 }
  
  	 private void relaxNeighbours(Node roadFromThis){
	
  	 	for(int i=0;i<this.clientRoads.size();i++){
			
			if(clientRoads.get(i).get(0).toString().contentEquals(roadFromThis.name)){

				for(int j =0;j<this.settledNodes.size();j++){
				
					if(!(clientRoads.get(i).get(1).toString().contentEquals(this.settledNodes.get(j).name))){
					
						Node gotNode = findNode(clientRoads.get(i).get(1).toString());

						if(gotNode.shortestDist > roadFromThis.shortestDist + Double.parseDouble(clientRoads.get(i).get(2).toString())){
							gotNode.shortestDist = (int)(roadFromThis.shortestDist + Double.parseDouble(clientRoads.get(i).get(2).toString()));

							gotNode.nodeNames.removeAllElements();
							for(int p=0;p<roadFromThis.nodeNames.size();p++){
								gotNode.nodeNames.addElement(roadFromThis.nodeNames.get(p));
							}
							
							gotNode.nodeNames.addElement(roadFromThis.name);
							gotNode.nodeNames.addElement(clientRoads.get(i).get(1).toString());
										
							this.unSettledNodes.addElement(gotNode);
								
						}	
					}
				}
							
			}
			
			if(clientRoads.get(i).get(1).toString().contentEquals(roadFromThis.name)){
			
				for(int j =0;j<this.settledNodes.size();j++){
					
					if(!(clientRoads.get(i).get(0).toString().contentEquals(this.settledNodes.get(j).name))){
					
						Node gotNode = findNode(clientRoads.get(i).get(0).toString());
						
						if(gotNode.shortestDist > roadFromThis.shortestDist + Double.parseDouble(clientRoads.get(i).get(2).toString())){
							gotNode.shortestDist = (int)(roadFromThis.shortestDist + Double.parseDouble(clientRoads.get(i).get(2).toString()));
							gotNode.nodeNames.removeAllElements();
							for(int p=0;p<roadFromThis.nodeNames.size();p++){
								gotNode.nodeNames.addElement(roadFromThis.nodeNames.get(p));
							}
							
							gotNode.nodeNames.addElement(roadFromThis.name);
							gotNode.nodeNames.addElement(clientRoads.get(i).get(0).toString());
							
							this.unSettledNodes.addElement(gotNode);
								
						}	
					}
				}
				
			}
		}
  	 	
  	 }
  
     private Node extractMinimum (Vector<Node> visitedPoints) {

		Node nodeWithSmallestDistInHere = null;
		int x = Integer.MAX_VALUE;
		
		for (int i=0; i<visitedPoints.size(); i++) {
           if (visitedPoints.get(i).shortestDist < x) {
           	x = visitedPoints.get(i).shortestDist; 
           	nodeWithSmallestDistInHere=visitedPoints.get(i);

           	}
        }
        
        visitedPoints.removeElement(nodeWithSmallestDistInHere);
        
        return nodeWithSmallestDistInHere;
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

 					clientRoads.addElement(road);
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

 		//			townsAll.addElement(town);
 					clientsAll.addElement(town);

 				}

 				rs.close();
 				statement.close();
 				connection.close();

 		  }catch(Exception ex){
 		  		System.out.println(ex.getMessage());
 		  }

 	}

     public Node getDestinationNode(Node thisOne){
     	Node wantedPathNode = null;
       for (int j=0; j<this.clientsAll.size(); j++) {
   try{
       		if((thisOne.x == this.clientsAll.get(j).x)&&(thisOne.y == this.clientsAll.get(j).y)&&(thisOne.name.contentEquals(this.clientsAll.get(j).name))){
       			wantedPathNode = this.clientsAll.get(j);
       			break;
       		}
   	}catch(NullPointerException e){}		

		}
     	return wantedPathNode;
     }
    
}
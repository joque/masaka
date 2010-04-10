package graph;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Event;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class kim2 extends Canvas {

	int width, height;
	public JFrame now;
	int iteration, step;
	int n,m;
	private Vector<Node> clientsAll;
	public Vector<Vector> clientsRoads;
	private Vector<Vector> townsAll;
	public Vector<Node> plottedPoints;
	private CompGeomTest thedialog;

	kim2(int cw, int ch, JFrame OwnerForm,CompGeomTest  thedialog) {

		setSize(width = cw, height = ch);
		setBackground(Color.white);
		clientsAll = new Vector<Node>();
		clientsRoads = new Vector<Vector>();
		this.thedialog = thedialog;
		plottedPoints = new Vector<Node>();
		townsAll = new Vector<Vector>();
		
		
		try {
			InputStream is;
			is = ClientsBefore.class.getResource("Clien.obj").openStream();
			input_graphNeighbours(is);
			try {
				if (is != null)
					is.close();
			} catch (Exception ex) {
			}
		} catch (FileNotFoundException ec) {
			System.err.println("File not found.");
		} catch (IOException ew) {
			System.err.println("Cannot access file.");
		}
			
			Node F =new Node();
			F.name ="DIPO";
			F.x = 400;
			F.y = 300;
			plottedPoints.add(F);
				
		this.now = OwnerForm;
		
	}

	public void paint(Graphics g) {

		width = getSize().width;
		height = getSize().height;
		g.setColor(Color.LIGHT_GRAY);
		
		FontMetrics fm = g.getFontMetrics();
		for (int i = 0; i < this.clientsRoads.size(); i++){
			paintRoads(g,this.clientsRoads.get(i), fm);
		}
		
		for (int i = 0; i < this.clientsAll.size(); i++){
			paintNode(g,this.clientsAll.get(i), fm);
		}
		
		DrawPoints(g,this.plottedPoints);
	}
	
	private void DrawPoints(Graphics g,Vector<Node> plots) {

		for(int i=0;i<plots.size();i++){
				g.setColor(Color.blue);
				g.fillOval(plots.get(i).x, plots.get(i).y, 10,10);
				g.setColor(Color.black);
		}

	}

	private void paintNode(Graphics g, Node n, FontMetrics fm) {
		
		int x,y;
		
			 x = n.x ;
			 y = n.y;
		
		int w = fm.stringWidth(n.name) + 10;
		int h = fm.getHeight() + 4;
		n.w = w;
		n.h = h;

		g.setColor(getBackground());
		g.fillRect(x - w / 2 + 1, y - h / 2 + 1, w - 1, h - 1);

		g.setColor(Color.black);
		g.drawString(n.name, x - (w - 10) / 2, (y - (h - 4) / 2)+ fm.getAscent());
	
		g.setColor(getBackground());
		
	}

	public void paintRoads(Graphics g, Vector road, FontMetrics fm) {
	
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

			g.drawRect(((int)((node1.x+node2.x)/2)), ((int)((node1.y + node2.y)/2)), 10, 4);
			g.drawLine(node1.x ,node1.y ,node2.x ,node2.y);
			g.drawString(road.get(2).toString(), ((int)((node1.x+node2.x)/2)) , ((int)((node1.y + node2.y)/2)));
		
	}

	public boolean mouseDown(Event e, int x, int y) {

			Node pt = new Node();
			pt.x = x;
			pt.y = y;
			this.plottedPoints.add(pt);
		
			repaint();
		
			JDialog viewQty = new NewClientToRegister(getLocatedInThisTown(x, y) ,this.now,this,this.thedialog,x,y);
			try{	
			((files.MainFrame)now).desktop.add( viewQty );
			}catch(IllegalArgumentException ex){}   
				
		return true;
	}

	private void input_graphNeighbours(InputStream is) throws IOException {
		String s;
		
		Reader r = new BufferedReader(new InputStreamReader(is));
		StreamTokenizer st = new StreamTokenizer(r);
		st.commentChar('#');
		
		boolean b = true;
		
		try{
			
		while(b){
			st.nextToken();
			s = st.sval;
			
			if(s.equals("C")){
				Node client = new Node();
				st.nextToken();
				client.name = st.sval;
				st.nextToken();
				client.x = (int)st.nval;
				st.nextToken();
				client.y = (int)st.nval;
				
			//	this.clientsAll.addElement(client);
			}
			else if(s.equals("P")){
				Vector road = new Vector();
				st.nextToken();
				road.add(st.sval);
				st.nextToken();
				road.add(st.sval);
				st.nextToken();
				road.add((int)st.nval);
				
				this.clientsRoads.addElement(road);
			}
			else if(s.equals("T")){
				Vector town = new Vector();
				st.nextToken();
				town.add(st.sval);
				st.nextToken();
				town.add((int)st.nval);
				st.nextToken();
				town.add((int)st.nval);
				st.nextToken();
				town.add((int)st.nval);
				st.nextToken();
				town.add((int)st.nval);
				
				this.townsAll.addElement(town);
			}
			
		}
		
		}catch(NullPointerException x){	}
		
		for(int i=0;i<this.townsAll.size();i++){
			Node client = new Node();
			client.name =this.townsAll.get(i).get(0).toString();
			client.x = ((Integer.parseInt(this.townsAll.get(i).get(1).toString()))+(Integer.parseInt(this.townsAll.get(i).get(3).toString())))/2;
			client.y = ((Integer.parseInt(this.townsAll.get(i).get(2).toString()))+(Integer.parseInt(this.townsAll.get(i).get(4).toString())))/2;
			this.clientsAll.add(client);
		}
		
		Node F =new Node();
		F.name ="DIPO";
		F.x = 400;
		F.y = 300;
		this.clientsAll.add(F);

	}

	private String getLocatedInThisTown(int x,int y){
		String townName = "";
		for(int i=0;i<this.townsAll.size();i++){
			int x1 =Integer.parseInt(this.townsAll.get(i).get(1).toString());
			int x2 =Integer.parseInt(this.townsAll.get(i).get(3).toString());
			int y1 =Integer.parseInt(this.townsAll.get(i).get(2).toString());
			int y2 =Integer.parseInt(this.townsAll.get(i).get(4).toString());
			
			if((x1<= x)&&(x <= x2)){
				if((y1<= y)&&(y <= y2)){
					townName = this.townsAll.get(i).get(0).toString();
					break;
				}
			}
			
			if((x2<= x)&&(x <= x1)){
				if((y2<= y)&&(y <= y1)){
					townName = this.townsAll.get(i).get(0).toString();
					break;
				}
			}
		}
		
		if((townName.contentEquals(""))||(townName.length() == 0)){
			double shortestDistanceFromPreviousTown = Integer.MAX_VALUE;
			for(int i=0;i<this.townsAll.size();i++){
				int x1 =Integer.parseInt(this.townsAll.get(i).get(1).toString());
				int x2 =Integer.parseInt(this.townsAll.get(i).get(3).toString());
				int y1 =Integer.parseInt(this.townsAll.get(i).get(2).toString());
				int y2 =Integer.parseInt(this.townsAll.get(i).get(4).toString());
				
				if((Math.sqrt(Math.pow((x - ((x1+x2)/2)),2) + Math.pow((y - ((y1+y2)/2)),2))) < shortestDistanceFromPreviousTown){
					shortestDistanceFromPreviousTown = (Math.sqrt(Math.pow((x - ((x1+x2)/2)),2) + Math.pow((y - ((y1+y2)/2)),2)));
					townName = this.townsAll.get(i).get(0).toString();
				}
				
			}
		}
	
		return townName;
	}

}
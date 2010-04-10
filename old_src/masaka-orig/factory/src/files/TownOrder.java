package files;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class TownOrder extends JInternalFrame{
	private int screenHeight;
	private int screenWidth;
    private static JPanel jpShow = new JPanel();
    public static JTable tbCustomer;
    private static JScrollPane jspTable;
    private JDesktopPane desktop = new JDesktopPane();
	JFrame JFParentFrame;
    private static Vector<String> columnNames = new Vector<String>();
	public static Vector<Vector> cumulativeForTown = new Vector<Vector>();

  public TownOrder(String whichOne,JFrame getParentFrame, Dimension screen){
        super( "Cummulative Order in a Town", false, false, false, false );
    	screenHeight = (screen.height/3);
    	screenWidth = (screen.width/3);
    	jpShow.setLayout( null );
		JFParentFrame = getParentFrame;
		
        columnNames.addElement("Town Name");
        columnNames.addElement("Order");
        
		jspTable = new JScrollPane(tbCustomer);
        jspTable.setBounds( (screenWidth*2/25), (screenHeight*2/25),(screenWidth*8/10), (screenHeight*7/10));
        jpShow.add(jspTable);

		desktop.putClientProperty("JDesktopPane.dragMode", "outline");
		getContentPane().add(desktop, BorderLayout.CENTER);
  
        getContentPane().add( jpShow );
        setSize(screenWidth,screenHeight );
        setLocation((screen.width/3),(screen.height*15/44)+120);
        setVisible( true );
   }
	
  public static void reloadRecord(Vector<String> newMadeOrder) {
		jspTable.getViewport().removeAll();
		Vector<Vector> newCumulativeForTown = new Vector<Vector>();
		boolean townExists = false;

		if(cumulativeForTown.size() == 0){
			Vector singleTown = new Vector();

			singleTown.addElement(newMadeOrder.elementAt(2).toString());
			singleTown.addElement(newMadeOrder.elementAt(1).toString());
		
			newCumulativeForTown.addElement(singleTown);
			townExists = true;
		}
		
		for(int i=0;i<cumulativeForTown.size();i++){
			Vector singleTown = new Vector();
			
			singleTown.addElement(cumulativeForTown.get(i).get(0).toString());

			if((cumulativeForTown.get(i).get(0).toString()).contentEquals(newMadeOrder.elementAt(2).toString())){
				singleTown.addElement((Integer.parseInt(cumulativeForTown.get(i).get(1).toString())+Integer.parseInt(newMadeOrder.elementAt(1).toString())));
				townExists = true;
			}else{
				singleTown.addElement(cumulativeForTown.get(i).get(1).toString());
			}
			
			newCumulativeForTown.addElement(singleTown);
		}
		
		if(townExists == false){
			Vector singleTown = new Vector();

			singleTown.addElement(newMadeOrder.elementAt(2).toString());
			singleTown.addElement(newMadeOrder.elementAt(1).toString());
		
			newCumulativeForTown.addElement(singleTown);
		}

		DefaultTableModel dtmCustomer2 = new DefaultTableModel(newCumulativeForTown, columnNames);

		tbCustomer = new JTable(dtmCustomer2) {
			public boolean isCellEditable(int iRow, int iCol) {
				return false; // Disable All Columns of Table.
			}
		};
		(tbCustomer.getColumnModel().getColumn(0)).setPreferredWidth(100);
		(tbCustomer.getColumnModel().getColumn(1)).setPreferredWidth(50);

		tbCustomer.setRowHeight(20);
		tbCustomer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jspTable.getViewport().add(tbCustomer);
		jpShow.repaint();
		
		cumulativeForTown.removeAllElements();
		cumulativeForTown = newCumulativeForTown;
		
	}

  public static void deleteRecord(String town, int order) {
		jspTable.getViewport().removeAll();
		Vector<Vector> rowDataCollections2 = new Vector<Vector>();
		
		for(int i=0;i<cumulativeForTown.size();i++){
			Vector<String> rowDataSingle2 = new Vector<String>();
			
			if(town.contentEquals(cumulativeForTown.get(i).get(0).toString())){
				if(Integer.parseInt(cumulativeForTown.get(i).get(1).toString()) > order){
					rowDataSingle2.addElement(cumulativeForTown.get(i).get(0).toString());
					rowDataSingle2.addElement(""+(Integer.parseInt(cumulativeForTown.get(i).get(1).toString()) - order));
			
					rowDataCollections2.addElement(rowDataSingle2);
				}
			}else{
			rowDataSingle2.addElement(cumulativeForTown.get(i).get(0).toString());
			rowDataSingle2.addElement(cumulativeForTown.get(i).get(1).toString());
	
			rowDataCollections2.addElement(rowDataSingle2);
			}
		}

		cumulativeForTown.removeAllElements();
		cumulativeForTown = rowDataCollections2;

		DefaultTableModel dtmCustomer2 = new DefaultTableModel(cumulativeForTown, columnNames);

		tbCustomer = new JTable(dtmCustomer2) {
			public boolean isCellEditable(int iRow, int iCol) {
				return false; // Disable All Columns of Table.
			}
		};
		(tbCustomer.getColumnModel().getColumn(0)).setPreferredWidth(100);
		(tbCustomer.getColumnModel().getColumn(1)).setPreferredWidth(50);

		tbCustomer.setRowHeight(20);
		tbCustomer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jspTable.getViewport().add(tbCustomer);
		jpShow.repaint();
		
	}

}

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

public class OrdersMade extends JInternalFrame {
	private int screenHeight;
	private int screenWidth;
	private static JPanel jpShow = new JPanel();
	public static JTable tbCustomer;
	public static JScrollPane jspTable;
	private JDesktopPane desktop = new JDesktopPane();
	JFrame JFParentFrame;
	private static Vector<String> columnNames = new Vector<String>();
	public static Vector<Vector> rowDataCollections = new Vector<Vector>();

	OrdersMade(String whichOne, JFrame getParentFrame, Dimension screen) {
		super("All orders from Clients", false, false, false, false);
		screenHeight = (screen.height * 2 / 5);
		screenWidth = (screen.width * 2 / 5);
		jpShow.setLayout(null);
		JFParentFrame = getParentFrame;

		columnNames.addElement("Client Names");
		columnNames.addElement("Orders");
		columnNames.addElement("Town Location");

		jspTable = new JScrollPane();
		jspTable.setBounds((screenWidth * 1 / 10), (screenHeight * 1 / 20),(screenWidth * 8 / 10), (screenHeight * 16 / 20));
		jpShow.add(jspTable);

		desktop.putClientProperty("JDesktopPane.dragMode", "outline");
		getContentPane().add(desktop, BorderLayout.CENTER);

		getContentPane().add(jpShow);
		setSize(screenWidth, screenHeight);
		setLocation((screen.width / 2), (screen.height / 22));
		setVisible(true);
	}

	public static void reloadRecord(Vector<String> rowDataSingle) {
		jspTable.getViewport().removeAll();
		Vector<Vector> rowDataCollections2 = new Vector<Vector>();
		
		for(int i=0;i<rowDataCollections.size();i++){
			Vector<String> rowDataSingle2 = new Vector<String>();
			
			rowDataSingle2.addElement(rowDataCollections.get(i).get(0).toString());
			rowDataSingle2.addElement(rowDataCollections.get(i).get(1).toString());
			rowDataSingle2.addElement(rowDataCollections.get(i).get(2).toString());
	
			rowDataCollections2.addElement(rowDataSingle2);
		}

		rowDataCollections2.addElement(rowDataSingle);

		rowDataCollections.removeAllElements();
		rowDataCollections = rowDataCollections2;

		DefaultTableModel dtmCustomer2 = new DefaultTableModel(rowDataCollections, columnNames);

		tbCustomer = new JTable(dtmCustomer2) {
			public boolean isCellEditable(int iRow, int iCol) {
				return false; // Disable All Columns of Table.
			}
		};
		(tbCustomer.getColumnModel().getColumn(0)).setPreferredWidth(100);
		(tbCustomer.getColumnModel().getColumn(1)).setPreferredWidth(50);
		(tbCustomer.getColumnModel().getColumn(2)).setPreferredWidth(100);

		tbCustomer.setRowHeight(20);
		tbCustomer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jspTable.getViewport().add(tbCustomer);

		jpShow.repaint();
	}

	public static void deleteRecord(String name, int order, String town) {
		jspTable.getViewport().removeAll();
		Vector<Vector> rowDataCollections2 = new Vector<Vector>();
		boolean alreadyDeleted = false;
		
		for(int i=0;i<rowDataCollections.size();i++){
			Vector<String> rowDataSingle2 = new Vector<String>();
			
			if((name.contentEquals(rowDataCollections.get(i).get(0).toString()))&&(town.contentEquals(rowDataCollections.get(i).get(2).toString()))&&(order == Integer.parseInt(rowDataCollections.get(i).get(1).toString()))){
				if(alreadyDeleted == false){
					alreadyDeleted = true;
				}else{
					rowDataSingle2.addElement(rowDataCollections.get(i).get(0).toString());
					rowDataSingle2.addElement(rowDataCollections.get(i).get(1).toString());
					rowDataSingle2.addElement(rowDataCollections.get(i).get(2).toString());
			
					rowDataCollections2.addElement(rowDataSingle2);
				}
			}else{
			rowDataSingle2.addElement(rowDataCollections.get(i).get(0).toString());
			rowDataSingle2.addElement(rowDataCollections.get(i).get(1).toString());
			rowDataSingle2.addElement(rowDataCollections.get(i).get(2).toString());
	
			rowDataCollections2.addElement(rowDataSingle2);
			}
		}

		rowDataCollections.removeAllElements();
		rowDataCollections = rowDataCollections2;

		DefaultTableModel dtmCustomer2 = new DefaultTableModel(rowDataCollections, columnNames);

		tbCustomer = new JTable(dtmCustomer2) {
			public boolean isCellEditable(int iRow, int iCol) {
				return false; // Disable All Columns of Table.
			}
		};
		(tbCustomer.getColumnModel().getColumn(0)).setPreferredWidth(100);
		(tbCustomer.getColumnModel().getColumn(1)).setPreferredWidth(50);
		(tbCustomer.getColumnModel().getColumn(2)).setPreferredWidth(100);

		tbCustomer.setRowHeight(20);
		tbCustomer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jspTable.getViewport().add(tbCustomer);

		jpShow.repaint();
		
		TownOrder.deleteRecord(town, order);
	}

}

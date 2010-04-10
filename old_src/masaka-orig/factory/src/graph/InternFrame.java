package graph;
import java.awt.*;
import javax.swing.*;
import files.*;

public class InternFrame  extends JInternalFrame{
	private JDesktopPane desktop = new JDesktopPane();
	JFrame JFParentFrame;
	Dimension screen = 	Toolkit.getDefaultToolkit().getScreenSize();
    private static JPanel jpShow = new JPanel();
    

    public InternFrame(JFrame getParentFrame, MainFrame main){
        super( "List of Vehicles available", false, true, false, true );
       	jpShow.setLayout( null );
		JFParentFrame = getParentFrame;
		
	}	

}

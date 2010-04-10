package files;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import graph.UnCertainities;
import graph.dialogGraphs;

public class Problems extends JInternalFrame implements ActionListener{
	private int screenHeight;
	private int screenWidth;
    private static JPanel jpShow = new JPanel();
    private JDesktopPane desktop = new JDesktopPane();
    private MainFrame main;
	private	GridBagLayout gridbag = new GridBagLayout();
	private GridBagConstraints c = new GridBagConstraints();
	private ButtonGroup bg2 = new ButtonGroup();
  	private JRadioButton variations, progress;


  public Problems(String whichOne, MainFrame getParentFrame, Dimension screen){
        super( "The Proposed routing structures", false, false, false, false );
    	screenHeight = (screen.height/3);
    	screenWidth = (screen.width/4);
    	main = getParentFrame;

    	jpShow.setBorder(BorderFactory.createRaisedBevelBorder());
    	jpShow.setLayout(gridbag);	
	   	c.fill = GridBagConstraints.HORIZONTAL;
				
		   JLabel jlabel4 = new JLabel(" Assessment of the Suggested variations");
				c.fill = GridBagConstraints.BOTH;				
				c.weightx = 0.1;     	             						
				c.ipady = 0;       						
				c.ipadx = 0;  
				c.gridx = 0;       						     
	        	c.gridy = 0;       						
	        	gridbag.setConstraints(jlabel4, c);				
	        	jpShow.add(jlabel4);	
		
	       variations = new JRadioButton("Journey variations    ",false);
				c.fill = GridBagConstraints.WEST;		
				c.gridx = 0;       						     
				c.gridy = 1; 
				bg2.add(variations);      						
				gridbag.setConstraints(variations, c);				
				jpShow.add(variations);
				variations.addActionListener(this);  
	
		JLabel jlabelfree = new JLabel("                   ");
				c.fill = GridBagConstraints.BOTH;				
				c.gridx = 0;       						     
	        	c.gridy = 2;       						
	        	gridbag.setConstraints(jlabelfree, c);				
	        	jpShow.add(jlabelfree);
	        	
	    JLabel jlabel2 = new JLabel(" Journey Progress with vehicles needed");
				c.fill = GridBagConstraints.BOTH;				
				c.gridx = 0;       						     
				c.gridy = 3;      						
	        	gridbag.setConstraints(jlabel2, c);				
	        	jpShow.add(jlabel2);
	        	
	        progress = new JRadioButton("Jouney Progress       ",false);
				c.fill = GridBagConstraints.WEST;		
				c.gridx = 0;       						     
	        	c.gridy = 4;       					
				bg2.add(progress);	
	        	gridbag.setConstraints(progress, c);				
	        	jpShow.add(progress);
	        	progress.addActionListener(this);
			
		desktop.putClientProperty("JDesktopPane.dragMode", "outline");
		getContentPane().add(desktop, BorderLayout.CENTER);
  
        getContentPane().add( jpShow );
        setSize(screenWidth,screenHeight );
        setLocation((screen.width *7/10),(screen.height*15/44)+120);
        setVisible( true );
        
   }

  public void actionPerformed(ActionEvent ae) {
		Object obj = ae.getSource();

	    if (obj == progress) {
	    	if((this.main.to.cumulativeForTown != null)&&(this.main.to.cumulativeForTown.size() != 0)){
	    		JDialog viewCell = new	dialogGraphs(false,false,this.main, this.main, false, this.main.to.cumulativeForTown);
	    	}
				JDialog viewQty = new UnCertainities(true,true,this.main, this.main);
				try{
				desktop.add( viewQty );
				}catch(IllegalArgumentException e){}  
	        }
	        
	 /*   if (obj == RoadClosure) {
	        
				JDialog viewQty = new UnCertainities(true,false,this.main, this.main);
				try{
				desktop.add( viewQty );
				}catch(IllegalArgumentException e){}
	        }  */
		
		if(obj == variations){
			JDialog viewCell = new	dialogGraphs(false,false,this.main, this.main, true, this.main.to.cumulativeForTown);
			try{
				desktop.add( viewCell );
			}catch(IllegalArgumentException e){}   
		}  
		
	}
  
}

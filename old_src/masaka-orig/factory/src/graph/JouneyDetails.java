package graph;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

public class JouneyDetails extends Thread {
	
	public String carName;
	int NoOfKmCovered;
	Vector<Node> theNodesSupplied = new Vector<Node>();
	String direction;
	int jouneyCost;
	Vector<String> allPlacesToVisit = new Vector<String>();
	int totalCapacityCarriedByThisVehicle = 0;
	int totalCapacityCarriedByThisVehicleRightNow = 0;
	Vector<String> allPlacesVisitedWithLoadStillCarriedOnTruck = new Vector<String>();
	private int c_millisecond, c_second, c_minute, c_hour;
	public boolean isSuspended = true;
	private double[] offLoadingIntervals = { 0.4, 0.6, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0, 5.5 };
	private double[] changingNodeIntervals = { 0.3, 0.5, 0.7, 0.8, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0 };
	private static int clock_interval = 1000; // in milliseconds

	public JouneyDetails(Vector<String> allPlacesVisitedWithLoadStillCarriedOnTruck, int totalCapacityCarriedByThisVehicleRightNow, int totalCapacityCarriedByThisVehicle, String carName, int NoOfKmCovered, Vector<Node> theNodesSupplied, String direction, int jouneyCost, Vector<String> allPlacesToVisit){
		
		this.carName = carName;
		this.NoOfKmCovered = NoOfKmCovered;
		this.theNodesSupplied = theNodesSupplied;
		this.direction = direction;
		this.jouneyCost = jouneyCost;
		this.allPlacesToVisit = allPlacesToVisit;
		this.totalCapacityCarriedByThisVehicle = totalCapacityCarriedByThisVehicle;
		this.totalCapacityCarriedByThisVehicleRightNow = totalCapacityCarriedByThisVehicleRightNow;
		this.allPlacesVisitedWithLoadStillCarriedOnTruck = allPlacesVisitedWithLoadStillCarriedOnTruck;
		
	}
	
	public JouneyDetails(String carName, Vector<String> allPlacesToVisit){
		
		this.carName = carName;
		this.allPlacesToVisit = allPlacesToVisit;
	
	}
	
	public JouneyDetails(){}
	   
	public void run() {
	      while (!isInterrupted()) {
	         try {
	            sleep(clock_interval);
	         } catch (InterruptedException e) {
	            break; // the main thread wants this thread to end
	         }

	         c_millisecond -= clock_interval; 
	         
	         if (c_millisecond>=1000) {
	            c_second += c_millisecond/1000;
	      	    c_millisecond = c_millisecond%1000;
	         }
	         
	         if (c_second>=60) {
	      	    c_minute += c_second/60;
	      	    c_second = c_second%60;
	         }
	         
	         if (c_minute>=60) {
	      	    c_hour += c_minute/60;
	            c_minute = c_minute%60;
	         }
	         
	         if (c_millisecond<0) {
	      	    c_second--;
	            c_millisecond += 1000;
	         }
	         
	         if (c_second<0) {
	      	    c_minute--;
	            c_second += 60;
	         }
	         
	         if (c_minute<0) {
	            c_hour--;
	            c_minute += 60;
	         }
	         
	      }

	   }	

	private String getClock() {
		      // returning the clock as a string of HH:mm:ss:SSS format
		      GregorianCalendar c = new GregorianCalendar();
		      c.set(Calendar.HOUR_OF_DAY,c_hour);
		      c.set(Calendar.MINUTE,c_minute);
		      c.set(Calendar.SECOND,c_second);
		      c.set(Calendar.MILLISECOND,c_millisecond);
		      SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss:SSS");
		      return f.format(c.getTime());
	}

	public void changingFromTownToTown(int carNo, String whereIam, String whereIamHeading){
		
		if(whereIam.contentEquals(whereIamHeading) == true){
			int totalSec = (int)(offLoadingIntervals[carNo]* 60.0);

	      	 c_hour = (int)(totalSec/3600);
	      	 int remainderSeconds = (int)(totalSec%3600);
	      	 c_minute = (int)(remainderSeconds/60);
	         c_second = (int)(remainderSeconds%60);
	         c_millisecond = 0;
	         
		}else{
			int totalSec = (int)(changingNodeIntervals[carNo]* 60.0);

	      	 c_hour = (int)(totalSec/3600);
	      	 int remainderSeconds = (int)(totalSec%3600);
	      	 c_minute = (int)(remainderSeconds/60);
	         c_second = (int)(remainderSeconds%60);
	         c_millisecond = 0;         
		}
        this.isSuspended = false;
			//System.out.println("hr..." +c_hour+"   min..." +c_minute+"    sec..." +c_second+"   micsec..." +c_millisecond);
	}
	
	public boolean startCount(){
		
	      if(this.isSuspended == false) {
	         // System.out.print(this.carName+"         "+ this.getClock()+"\r");
	       }
	         if (this.getClock().toString().contentEquals("00:00:00:000")) {
		            this.suspend();
		            this.isSuspended = true;
		     }
	         
	         if (this.getClock().toString().contentEquals("23:59:55:000")) {
		            this.suspend();
		            this.isSuspended = true;
		     }
	         
	         if (this.getClock().toString().contentEquals("23:59:50:000")) {
		            this.suspend();
		            this.isSuspended = true;
		     }
	         
	         if (this.getClock().toString().contentEquals("23:59:45:000")) {
		            this.suspend();
		            this.isSuspended = true;
		     }
	         
	         if (this.getClock().toString().contentEquals("23:59:40:000")) {
		            this.suspend();
		            this.isSuspended = true;
		     }
	         
	         if (this.getClock().toString().contentEquals("23:59:35:000")) {
		            this.suspend();
		            this.isSuspended = true;
		     }
	         
	         if (this.getClock().toString().contentEquals("23:59:30:000")) {
		            this.suspend();
		            this.isSuspended = true;
		     }
	         
	         if (this.getClock().toString().contentEquals("23:59:25:000")) {
		            this.suspend();
		            this.isSuspended = true;
		     }
	         
	         if (this.getClock().toString().contentEquals("23:59:20:000")) {
		            this.suspend();
		            this.isSuspended = true;
		     }
	         
	         if (this.getClock().toString().contentEquals("23:59:15:000")) {
		            this.suspend();
		            this.isSuspended = true;
		     }
	         
	         if (this.getClock().toString().contentEquals("23:59:10:000")) {
		            this.suspend();
		            this.isSuspended = true;
		     }
	         
	         if (this.getClock().toString().contentEquals("23:59:05:000")) {
		            this.suspend();
		            this.isSuspended = true;
		     }
	         
	         if (this.getClock().toString().contentEquals("23:59:00:000")) {
		            this.suspend();
		            this.isSuspended = true;
		     }
	         
	   return this.isSuspended;
	}
	
}

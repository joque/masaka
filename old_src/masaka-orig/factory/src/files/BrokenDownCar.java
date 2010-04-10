package files;

import graph.JouneyDetails;

import java.util.Random;
import java.util.Vector;

public class BrokenDownCar {
	
	private Vector<Vector> carsOnWholeRoute;
	private Vector brokenCarDetails;
	  
	public BrokenDownCar(Vector<JouneyDetails> wholeRoute){
		
		carsOnWholeRoute = new Vector<Vector>();
		
		for(int i=0;i<wholeRoute.size();i++){
			Vector car = new Vector();
			car.add(i);
			car.add(wholeRoute.get(i).carName);
			
			carsOnWholeRoute.add(car);
		}
		
		Vector carnul = new Vector();
		carnul.add(wholeRoute.size()+1);
		carnul.add("");	
		carsOnWholeRoute.add(carnul);
		
		setBrokenCarDetails(this.carsOnWholeRoute);
       
    }
	
	public void setBrokenCarDetails(Vector<Vector> carDetails){
		Random allCarsAvailable = new Random();
		int brokenCarNo = allCarsAvailable.nextInt(carDetails.size());
		this.brokenCarDetails = carDetails.get(brokenCarNo);
	}
	
	public Vector getBrokenCarDetails(){
		return this.brokenCarDetails;
	}
       
}

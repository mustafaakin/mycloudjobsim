import java.util.Random;


public class RandomValue {
	public double average; // between 0 and 1
	public double deviation;	// between 0 and 1
	static Random rnd = new Random();
	
	public RandomValue(double average, double deviation){
		this.average = average;
		this.deviation = deviation;
	}
	
	public double generateRandom(){
		return average;
		// return (rnd.nextGaussian() * deviation) + average;
	}
}

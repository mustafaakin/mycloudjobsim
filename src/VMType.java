public class VMType {
	double price;
	String name;
	RandomValue booting;
	
	public VMType(String name, double price, RandomValue booting){
		this.name = name;
		this.price = price;
		this.booting = booting;
	}
	
	@Override
	public String toString() {
		return String.format("VM %s, %.2f per hour, booting time avg: %.2f, dev: %.2f", name, price, booting.average, booting.deviation);
	}
}

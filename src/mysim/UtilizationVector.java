package mysim;
public class UtilizationVector {
	public RandomValue CPU;
	public RandomValue RAM;
	public RandomValue disk;
	public RandomValue network;

	public UtilizationVector(RandomValue CPU, RandomValue RAM, RandomValue disk,
			RandomValue network) {
		this.CPU = CPU;
		this.RAM = RAM;
		this.disk = disk;
		this.network = network;
	}
}

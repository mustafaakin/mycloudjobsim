package mysim;
import java.util.HashMap;


public class JobType {
	public String name;
	public HashMap<VMType, UtilizationVector> utilizationVector = new HashMap<VMType, UtilizationVector>();
	public HashMap<VMType, RandomValue> times = new HashMap<VMType, RandomValue>();
	
	public JobType(String name){
		this.name = name;
	}
	
	public void addVMUtilization(VMType vmType, UtilizationVector vector, RandomValue time){
		utilizationVector.put(vmType, vector);
		times.put(vmType, time);
	}
}

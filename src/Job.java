import java.util.HashMap;

public class Job {
	static int counter = 0;
	JobType type;
	int arrivalTime;
	VM assignedTo;
	int time = 0;
	int id;
	double utilCPU, utilRAM, utilDisk, utilNetwork;
	boolean isFinished = false;
	int startTime = -1;
	
	public void setFinished() {
		this.isFinished = true;
	}
	
	
	public void startJob(int time){
		startTime = time;
	}
	
	public boolean isStarted(){
		return startTime != -1;
	}
	
	public void assignVM(VM vm) {
		vm.jobs.add(this);
		VMType vmType = vm.type;
		RandomValue r = type.times.get(vmType);
		time = (int) r.generateRandom();
		UtilizationVector vector = type.utilizationVector.get(vmType);
		utilCPU = vector.CPU.generateRandom();
		utilRAM = vector.RAM.generateRandom();
		utilDisk = vector.disk.generateRandom();
		utilNetwork = vector.network.generateRandom();
	}

	public Job(JobType type, int arrivalTime) {
		this.type = type;
		this.arrivalTime = arrivalTime;
		id = counter;
		counter++;
	}

	@Override
	public String toString() {
		return id + " " + type.name + " " + arrivalTime;
	}
}

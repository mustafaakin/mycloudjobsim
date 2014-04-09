package mysim;

import java.util.HashMap;

public class Job {
	static int counter = 0;
	JobType type;
	int arrivalTime;
	VM assignedTo;
	double time = 0;
	int id;
	double utilCPU, utilRAM, utilDisk, utilNetwork;
	boolean isStarted = false;
	boolean isDefunct = false;

	public void setDefunct(boolean isDefunct) {
		this.isDefunct = isDefunct;
	}

	public boolean isDefunct() {
		return isDefunct;
	}

	public boolean isPreviouslyFinished() {
		return time == -1;
	}

	public boolean isFinished() {
		return time <= 0;
	}

	public int getId() {
		return id;
	}

	public void startJob() {
		isStarted = true;
	}

	public boolean isStarted() {
		return isStarted;
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
		assignedTo = vm;
	}

	public Job(JobType type, int arrivalTime) {
		this.type = type;
		this.arrivalTime = arrivalTime;
		id = counter;
		counter++;
	}

	public void update(double decrease) {
		if (isStarted() && !isFinished()) {
			time = time - decrease;
			if (time < 0) {
				time = 0;
			}
		}

	}

	@Override
	public String toString() {
		return String.format("#%d, %.1f", id, time);
	}

}

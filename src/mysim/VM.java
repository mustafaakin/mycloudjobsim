package mysim;
import java.util.ArrayList;

public class VM {
	static int counter = 0;
	public VMType type;
	public ArrayList<Job> jobs = new ArrayList<Job>();
	public int bootStartTime = -1;
	public int bootTime = 0;
	public int id;
	private boolean isDestroyed = false;
	int destroyTime = -1;
	
	public ArrayList<Job> getJobs() {
		return jobs;
	}
	
	public boolean isDestroyed() {
		return isDestroyed;
	}
	
	public void destroy(int time) {
		isDestroyed = true;
		destroyTime = time;
	}
	
	public VM(VMType type, int bootStartTime) {
		counter++;
		id = counter;
		this.type = type;
		this.bootStartTime = bootStartTime;
		bootTime = (int) type.booting.generateRandom();
	}

	public boolean isBooted(int time) {
		return time == bootStartTime + bootTime;
	}

	public boolean isUp(int time) {
		return !isDestroyed && (time >= bootStartTime + bootTime);
	}

	public double getJobSpeed() {
		if (jobs.size() == 0)
			return 1;
		else {
			// CPU, RAM, disk, network
			double utilCPU = 0, utilRAM = 0, utilDisk = 0, utilNetwork = 0;

			for (Job j : jobs) {
				if (j.isStarted() && !j.isFinished()) {
					utilCPU += j.utilCPU;
					utilRAM += j.utilRAM;
					utilDisk += j.utilDisk;
					utilNetwork += j.utilNetwork;
				}
			}
			
			double speed = 1;
			
			if ( utilCPU > 1) {
				speed /= utilCPU;
			}
			if ( utilRAM > 1) {
				speed /= Math.exp(utilRAM);
			}
			if ( utilDisk > 1) {
				speed /= utilDisk / 1.5;
			}
			if ( utilNetwork > 1) {
				speed /= utilNetwork / 1.1;
			}

			return speed;		
		}
	}
	public double getJobSpeedIfJobPut(Job job) {
		if (jobs.size() == 0)
			return 1;
		else {
			// CPU, RAM, disk, network
			double utilCPU = 0, utilRAM = 0, utilDisk = 0, utilNetwork = 0;

			for (Job j : jobs) {
				if (j.isStarted() && !j.isFinished()) {
					utilCPU += j.utilCPU;
					utilRAM += j.utilRAM;
					utilDisk += j.utilDisk;
					utilNetwork += j.utilNetwork;
				}
			}
			utilCPU += job.utilCPU;
			utilRAM += job.utilRAM;
			utilDisk += job.utilDisk;
			utilNetwork += job.utilNetwork;
			
			double speed = 1;
			
			if ( utilCPU > 1) {
				speed /= utilCPU;
			}
			if ( utilRAM > 1) {
				speed /= Math.exp(utilRAM - 0.5);
			}
			if ( utilDisk > 1) {
				speed /= utilDisk / 1.5;
			}
			if ( utilNetwork > 1) {
				speed /= utilNetwork / 1.1;
			}

			return speed;		
		}
	}

}

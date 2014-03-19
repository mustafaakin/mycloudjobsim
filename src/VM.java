import java.util.ArrayList;

public class VM {
	static int counter = 0;
	public VMType type;
	public ArrayList<Job> jobs = new ArrayList<Job>();
	public int bootStartTime = -1;
	public int bootTime = 0;
	public int id;

	public VM(VMType type) {
		counter++;
		id = counter;
		this.type = type;
		bootTime = (int) type.booting.generateRandom();
	}

	public boolean isBooted(int time) {
		return time == bootStartTime + bootTime;
	}

	public boolean isUp(int time) {
		return time >= bootStartTime + bootTime;
	}

	public double getJobSpeed() {
		if (jobs.size() == 0)
			return 1;
		else {
			// CPU, RAM, disk, network
			double utilCPU = 0, utilRAM = 0, utilDisk = 0, utilNetwork = 0;

			for (Job j : jobs) {
				if (j.isStarted() && !j.isFinished) {
					utilCPU += j.utilCPU;
					utilRAM += j.utilRAM;
					utilDisk += j.utilDisk;
					utilNetwork += j.utilNetwork;
				}
			}
			// For now, simple assumption that CPU affects
			if (utilCPU < 1) {
				return 1;
			} else {
				return 1.0 / utilCPU;
			}
		}
	}

}

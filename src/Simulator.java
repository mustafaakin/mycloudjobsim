import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class Simulator {
	HashMap<String, VMType> vmTypes = new HashMap<String, VMType>();
	HashMap<String, JobType> jobTypes = new HashMap<String, JobType>();
	ArrayList<Job> jobs = new ArrayList<Job>();

	ArrayList<VM> vms = new ArrayList<VM>();

	int time = 0;

	public void parseCase(String filenamePrefix) {
		try {
			// Parse VMs
			Scanner scan = new Scanner(new File(filenamePrefix + "_vms"));
			while (scan.hasNextLine()) {
				String name = scan.next();
				double price = scan.nextDouble();
				double avg = scan.nextDouble();
				double dev = scan.nextDouble();
				RandomValue booting = new RandomValue(avg, dev);
				VMType vm = new VMType(name, price, booting);
				vmTypes.put(name, vm);
			}
			scan.close();

			// Parse JobTypes
			scan = new Scanner(new File(filenamePrefix + "_jobtypes"));
			while (scan.hasNextLine()) {
				String name = scan.next();
				String vmname = scan.next();

				// 1 time, 4 resource
				RandomValue[] randoms = new RandomValue[5];

				for (int i = 0; i < randoms.length; i++) {
					double avg = scan.nextDouble();
					double dev = scan.nextDouble();
					RandomValue r = new RandomValue(avg, dev);
					randoms[i] = r;
				}

				// Order: CPU, RAM, disk, network
				UtilizationVector vector = new UtilizationVector(randoms[1], randoms[2], randoms[3], randoms[4]);

				// Find which VM that machine belongs to
				VMType vm = vmTypes.get(vmname);
				JobType job = jobTypes.get(name);
				if (job == null) {
					job = new JobType(name);
					jobTypes.put(name, job);
				}
				job.addVMUtilization(vm, vector, randoms[0]);
			}
			scan.close();

			scan = new Scanner(new File(filenamePrefix + "_jobs"));
			while (scan.hasNextLine()) {
				String name = scan.next();
				int time = scan.nextInt();
				JobType jobType = jobTypes.get(name);
				if (jobType == null) {
					System.err.println("Job name " + name + " does not exist.");
					System.exit(1);
				}
				Job job = new Job(jobType, time);
				jobs.add(job);
			}
			scan.close();

			Collections.sort(jobs, new Comparator<Job>() {
				@Override
				public int compare(Job a, Job b) {
					return a.arrivalTime - b.arrivalTime;
				}

			});

		} catch (IOException ex) {
			System.err.println("File read error: " + ex.getMessage());
			System.exit(1);
		}
	}

	public void printState() {
		// First VMs
		Collection<VMType> vms = vmTypes.values();
	}

	public Simulator(String filenamePrefix) {
		parseCase(filenamePrefix);
	}

	public void startSimulation() {
		time = 0;
		VM vmX = new VM(vmTypes.get("m1.large"));
		vmX.bootStartTime = 0;
		vms.add(vmX);

		while (time < 1000) {
			for (Job j : jobs) {
				if (j.arrivalTime > time) {
					// No need to go further since it is a sorted list
					// Can be made more efficient by removing already made jobs
					// but whatever for now
					break;
				}
				if (j.arrivalTime == time) {
					HashMap<VMType, RandomValue> times = j.type.times;

					double minTime = Double.MAX_VALUE;
					VMType decidedVMType = null;
					for (Entry<VMType, RandomValue> e : times.entrySet()) {
						VMType type = e.getKey();
						double expectedTime = e.getValue().average;

						if (expectedTime < minTime) {
							minTime = expectedTime;
							decidedVMType = type;
						}
					}

					log(j.type.name + " assigned to " + decidedVMType.name + " id: " + vmX.id);
					j.assignVM(vmX);
					if (vmX.isBooted(time)) {
						j.startTime = time;
					}
				}
			}

			// Examine if each VM completed
			for (VM vm : vms) {
				if (vm.isBooted(time)) {
					log("VM " + vm.id + " booted");
				}
				if (vm.isUp(time)) {
					for (Job j : vm.jobs) {
						if (!j.isStarted()) {
							j.startJob(time);
							log("Job #" + j.id + " started on VM #" + vm.id + String.format(" JobSpeed:%.2f", vm.getJobSpeed()));
							// printVMStates();
						}
					}
					for (Job j : vm.jobs) {
						if (j.isStarted() && !j.isFinished) {
							if (time >= j.startTime + (double) j.time / vm.getJobSpeed()) {
								j.setFinished();
								log("Job #" + j.id + " finished on VM #" + vm.id + String.format(" JobSpeed:%.2f", vm.getJobSpeed()));
								// printVMStates();
							}
						}
					}
				}
			}

			time++;
		}
	}

	private void printVMStates() {
		String vmStates = "";
		for (VM vm : vms) {
			String str = String.format("VM#%3d JobSpeed: %.2f ", vm.id, vm.getJobSpeed());
			vmStates += str;
		}
		log(vmStates);

	}

	private void log(String str) {
		System.out.println(String.format("%5ds\t%s", time, str));
	}
}

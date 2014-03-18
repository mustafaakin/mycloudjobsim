import java.io.File;
import java.io.IOException;
import java.util.*;

public class Simulator {
	HashMap<String, VMType> vmTypes = new HashMap<String, VMType>();
	HashMap<String, JobType> jobTypes = new HashMap<String, JobType>();
	ArrayList<Job> jobs = new ArrayList<Job>();

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
				UtilizationVector vector = new UtilizationVector(randoms[1],
						randoms[2], randoms[3], randoms[4]);
				
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
				if ( jobType == null){
					System.err.println("Job name " + name + " does not exist.");
					System.exit(1);
				}
				Job job = new Job(jobType, time);
				jobs.add(job);
			}
			scan.close();

		} catch (IOException ex) {
			System.err.println("File read error: " + ex.getMessage());
			System.exit(1);
		}
	}
	public void printState(){
		// First VMs
		Collection<VMType> vms = vmTypes.values();
	}
	
	
	public Simulator(String filenamePrefix){
		parseCase(filenamePrefix);
	}
}

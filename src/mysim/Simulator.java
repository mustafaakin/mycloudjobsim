package mysim;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import mysim.allocation.IAllocationPolicy;

public class Simulator {
	State state = new State();
	IAllocationPolicy policy;
	
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
				state.getVmTypes().put(name, vm);
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
				VMType vm = state.getVmTypes().get(vmname);
				JobType job = state.getJobTypes().get(name);
				if (job == null) {
					job = new JobType(name);
					state.getJobTypes().put(name, job);
				}
				job.addVMUtilization(vm, vector, randoms[0]);
			}
			scan.close();

			scan = new Scanner(new File(filenamePrefix + "_jobs"));
			while (scan.hasNextLine()) {
				String name = scan.next();
				int time = scan.nextInt();
				JobType jobType = state.getJobTypes().get(name);
				if (jobType == null) {
					System.err.println("Job name " + name + " does not exist.");
					System.exit(1);
				}
				Job job = new Job(jobType, time);
				state.getJobs().add(job);
			}
			scan.close();

			Collections.sort(state.getJobs(), new Comparator<Job>() {
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

	public double calculateCost(){
		double cost = 0;
		for(VM vm : state.getVms()){
			cost += Math.ceil((vm.destroyTime - vm.bootStartTime )/60.0) * vm.type.getPrice(); 
		}
		return cost;
	}
	
	public Simulator(String filenamePrefix, IAllocationPolicy policy) {
		parseCase(filenamePrefix);
		this.policy = policy;
	}


	public void startSimulation() {		
		while (!state.isOver()) {			
			finishJobs();
			assignJobs();
			updateJobs();
			state.tick();
		}
		// All Jobs are assigned to some VM
		state.log("All jobs assigned");		
		while(!state.isComplete()){
			finishJobs();
			assignJobs();
			updateJobs();
			state.tick();
		}
		policy.done(state);
		
		System.err.println("\nSIMULATION ENDED AT TIME: " +  (state.getTime() - 1) + " minutes, TOTAL COST: $" + calculateCost());
	}
	
	private void finishJobs() {
		for (VM vm : state.getVms()) {
			if (vm.isBooted(state.getTime())) {
				state.log("VM #" + vm.id + " booted");
			}
			if (vm.isUp(state.getTime())) {
				for (Job j : vm.jobs) {
					if (!j.isDefunct() && j.isFinished()) {
						state.log("Job #" + j.id + " finished on VM #" + vm.id);
						j.setDefunct(true);
					}
				}
			}
		}

	}

	private void updateJobs() {
		for (VM vm : state.getVms()) {
			if (vm.isUp(state.getTime())) {
				for (Job j : vm.jobs) {
					j.update(vm.getJobSpeed());
					// System.out.println(vm.getJobSpeed());
				}
			}
		}
	}

	private void assignJobs() {
		for (Job j : state.getJobs()) {
			if (j.arrivalTime > state.getTime()) {
				// No need to go further since it is a sorted list
				// Can be made more efficient by removing already made jobs
				// but whatever for now
				break;
			}
			if (j.arrivalTime == state.getTime()) {
				policy.addJob(state, j);
			}
		}
		policy.update(state);
	}

	private void jobStatus() {
		String str = "";
		for (Job j : state.getJobs()) {
			str += j.toString() + "\t";
		}
		state.log(str);
	}


}

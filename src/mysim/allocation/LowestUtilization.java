package mysim.allocation;

import java.util.ArrayList;
import java.util.Map.Entry;

import mysim.Job;
import mysim.JobType;
import mysim.RandomValue;
import mysim.State;
import mysim.UtilizationVector;
import mysim.VM;
import mysim.VMType;

public class LowestUtilization  implements IAllocationPolicy {

	public String toString() {
		return "LowestUtilization;" + ";" + String.format("%.1f", factor);
	};

	double factor;

	public LowestUtilization(double factor) {
		this.factor = factor;
	}

	private ArrayList<VM> vms = new ArrayList<VM>();
	private boolean done = false;

	@Override
	public void addJob(State s, Job j) {
		VM vm = null;
		VMType type = null;
		JobType jobType = j.getType();

		double utilization = Double.MAX_VALUE;
		// Find 
		for (Entry<VMType, UtilizationVector> e : jobType.utilizationVector.entrySet()) {
			UtilizationVector v = e.getValue();
			double u = v.CPU.average + Math.exp(v.RAM.average) + v.disk.average / 1.5 + v.network.average / 1.1;
			VMType vmtype = e.getKey();
			double price = s.getVmTypes().get(vmtype.getName()).getPrice();
			double time = jobType.times.get(vmtype).average;
			u = u * time;
			if ( u < utilization){
				type = e.getKey();
				utilization = u;
			}
		}

		boolean foundOne = false;
		for (VM vm2 : vms) {
			if (type == vm2.type) {
				double jobs = 0;
				for (Job j1 : vm2.getJobs()) {
					if (!j1.isFinished()) {
						jobs++;
					}
				}
				if (jobs  < factor) {
					foundOne = true;
					vm = vm2;
					break;
				}
			}
		}

		if (!foundOne) {
			vm = s.addVM(type, s.getTime());
			vms.add(vm);
		}
		if (vm == null) {
			System.err.println("VM cannot be null here.");
			System.exit(1);
		}
		s.log("Job #" + j.getId() + " assigned to VM #" + vm.id);
		j.assignVM(vm);
	}

	@Override
	public void update(State s) {
		for (VM vm : vms)
			if (vm != null && vm.isUp(s.getTime())) {
				for (Job j : vm.jobs) {
					if (!j.isStarted() && !j.isFinished()) {
						s.log("Job #" + j.getId() + " started on VM #" + vm.id);
						j.startJob();
					}
					if (j.isFinished()) {
						// Cannot destroy if other jobs are present
						// j.getAssignedTo().destroy(s.getTime());
						// s.log("VM #" + vm.id + " is destroyed");
					}
				}
			}
	}

	@Override
	public void done(State s) {
		for (VM vm : vms) {
			if (!vm.isDestroyed()) {
				vm.destroy(s.getTime());
				s.log("VM #" + vm.id + " is destroyed");
			}
		}
	}

}

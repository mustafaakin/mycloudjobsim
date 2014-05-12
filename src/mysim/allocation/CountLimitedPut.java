package mysim.allocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Map.Entry;

import mysim.Job;
import mysim.JobType;
import mysim.RandomValue;
import mysim.State;
import mysim.VM;
import mysim.VMType;

public class CountLimitedPut implements IAllocationPolicy {

	public String toString() {
		return "CountLimitedPut;" + strategy  + ";" + String.format("%.1f", factor);
	};

	Strategy strategy;
	double factor;

	public CountLimitedPut(Strategy strategy, double factor) {
		this.factor = factor;
		this.strategy = strategy;
	}

	private ArrayList<VM> vms = new ArrayList<VM>();
	private boolean done = false;

	@Override
	public void addJob(State s, Job j) {
		VM vm = null;
		VMType type = null;
		JobType jobType = j.getType();

		if (strategy == Strategy.Fastest) {
			double time = Double.MAX_VALUE;
			for (Entry<VMType, RandomValue> e : jobType.times.entrySet()) {
				double t = e.getValue().average;
				if (t < time) {
					time = t;
					type = e.getKey();
				}
			}
		} else if (strategy == Strategy.Cheapest) {
			double cost = Double.MAX_VALUE;
			for (Entry<String, VMType> e : s.getVmTypes().entrySet()) {
				double t = e.getValue().getPrice();
				if (t < cost) {
					cost = t;
					type = e.getValue();
				}
			}
		} else if (strategy == Strategy.Random) {
			Collection<VMType> vmtypes = s.getVmTypes().values();
			Random r = new Random();
			int randomIdx = r.nextInt(vmtypes.size());
			int idx = 0;
			for (VMType v : vmtypes) {
				if (idx == randomIdx) {
					type = v;
					break;
				}
				idx++;
			}
		} else if (strategy == Strategy.CostPerf) {
			double perf = Double.MAX_VALUE;
			for (Entry<VMType, RandomValue> e : jobType.times.entrySet()) {
				VMType vmtype = e.getKey();
				double vmcost = vmtype.getPrice();
				double vmtime = e.getValue().average;			
				double performance = vmcost * vmcost *  vmtime;			
				if (performance < perf) {
					performance = perf;
					type = e.getKey();
				}
			}
		} else {
			throw new IllegalArgumentException("Unknown Strategy: " + strategy);
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
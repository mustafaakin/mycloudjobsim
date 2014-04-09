package mysim.allocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Random;

import mysim.Job;
import mysim.JobType;
import mysim.RandomValue;
import mysim.State;
import mysim.VM;
import mysim.VMType;

public class VMPerJob implements IAllocationPolicy {


	Strategy strategy;

	public VMPerJob(Strategy s) {
		strategy = s;
	}
@Override
public String toString() {
	return "VMPer Job " + strategy;
}
	private ArrayList<VM> vms = new ArrayList<VM>();
	private boolean done = false;

	@Override
	public void addJob(State s, Job j) {
		VM vm;
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
		} else {
			Collection<VMType> vmtypes = s.getVmTypes().values();
			Random r = new Random();
			int randomIdx = r.nextInt(vmtypes.size());
			int idx = 0;
			for(VMType v : vmtypes){
				if ( idx == randomIdx){
					type = v;
					break;
				}
				idx++;
			}
		}

		vm = s.addVM(type, s.getTime());
		vms.add(vm);

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
						j.getAssignedTo().destroy(s.getTime());
						s.log("VM #" + vm.id + " is destroyed");
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

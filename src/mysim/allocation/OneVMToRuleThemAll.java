package mysim.allocation;

import java.util.Map.Entry;

import mysim.Job;
import mysim.State;
import mysim.VM;
import mysim.VMType;

public class OneVMToRuleThemAll implements IAllocationPolicy {
	private VM vm;

	@Override
	public void addJob(State s, Job j) {
		if (vm == null) {
			VMType type = null;
			double price = Double.MAX_VALUE;
			for(Entry<String, VMType> e : s.getVmTypes().entrySet()){
				VMType type2 = e.getValue();
				if ( type2.getPrice() < price){
					type = type2;
					price = type2.getPrice();
				}
			}
			vm = s.addVM(type, s.getTime());
		}
		s.log("Job #" + j.getId() + " assigned to VM #" + vm.id);
		j.assignVM(vm);
	}

	@Override
	public void update(State s) {
		if (vm != null && vm.isUp(s.getTime())) {		
			boolean isFinished = true;
			for (Job j : vm.jobs) {
				if (!j.isStarted() && !j.isFinished()) {
					s.log("Job #" + j.getId() + " started on VM #" + vm.id);
					j.startJob();
				}
				if ( !j.isFinished()){
					isFinished = false;
				}
			}
			if ( isFinished){
				vm.destroy(s.getTime());
				s.log("VM #" + vm.id + " is destroyed");
			}
		}
	}
}

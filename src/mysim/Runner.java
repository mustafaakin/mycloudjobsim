package mysim;

import mysim.allocation.*;

public class Runner {
	public static void report(String filename, IAllocationPolicy policy) {
		Simulator s = new Simulator(filename, policy);
		State st = s.startSimulation();
		String msg = String.format("%50s Time: %4d minutes Cost: %4.3f $",
				policy.toString(), st.getTime(), st.getCost());
		System.out.println(msg);
	}

	public static void main(String[] args) {
		// Simulator s = new Simulator("case1", new OneVMToRuleThemAll());
		State.LOG = false;
		report("case1", new OneVMToRuleThemAll());
		for(int i = 1; i < 10; i++){
			report("case1", new VMPerJob(Strategy.Cheapest));
			report("case1", new VMPerJob(Strategy.Random));
			report("case1", new VMPerJob(Strategy.Fastest));
			
			report("case1", new IntelligentVMPerJob(Strategy.Cheapest, i));			
			report("case1", new IntelligentVMPerJob(Strategy.Random, i));			
			report("case1", new IntelligentVMPerJob(Strategy.Fastest, i));
			

		}

	}

}

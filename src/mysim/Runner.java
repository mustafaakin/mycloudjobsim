package mysim;

import mysim.allocation.*;

public class Runner {
	public static void report(String filename, IAllocationPolicy policy) {
		Simulator s = new Simulator(filename, policy);
		State st = s.startSimulation();
		String msg = String.format("%50s\t%4d\t%4.2f",
				policy.toString(), st.getTime(), st.getCost());
		System.out.println(msg);
	}

	public static void main(String[] args) {
		State.LOG = false;
		// report("case1", new OneVMToRuleThemAll());
		report("case1", new VMPerJob(Strategy.Cheapest));
		// report("case1", new VMPerJob(Strategy.Random));
		report("case1", new VMPerJob(Strategy.Fastest));
		report("case1", new VMPerJob(Strategy.CostPerf));
		
		System.out.println();
		
		for(double i = 1; i < 10; i = i + 0.2){			
			report("case1", new IntelligentVMPerJob(Strategy.Cheapest, i));			
			// report("case1", new IntelligentVMPerJob(Strategy.Random, i));			
			report("case1", new IntelligentVMPerJob(Strategy.Fastest, i));
			report("case1", new IntelligentVMPerJob(Strategy.CostPerf, i));
			System.out.println();
		}
	}
}

package mysim;

import mysim.allocation.*;

public class Runner {
	public static void report(String filename, IAllocationPolicy policy) {
		Simulator s = new Simulator(filename, policy);
		State st = s.startSimulation();
		String msg = String.format("%50s;%4d;%4.2f", policy.toString(), st.getTime(), st.getCost());
		System.out.println(msg);
	}

	public static void main(String[] args) {

		int[] times = new int[] { 5, 10, 60, 120, 1000 };
		int[] counts = new int[] { 5, 10, 50, 100, 200, 500, 1000, 10000 };

		State.LOG = false;

		for (int time : times) {			
			for (int count : counts) {
				JobGen.generate(time, count);
				System.out.println("~ ===================================================================");
				System.out.println("~ ======  Time:" + time + "===== Count:" + count + "============");
								
				report("case1", new VMPerJob(Strategy.Cheapest));
				report("case1", new VMPerJob(Strategy.Random));
				report("case1", new VMPerJob(Strategy.Fastest));
				report("case1", new VMPerJob(Strategy.CostPerf));
				System.out.println();

				for (double i = 1; i < 10; i = i + 1) {
					report("case1", new CountLimitedPut(Strategy.Cheapest, i));
					report("case1", new IntelligentVMPerJob(Strategy.Cheapest, i));

					report("case1", new CountLimitedPut(Strategy.Random, i));
					report("case1", new IntelligentVMPerJob(Strategy.Random, i));
					
					report("case1", new CountLimitedPut(Strategy.Fastest, i));
					report("case1", new IntelligentVMPerJob(Strategy.Fastest, i));
					
					report("case1", new CountLimitedPut(Strategy.CostPerf, i));
					report("case1", new IntelligentVMPerJob(Strategy.CostPerf, i));

					report("case1", new LowestUtilization(i));
					System.out.println();
					
				}

			}
		}
	}
}

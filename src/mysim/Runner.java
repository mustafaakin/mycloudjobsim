package mysim;
import mysim.allocation.OneVMToRuleThemAll;

public class Runner {


	public static void main(String[] args) {
		Simulator s = new Simulator("case1", new OneVMToRuleThemAll());
		s.startSimulation();
	}

}

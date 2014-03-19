package mysim.allocation;
import mysim.Job;
import mysim.State;

public interface IAllocationPolicy {
	public void addJob(State s, Job j);
	public void update(State s);
}

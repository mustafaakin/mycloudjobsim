package mysim.allocation;
import mysim.Job;
import mysim.State;

public interface IAllocationPolicy {
	// It notifies the allocation policy but you should not start 
	public void addJob(State s, Job j);
	// Jobs must be started here, but you can delay the starting of them if you wish, or you can close VMs if you wish
	public void update(State s);
	// Notify if there are no jobs
	public void done(State s);
}

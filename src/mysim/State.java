package mysim;

import java.util.ArrayList;
import java.util.HashMap;

public class State {
	private HashMap<String, VMType> vmTypes = new HashMap<String, VMType>();
	private HashMap<String, JobType> jobTypes = new HashMap<String, JobType>();
	private ArrayList<Job> jobs = new ArrayList<Job>();
	private ArrayList<VM> vms = new ArrayList<VM>();
	private int time = 0;

	public int getTime() {
		return time;
	}

	public ArrayList<VM> getVms() {
		return vms;
	}

	public ArrayList<Job> getJobs() {
		return jobs;
	}

	public HashMap<String, JobType> getJobTypes() {
		return jobTypes;
	}

	public HashMap<String, VMType> getVmTypes() {
		return vmTypes;
	}

	public State() {

	}

	public VM addVM(VMType t, int time) {
		VM vm = new VM(t, time);
		vms.add(vm);
		return vm;
	}

	public Job addJob(JobType jobType, VM vm) {
		Job j = new Job(jobType, time);
		j.assignVM(vm);
		jobs.add(j);
		return j;
	}

	public void tick() {
		time++;
	}

	public boolean isOver() {
		// In efficient as hell
		for (Job j : jobs) {
			if (j.assignedTo == null)
				return false;
		}

		return true;
	}
	
	public boolean isComplete(){
		for (Job j : jobs) {
			if ( ! j.isDefunct)
				return false;
		}
		return true;
	}
	
	public void log(String str) {
		System.out.println(String.format("%5dm\t%s", getTime(), str));
	}

}

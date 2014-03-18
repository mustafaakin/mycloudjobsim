import java.util.HashMap;


public class Job {
	JobType type;
	int arrivalTime;
	VM assignedTo;	
	
	public Job(JobType type, int arrivalTime){
		this.type = type;
		this.arrivalTime = arrivalTime;
	}
}

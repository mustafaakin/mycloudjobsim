import java.io.PrintWriter;
import java.util.*;
import java.io.*;

public class JobGen {

	public static void main(String[] args) throws Exception {
		File f = new File("case1_jobs");
		PrintWriter pw = new PrintWriter(f);
		final int JOB_COUNT = 50;
		final int TIME = 10;
		final String[] JOBS = new String[] { "redis", "scientific", "web", "long"};
		final double[] DISTR = new double[] { 0.3, 0.6, 0.9, 1};
		
		Random r = new Random();
		String str = "";
		for (int i = 0; i < JOB_COUNT; i++) {
			int time = r.nextInt(TIME);
			double value = r.nextDouble();
			for(int j = 0; j < JOB_COUNT; j++){
				if ( value < DISTR[j]){
					str = JOBS[j] + "\t" + time;
					System.out.println(str);
					pw.println(str);
					break;
				}
			}
		}
		pw.print(str);
		pw.flush();
		pw.close();
	}
}

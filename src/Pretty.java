import java.io.*;
import java.util.*;

public class Pretty {

	public static void main(String[] args) throws Exception {
		Scanner scan = new Scanner(new File("/home/mustafa/runs/qwoeı"));

		int time = 0, count = 0;
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			if (line.contains("~")) {
				line = scan.nextLine();
				// System.out.println(line);
				int x = line.indexOf(":");
				line = line.substring(x + 1);
				x = line.indexOf("=");
				String str = line.substring(0, x);

				time = Integer.parseInt(str);

				x = line.indexOf(":");
				str = line.substring(x + 1);
				x = str.indexOf("=");
				str = str.substring(0, x);
				count = Integer.parseInt(str);
			} else {
				if (line.length() > 5)
					System.out.println(time + ";" + count + ";" + line.trim());
			}

		}

		scan.close();
	}

}

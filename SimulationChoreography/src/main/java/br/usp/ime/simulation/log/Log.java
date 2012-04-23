package br.usp.ime.simulation.log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class Log {
	public static BufferedWriter log;
	public static final String LOGNAME = "sim.log";
	
	public void open(){
		log = openLog("sim.log");
	}
	
    private static BufferedWriter openLog(final String filename) {
        FileWriter fstream = null;
        try {
            fstream = new FileWriter(filename);
        } catch (IOException e) {
            System.err.println("Error while opening " + filename);
            e.printStackTrace();
        }

        return new BufferedWriter(fstream);
    }
	
    public void record( final double start, final double end, String... extraCols) {
        String line = end + " " + (end - start);

        for (String column : extraCols) {
            line = line + " " + column;
        }

        writeln(line);
    }
    
    private static void writeln(String line) {
        try {
            synchronized (Log.class) {
                log.write(line + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error while writing to file");
            e.printStackTrace();
        }
    }
    
    public void close(){
    	try {
			log.close();
		} catch (IOException e) {
			System.err.println("Error while close file");
			e.printStackTrace();
		}
    }
}

package br.usp.ime.simulation.datatypes;

import org.simgrid.msg.Task;

public class ResponseTask extends Task {

	public int instanceId;
	public String serviceName;
	public String serviceMethod;
	
	public ResponseTask(double outputFileSize) {
		super("", 0, outputFileSize);
	}
}

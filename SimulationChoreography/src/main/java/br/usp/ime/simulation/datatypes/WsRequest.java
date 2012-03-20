package br.usp.ime.simulation.datatypes;

import org.simgrid.msg.Task;

public class WsRequest extends Task{

	public String senderMailbox;
	public String serviceName;
	public String serviceMethod;
	public double inputMessageSize;
	public boolean done = false;
	
	public WsRequest(String wsName, String wsMethod, double inputFileSize, String senderMailbox) {
		super(wsMethod, 1, inputFileSize);

		this.senderMailbox = senderMailbox;
		serviceMethod = wsMethod;
		serviceName = wsName;
		inputMessageSize = inputFileSize;
		
	}
	
}

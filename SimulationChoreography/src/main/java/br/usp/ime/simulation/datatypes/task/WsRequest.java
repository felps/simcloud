package br.usp.ime.simulation.datatypes.task;

import org.simgrid.msg.Task;

public class WsRequest extends Task{

	public String senderMailbox;
	public String serviceName;
	public String serviceMethod;
	public double inputMessageSize;
	public boolean done = false;
	public int instanceId = -1;
	public String destination;
	
	public WsRequest(String wsName, String wsMethod, double messageSize, String senderMailbox) {
		super(wsMethod, 1, messageSize);

		this.senderMailbox = senderMailbox;
		serviceMethod = wsMethod;
		serviceName = wsName;
		inputMessageSize = messageSize;
		
	}

	

	public String serializeWsRequest() {
		WsRequest wsRequest = this;
		String serviceName = "null";
		String senderMailbox = "null";
		String serviceMethod = "null";
		double inputMessageSize = 0;
		boolean done = false;
		int instanceId = -1;
		String destination = "null";
	
		serviceName = wsRequest.serviceName;
		senderMailbox = wsRequest.senderMailbox;
		serviceMethod = wsRequest.serviceMethod;
		inputMessageSize = wsRequest.inputMessageSize;
		done = wsRequest.done;
		instanceId = wsRequest.instanceId;
		destination = wsRequest.destination;
	
		String serializedWsRequest;
		serializedWsRequest = "WsRequest" + ";" + serviceName + ";"
				+ senderMailbox + ";" + serviceMethod + ";"
				+ inputMessageSize + ";" + done + ";" + instanceId + ";"
				+ destination;
		return serializedWsRequest;
	}

	public static WsRequest deserializeWsRequest(String serializedWsRequest)
			throws Exception {
	
		String[] args = serializedWsRequest.split(";");
		System.out.println(args.length);
		if (args.length == 8) {
			WsRequest wsRequest = deserializeWsRequest(args);
	
			return wsRequest;
		} else
			throw (new Exception());
	
	}
	
	private static WsRequest deserializeWsRequest(String... args) {
		String serviceName = args[1];
		String senderMailbox = args[2];
		String serviceMethod = args[3];
		double inputMessageSize = Double.parseDouble(args[4]);
		boolean done = Boolean.parseBoolean(args[5]);
		int instanceId = Integer.parseInt(args[6]);
		String destination = args[7];
	
		WsRequest wsRequest = new WsRequest(serviceName, serviceMethod,
				inputMessageSize, senderMailbox);
		wsRequest.done = done;
		wsRequest.instanceId = instanceId;
		wsRequest.destination = destination;
		return wsRequest;
	}
	
}

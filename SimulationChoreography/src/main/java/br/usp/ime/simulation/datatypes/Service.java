package br.usp.ime.simulation.datatypes;

import java.util.HashMap;

import org.simgrid.msg.Host;
import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;
import org.simgrid.msg.Process;
import org.simgrid.msg.Task;

import commTime.FinalizeTask;

public class Service extends Process {

	private HashMap<String, WsMethod> methods = new HashMap<String, WsMethod>();
	private Host host;
	private String wsName;

	public void main(String[] args) throws MsgException {
		if (args.length < 4) {
			Msg.info("Each service must have a name and at least one method");
			System.exit(1);
		}

		if (((args.length - 1) % 3) != 0) {
			Msg.info("Each method must have 4 parameters: a name, computing size and output file size");
			System.exit(1);
		}

		wsName = args[0];
		host = getHost();

		for (int i = 1; i < args.length; i += 3) {
			createMethod(args[i], args[i + 1], args[i + 2]);
		}

		Msg.info("Receiving on 'WS_" + wsName + "_at_" + host.getName()
				+ "' from Service '" + wsName + "'");

		while (true) {
			Task task = Task.receive("WS_" + wsName + "_at_" + host.getName());

			if (task instanceof WsRequest) {
				executeMethod((WsRequest) task);
			} 
			
			if (task instanceof FinalizeTask)
				break;
		}

		Msg.info("Received Finalize. Goodbye!");
	}

	public void executeMethod(WsRequest request) throws MsgException {
		WsMethod method = requestWsMethodTask(request.serviceMethod);

		method.execute();

		String responseMailbox = request.senderMailbox;
		double outputFileSize = method.getOutputFileSizeInBytes();

		ResponseTask response = new ResponseTask(outputFileSize);
		response.serviceName = wsName;

		response.send(responseMailbox);
	}

	public WsMethod requestWsMethodTask(String wsMethodName) {
		return methods.get(wsMethodName);
	}

	private void createMethod(String name, String computeSize,
			String outputFileSize) {
		WsMethod method = new WsMethod(name, Double.parseDouble(computeSize),
				0, Double.parseDouble(outputFileSize));

		methods.put(name, method);
	}
}

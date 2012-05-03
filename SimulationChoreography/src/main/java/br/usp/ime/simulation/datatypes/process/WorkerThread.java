package br.usp.ime.simulation.datatypes.process;

import java.util.HashMap;

import org.simgrid.msg.Host;
import org.simgrid.msg.HostFailureException;
import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;
import org.simgrid.msg.Process;
import org.simgrid.msg.Task;
import org.simgrid.msg.TimeoutException;
import org.simgrid.msg.TransferFailureException;

import br.usp.ime.simulation.datatypes.task.ResponseTask;
import br.usp.ime.simulation.datatypes.task.WsMethod;
import br.usp.ime.simulation.datatypes.task.WsRequest;
import br.usp.ime.simulation.experiments.control.ControlVariables;

import commTime.FinalizeTask;

public class WorkerThread extends Process {

	private HashMap<String, WsMethod> methods;
	private Host host;
	private String wsName;
	private String myMailbox;

	public WorkerThread(String[] mainArgs, Host host) {
		super(host, "WsRequestSender", mainArgs);
	}

	@Override
	public void main(String[] args) throws MsgException {
		wsName = args[0];
		myMailbox = args[1];
		methods = new HashMap<String, WsMethod>();
		// host = getHost();

		createWebMethods(args);

		if (ControlVariables.DEBUG || ControlVariables.PRINT_MAILBOXES)
			Msg.info("Receiving on '" + myMailbox + "'");

		workerThreadExecution();

	}

	private void createWebMethods(String[] args) {
		for (int i = 2; i < args.length; i += 3) {
			createMethod(args[i], args[i + 1], args[i + 2]);
		}
	}

	private void workerThreadExecution() throws TransferFailureException,
			HostFailureException, TimeoutException, MsgException {
		while (true) {
			double startTime = Msg.getClock();
			Task task = Task.receive(myMailbox);
			if (ControlVariables.DEBUG
					|| ControlVariables.PRINT_TASK_TRANSMISSION)
				Msg.info("Received task from " + task.getSource().getName());
			if (task instanceof WsRequest) {
				WsRequest wsRequest = (WsRequest) task;
				wsRequest.startTime = startTime;
				executeMethod(wsRequest);
			}

			if (task instanceof FinalizeTask)
				break;
		}
	}

	public void executeMethod(WsRequest request) throws MsgException {
		WsMethod method = requestWsMethodTask(request.serviceMethod);
		method.execute();
		if (ControlVariables.DEBUG || ControlVariables.PRINT_ALERTS)
			Msg.info("Task completed");
		String responseMailbox = request.senderMailbox;
		double outputFileSize = method.getOutputFileSizeInBytes();
		sendResponseTask(request, responseMailbox, outputFileSize);
	}

	private void sendResponseTask(WsRequest request, String responseMailbox,
			double outputFileSize) throws TransferFailureException,
			HostFailureException, TimeoutException {
		ResponseTask response = new ResponseTask(outputFileSize);
		response.serviceName = wsName;
		response.instanceId = request.instanceId;
		response.requestServed = request;
		response.serviceMethod = request.serviceMethod;
		if (ControlVariables.DEBUG || ControlVariables.PRINT_TASK_TRANSMISSION)
			Msg.info("Sending response from " + request.destination);
		response.send(responseMailbox);
	}

	public WsMethod requestWsMethodTask(String wsMethodName) {
		WsMethod method = methods.get(wsMethodName);
		WsMethod cloneMethod = new WsMethod(method.getName(),
				method.getComputeDuration(), 0,
				method.getOutputFileSizeInBytes());
		return cloneMethod;
	}

	private void createMethod(String name, String computeSize,
			String outputFileSize) {
		WsMethod method = new WsMethod(name, Double.parseDouble(computeSize),
				0, Double.parseDouble(outputFileSize));

		methods.put(name, method);
	}

}

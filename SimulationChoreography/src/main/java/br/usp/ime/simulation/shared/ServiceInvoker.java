package br.usp.ime.simulation.shared;

import java.io.IOException;

import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;
import org.simgrid.msg.Process;
import org.simgrid.msg.Task;

import br.usp.ime.simulation.datatypes.task.CoordinationMessage;
import br.usp.ime.simulation.datatypes.task.ResponseTask;
import br.usp.ime.simulation.datatypes.task.WsRequest;
import br.usp.ime.simulation.experiments.control.ControlVariables;
import br.usp.ime.simulation.log.Log;

public abstract class ServiceInvoker extends Process {

	protected void invokeWsMethod(WsRequest request, String sender,
			String destination) throws MsgException {

		request.destination = destination;
		request.senderMailbox = sender;

		try {
			sendTask(request, sender, destination, WsRequest.toString(request));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	protected void sendCoordinationMessage(CoordinationMessage message,
			String sender, String destination) {

		try {
			sendTask(message, sender, destination, CoordinationMessage.toString(message));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void sendTask(Task request, String sender, String destination,
			String serializedObject) {

		String serialization;

		serialization = serializedObject;
		String[] args = new String[2];
		args[0] = serialization;
		args[1] = destination;
		new WsRequestSender(args, getHost());

	}

	public abstract void notifyCompletion(WsRequest request,
			ResponseTask response) throws MsgException;

	public static Task getResponse(String sender) {
		Task response = null;

		response = tryUntilAMessageIsGot(sender, response);

		if (response instanceof ResponseTask) {
			if (ControlVariables.DEBUG || ControlVariables.PRINT_ALERTS
					|| ControlVariables.PRINT_TASK_TRANSMISSION)
				Msg.info("Task " + ((ResponseTask) response).serviceMethod
						+ " for orchestration "
						+ ((ResponseTask) response).instanceId
						+ " was succesfully executed by "
						+ ((ResponseTask) response).requestServed.destination);
		} else {
			Msg.info("Something went wrong...");
			System.exit(1);
		}
		return response;
	}

	private static Task tryUntilAMessageIsGot(String sender, Task response) {
		try {
			if (ControlVariables.DEBUG || ControlVariables.PRINT_MAILBOXES)
				Msg.info(" Trying to get response at mailbox: " + sender);
			response = Task.receive(sender);

		} catch (MsgException e) {
			if (ControlVariables.DEBUG || ControlVariables.PRINT_MAILBOXES)
				Msg.info(" Could not get message! ");
			response = tryUntilAMessageIsGot(sender, response);
		}
		return response;
	}
}

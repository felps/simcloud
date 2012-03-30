package br.usp.ime.simulation.shared;

import java.io.IOException;

import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;
import org.simgrid.msg.Process;
import org.simgrid.msg.Task;

import br.usp.ime.simulation.datatypes.task.ResponseTask;
import br.usp.ime.simulation.datatypes.task.WsRequest;

public abstract class ServiceInvoker extends Process {

	protected void invokeWsMethod(WsRequest request, String sender,
			String destination) throws MsgException {
		request.destination = destination;
		request.senderMailbox = sender;

		String serialization;
		try {

			serialization = WsRequest.toString(request);
			String[] args = new String[2];
			args[0] = serialization;
			args[1] = destination;
			new WsRequestSender(args, getHost());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return;

	}


	public abstract void notifyCompletion(WsRequest request,
			ResponseTask response) throws MsgException;

	public static Task getResponse(String sender) {
		Task response = null;

		response = tryUntilAMessageIsGot(sender, response);

		Msg.info(" BLE " + response.getClass().getName());

		if (response instanceof ResponseTask) {

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
			Msg.info(" Trying to get response at mailbox: " + sender);
			response = Task.receive(sender, 5);

		} catch (MsgException e) {
			Msg.info(" Could not get message! ");
			response =  tryUntilAMessageIsGot(sender, response);
		}
		return response;
	}
}

package br.usp.ime.simulation.shared;

import org.simgrid.msg.*;
import org.simgrid.msg.Process;

import br.usp.ime.simulation.datatypes.task.ResponseTask;
import br.usp.ime.simulation.datatypes.task.WsRequest;

public abstract class ServiceInvoker extends Process {

	protected void invokeWsMethod(WsRequest request, String sender,
			String destination) throws MsgException {

		new TaskSender(request, destination, sender, getHost());

		return;

	}

	public abstract void notifyCompletion(WsRequest request,
			ResponseTask response) throws MsgException;

	public static Task getResponse(String sender) {
		Task response = null;

			response = tryUntilAMessageIsGot(sender, response);

			Msg.info(" BLE "+response.getClass().getName());
			
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
			Msg.info(" Could not get message! " );
			return tryUntilAMessageIsGot(sender, response);
		}
		return response;
	}
}

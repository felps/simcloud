package br.usp.ime.simulation.orchestration;

import org.simgrid.msg.*;
import org.simgrid.msg.Process;

import br.usp.ime.simulation.datatypes.task.ResponseTask;
import br.usp.ime.simulation.datatypes.task.WsRequest;

public abstract class ServiceInvoker extends Process{

	protected boolean invokeWsMethod(WsRequest request, String destination, String sender)
			throws MsgException {
			
				Msg.info("Created Task for " + request.serviceMethod
						+ "with compute duration of " + request.getComputeDuration()
						+ " and message size of " + request.inputMessageSize + " at "
						+ destination);
			
				request.senderMailbox = sender;
				request.send(destination);
			
				Task response = Task.receive(sender);
			
				if (response instanceof ResponseTask) {
					Msg.info("Task for " + request.serviceMethod
							+ " was succesfully executed by " + destination);
			
					return true;
				} else {
					Msg.info("Something went wrong...");
					System.exit(1);
					return false;
				}
			}
}

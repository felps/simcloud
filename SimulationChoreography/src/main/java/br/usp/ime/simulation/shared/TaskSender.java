package br.usp.ime.simulation.shared;

import org.simgrid.msg.HostFailureException;
import org.simgrid.msg.Msg;
import org.simgrid.msg.Task;
import org.simgrid.msg.TimeoutException;
import org.simgrid.msg.TransferFailureException;

import commTime.FinalizeTask;

import br.usp.ime.simulation.datatypes.task.WsRequest;

public class TaskSender implements Runnable {
	private Task request;
	private String destination;
	private String sender;

	public TaskSender(Task request, String destinationMailbox,
			String senderMailbox) {
		this.request = request;
		this.destination = destinationMailbox;
		this.sender = senderMailbox;
	}

	public void run() {
		if (request instanceof WsRequest) {
			Msg.info("Created Task for " + ((WsRequest) request).serviceMethod
					+ " with compute duration of "
					+ request.getComputeDuration() + " and message size of "
					+ ((WsRequest) request).inputMessageSize + " at "
					+ destination);

			((WsRequest) request).senderMailbox = sender;
		} else if (request instanceof FinalizeTask)
			Msg.info(" Terminating service at " + destination);
		else
			Msg.info(" Sending generic task to " + destination);

		try {
			request.send(destination);
		} catch (TransferFailureException e) {
			e.printStackTrace();
		} catch (HostFailureException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}

}

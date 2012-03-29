package br.usp.ime.simulation.shared;

import org.simgrid.msg.Host;
import org.simgrid.msg.HostFailureException;
import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;
import org.simgrid.msg.Task;
import org.simgrid.msg.TimeoutException;
import org.simgrid.msg.TransferFailureException;

import br.usp.ime.simulation.datatypes.task.WsRequest;

import commTime.FinalizeTask;

public class TaskSender extends org.simgrid.msg.Process{
	private Task request;
	private String destination;
	private String sender;

	public TaskSender(Task request, String destinationMailbox,
			String senderMailbox, Host host) {
		super(host,"TaskSender");
		this.request = request;
		this.destination = destinationMailbox;
		this.sender = senderMailbox;
		this.start();
	}

	@Override
	public void main(String[] args) throws MsgException {
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

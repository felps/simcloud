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

public class WsRequestSender extends org.simgrid.msg.Process{
	private Task request;
	private String destination;
	private String sender;
	private String[] strings = new String[1];

	public WsRequestSender(String[] serializedRequest, String destinationMailbox,
			String senderMailbox, Host host) {
		super(host,"TaskSender", serializedRequest);
	}

	@Override
	public void main(String[] args) throws MsgException {
		
		Task request = null;
		try {
			request = TaskSerializer.deserializeTask(args[0]);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

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

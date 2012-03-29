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
	
	public WsRequest request;
	
	public WsRequestSender(String[] args, Host host) {
		super(host,"WsRequestSender", args);
	}

	@Override
	public void main(String[] args) throws MsgException {
		String destination = args[1];
		Task request = null;
		try {
			Msg.info("Deserializing request: "+args[0]);
			request = WsRequest.fromString(args[0]);
			Msg.info("DONE!");
			Msg.info(request.getClass().getName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (request instanceof WsRequest) {
			Msg.info("Created Task for " + ((WsRequest) request).serviceMethod
					+ " with compute duration of "
					+ request.getComputeDuration() + " and message size of "
					+ ((WsRequest) request).inputMessageSize + " at "
					+ destination);
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

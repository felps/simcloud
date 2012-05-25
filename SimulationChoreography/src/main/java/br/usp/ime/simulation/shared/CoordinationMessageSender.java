package br.usp.ime.simulation.shared;

import org.simgrid.msg.Host;
import org.simgrid.msg.HostFailureException;
import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;
import org.simgrid.msg.Task;
import org.simgrid.msg.TimeoutException;
import org.simgrid.msg.TransferFailureException;

import br.usp.ime.simulation.datatypes.task.CoordinationMessage;
import br.usp.ime.simulation.datatypes.task.WsRequest;
import br.usp.ime.simulation.experiments.control.ControlVariables;

import commTime.FinalizeTask;

public class CoordinationMessageSender extends org.simgrid.msg.Process {

	public WsRequest request;

	public CoordinationMessageSender(String[] args, Host host) {
		super(host, "CoordinationMessageSender", args);
	}

	@Override
	public void main(String[] args) throws MsgException {
		String destination = args[1];
		Task request = null;
		try {
			request = CoordinationMessage.fromString(args[0]);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (request instanceof CoordinationMessage) {
			CoordinationMessage coordinationMessage = (CoordinationMessage) request;
			CoordinationMessage clonedCoordinationMessage = cloneCoordinationMessage(
					coordinationMessage, destination);
			if (ControlVariables.DEBUG
					|| ControlVariables.PRINT_TASK_TRANSMISSION)
				Msg.info("Created CoordinationTask for " + destination
						+ " with compute duration of "
						+ clonedCoordinationMessage.getComputeDuration()
						+ " and message size of "
						+ clonedCoordinationMessage.inputMessageSize + " at " + destination);
			clonedCoordinationMessage.startTime = Msg.getClock();
			clonedCoordinationMessage.send(destination);
		} else {
			if (request instanceof FinalizeTask)
				if (ControlVariables.DEBUG || ControlVariables.PRINT_ALERTS)
					Msg.info(" Terminating service at " + destination);
				else if (ControlVariables.DEBUG
						|| ControlVariables.PRINT_ALERTS)
					Msg.info(" Sending generic task to " + destination);
			request.send(destination);
		}
	}

	private CoordinationMessage cloneCoordinationMessage(
			CoordinationMessage coordinationMessage, String destination) {
		CoordinationMessage clonedMessage = new CoordinationMessage(coordinationMessage.inputMessageSize);
		clonedMessage.instanceId = coordinationMessage.instanceId;
		return clonedMessage;
	}

}

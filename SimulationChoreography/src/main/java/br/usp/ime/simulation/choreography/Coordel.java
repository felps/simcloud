package br.usp.ime.simulation.choreography;

import br.usp.ime.simulation.datatypes.task.CoordinationMessage;
import br.usp.ime.simulation.datatypes.task.ResponseTask;
import br.usp.ime.simulation.datatypes.task.WsRequest;
import br.usp.ime.simulation.experiments.control.ControlVariables;
import br.usp.ime.simulation.log.Log;
import br.usp.ime.simulation.shared.ServiceInvoker;

import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;
import org.simgrid.msg.Task;

public class Coordel extends ServiceInvoker {

	private String inputMailbox;
	private String wsMailbox;
	private String nextCoordelMailbox;
	private String invokedMethod;
	private double outgoingRequestSize;

	@Override
	public void notifyCompletion(WsRequest request, ResponseTask response)
			throws MsgException {

	}

	@Override
	public void main(String[] args) throws MsgException {
		if (args.length != 4) {
			Msg.info("The Coordel must receive 4 inputs: Input Mailbox, "
					+ "Service Mailbox, Invoked Method,  "
					+ "Next Coordel's Mailbox and Outgoing Request Size");
			System.exit(1);
		}

		parseArgs(args);

		enact();
	}

	private void parseArgs(String[] args) {

		inputMailbox = args[1];
		wsMailbox = args[2];
		invokedMethod = args[3];
		nextCoordelMailbox = args[4];
	}

	private void enact() {
		Task incomingMessage;
		WsRequest outgoingRequest;
		while (true) {
			try {
				incomingMessage = receiveMsg(inputMailbox);
				if (incomingMessage instanceof CoordinationMessage) {
					CoordinationMessage incomingRequest = (CoordinationMessage) incomingMessage;
					WsRequest wsRequest = createWsRequest();
					invokeWsMethod(wsRequest, inputMailbox, wsMailbox);
				}
				if (incomingMessage instanceof ResponseTask) {
					outgoingRequest = createMessageToNextCoordel();
					outgoingRequest.send(nextCoordelMailbox);
				}
			} catch (MsgException e) {
				e.printStackTrace();
			}
		}
	}

	private WsRequest createWsRequest() {
		return null;
	}

	private WsRequest createMessageToNextCoordel() {
		WsRequest nextRequest = new WsRequest("anything", invokedMethod,
				outgoingRequestSize, inputMailbox);
		return nextRequest;
	}

	private WsRequest receiveMsg(String inputMailbox2) {

		return null;
	}

}

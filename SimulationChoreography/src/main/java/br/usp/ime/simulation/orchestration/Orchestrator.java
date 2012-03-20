package br.usp.ime.simulation.orchestration;

import java.util.ArrayList;
import java.util.HashMap;

import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;
import org.simgrid.msg.Process;
import org.simgrid.msg.Task;

import br.usp.ime.simulation.datatypes.Orchestration;
import br.usp.ime.simulation.datatypes.ResponseTask;
import br.usp.ime.simulation.datatypes.WsRequest;

import commTime.FinalizeTask;

public class Orchestrator extends Process {

	private String deploymentInfo = "";
	private String bpelFile = "";
	private Orchestration orch = new Orchestration();

	public void main(String[] args) throws MsgException {
		if (args.length != 4) {
			Msg.info("The orchestrator must recieve 4 inputs: Request quantity, request per sec rate, orchestration descriptor and ammount of orchestrated services");
			System.exit(1);
		}

		orch.createServiceList(deploymentInfo);
		Msg.info("Starting Orchestration...");
		orchestrate();
	}

	private void orchestrate() throws MsgException {
		orch.parseBpelFile(bpelFile);

		while (orch.getReadyTasks().size() > 0) {

			for (WsRequest request : orch.getReadyTasks()) {
				String chosenMailbox = getWsMailbox(request);
				if (invokeWsMethod(request, chosenMailbox, "Orchestrator"))
					orch.notifyTaskConclusion(request);
			}
		}

		finalizeOrchestration();

	}

	private boolean invokeWsMethod(WsRequest request, String destination,
			String sender) throws MsgException {

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

	private String getWsMailbox(WsRequest request) {
		String chosenMailbox = " ABSOLUTELY NO ONE (This is an ERROR!)";

		if (orch.getServiceMethodsMailboxes().get(request.serviceMethod) != null) {
			chosenMailbox = orch.getServiceMethodsMailboxes().get(
					request.serviceMethod);
		} else
			Msg.info(request.serviceMethod);
		return chosenMailbox;
	}

	private void finalizeOrchestration() throws MsgException {
		ArrayList<String> removedMailboxes = new ArrayList<String>();
		Msg.info("Telling everyone the orchestration is done...");
		for (String mailbox : orch.getServiceMethodsMailboxes().values()) {
			if (!removedMailboxes.contains(mailbox)) {
				FinalizeTask task = new FinalizeTask();
				task.send(mailbox);
				removedMailboxes.add(mailbox);
			}
		}

		Msg.info("Orchestration is done. Bye!");

	}

}

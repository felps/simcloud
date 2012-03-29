package br.usp.ime.simulation.orchestration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;

import br.usp.ime.simulation.datatypes.task.ResponseTask;
import br.usp.ime.simulation.datatypes.task.WsRequest;
import br.usp.ime.simulation.shared.ServiceInvoker;

import commTime.FinalizeTask;

public class Orchestrator extends ServiceInvoker {

	private String deploymentInfo = "";
	private String bpelFile = "";
	private String myMailbox = "Orchestrator";
	private HashMap<Integer, Orchestration> orchestrationInstances = new HashMap<Integer, Orchestration>();
	private ArrayList<String> mailboxes = new ArrayList<String>();
	
	public void main(final String[] args) throws MsgException {
		if (args.length != 4) {
			Msg.info("The orchestrator must receive 4 inputs: Request quantity, request per sec rate, orchestration descriptor and ammount of orchestrated services");
			System.exit(1);
		}

		final int ammountOfInstances = Integer.parseInt(args[0]);
		Msg.info("Starting " + ammountOfInstances + " Orchestrations...");
		orchestrate(ammountOfInstances);
	}

	private void orchestrate(final int ammountOfInstances) throws MsgException {

		for (int i = 0; i < ammountOfInstances; i++) {
			Orchestration orquestration = new Orchestration(i);
			orquestration.createServiceList(deploymentInfo);
			orquestration.parseBpelFile(bpelFile);
			for (Set<String> listEndpoint : orquestration
					.getServiceMethodsMailboxEndpoints().values())
				mailboxes.addAll(listEndpoint);
			orchestrationInstances.put(i, orquestration);
		}

		sendInitialTasks();

		Msg.info(" BLA " );
		while (!orchestrationInstances.isEmpty()) {
			ResponseTask response = (ResponseTask) getResponse(myMailbox);

			Orchestration orch = orchestrationInstances
					.get(response.instanceId);
			orch.notifyTaskConclusion(response.requestServed);

			if (orch.getReadyTasks().isEmpty()) {
				Msg.info("No more tasks for orchestration " + orch.getId());
				Msg.info("" + orchestrationInstances.size());
				orchestrationInstances.remove(orch.getId());
			} else {
				submitReadyTasks(orch);
			}
		}

		finalizeOrchestration();

	}

	private void sendInitialTasks() throws MsgException {
		for (Orchestration orch : orchestrationInstances.values()) {
			submitReadyTasks(orch);
		}
		Msg.info("Done sending tasks");
	}

	private void submitReadyTasks(Orchestration orch) throws MsgException {
		List<WsRequest> readyTasks = orch.getReadyTasks();
		Msg.info(readyTasks.size() + " tasks are ready!");
		for (WsRequest request : readyTasks) {
			String chosenMailbox = orch.getWsMailbox(request);
			Msg.info(" Sending request for " + request.serviceMethod + " not to " + request.destination + " but to " + chosenMailbox);
			invokeWsMethod(request, myMailbox, chosenMailbox);
		}
	}

	private void finalizeOrchestration() throws MsgException {
		ArrayList<String> removedMailboxes = new ArrayList<String>();
		Msg.info("Telling everyone the orchestration is done...");
		for (String mailbox : mailboxes) {
			if (!removedMailboxes.contains(mailbox)) {
				FinalizeTask task = new FinalizeTask();
				Msg.info(" Sending termination signal to "+mailbox);
				task.send(mailbox);
				removedMailboxes.add(mailbox);
			}
		}

		Msg.info("Orchestration is done. Bye!");

	}

	@Override
	public void notifyCompletion(WsRequest request, ResponseTask response)
			throws MsgException {
		Orchestration orch = orchestrationInstances.get(response.instanceId);
		orch.notifyTaskConclusion(request);
		submitReadyTasks(orch);
	}

}

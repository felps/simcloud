package br.usp.ime.simulation.orchestration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;


import br.usp.ime.simulation.datatypes.task.WsRequest;

import commTime.FinalizeTask;

public class Orchestrator extends ServiceInvoker {

	private String deploymentInfo = "";
	private String bpelFile = "";
	private HashMap<Integer, Orchestration> orchestrationInstances = new HashMap<Integer, Orchestration>();
	
	private ArrayList<String> mailboxes = new ArrayList<String>();
	
	private String myMailbox = "Orchestrator";
	private ArrayList<WsRequest> tasksToBeSubmitted = new ArrayList<WsRequest>();

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
			mailboxes.addAll(orquestration.getServiceMethodsMailboxEndpoints().values());
			orchestrationInstances.put(i, orquestration);
		}

		Msg.info("Teste1");
		sendInitialTasks();
		Msg.info("Teste2");

		while (!orchestrationInstances.isEmpty()) {
			List<Integer> completedInstances = new ArrayList<Integer>();
			for (Orchestration orch : orchestrationInstances.values()) {
				if (orch.getReadyTasks().isEmpty()) {
					Msg.info("No more tasks for orchestration " + orch.getId());
					completedInstances.add(orch.getId());
					Msg.info("" + orchestrationInstances.size());
				} else {
					submitReadyTasks(orch);
				}
			}
			
			for (int instanceId : completedInstances){
				orchestrationInstances.remove(instanceId);
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
			invokeWsMethod(request, myMailbox, chosenMailbox);
			orch.notifyTaskConclusion(request);
		}
	}



	private void finalizeOrchestration() throws MsgException {
		ArrayList<String> removedMailboxes = new ArrayList<String>();
		Msg.info("Telling everyone the orchestration is done...");
		for (String mailbox : mailboxes) {
			if (!removedMailboxes.contains(mailbox)) {
				FinalizeTask task = new FinalizeTask();
				task.send(mailbox);
				removedMailboxes.add(mailbox);
			}
		}

		Msg.info("Orchestration is done. Bye!");

	}

}

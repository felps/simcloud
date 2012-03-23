package br.usp.ime.simulation.orchestration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashMap;

import org.simgrid.msg.HostFailureException;
import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;
import org.simgrid.msg.Process;
import org.simgrid.msg.Task;
import org.simgrid.msg.TimeoutException;
import org.simgrid.msg.TransferFailureException;

import br.usp.ime.simulation.datatypes.Orchestration;
import br.usp.ime.simulation.datatypes.ResponseTask;
import br.usp.ime.simulation.datatypes.WsRequest;

import commTime.FinalizeTask;

public class Orchestrator extends ServiceInvoker {

	private String deploymentInfo = "";
	private String bpelFile = "";
	private HashMap<Integer, Orchestration> orchestrationInstances = new HashMap<Integer, Orchestration>();
	private ArrayList<String> mailboxes = new ArrayList<String>();
	private String myMailbox = "Orchestrator";
	private ArrayList<WsRequest> tasksToBeSubmitted = new ArrayList<WsRequest>();

	public void main(String[] args) throws MsgException {
		if (args.length != 4) {
			Msg.info("The orchestrator must recieve 4 inputs: Request quantity, request per sec rate, orchestration descriptor and ammount of orchestrated services");
			System.exit(1);
		}

		int ammountOfInstances = Integer.parseInt(args[0]);
		Msg.info("Starting " + ammountOfInstances + " Orchestrations...");
		orchestrate(ammountOfInstances);
	}

	private void orchestrate(int ammountOfInstances) throws MsgException {

		
		for (int i = 0; i < ammountOfInstances; i++) {
			Orchestration instance = new Orchestration(i);
			instance.createServiceList(deploymentInfo);
			instance.parseBpelFile(bpelFile);
			mailboxes.addAll(instance.getServiceMethodsMailboxes().values());
			orchestrationInstances.put(i, instance);
		}

		Msg.info("Teste1");
		sendInitialTasks();
		Msg.info("Teste2");

		while (!orchestrationInstances.isEmpty()) {
			ArrayList<Integer> completedInstances = new ArrayList<Integer>();
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
		Msg.info(orch.getReadyTasks().size() + " tasks are ready!");
		for (WsRequest request : orch.getReadyTasks()) {
			String chosenMailbox = getWsMailbox(request, orch);
			invokeWsMethod(request, chosenMailbox, myMailbox);
			orch.notifyTaskConclusion(request);
		}
	}

	private String getWsMailbox(WsRequest request, Orchestration orch) {
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

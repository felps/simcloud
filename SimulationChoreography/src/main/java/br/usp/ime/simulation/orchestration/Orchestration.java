package br.usp.ime.simulation.orchestration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;

import br.usp.ime.simulation.datatypes.task.WsRequest;
import commTime.FinalizeTask;

public class Orchestration {
	private ManagerRequest managerRequest;
	private ManagerServiceList managerServiceList;

	private final int id;

	public Orchestration(int id) {
		managerServiceList = new ManagerServiceList();
		managerRequest = new ManagerRequest();
		this.id = id;
	}
	
	public int getId(){
		return id;
	}

	public void addRequest(final WsRequest request){
		managerRequest.addRequest(request);
	}
	
	public void addDependency(final WsRequest request,final WsRequest dependency){
		managerRequest.addDependency(request,dependency);
	}
	
	public void parseBpelFile(final String fileName) {
		Msg.info("Creating requests");
		WsRequest ws1 = new WsRequest("supermarket", "getPrice", 30000, null);
		ws1.instanceId = this.id;

		addRequest(ws1);
		/*
		WsRequest ws2 = new WsRequest("supermarket", "sellProduct", 50000, null);
		ws2.instanceId = this.id;
		
		addRequest(ws2);
		addDependency(ws2, ws1);
		*/
		Msg.info("Requests created");
	}

	public List<WsRequest> getReadyTasks() {
		return managerRequest.getReadyTasks();
	}

	public void notifyTaskConclusion(final WsRequest request) {
		managerRequest.notifyTaskConclusion(request);
	}
	
	public void createServiceList(final String deploymentInfo) {
		managerServiceList.createServiceList(deploymentInfo);
	}
	
	public void finalizeOrchestration() throws MsgException {
		List<String> removedMailboxes = new ArrayList<String>();
		Msg.info("Telling everyone the orchestration is done...");
		Map<String, Set<String>> serviceMethodsMailboxEndpoints = managerServiceList.getServiceMethodsMailboxEndpoints();
		
		for(Set<String> listEndpoint : serviceMethodsMailboxEndpoints.values())
			for (String mailbox : listEndpoint) {
				if (!removedMailboxes.contains(mailbox)) {
					FinalizeTask task = new FinalizeTask();
					task.send(mailbox);
					removedMailboxes.add(mailbox);
				}
			}
		Msg.info("Orchestration is done. Bye!");
	}
	
	public Map<String, Set<String>> getServiceMethodsMailboxEndpoints() {
		return managerServiceList.getServiceMethodsMailboxEndpoints();
	}
	
	public String getWsMailbox(WsRequest request) {
		String chosenMailbox = " ABSOLUTELY NO ONE (This is an ERROR!)";
		Map<String, Set<String>>  serviceMethodsMailboxEndpoints = getServiceMethodsMailboxEndpoints();
		
		if (serviceMethodsMailboxEndpoints.get(request.serviceMethod) != null) {
			chosenMailbox = serviceMethodsMailboxEndpoints.get(request.serviceMethod).iterator().next();
		} else
			Msg.info(request.serviceMethod);
		return chosenMailbox;
	}

}

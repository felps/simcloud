package br.usp.ime.simulation.datatypes;

import java.util.ArrayList;
import java.util.HashMap;

import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;
import org.simgrid.msg.Task;

import commTime.FinalizeTask;

public class Orchestration {
	private ArrayList<WsRequest> requests;

	private HashMap<WsRequest, ArrayList<WsRequest>> dependsOn;
	private HashMap<WsRequest, ArrayList<WsRequest>> isDependencyOf;
	private String deploymentInfo;
	private int id;
	private HashMap<String, String> serviceMethodsMailboxEndpoints = new HashMap<String, String>();

	public Orchestration(int id) {
		dependsOn = new HashMap<WsRequest, ArrayList<WsRequest>>();
		isDependencyOf = new HashMap<WsRequest, ArrayList<WsRequest>>();
		requests = new ArrayList<WsRequest>();
		this.id = id;
	}
	
	public int getId(){
		return id;
	}

	public HashMap<String, String> getServiceMethodsMailboxes() {
		return serviceMethodsMailboxEndpoints;
	}

	public void addRequest(WsRequest request){
		requests.add(request);
		dependsOn.put(request, (new ArrayList<WsRequest>()));
		isDependencyOf.put(request, (new ArrayList<WsRequest>()));
	}
	
	public void addDependency(WsRequest request, WsRequest dependency){
		if(!requests.contains(request)){
			Msg.info("Error adding dependency to non-existing request");
			return;
		}
		
		dependsOn.get(request).add(dependency);
		isDependencyOf.get(dependency).add(request);
	}
	
	public void parseBpelFile(String fileName) {
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

	public ArrayList<WsRequest> getReadyTasks() {
		ArrayList<WsRequest> readyTasks = new ArrayList<WsRequest>();

		for (WsRequest request : requests) {
			if (dependsOn.get(request).isEmpty() && !request.done) {
				readyTasks.add(request);
			}
		}

		return readyTasks;
	}

	public void notifyTaskConclusion(WsRequest request) {
		request.done = true;
		for (WsRequest dependency : isDependencyOf.get(request)) {
			removeThisRequestsDependencyOn(request, dependency);
		}
	}

	private void removeThisRequestsDependencyOn(WsRequest request,
			WsRequest dependency) {
		dependsOn.get(dependency).remove(request);
	}
	
	//
	public void createServiceList(String deploymentInfo) {
		Msg.info("Creating Service List");
		try {
			parseDeploymentFile(deploymentInfo);
		} catch (MsgException e) {
			e.printStackTrace();
		}
		this.deploymentInfo = deploymentInfo;
	}

	private void parseDeploymentFile(String deploymentFile) throws MsgException {
		addNewServiceMethod("supermarket", "getPrice", "Bellemarre");
		addNewServiceMethod("supermarket", "sellProduct", "Bellemarre");
	}

	private void addNewServiceMethod(String wsName, String wsMethodName,
			String hostname) {

		serviceMethodsMailboxEndpoints.put(wsMethodName, "WS_" + wsName
				+ "_at_" + hostname);

	}
	
	public void finalizeOrchestration() throws MsgException {
		ArrayList<String> removedMailboxes = new ArrayList<String>();
		Msg.info("Telling everyone the orchestration is done...");
		for (String mailbox : getServiceMethodsMailboxes().values()) {
			if (!removedMailboxes.contains(mailbox)) {
				FinalizeTask task = new FinalizeTask();
				task.send(mailbox);
				removedMailboxes.add(mailbox);
			}
		}

		Msg.info("Orchestration is done. Bye!");

	}
}

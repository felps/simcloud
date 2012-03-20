package br.usp.ime.simulation.datatypes;

import java.util.ArrayList;
import java.util.HashMap;

import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;
import org.simgrid.msg.Task;

public class Orchestration {
	private ArrayList<WsRequest> requests;
	private HashMap<WsRequest, ArrayList<WsRequest>> dependsOn;
	private HashMap<WsRequest, ArrayList<WsRequest>> dependsUpon;
	private String deploymentInfo;
	private HashMap<String, String> serviceMethodsMailboxEndpoints = new HashMap<String, String>();

	public Orchestration() {
		dependsOn = new HashMap<WsRequest, ArrayList<WsRequest>>();
		dependsUpon = new HashMap<WsRequest, ArrayList<WsRequest>>();
		requests = new ArrayList<WsRequest>();
	}

	public HashMap<String, String> getServiceMethodsMailboxes() {
		return serviceMethodsMailboxEndpoints;
	}

	public void addRequest(WsRequest request){
		requests.add(request);
		dependsOn.put(request, (new ArrayList<WsRequest>()));
		dependsUpon.put(request, (new ArrayList<WsRequest>()));
	}
	
	public void addDependency(WsRequest request, WsRequest dependency){
		if(!requests.contains(request)){
			Msg.info("Error adding dependency to non-existing request");
			return;
		}
		
		dependsOn.get(request).add(dependency);
		dependsUpon.get(dependency).add(request);
	}
	
	public void parseBpelFile(String fileName) {
		Msg.info("Creating requests");
		WsRequest ws1 = new WsRequest("supermarket", "getPrice", 30000, null);

		requests.add(ws1);
		dependsUpon.put(ws1, new ArrayList<WsRequest>());
		dependsOn.put(ws1, new ArrayList<WsRequest>());

		WsRequest ws2 = new WsRequest("supermarket", "sellProduct", 50000, null);

		requests.add(ws2);
		ArrayList<WsRequest> deps = (new ArrayList<WsRequest>());
		deps.add(ws1);
		dependsUpon.put(ws2, deps);
		dependsOn.put(ws2, new ArrayList<WsRequest>());

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
		for (WsRequest dependency : dependsUpon.get(request)) {
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
}

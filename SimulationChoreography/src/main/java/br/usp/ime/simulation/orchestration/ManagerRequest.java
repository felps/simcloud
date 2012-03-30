package br.usp.ime.simulation.orchestration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simgrid.msg.Msg;

import br.usp.ime.simulation.datatypes.task.WsRequest;

public class ManagerRequest {
	private List<WsRequest> requests;
	private Map<WsRequest, ArrayList<WsRequest>> dependsOn;
	private Map<WsRequest, ArrayList<WsRequest>> isDependencyOf;

	public ManagerRequest() {
		dependsOn = new HashMap<WsRequest, ArrayList<WsRequest>>();
		isDependencyOf = new HashMap<WsRequest, ArrayList<WsRequest>>();
		requests = new ArrayList<WsRequest>();
	}

	public void addRequest(WsRequest request) {
		requests.add(request);
		dependsOn.put(request, new ArrayList<WsRequest>());
		isDependencyOf.put(request, new ArrayList<WsRequest>());
	}

	public void addDependency(WsRequest request, WsRequest dependency) {
		if (!requests.contains(request)) {
			Msg.info("Error adding dependency to non-existing request");
			return;
		}

		dependsOn.get(request).add(dependency);
		isDependencyOf.get(dependency).add(request);
	}

	public List<WsRequest> getReadyTasks() {
		final List<WsRequest> readyTasks = new ArrayList<WsRequest>();

		for (WsRequest request : requests) {
			if (isReadyTask(request)) {
				readyTasks.add(request);
			}
		}

		return readyTasks;
	}

	private boolean isReadyTask(WsRequest wsRequest) {
		if (dependsOn.get(wsRequest) != null
				&& dependsOn.get(wsRequest).isEmpty() && !wsRequest.done)
			return true;

		return false;
	}

	private void doneWsRequest(WsRequest request,Set<WsRequest> setRequest){
		for(WsRequest i: setRequest){
			if(i.equals(request)){
				i.done = true;
				break;
			}
		}
	}
	public void notifyTaskConclusion(WsRequest request) {
			if (isDependencyOf.get(request) != null) {
			doneWsRequest(request,isDependencyOf.keySet());
			for (WsRequest dependency : isDependencyOf.get(request)) {
				removeThisRequestsDependencyOn(request, dependency);
			}
		}
		
		else{
			Msg.info("Could not find inputted request (" + request.toString() + ")");
			for(WsRequest req : isDependencyOf.keySet()){
				Msg.info("key: " + req.toString());
			}
		}
			
	}

	private void removeThisRequestsDependencyOn(WsRequest request,
			WsRequest dependency) {
		dependsOn.get(dependency).remove(request);
	}
}

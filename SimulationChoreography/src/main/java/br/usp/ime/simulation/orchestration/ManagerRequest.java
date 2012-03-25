package br.usp.ime.simulation.orchestration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simgrid.msg.Msg;

import br.usp.ime.simulation.datatypes.task.WsRequest;

public class ManagerRequest {
	private final List<WsRequest> requests;
	private final Map<WsRequest, ArrayList<WsRequest>> dependsOn;
	private final Map<WsRequest, ArrayList<WsRequest>> isDependencyOf;
	
	public ManagerRequest(){
		dependsOn = new HashMap<WsRequest, ArrayList<WsRequest>>();
		isDependencyOf = new HashMap<WsRequest, ArrayList<WsRequest>>();
		requests = new ArrayList<WsRequest>();
	}

	public void addRequest(final WsRequest request){
		requests.add(request);
		dependsOn.put(request, new ArrayList<WsRequest>());
		isDependencyOf.put(request, new ArrayList<WsRequest>());
	}
	
	public void addDependency(final WsRequest request,final WsRequest dependency){
		if(!requests.contains(request)){
			Msg.info("Error adding dependency to non-existing request");
			return;
		}
		
		dependsOn.get(request).add(dependency);
		isDependencyOf.get(dependency).add(request);
	}
	
	public List<WsRequest> getReadyTasks() {
		final List<WsRequest> readyTasks = new ArrayList<WsRequest>();
		
		for (WsRequest request : requests) {
			if (dependsOn.get(request).isEmpty() && !request.done) {
				readyTasks.add(request);
			}
		}

		return readyTasks;
	}
	
	public void notifyTaskConclusion(final WsRequest request) {
		request.done = true;
		for (WsRequest dependency : isDependencyOf.get(request)) {
			removeThisRequestsDependencyOn(request, dependency);
		}
	}
	
	private void removeThisRequestsDependencyOn(final WsRequest request,
			WsRequest dependency) {
		dependsOn.get(dependency).remove(request);
	}
}

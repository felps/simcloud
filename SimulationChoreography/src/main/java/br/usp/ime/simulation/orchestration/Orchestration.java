package br.usp.ime.simulation.orchestration;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simgrid.msg.Msg;

import br.usp.ime.simulation.datatypes.task.WsRequest;

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
		request.instanceId = this.id;
		request.senderMailbox = "Orchestrator";
		managerRequest.addRequest(request);
	}
	
	public void addDependency(final WsRequest request,final WsRequest dependency){
		managerRequest.addDependency(request,dependency);
	}
	
	
	
	public void parseBpelFile(final String fileName) {
		makePurchaseRequest();
	}

	public void parseBpelFileSmallOrchestration() {
		WsRequest ws1 = new WsRequest("supermarket", "getPrice", 30000, null);
		ws1.instanceId = this.id;
		ws1.senderMailbox = "Orchestrator";

		addRequest(ws1);
		
		WsRequest ws2 = new WsRequest("supermarket", "purchase", 50000, null);
		ws2.instanceId = this.id;
		ws2.senderMailbox = "Orchestrator";

		addRequest(ws2);
		addDependency(ws2, ws1);
	}
	
	private void getDeliveryStatusRequest() {
		WsRequest ws1 = new WsRequest("shipper", "getDeliveryStatus", 668.0, null);
		
		addRequest(ws1);
	}
	
	private void getLowestPriceRequest() {
		WsRequest ws0 = new WsRequest("registry","getList",299,null);
		addRequest(ws0);

		WsRequest ws1 = new WsRequest("supermarket", "getPrice", 336, null);
		addRequest(ws1);
		addDependency(ws1,ws0);
		WsRequest ws2 = new WsRequest("supermarket", "getPrice", 336, null);
		addRequest(ws2);
		addDependency(ws2,ws1);

		WsRequest ws3 = new WsRequest("supermarket", "getPrice", 336, null);
		addRequest(ws3);
		addDependency(ws3,ws2);
		//TODO task to take lowest prices
	}
	
	private void makePurchaseRequest(){
		WsRequest ws1 = new WsRequest("supermarket","purchase",420,null);
		WsRequest ws2 = new WsRequest("shipper","setDelivery",654,null);
		WsRequest ws3 = new WsRequest("supermarket","purchase",420,null);
		WsRequest ws4 = new WsRequest("shipper","setDelivery",654,null);
		WsRequest ws5 = new WsRequest("supermarket","purchase",420,null);
		WsRequest ws6 = new WsRequest("shipper","setDelivery",654,null);
		addRequest(ws1);
		addRequest(ws2);
		addRequest(ws3);
		addRequest(ws4);
		addRequest(ws5);
		addRequest(ws6);
		addDependency(ws2,ws1);
		addDependency(ws3,ws2);
		addDependency(ws4,ws3);
		addDependency(ws5,ws4);
		addDependency(ws6,ws5);
	}
	
	public List<WsRequest> getReadyTasks() {
		return managerRequest.getReadyTasks();
	}

	public void notifyTaskConclusion(WsRequest request) {
		managerRequest.notifyTaskConclusion(request);
	}
	
	public void createServiceList(final String deploymentInfo) {
		managerServiceList.createServiceList(deploymentInfo);
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
			Msg.info(" Could not find a provider for method " +request.serviceMethod);
		request.destination= chosenMailbox;
		return chosenMailbox;
	}

}

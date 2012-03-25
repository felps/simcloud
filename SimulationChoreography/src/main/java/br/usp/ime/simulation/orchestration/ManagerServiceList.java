package br.usp.ime.simulation.orchestration;

import java.util.HashMap;
import java.util.Map;

import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;

public class ManagerServiceList {
	private final Map<String, String> serviceMethodsMailboxEndpoints;
	private String deploymentInfo;

	public String getDeploymentInfo() {
		return deploymentInfo;
	}

	public void setDeploymentInfo(String deploymentInfo) {
		this.deploymentInfo = deploymentInfo;
	}
	
	public Map<String, String> getServiceMethodsMailboxEndpoints() {
		return serviceMethodsMailboxEndpoints;
	}

	public ManagerServiceList(){
		serviceMethodsMailboxEndpoints = new HashMap<String, String>();
	}
	
	public void createServiceList(final String deploymentInfo) {
		Msg.info("Creating Service List");
		try {
			parseDeploymentFile(deploymentInfo);
		} catch (MsgException e) {
			e.printStackTrace();
		}
		this.setDeploymentInfo(deploymentInfo);
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

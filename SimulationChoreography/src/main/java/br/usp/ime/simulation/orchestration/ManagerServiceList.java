package br.usp.ime.simulation.orchestration;

import java.util.HashMap;
import java.util.Map;

import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;

public class ManagerServiceList {
	private final Map<String, String> serviceMethodsMailboxEndpoints;
	private String deploymentInfo;
	
	public ManagerServiceList(){
		serviceMethodsMailboxEndpoints = new HashMap<String, String>();
	}
	
	public String getDeploymentInfo() {
		return deploymentInfo;
	}

	public void setDeploymentInfo(final String deploymentInfo) {
		this.deploymentInfo = deploymentInfo;
	}
	
	public Map<String, String> getServiceMethodsMailboxEndpoints() {
		return serviceMethodsMailboxEndpoints;
	}
	
	public void createServiceList(final String deploymentInfo) {
		Msg.info("Creating Service List");
		try {
			parseDeploymentFile(deploymentInfo);
		} catch (MsgException e) {
			e.printStackTrace();
		}
		setDeploymentInfo(deploymentInfo);
	}

	private void parseDeploymentFile(final String deploymentFile) throws MsgException {
		addNewServiceMethod("supermarket", "getPrice", "Bellemarre");
		addNewServiceMethod("supermarket", "sellProduct", "Bellemarre");
	}

	private void addNewServiceMethod(final String wsName,final String wsMethodName,
			final String hostname) {

		serviceMethodsMailboxEndpoints.put(wsMethodName, "WS_" + wsName
				+ "_at_" + hostname);

	}
}

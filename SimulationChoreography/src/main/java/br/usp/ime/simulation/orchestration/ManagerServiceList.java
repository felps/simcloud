package br.usp.ime.simulation.orchestration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;

import br.usp.ime.simulation.experiments.control.ControlVariables;

public class ManagerServiceList {
	private final Map<String, Set<String>> serviceMethodsMailboxEndpoints;
	private String deploymentInfo;

	public ManagerServiceList() {
		serviceMethodsMailboxEndpoints = new HashMap<String, Set<String>>();
	}

	public String getDeploymentInfo() {
		return deploymentInfo;
	}

	public void setDeploymentInfo(final String deploymentInfo) {
		this.deploymentInfo = deploymentInfo;
	}

	public Map<String, Set<String>> getServiceMethodsMailboxEndpoints() {
		return serviceMethodsMailboxEndpoints;
	}

	public void createServiceList(final String deploymentInfo) {
		if (ControlVariables.DEBUG || ControlVariables.PRINT_ALERTS)
			Msg.info("Creating Service List");
		try {
			parseDeploymentFileOpenCirrus(deploymentInfo);
		} catch (MsgException e) {
			e.printStackTrace();
		}
		setDeploymentInfo(deploymentInfo);
	}

	private void parseDeploymentFile(final String deploymentFile)
			throws MsgException {
		// example to small orchestration
		// addNewServiceMethod("supermarket", "getPrice", "Bellemarre");
		// addNewServiceMethod("supermarket", "sellProduct", "Bellemarre"
		addNewServiceMethod("supermarket", "getPrice", "Bellemarre");
		addNewServiceMethod("supermarket", "purchase", "Bellemarre");
		// addNewServiceMethod("supermarket", "getPrice", "supermarket2");
		// addNewServiceMethod("supermarket", "purchase", "supermarket2");
		// addNewServiceMethod("supermarket", "getPrice", "supermarket3");
		// addNewServiceMethod("supermarket", "purchase", "supermarket3");
		addNewServiceMethod("shipper", "getDeliveryStatus", "iRMX");
		addNewServiceMethod("shipper", "setDelivery", "iRMX");
		addNewServiceMethod("registry", "getList", "Revoada");
		// addNewServiceMethod("shipper", "setDelivery", "shipper");
		addNewServiceMethod("registry", "getList", "Revoada");

	}

	private void parseDeploymentFileOpenCirrus(final String deploymentFile)
			throws MsgException {
		// example to small orchestration
		addNewServiceMethod("supermarket", "getPrice", "Bellemarre");
		addNewServiceMethod("supermarket", "purchase", "Bellemarre");
		// addNewServiceMethod("supermarket", "getPrice", "Bellemarre");
		// addNewServiceMethod("supermarket", "purchase", "Bellemarre");
		// addNewServiceMethod("supermarket", "getPrice", "supermarket2");
		// addNewServiceMethod("supermarket", "purchase", "supermarket2");
		// addNewServiceMethod("supermarket", "getPrice", "supermarket3");
		// addNewServiceMethod("supermarket", "purchase", "supermarket3");
		addNewServiceMethod("shipper", "getDeliveryStatus", "iRMX");
		addNewServiceMethod("shipper", "setDelivery", "iRMX");
		addNewServiceMethod("registry", "getList", "Revoada");
		// addNewServiceMethod("shipper", "setDelivery", "shipper");
		// addNewServiceMethod("registry", "getList", "Bellemarre");
	}

	private void addNewServiceMethod(final String wsName,
			final String wsMethodName, final String hostname) {
		if (serviceMethodsMailboxEndpoints.get(wsMethodName) == null) {
			serviceMethodsMailboxEndpoints.put(wsMethodName,
					new HashSet<String>());
		}
		serviceMethodsMailboxEndpoints.get(wsMethodName).add(
				"WS_" + wsName + "_at_" + hostname);

	}
}

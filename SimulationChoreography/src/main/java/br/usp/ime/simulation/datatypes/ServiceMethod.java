//package br.usp.ime.simulation.datatypes;
//
//import org.simgrid.msg.Host;
//import org.simgrid.msg.HostFailureException;
//import org.simgrid.msg.Msg;
//import org.simgrid.msg.MsgException;
//import org.simgrid.msg.Process;
//import org.simgrid.msg.Task;
//import org.simgrid.msg.TimeoutException;
//import org.simgrid.msg.TransferFailureException;
//
//import commTime.FinalizeTask;
//
//public class ServiceMethod extends Process {
//
//	public int computingSizeInMI;
//	public int inputFileSizeInBytes;
//	public int outputFileSizeInBytes;
//	public String wsMethodName;
//	public String wsName;
//	public Host wsHost;
//
//	@Override
//	public void main(String[] args) throws MsgException {
//		wsName = args[0];
//		wsMethodName = args[1];
//		wsHost = getHost();
//
//		Msg.info("Receiving on 'WS_" + wsName + "_Method_" + wsMethodName
//				+ "_at_" + wsHost.getName() + "' from Service '" + wsName + "'");
//
//		while (true) {
//			Task task = Task.receive("WS_" + wsName + "_Method_" + wsMethodName
//					+ "_at_" + wsHost.getName());
//
//			if (task instanceof FinalizeTask)
//				break;
//
//			task.execute();
//
//			ResponseTask response = new ResponseTask();
//			response.serviceMethod = wsMethodName;
//			response.serviceName = wsName;
//			
//			response.send("Orchestrator");
//		}
//
//		Msg.info("Received Finalize. Goodbye!");
//	}
//}

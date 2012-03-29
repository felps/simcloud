package br.usp.ime.simulation.shared;

import org.simgrid.msg.Task;

import commTime.FinalizeTask;

import br.usp.ime.simulation.datatypes.task.WsRequest;

public class TaskSerializer {

	public static String serializeTask(Task task, double messageSize) {

		if (task instanceof WsRequest) {

			WsRequest wsRequest = (WsRequest) task;

			return wsRequest.serializeWsRequest();

		} else

		if (task instanceof FinalizeTask) {
			return ((FinalizeTask) task).serializeFinalizeTask();
		}
		
		else
			return serializeGenericTask(task, messageSize);
	}

	private static String serializeGenericTask(Task task, double messageSize) {
		String serializedTask = task.getName() + "\t;\t"
				+ task.getComputeDuration() + "\t;\t" + messageSize;
		return serializedTask;
	}

	public static Task deserializeTask(String serializedTask) throws Exception {

		System.out.println(serializedTask);
		String[] args = serializedTask.split("\t;\t");

		if (args[0].equals("WsRequest"))
			return WsRequest.deserializeWsRequest(serializedTask);

		else if (args[0].equals("FinalizeTask"))
			return FinalizeTask.deserializeFinalizeTask(serializedTask);

		return deserializeGenericTask(args);

	}

	private static Task deserializeGenericTask(String[] args) {
		String name = args[0];
		System.out.println(args[0]);
		System.out.println(args[1]);
		System.out.println(args[2]);
		double computeDuration = Double.parseDouble(args[1]);
		double messageSize = Double.parseDouble(args[2]);
		return (new Task(name, computeDuration, messageSize));
	}
}

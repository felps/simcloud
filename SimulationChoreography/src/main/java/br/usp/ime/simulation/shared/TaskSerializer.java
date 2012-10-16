package br.usp.ime.simulation.shared;

import java.io.IOException;

//import org.junit.runner.Request;
import org.simgrid.msg.Task;

import commTime.FinalizeTask;

import br.usp.ime.simulation.datatypes.task.WsRequest;

public class TaskSerializer {

	public static String serializeTask(Task task, double messageSize) {

		if (task instanceof WsRequest) {

			try {
				WsRequest wsRequest = (WsRequest) task;
				return WsRequest.toString(wsRequest);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

		} else

		if (task instanceof FinalizeTask) {
			return ((FinalizeTask) task).serializeFinalizeTask();
		}

		else
			return serializeGenericTask(task, messageSize);

	}

	private static String serializeGenericTask(Task task, double messageSize) {
		String serializedTask = task.getName() + ";"
				+ task.getComputeDuration() + ";" + messageSize;
		return serializedTask;
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

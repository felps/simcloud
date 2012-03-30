package br.usp.ime.simulation.shared;

import java.util.ArrayList;

import org.simgrid.msg.HostFailureException;
import org.simgrid.msg.Msg;
import org.simgrid.msg.Task;
import org.simgrid.msg.TimeoutException;
import org.simgrid.msg.TransferFailureException;

public class TaskReceiver implements Runnable {

	private ArrayList<Task> incomingTasks;
	private String myMailbox;

	public TaskReceiver(String mailbox) {
		super();
		myMailbox = mailbox;
	}
	public Task getIncomingTask() {
		Task returnedTask = null;
		try {
			returnedTask = incomingTasks.get(0);
			incomingTasks.remove(returnedTask);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnedTask;
	}

	@Override
	public void run() {

		while (true) {
			try {
				Task newTask = Task.receive(myMailbox, 5);
				incomingTasks.add(newTask);
			} catch (TransferFailureException e) {
				Msg.info("TransferFailurexecption in getting Task from mailbox "+ myMailbox);
				e.printStackTrace();
			} catch (HostFailureException e) {
				Msg.info("HostFailurexecption in getting Task from mailbox "+ myMailbox);
				e.printStackTrace();
			} catch (TimeoutException e) {
			}

		}
	}

}

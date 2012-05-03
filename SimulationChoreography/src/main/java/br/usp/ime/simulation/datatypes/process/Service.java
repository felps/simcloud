package br.usp.ime.simulation.datatypes.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.simgrid.msg.Host;
import org.simgrid.msg.HostFailureException;
import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;
import org.simgrid.msg.Process;
import org.simgrid.msg.Task;
import org.simgrid.msg.TimeoutException;
import org.simgrid.msg.TransferFailureException;

import br.usp.ime.simulation.datatypes.task.ResponseTask;
import br.usp.ime.simulation.datatypes.task.WsMethod;
import br.usp.ime.simulation.datatypes.task.WsRequest;
import br.usp.ime.simulation.experiments.control.ControlVariables;

import commTime.FinalizeTask;

public class Service extends Process {

	private HashMap<String, WsMethod> methods = new HashMap<String, WsMethod>();
	private Host host;
	private String wsName;
	private List<String> workerMailboxes;
	private String[] mainArgs;
	private String myMailbox;
	private boolean ended = false;

	public void main(String[] args) throws MsgException {
		if (args.length < 5) {
			Msg.info("Each service must have a name, an ammount of parallel threads and at least one method");
			System.exit(1);
		}

		if (((args.length - 2) % 3) != 0) {
			Msg.info("Each method must have 4 parameters: a name, computing size and output file size");
			System.exit(1);
		}

		wsName = args[0];
		mainArgs = args.clone();
		myMailbox = "WS_" + wsName + "_at_" + getHost().getName();
		workerMailboxes = new ArrayList<String>();

		createWorkerThreads(Integer.parseInt(args[1]));

		if (ControlVariables.DEBUG || ControlVariables.PRINT_MAILBOXES)
			Msg.info("Receiving on '" + myMailbox + "' from Service '" + wsName
					+ "'");

		redirectTasks();

	}

	private void redirectTasks() {
		int i = 0;
		Task currentTask;
		while (true) {
			try {
				double startTime = Msg.getClock();
				currentTask = receiveNewTask();
				String mailbox = getNextMailbox(i);
				if (ControlVariables.DEBUG || ControlVariables.PRINT_MAILBOXES)
					Msg.info("Task received at service " + wsName
							+ ". Redirecting to " + mailbox);
				processTask(mailbox, currentTask);
			} catch (TransferFailureException e) {
				e.printStackTrace();
			} catch (HostFailureException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
			i++;
			if (ended)
				break;
		}
	}

	private void processTask(String mailbox, Task currentTask)
			throws TransferFailureException, HostFailureException,
			TimeoutException {
		if (currentTask instanceof WsRequest) {
			redirectTask(currentTask, mailbox);
		}
		if (currentTask instanceof FinalizeTask) {
			if (ControlVariables.DEBUG || ControlVariables.PRINT_ALERTS)
				Msg.info("Received Finalize. So this is WS_" + wsName + "_at_"
						+ getHost().getName() + " saying Goodbye!");

			finalizeWorkers();
			ended = true;
		}
	}

	private String getNextMailbox(int lastUsedMailboxIndex) {
		int j;
		if (lastUsedMailboxIndex >= workerMailboxes.size())
			j = 0;
		else
			j = lastUsedMailboxIndex;

		String mailbox = workerMailboxes.get(j);

		return mailbox;
	}

	private Task receiveNewTask() throws TransferFailureException,
			HostFailureException, TimeoutException {
		Task currentTask = Task.receive(myMailbox);
		if (ControlVariables.DEBUG || ControlVariables.PRINT_TASK_TRANSMISSION)
			Msg.info("Received task from " + currentTask.getSource().getName());
		return currentTask;
	}

	private void redirectTask(Task task, String mailbox)
			throws TransferFailureException, HostFailureException,
			TimeoutException {
		if (ControlVariables.DEBUG || ControlVariables.PRINT_TASK_TRANSMISSION)
			Msg.info("Redirecting to worker thread at " + mailbox);
		task.send(mailbox);

	}

	private void createWorkerThreads(int workerAmmount) {
		for (int workerId = 0; workerId < workerAmmount; workerId++) {
			createsingleWorker(workerId);
		}
		if (ControlVariables.DEBUG || ControlVariables.PRINT_ALERTS)
			Msg.info("Done creating worker threads.");
	}

	private void createsingleWorker(int workerThreadId) {
		String[] arguments = mainArgs.clone();
		arguments[1] = myMailbox + "_WORKER_THREAD_" + workerThreadId;
		workerMailboxes.add(arguments[1]);
		new WorkerThread(arguments, getHost());
	}

	private void finalizeWorkers() {
		for (String mailbox : workerMailboxes) {
			try {
				(new FinalizeTask()).send(mailbox);
			} catch (Exception e) {
				Msg.info("Could not finalize worker thread at " + mailbox);
			}
		}
	}
}

/*
 * Contains all the native methods related to Process, Host and Task.
 *
 * Copyright 2006,2007,2010 The SimGrid Team           
 * All right reserved. 
 *
 * This program is free software; you can redistribute 
 * it and/or modify it under the terms of the license 
 *(GNU LGPL) which comes with this package. 
 */

package org.simgrid.msg;

import org.simgrid.msg.Process;

/* FIXME: split into internal classes of Msg, Task, Host etc. */

/**
 *  Contains all the native methods related to Process, Host and Task.
 */
final class MsgNative {

	/******************************************************************
	 * The natively implemented methods connected to the MSG Process  *
	 ******************************************************************/
	/**
	 * The natively implemented method to create an MSG process.
	 *
	 * @param process The java process object to bind with the MSG native process.
	 * @param host    A valid (binded) host where create the process.
	 *
	 * @see  Process constructors.
	 */
	final static native
	void processCreate(Process process, Host host);

	/**
	 * The natively implemented method to kill all the process of the simulation.
	 *
	 * @param resetPID        Should we reset the PID numbers. A negative number means no reset
	 *                        and a positive number will be used to set the PID of the next newly
	 *                        created process.
	 *
	 * @return                The function returns the PID of the next created process.
	 */
	final static native int processKillAll(int resetPID);

	/**
	 * The natively implemented method to suspend an MSG process.
	 *
	 * @param process        The valid (binded with a native process) java process to suspend.
	 *
	 * @see                 Process.pause()
	 */
	final static native void processSuspend(Process process);

	/**
	 * The natively implemented method to kill a MSG process.
	 *
	 * @param process        The valid (binded with a native process) java process to kill.
	 *
	 * @see                 Process.kill()
	 */
	final static native void processKill(Process process);

	/**
	 * The natively implemented method to resume a suspended MSG process.
	 *
	 * @param process        The valid (binded with a native process) java process to resume.
	 *
	 *
	 * @see                 Process.restart()
	 */
	final static native void processResume(Process process);

	/**
	 * The natively implemented method to test if MSG process is suspended.
	 *
	 * @param process        The valid (binded with a native process) java process to test.
	 *
	 * @return                If the process is suspended the method retuns true. Otherwise the
	 *                        method returns false.
	 *
	 * @see                 Process.isSuspended()
	 */
	final static native boolean processIsSuspended(Process process);

	/**
	 * The natively implemented method to get the host of a MSG process.
	 *
	 * @param process        The valid (binded with a native process) java process to get the host.
	 *
	 * @return                The method returns the host where the process is running.
	 *
	 * @exception            HostNotFoundException if the SimGrid native code failed (initialization error?).
	 *
	 * @see                 Process.getHost()
	 */
	final static native Host processGetHost(Process process);

	/**
	 * The natively implemented method to get a MSG process from his PID.
	 *
	 * @param PID            The PID of the process to get.
	 *
	 * @return                The process with the specified PID.
	 *
	 * @see                 Process.getFromPID()
	 */
	final static native Process processFromPID(int PID) ;

	/**
	 * The natively implemented method to get the PID of a MSG process.
	 *
	 * @param process        The valid (binded with a native process) java process to get the PID.
	 *
	 * @return                The PID of the specified process.
	 *
	 * @see                 Process.getPID()
	 */
	final static native int processGetPID(Process process);

	/**
	 * The natively implemented method to get the PPID of a MSG process.
	 *
	 * @param process        The valid (binded with a native process) java process to get the PID.
	 *
	 * @return                The PPID of the specified process.
	 *
	 * @see                 Process.getPPID()
	 */
	final static native int processGetPPID(Process process);

	/**
	 * The natively implemented method to get the current running process.
	 *
	 * @return             The current process.
	 *
	 * @see                Process.currentProcess()
	 */
	final static native Process processSelf();

	/**
	 * The natively implemented method to migrate a process from his currnet host to a new host.
	 *
	 * @param process        The (valid) process to migrate.
	 * @param host            A (valid) host where move the process.
	 *
	 *
	 * @see                Process.migrate()
	 * @see                Host.getByName()
	 */
	final static native void processMigrate(Process process, Host host) ;

	/**
	 * The natively implemented native to request the current process to sleep 
	 * until time seconds have elapsed.
	 *
	 * @param seconds        The time the current process must sleep.
	 *
	 * @exception            HostFailureException if the SimGrid native code failed.
	 *
	 * @see                 Process.waitFor()
	 */
	final static native void processWaitFor(double seconds) throws HostFailureException;

	/**
	 * The natively implemented native method to exit a process.
	 *
	 * @see                Process.exit()
	 */
	final static native void processExit(Process process);


	/******************************************************************
	 * The natively implemented methods connected to the MSG host     *
	 ******************************************************************/

	/**
	 * The natively implemented method to get an host from his name.
	 *
	 * @param name            The name of the host to get.
	 *
	 * @return                The host having the specified name.
	 *
	 * @exception            HostNotFoundException if there is no such host
	 *                       
	 *
	 * @see                Host.getByName()
	 */
	final static native Host hostGetByName(String name) throws HostNotFoundException;

	/**
	 * The natively implemented method to get the name of an MSG host.
	 *
	 * @param host            The host (valid) to get the name.
	 *
	 * @return                The name of the specified host.
	 *
	 * @see                Host.getName()
	 */
	final static native String hostGetName(Host host);

	/**
	 * The natively implemented method to get the number of hosts of the simulation.
	 *
	 * @return                The number of hosts of the simulation.
	 *
	 * @see                Host.getNumber()
	 */
	final static native int hostGetCount();

	/**
	 * The natively implemented method to get the host of the current runing process.
	 *
	 * @return                The host of the current running process.
	 *
	 * @see                Host.currentHost()
	 */
	final static native Host hostSelf();

	/**
	 * The natively implemented method to get the speed of a MSG host.
	 *
	 * @param host            The host to get the host.
	 *
	 * @return                The speed of the specified host.
	 *
	 * @see                Host.getSpeed()
	 */

	final static native double hostGetSpeed(Host host);

	/**
	 * The natively implemented native method to test if an host is avail.
	 *
	 * @param host            The host to test.
	 *
	 * @return                If the host is avail the method returns true. 
	 *                        Otherwise the method returns false.
	 *
	 * @see                Host.isAvail()
	 */
	final static native boolean hostIsAvail(Host host);

	/**
	 * The natively implemented native method to get all the hosts of the simulation.
	 *
	 * @return                A array which contains all the hosts of simulation.
	 */

	final static native Host[] allHosts();

	/**
	 * The natively implemented native method to get the number of running tasks on a host.
	 *
	 * @param                The host concerned by the operation.
	 *
	 * @return                The number of running tasks.
	 */
	final static native int hostGetLoad(Host host);

	/******************************************************************
	 * The natively implemented methods connected to the MSG task     *
	 ******************************************************************/

	/**
	 * The natively implemented method to create a MSG task.
	 *
	 * @param name            The name of th task.
	 * @param computeDuration    A value of the processing amount (in flop) needed 
	 *                        to process the task. If 0, then it cannot be executed
	 *                        with the execute() method. This value has to be >= 0.
	 * @param messageSize        A value of amount of data (in bytes) needed to transfert 
	 *                        this task. If 0, then it cannot be transfered this task. 
	 *                        If 0, then it cannot be transfered with the get() and put() 
	 *                        methods. This value has to be >= 0.
	 * @param task            The java task object to bind with the native task to create.
	 *
	 * @exception             IllegalArgumentException if compute duration <0 or message size <0
	 *
	 * @see                    Task.create()
	 */
	final static native void taskCreate(Task task, String name,
			double computeDuration,
			double messageSize)
	throws IllegalArgumentException;

	/**
	 * The natively implemented method to get the sender of a task.
	 *
	 * @param    task            The task (valid) to get the sender.
	 *
	 * @return                The sender of the task.
	 *
	 * @see                    Task.getSender()
	 */
	final static native Process taskGetSender(Task task);

	/**
	 * The natively implementd method to get the source of a task.
	 *
	 * @param task            The task to get the source.
	 *
	 * @return                The source of the task.
	 *
	 *
	 * @see                    Task.getSource()
	 */
	final static native Host taskGetSource(Task task);

	/**
	 * The natively implemented method to get the name of the task.
	 *
	 * @param task            The task to get the name.
	 *
	 * @return                 The name of the specified task.
	 *
	 * @see                    Task.getName()
	 */
	final static native String taskGetName(Task task);

	/**
	 * The natively implemented method to cancel a task.
	 *
	 * @param task            The task to cancel.
	 *
	 *
	 * @see                    Task.cancel().
	 */
	final static native void taskCancel(Task task);

	/**
	 * The natively implemented method to create a MSG parallel task.
	 *
	 * @param name                The name of the parallel task.
	 * @param hosts                The list of hosts implied by the parallel task.
	 * @param computeDurations    The total number of operations that have to be performed
	 *                            on the hosts.
	 * @param messageSizes        An array of doubles
	 *
	 * @see                        ParallelTask.create()
	 */
	final static native void parallelTaskCreate(Task pTask, String name,
			Host[]hosts,
			double[]computeDurations,
			double[]messageSizes)
	throws NullPointerException, IllegalArgumentException;

	/**
	 * The natively implemented method to get the computing amount of the task.
	 *
	 * @param task            The task to get the computing amount.
	 *
	 * @return                The computing amount of the specified task.
	 *
	 * @see                    Task.getComputeDuration()
	 */
	final static native double taskGetComputeDuration(Task task);

	/**
	 * The natively implemented method to get the remaining computation
	 *
	 * @param task            The task to get the remaining computation.
	 *
	 * @return                The remaining computation of the specified task.
	 *
	 * @see                    Task.getRemainingDuration()
	 */
	final static native double taskGetRemainingDuration(Task task);

	/**
	 * The natively implemented method to set the priority of a task.
	 *
	 * @param task            The task to set the priority
	 *
	 * @param priority        The new priority of the specified task.
	 *
	 * @see                    Task.setPriority()
	 */
	final static native void taskSetPriority(Task task, double priority);

	/**
	 * The natively implemented method to destroy a MSG task.
	 *
	 * @param                    The task to destroy.
	 *
	 *
	 * @see                    Task.destroy()
	 */
	final static native void taskDestroy(Task task) ;

	/**
	 * The natively implemented method to execute a MSG task.
	 *
	 * @param task            The task to execute.
	 *
	 * @exception             HostFailureException,TaskCancelledException on error in the C world
	 *
	 * @see                    Task.execute()
	 */
	final static native void taskExecute(Task task) throws HostFailureException,TaskCancelledException;

	/* ****************************************************************
	 * Communication methods thru mailboxes                           *
	 **************************************************************** */

	final static native void taskSend(String alias, Task task, double timeout) throws TransferFailureException,HostFailureException,TimeoutException;
	final static native Task taskReceive(String alias, double timeout, Host host) throws TransferFailureException,HostFailureException,TimeoutException;
	final static native int taskListenFrom(String alias);
	final static native boolean taskListen(String alias);
	final static native int taskListenFromHost(String alias, Host host);

	/* ***************************************************************
	 * Task sending methods                                          *
	 *************************************************************** */

	/**
	 * The natively implemented method to send a task in a mailbox associated with an alias,  with a bounded transmition
	 * rate.
	 * 
	 * @param alias            The alias of the mailbox.
	 * @param task            The task to put.
	 * @param max_rate        The bounded transmition rate.
	 *
	 * @exception             NativeException on error in the C world
	 */ 
	final static native void taskSendBounded(String alias, Task task, double maxrate) throws TransferFailureException,HostFailureException,TimeoutException;

}

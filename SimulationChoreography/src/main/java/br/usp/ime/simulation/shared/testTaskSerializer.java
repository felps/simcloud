package br.usp.ime.simulation.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.simgrid.msg.Task;

import commTime.FinalizeTask;

import br.usp.ime.simulation.datatypes.task.WsRequest;

public class testTaskSerializer {

	Task task;
	WsRequest wsRequest;
	FinalizeTask finalizeTask;

	@Before
	public void setUp() throws Exception {
		
		task = mock(Task.class);
		when(task.getName()).thenReturn("test");
		when(task.getComputeDuration()).thenReturn(965.0);
		
		wsRequest = mock(WsRequest.class);
		
		wsRequest.serviceName = "service";
		wsRequest.serviceMethod = "method";
		wsRequest.senderMailbox = "sender";
		wsRequest.inputMessageSize = 1345;
		wsRequest.destination = "destination";
		wsRequest.done = false;
		wsRequest.instanceId = 9870;

		when(wsRequest.serializeWsRequest()).thenReturn(
				"WsRequest;service;sender;method;1345;false;9870;destination");
		
		finalizeTask = mock(FinalizeTask.class);
		when(finalizeTask.serializeFinalizeTask()).thenReturn("FinalizeTask");
		
	}

	@Test
	public void testSerializeGenericTask() {
		String serializedTask = TaskSerializer.serializeTask(task, 1202);

		assertEquals("test\t965.0\t1202.0", serializedTask);
	}

	@Test
	public void testDeserializeGenericTask() throws Exception {
		assertEquals(task, TaskSerializer.deserializeTask("test\t965\t1202"));
	}

	@Test
	public void testSerializeAndDeserializeGenericTask() {
		fail("Not yet implemented");
	}

	@Test
	public void testSerializeWsRequest() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeserializeWsRequest() {
		fail("Not yet implemented");
	}

	@Test
	public void testSerializeAndDeserializeWsRequest() {
	}

	@Test
	public void testSerializeFinalizeTask() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeserializeFinalizeTask() {
		fail("Not yet implemented");
	}

	@Test
	public void testSerializeAndDeserializeFinalizeTask() {
		fail("Not yet implemented");
	}
}

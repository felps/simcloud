//package br.usp.ime.simulation.datatypes;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.fail;
//
//import java.util.ArrayList;
//
//import org.junit.Before;
//import org.junit.Test;
//
//public class OrchestrationTest {
//
//	private Orchestration orchestration;
//	private WsRequest requestGetPrice;
//	private WsRequest requestSellProduct;
//
//	@Before
//	public void setUp() {
//		this.orchestration = new Orchestration();
//		requestGetPrice = new WsRequest();
//		requestGetPrice.id = 1;
//		requestGetPrice.serviceName = "supermarket";
//		requestGetPrice.serviceMethod = "getPrice";
//		requestGetPrice.computeDuration = 4000;
//		requestGetPrice.inputMessageSize = 300;
//
//		requestSellProduct = new WsRequest();
//		requestSellProduct.id = 2;
//		requestSellProduct.serviceName = "supermarket";
//		requestSellProduct.serviceMethod = "sellProduct";
//		requestSellProduct.computeDuration = 4000;
//		requestSellProduct.inputMessageSize = 300;
//
//	}
//
//	// @Test
//	// public void testParseBpelFile() {
//	//
//	// }
//
//	@Test
//	public void testGetReadyTasks() {
//		orchestration.addRequest(requestGetPrice);
//		orchestration.addRequest(requestSellProduct);
//
//		orchestration.addDependency(requestSellProduct, requestGetPrice);
//
//		ArrayList<WsRequest> expectedValue = new ArrayList<WsRequest>();
//		expectedValue.add(requestGetPrice);
//
//		assertEquals(expectedValue, orchestration.getReadyTasks());
//	}
//
//	@Test
//	public void testNotifyTaskConclusion() {
//		orchestration.addRequest(requestGetPrice);
//		orchestration.addRequest(requestSellProduct);
//
//		orchestration.addDependency(requestSellProduct, requestGetPrice);
//
//		ArrayList<WsRequest> expectedValue = new ArrayList<WsRequest>();
//		expectedValue.add(requestGetPrice);
//
//		assertEquals(expectedValue, orchestration.getReadyTasks());
//
//		orchestration.notifyTaskConclusion(requestGetPrice);
//
//		expectedValue = new ArrayList<WsRequest>();
//		expectedValue.add(requestSellProduct);
//
//		assertEquals(expectedValue, orchestration.getReadyTasks());
//
//		orchestration.notifyTaskConclusion(requestSellProduct);
//
//		expectedValue = new ArrayList<WsRequest>();
//
//		assertEquals(expectedValue, orchestration.getReadyTasks());
//
//	}
//}

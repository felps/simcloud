package br.usp.ime.simulation.datatypes.task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.simgrid.msg.Task;

import biz.source_code.base64Coder.Base64Coder;

public class WsRequest extends Task implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String senderMailbox;
	public String serviceName;
	public String serviceMethod;
	public double inputMessageSize;
	public boolean done = false;
	public int instanceId = -1;
	public String destination;
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(inputMessageSize);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((senderMailbox == null) ? 0 : senderMailbox.hashCode());
		result = prime * result
				+ ((serviceMethod == null) ? 0 : serviceMethod.hashCode());
		result = prime * result
				+ ((serviceName == null) ? 0 : serviceName.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WsRequest other = (WsRequest) obj;
		if (Double.doubleToLongBits(inputMessageSize) != Double
				.doubleToLongBits(other.inputMessageSize))
			return false;
		if (senderMailbox == null) {
			if (other.senderMailbox != null)
				return false;
		} else if (!senderMailbox.equals(other.senderMailbox))
			return false;
		if (serviceMethod == null) {
			if (other.serviceMethod != null)
				return false;
		} else if (!serviceMethod.equals(other.serviceMethod))
			return false;
		if (serviceName == null) {
			if (other.serviceName != null)
				return false;
		} else if (!serviceName.equals(other.serviceName))
			return false;
		return true;
	}

	public WsRequest(String wsName, String wsMethod, double messageSize, String senderMailbox) {
		super(wsMethod, 1, messageSize);

		this.senderMailbox = senderMailbox;
		serviceMethod = wsMethod;
		serviceName = wsName;
		inputMessageSize = messageSize;
	}

	
	
	/** Read the object from Base64 string. */
    public static WsRequest fromString( String s ) throws IOException ,
                                                        ClassNotFoundException {
        byte [] data = Base64Coder.decode( s );
        ObjectInputStream ois = new ObjectInputStream( 
                                        new ByteArrayInputStream(  data ) );
        WsRequest o  = (WsRequest) ois.readObject();
        ois.close();
        return o;
    }

    /** Write the object to a Base64 string. */
    public static String toString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return new String( Base64Coder.encode( baos.toByteArray() ) );
    }


//	public String serializeWsRequest() {
//		WsRequest wsRequest = this;
//		String serviceName = "null";
//		String senderMailbox = "null";
//		String serviceMethod = "null";
//		double inputMessageSize = 0;
//		boolean done = false;
//		int instanceId = -1;
//		String destination = "null";
//	
//		serviceName = wsRequest.serviceName;
//		senderMailbox = wsRequest.senderMailbox;
//		serviceMethod = wsRequest.serviceMethod;
//		inputMessageSize = wsRequest.inputMessageSize;
//		done = wsRequest.done;
//		instanceId = wsRequest.instanceId;
//		destination = wsRequest.destination;
//	
//		String serializedWsRequest;
//		serializedWsRequest = "WsRequest" + ";" + serviceName + ";"
//				+ senderMailbox + ";" + serviceMethod + ";"
//				+ inputMessageSize + ";" + done + ";" + instanceId + ";"
//				+ destination;
//		return serializedWsRequest;
//	}
//
//	public static WsRequest deserializeWsRequest(String serializedWsRequest)
//			throws Exception {
//	
//		String[] args = serializedWsRequest.split(";");
//		System.out.println(args.length);
//		if (args.length == 8) {
//			WsRequest wsRequest = deserializeWsRequest(args);
//	
//			return wsRequest;
//		} else
//			throw (new Exception());
//	
//	}
//	
//	private static WsRequest deserializeWsRequest(String... args) {
//		String serviceName = args[1];
//		String senderMailbox = args[2];
//		String serviceMethod = args[3];
//		double inputMessageSize = Double.parseDouble(args[4]);
//		boolean done = Boolean.parseBoolean(args[5]);
//		int instanceId = Integer.parseInt(args[6]);
//		String destination = args[7];
//	
//		WsRequest wsRequest = new WsRequest(serviceName, serviceMethod,
//				inputMessageSize, senderMailbox);
//		wsRequest.done = done;
//		wsRequest.instanceId = instanceId;
//		wsRequest.destination = destination;
//		return wsRequest;
//	}
	
}

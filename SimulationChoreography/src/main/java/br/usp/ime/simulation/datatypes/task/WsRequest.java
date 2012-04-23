package br.usp.ime.simulation.datatypes.task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.simgrid.msg.HostFailureException;
import org.simgrid.msg.Msg;
import org.simgrid.msg.NativeException;
import org.simgrid.msg.Task;
import org.simgrid.msg.TimeoutException;
import org.simgrid.msg.TransferFailureException;

import biz.source_code.base64Coder.Base64Coder;

public class WsRequest extends Task implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int nextId = 0;
	public String senderMailbox;
	public String serviceName;
	public String serviceMethod;
	public double inputMessageSize;
	public boolean done = false;
	public int instanceId = -1;
	public String destination;
	public double startTime;
	private int id;
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public WsRequest(String wsName, String wsMethod, double messageSize, String senderMailbox) {
		super(wsMethod, 0, messageSize);

		this.id = incrementId();
		this.senderMailbox = senderMailbox;
		serviceMethod = wsMethod;
		serviceName = wsName;
		inputMessageSize = messageSize;
	}

	public WsRequest(int id,String wsName, String wsMethod, double messageSize, String senderMailbox) {
		super(wsMethod, 0, messageSize);

		this.id = id;
		this.senderMailbox = senderMailbox;
		serviceMethod = wsMethod;
		serviceName = wsName;
		inputMessageSize = messageSize;
	}
	


	private synchronized int incrementId() {
		return WsRequest.nextId++;
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		if (id != other.id)
			return false;
		return true;
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
}

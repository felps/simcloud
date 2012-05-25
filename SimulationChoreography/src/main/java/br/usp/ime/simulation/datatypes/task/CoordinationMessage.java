package br.usp.ime.simulation.datatypes.task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.simgrid.msg.Task;

import biz.source_code.base64Coder.Base64Coder;

public class CoordinationMessage extends Task implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1139826362366951699L;
	public int instanceId = -1;
	public double startTime;
	public double inputMessageSize;

	public CoordinationMessage(double inputMessageSize) {
		super("CoordinationTask", 1, inputMessageSize);
		this.inputMessageSize = inputMessageSize;
	}
	
	public static String toString(CoordinationMessage message) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( message );
        oos.close();
        return new String( Base64Coder.encode( baos.toByteArray() ) );
	}

	/** Read the object from Base64 string. */
    public static CoordinationMessage fromString( String s ) throws IOException ,
                                                        ClassNotFoundException {
        byte [] data = Base64Coder.decode( s );
        ObjectInputStream ois = new ObjectInputStream( 
                                        new ByteArrayInputStream(  data ) );
        CoordinationMessage o  = (CoordinationMessage) ois.readObject();
        ois.close();
        return o;
    }

}

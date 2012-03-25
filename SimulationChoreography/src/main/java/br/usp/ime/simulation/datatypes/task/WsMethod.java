package br.usp.ime.simulation.datatypes.task;

import org.simgrid.msg.Task;

public class WsMethod extends Task {

	private int computingSizeInMI;
	private double inputFileSizeInBytes;
	private double outputFileSizeInBytes;
	private String wsMethodName;
	
	public WsMethod(String wsMethodName, double averageComputeDuration, double inputFileSize, double outputFileSize) {
		super(wsMethodName, averageComputeDuration, (inputFileSize + outputFileSize));
		this.inputFileSizeInBytes =  inputFileSize;
		this.outputFileSizeInBytes = outputFileSize;
		this.wsMethodName = wsMethodName;
	}

	public int getComputingSizeInMI() {
		return computingSizeInMI;
	}

	public double getInputFileSizeInBytes() {
		return inputFileSizeInBytes;
	}

	public double getOutputFileSizeInBytes() {
		return outputFileSizeInBytes;
	}

	public String getWsMethodName() {
		return wsMethodName;
	}

	public void setWsMethodName(String wsMethodName) {
		this.wsMethodName = wsMethodName;
	}
	
	

}

package pong;

import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;
import org.simgrid.msg.Process;
import org.simgrid.msg.Task;

import commTime.FinalizeTask;

public class Ping extends Process {

	public static final int COUNT =1000; 
	@Override
	public void main(String[] args) throws MsgException {

		Task task = new Task();
		Pong pong = new Pong(getHost());
		
		for(int i=0; i<COUNT; i++){
			Msg.info("ping");
			task.send("pong");
			Task.receive("ping");
		}
		
		FinalizeTask endIt = new FinalizeTask();
		endIt.send("pong");

		Msg.info(" See ya ! Ping out!" );
	}

}

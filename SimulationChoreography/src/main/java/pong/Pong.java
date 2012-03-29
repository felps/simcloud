package pong;

import org.simgrid.msg.Host;
import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;
import org.simgrid.msg.Task;

import commTime.FinalizeTask;

public class Pong extends org.simgrid.msg.Process{

	public Pong(Host host) {
		super(host, "pong.Pong");
	}
	@Override
	public void main(String[] args) throws MsgException {
		while (true){
			Task task = Task.receive("pong");
			Msg.info("pong");
			
			if(task instanceof FinalizeTask) break;
			task.send("ping");
		}
		
		Msg.info(" See ya ! Pong out!" );
		
	}
	

}

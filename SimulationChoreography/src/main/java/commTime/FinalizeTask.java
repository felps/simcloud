/*
 * Copyright 2006,2007,2010. The SimGrid Team. All rights reserved. 
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the license (GNU LGPL) which comes with this package. 
 */

package commTime;

import org.simgrid.msg.*;

public class FinalizeTask extends Task {
	public FinalizeTask() {
		super("", 0, 0);
	}

	public  String serializeFinalizeTask() {
		return "FinalizeTask";
	}

	public static FinalizeTask deserializeFinalizeTask(String serializeTask) {
		if (serializeTask.equals("FinalizeTask"))
			return new FinalizeTask();
		else
			return null;
	}
}

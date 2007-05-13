package org.jnode.apps.jpartition.commands.framework;

public enum CommandStatus {
	NOT_RUNNING("not running"),
	RUNNING("running"),
	SUCCESS("success"),
	FAILED("failed");
	
	
	private final String name; 
	private CommandStatus(String name)
	{
		this.name = name;
	}
	
	final public String toString(){
		return name;
	}
}
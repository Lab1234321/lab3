package littlemylyn.entity;

import littlemylyn.entity.TaskManager.Kind;
import littlemylyn.entity.TaskManager.Status;

public class Task {
	
	// the name is the id too
	private String name;
	private Kind kind;
	private Status status;

	
	
	
	
	public Task(String name, Kind kind){
		this.name = name;
		this.kind = kind;
		this.status = TaskManager.Status.NEW;
	}
	
	public String getName(){
		return this.name;
	}
	
	public Kind getKind(){
		return kind;
	}
	
	public void setKind(Kind kind){
		this.kind = kind;
	}
	
	public Status getStatus(){
		return status;
	}
	
}

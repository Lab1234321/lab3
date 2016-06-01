package littlemylyn.entity;

import java.util.ArrayList;

import littlemylyn.entity.TaskManager.Kind;
import littlemylyn.entity.TaskManager.Status;

public class Task {
	
	// the name is the id too
	private String name;
	private Kind kind;
	private Status status;
	
	private ArrayList<String> relatedClasses;

	
	
	
	
	public Task(String name, Kind kind){
		this.name = name;
		this.kind = kind;
		this.status = TaskManager.Status.NEW;
		this.relatedClasses = new ArrayList<String>();
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
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public void addRelatedClass(String className){
		this.relatedClasses.add(className);
	}
	
	public ArrayList<String> getRelatedClass() {
		return this.relatedClasses;
	}
	
}

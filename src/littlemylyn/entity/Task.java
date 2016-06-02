package littlemylyn.entity;

import java.io.Serializable;
import java.util.ArrayList;

import littlemylyn.entity.TaskManager.Kind;
import littlemylyn.entity.TaskManager.Status;

public class Task implements Serializable{
	
	// the name is the id too
	private String name;
	private Kind kind;
	private Status status;
	
	private ArrayList<String[]> relatedClasses;

	public Task(String name, Kind kind){
		this.name = name;
		this.kind = kind;
		this.status = TaskManager.Status.NEW;
		this.relatedClasses = new ArrayList<String[]>();
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
	
	public void addRelatedClass(String[] className){
		boolean isContainted = false;
		for (String[] relatedClass: relatedClasses){
			if(relatedClass[0].equals(className[0])&&relatedClass[1].equals(className[1])){
				isContainted=true;
			}
		}
		if(!isContainted){
			this.relatedClasses.add(className);
		}
	}
	
	public ArrayList<String[]> getRelatedClass() {
		return this.relatedClasses;
	}
	
}

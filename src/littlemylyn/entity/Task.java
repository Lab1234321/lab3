package littlemylyn.entity;

import java.io.Serializable;
import java.util.ArrayList;

import littlemylyn.entity.TaskManager.Kind;
import littlemylyn.entity.TaskManager.Status;


public class Task implements Serializable{
	
	
	private String name;	// the name is the id too
	private Kind kind;
	private Status status;
	private ArrayList<String[]> relatedClasses;

	
	
	public Task(String name, Kind kind){
		this.name = name;
		this.kind = kind;
		this.status = TaskManager.Status.NEW;
		this.relatedClasses = new ArrayList<String[]>();
	}
	
	
	/*
	 * getter and setter
	 */
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
		System.out.println("Debug: className:"+className[0]+" "+className[1]);
		for (String[] relatedClass: relatedClasses){
			System.out.println("Debug: relatedClass:"+relatedClass[0]+" "+relatedClass[1]);
			
			if(relatedClass[0].equals(className[0])&&relatedClass[1].equals(className[1])){
				isContainted=true;
			}
		}
		System.out.println("isContainted: "+isContainted);
		if(!isContainted){
			this.relatedClasses.add(className);
		}
	}
	
	public void removeRelatedClass(String[] relatedClass){
		for(String[] c:relatedClasses){
			if(c[0].equals(relatedClass[0])&&c[1].equals(relatedClass[1])){
				relatedClasses.remove(c);
				break;
			}
		}
	}
	
	public ArrayList<String[]> getRelatedClass() {
		return this.relatedClasses;
	}
	
}

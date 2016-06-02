package littlemylyn.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class TaskManager {

	public  enum Kind{
		DEBUG, NEW_FEATURE, REFACTOR
	}
	public  enum Status{
		NEW, ACTIVATED, FINISHED 
	}
	
	public static final String DATAPATH = "D:/lab3/data/data.txt";
	
	/*
	 * I think we should use singleton to get the instance of
	 * TaskManager, because there can exist one instance at most
	 * in the whole lab  --written by zhangtao 
	 */
	private static TaskManager taskManager;
	
	private ArrayList<Task> taskList;
	
	
	
	private TaskManager(){
		taskList = readTaskList();
	}
	
	public static TaskManager getTaskManeger(){
		if (null == taskManager){
			taskManager = new TaskManager();
		}
		
		return taskManager;
	}
	
	/*
	 * There are some methods that can apply to taskList  
	 * */
	public Task getTask(String taskName){
		/*
		 * search the taskList, if find, just return, else return null(
		 * this may be not friendly, maybe we should wrap it)
		 */
		for (Task task:taskList){
			if(task.getName().equals(taskName)){
				return task;
			}
		}
		return null;
	}
	
	public ArrayList<Task> getTaskList(){
		return this.taskList;
	}
	
	
	public void addTask(Task task){
		taskList.add(task);
	}
	
	/*
	 * add the related classes to the activated task
	 * this is only one activated class at one time 
	 */
	
	public void addRelatedClass(String[] className) {
		for(Task task:taskList){
			if (task.getStatus()==Status.ACTIVATED) {
				task.addRelatedClass(className);
				break;
			}
		}
	}
	
	public Task getActivatedTask(){
		for (Task task:taskList){
			if(task.getStatus()==Status.ACTIVATED){
				return task;
			}
		}
		return null;
	}
	
	public void saveTaskList(){

		//save the task to a file, using absolute path
		try {
			ObjectOutputStream oStream = new ObjectOutputStream(new FileOutputStream(DATAPATH));
			oStream.writeObject(taskList);
			oStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<Task> readTaskList(){
		ArrayList<Task> taskList = new ArrayList<Task>();
		try {
			ObjectInputStream oInputStream = new ObjectInputStream(new FileInputStream(DATAPATH));
			taskList = (ArrayList<Task>)oInputStream.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return taskList;
	}
	
}

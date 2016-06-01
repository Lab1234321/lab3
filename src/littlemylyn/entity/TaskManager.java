package littlemylyn.entity;

import java.util.ArrayList;

public class TaskManager {

	public  enum Kind{
		DEBUG, NEW_FEATURE, REFACTOR
	}
	public  enum Status{
		NEW, ACTIVATED, FINISHED 
	}
	
	/*
	 * I think we should use singleton to get the instance of
	 * TaskManager, because there can exist one instance at most
	 * in the whole lab  --written by zhangtao 
	 */
	private static TaskManager taskManager;
	
	private ArrayList<Task> taskList;
	
	
	
	private TaskManager(){
		taskList = new ArrayList();
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
	
}

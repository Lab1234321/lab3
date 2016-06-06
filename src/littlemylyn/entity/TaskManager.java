package littlemylyn.entity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;

import littlemylyn.db.DBConnector;

/*
 * The TaskManager is used to manage all tasks
 */
public class TaskManager {
	public static final boolean useDB = true;

	/*
	 * The task's Kind and Status
	 */
	public  enum Kind{
		DEBUG, NEW_FEATURE, REFACTOR
	}
	public  enum Status{
		NEW, ACTIVATED, FINISHED 
	}
	
	/*
	 * The DATAPATH is the path to save tasks when the plugin is closed
	 */
	public static final String DATAPATH = "./data.txt";
	
	/*
	 * The taskList is used to store all tasks at runtime
	 */
	private ArrayList<Task> taskList;
	
	
	/*
	 * using singleton to get the instance of TaskManager, 
	 * because there can exist one instance at most in the whole lab
	 */
	private static TaskManager taskManager;
	
	private TaskManager(){
		/*
		 * read data from file to initialize tasklist
		 */
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
	
	/*
	 * when user create a task, use this method to add it to tasklist
	 */
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
	
	/*
	 * search tasklist then return the activated task
	 */
	public Task getActivatedTask(){
		for (Task task:taskList){
			if(task.getStatus()==Status.ACTIVATED){
				return task;
			}
		}
		return null;
	}

	
	/*
	 * save the tasklist to file
	 */
	public void saveTaskListToFile(ArrayList<Task> taskList) {
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
	
	public void saveTaskList(){
		if (useDB)
			try {
				DBConnector.writeTasks(taskList);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		saveTaskListToFile(taskList);
	}
	
	/*
	 * read task list from file
	 */
	public ArrayList<Task> readTaskListFromFile() {
		ArrayList<Task> taskList = new ArrayList<Task>();
		try {
			ObjectInputStream oInputStream = new ObjectInputStream(new FileInputStream(DATAPATH));
			taskList = (ArrayList<Task>)oInputStream.readObject();
			oInputStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return taskList;
	}
	
	public ArrayList<Task> readTaskList(){
		if (useDB) {
			try {
				return DBConnector.readTasks();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new ArrayList<Task>();
		}
		return readTaskListFromFile();
	}
	
}

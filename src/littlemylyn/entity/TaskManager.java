package littlemylyn.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/*
 * The TaskManager is used to manage all tasks
 */
public class TaskManager {

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
	public void saveTaskList(){
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
	
	/*
	 * read task list from file
	 */
	public ArrayList<Task> readTaskList(){
		ArrayList<Task> taskList = new ArrayList<Task>();
		try {
			ObjectInputStream oInputStream = new ObjectInputStream(new FileInputStream(DATAPATH));
			System.out.println(new File(DATAPATH).getAbsolutePath());
			taskList = (ArrayList<Task>)oInputStream.readObject();
			// FIXME: debug
			System.out.println("name: " + taskList.get(0).getName());
			System.out.println("kind: " + taskList.get(0).getKind());
			System.out.println("status: " + taskList.get(0).getStatus());
			ArrayList<String[]> relatedClasses = taskList.get(0).getRelatedClass();
			System.out.println("relatedClasses: ");
			for (String[] rc : relatedClasses) {
				for (int i = 0; i < rc.length; i++)
					System.out.print(rc[i] + "  ");
				System.out.println();
			}
			oInputStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return taskList;
	}
	
}

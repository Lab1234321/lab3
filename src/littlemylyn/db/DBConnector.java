package littlemylyn.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import littlemylyn.entity.Task;
import littlemylyn.entity.TaskManager.Kind;
import littlemylyn.entity.TaskManager.Status;

public class DBConnector {
	public static String url = "jdbc:mysql://127.0.0.1/littlemylyn";
	public static String name = "com.mysql.jdbc.Driver";
	public static String user = "test";
	public static String passwd = "test";
	
	protected Connection conn;
	protected PreparedStatement pst;
	
	public DBConnector() {}
	
	private DBConnector(String sql) throws ClassNotFoundException, SQLException {
		Class.forName(name);
		conn = DriverManager.getConnection(url, user, passwd);
		pst = conn.prepareStatement(sql);
	}
	
	public void close() throws SQLException {
		this.conn.close();
		this.pst.close();
	}
	
	private void writeTask(int id, Task task) throws SQLException, ClassNotFoundException {
		String sql;
		DBConnector db;
		
		String name = task.getName();
		int kind = task.getKind().ordinal();
		int status = task.getStatus().ordinal();
		String relatedClasses = "";
		for (String[] rc : task.getRelatedClass()) {
			relatedClasses += rc[1] + ";";
		}
//		System.out.println(name + " " + kind + " " + status + " " + relatedClass);
		if (id == 0)
			sql = "INSERT into taskinfo (name, kind, status, relatedClasses) values " + 
								"(\"" + name + "\", \"" + kind + "\", \"" + status + "\", \"" + relatedClasses + "\");";
		else
			sql = "INSERT into taskinfo (id, name, kind, status, relatedClasses) values " + 
					"(\"" + id + "\", \"" + name + "\", \"" + kind + "\", \"" + status + "\", \"" + relatedClasses + "\");";
		db = new DBConnector(sql);
		db.pst.executeUpdate(sql);
	}
	
	private void deleteTaskTable() throws ClassNotFoundException, SQLException {
		String sql;
		DBConnector db;
		
		sql = "DELETE FROM taskinfo;";
		db = new DBConnector(sql);
		db.pst.executeUpdate();
	}
	
	public void writeTasks(ArrayList<Task> taskList) throws ClassNotFoundException, SQLException {
		// FIXME: according to current version, database should be erased before write
		deleteTaskTable();
		
		int i = 0;
		for (Task t : taskList) {
			i++;
			writeTask(i, t);
		}
	}
	
	public ArrayList<Task> readTasks() throws ClassNotFoundException, SQLException {
		String sql;
		DBConnector db;
		ResultSet res;
		
		String name;
		int kind;
		int status;
		String relatedClasses;
		ArrayList<Task> taskList = new ArrayList<Task>();
		Task newTask = null;
		
		sql = "SELECT * FROM taskinfo;";
		db = new DBConnector(sql);
		res = db.pst.executeQuery();
		while (res.next()) {
			name = res.getString("name");
			kind = res.getInt("kind");
			status = res.getInt("status");
			relatedClasses = res.getString("RelatedClasses");
			newTask = new Task(name, Kind.values()[kind]);
			newTask.setStatus(Status.values()[status]);
			String[] tokens = relatedClasses.split(";");
			for (String token : tokens) {
				String[] token_split = token.split("/");
				String className = token_split[token_split.length-1];
				newTask.addRelatedClass(new String[] {className, token});
			}
			taskList.add(newTask);
		}
		return taskList;
	}
	
	
	
	public static void main(String[] args) {
		String sql;
		DBConnector db;
		ResultSet res;
		
		sql = "select * from taskinfo";
		try {
			db = new DBConnector(sql);
			res = db.pst.executeQuery();
			while (res.next()) {
				int id = res.getInt("id");
				String name = res.getString("name");
				int kind = res.getInt("kind");
				int status = res.getInt("status");
				String rc_raw = res.getString("relatedClasses");
				System.out.println(id + " " + name + " " + kind + " " +
						status + " " + rc_raw);
			}
			ArrayList<Task> taskList = new ArrayList<Task>();
			Task newTask = new Task("task_1", Kind.DEBUG);
			newTask.addRelatedClass(new String[] {"a.java", "a/a/a.java"});
			taskList.add(newTask);
			db.writeTasks(taskList);
			db.readTasks();
			res.close();
			db.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

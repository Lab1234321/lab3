package littlemylyn.views;

import java.util.ArrayList;

import littlemylyn.entity.Task;
import littlemylyn.entity.TaskManager;
import littlemylyn.entity.TaskManager.Status;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.core.runtime.IAdaptable;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class SampleView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "littlemylyn.views.SampleView";

	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action action1;
	private Action newTaskAction;
	//newTaskAction is used to created new class
//	private Action newTaskAction;
	private Action doubleClickAction;
	
//	private TaskManager taskManager=TaskManager.getTaskManeger();

	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */
	 
	class TreeObject implements IAdaptable {
		private String name;
		private TreeParent parent;
		
		public TreeObject(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public void setParent(TreeParent parent) {
			this.parent = parent;
		}
		public TreeParent getParent() {
			return parent;
		}
		public String toString() {
			return getName();
		}
		public Object getAdapter(Class key) {
			return null;
		}
		
		public void setName(String Name) {
			this.name = Name;
		}
	}
	
	class TreeParent extends TreeObject {
		private ArrayList children;
		public TreeParent(String name) {
			super(name);
			children = new ArrayList();
		}
		public void addChild(TreeObject child) {
			children.add(child);
			child.setParent(this);
		}
		public void removeChild(TreeObject child) {
			children.remove(child);
			child.setParent(null);
		}
		public TreeObject [] getChildren() {
			return (TreeObject [])children.toArray(new TreeObject[children.size()]);
		}
		public boolean hasChildren() {
			return children.size()>0;
		}
	}

	class ViewContentProvider implements IStructuredContentProvider, 
										   ITreeContentProvider {
		private TreeParent invisibleRoot;

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			if (parent.equals(getViewSite())) {
				if (invisibleRoot==null) initialize();
				return getChildren(invisibleRoot);
			}
			return getChildren(parent);
		}
		public Object getParent(Object child) {
			if (child instanceof TreeObject) {
				return ((TreeObject)child).getParent();
			}
			return null;
		}
		public Object [] getChildren(Object parent) {
			if (parent instanceof TreeParent) {
				return ((TreeParent)parent).getChildren();
			}
			return new Object[0];
		}
		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent)
				return ((TreeParent)parent).hasChildren();
			return false;
		}
		/*
		 * find the node according to the name
		 */
		
		public TreeParent findNode(String name){
			TreeParent[] children = (TreeParent[]) invisibleRoot.getChildren();
			for (TreeParent t: children){
				if (t.getName().equals(name)) {
					return t;
				}
			}
			return null;
		}
/*
 * We will set up a dummy model to initialize tree heararchy.
 * In a real code, you will connect to a real model and
 * expose its hierarchy.
 */
		private void initialize() {
			TreeObject to1 = new TreeObject("Leaf 1");
			TreeObject to2 = new TreeObject("Leaf 2");
			TreeObject to3 = new TreeObject("Leaf 3");
			TreeParent p1 = new TreeParent("Parent 1");
			p1.addChild(to1);
			p1.addChild(to2);
			p1.addChild(to3);
			
			TreeObject to4 = new TreeObject("Leaf 4");
			TreeParent p2 = new TreeParent("Parent 2");
			p2.addChild(to4);
			
			TreeParent root = new TreeParent("Root");
			root.addChild(p1);
			root.addChild(p2);
			
			invisibleRoot = new TreeParent("");
			invisibleRoot.addChild(root);
		}
	}
	class ViewLabelProvider extends LabelProvider {

		public String getText(Object obj) {
			return obj.toString();
		}
		public Image getImage(Object obj) {
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			if (obj instanceof TreeParent)
			   imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}
	}
	class NameSorter extends ViewerSorter {
	}
	
	
	
	/*
	 * The following interfaces are written by alexzhang, if you have some question, call him soon
	 * doubleClick() handle the double click event
	 */
	
	interface Kind{
		void doubleClick(String taskName);
	}
	interface Status{
		void doubleClick(String taskName);
	}
	interface RelatedClass{
		
	}
	
	/*
	 * The following classes are written by alexzhang, if you have some question, call him soon
	 * KindNode
	 * StatusNode
	 * RelatedClassNode
	 */
	class KindNode extends TreeObject implements Kind{

		public KindNode(String name) {
			super("Kind: "+name);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void doubleClick(String taskName) {
			// TODO Auto-generated method stub
			littlemylyn.entity.TaskManager.Kind kind  = getTaskKind();
			Task task = TaskManager.getTaskManeger().getTask(taskName);
			if (null == task){
				System.out.println("Error! the task is null");
			}else{
				task.setKind(kind);
				System.out.println("Kind to string: "+kind.toString());
				this.setName(kind.toString());
			}
		}
		
		@Override
		public void setName(String name){
			super.setName("Kind: "+name);
		}
	}

	class StatusNode extends TreeObject implements Status{

		public StatusNode(String name) {
			super("Status: "+name);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void doubleClick(String taskName) {
			// TODO Auto-generated method stub
			Task task = TaskManager.getTaskManeger().getTask(taskName);
			if (task == null) {
				System.out.println("Error! the task is null");
			}else{
				littlemylyn.entity.TaskManager.Status status = getStatus(taskName);
				this.setName(status.toString());
				
				for(Task t:TaskManager.getTaskManeger().getTaskList()){
					System.out.println(t.getName()+" "+t.getKind().toString()+" "+t.getStatus().toString());
				}
				
			}
		}
		@Override
		public void setName(String name){
			super.setName("Status: "+name);
		}
		
	}
	
	class RelatedClassNode extends TreeObject implements RelatedClass{

		public RelatedClassNode() {
			super("RelatedClass");
			// TODO Auto-generated constructor stub
		}
		
	}
	
	
	/**
	 * The constructor.
	 */
	public SampleView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "LittleMylyn.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				SampleView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(newTaskAction);
//		manager.add(new Separator());
//		manager.add(newTaskAction);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(newTaskAction);
//		manager.add(newTaskAction);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(newTaskAction);
//		manager.add(newTaskAction);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		newTaskAction = new Action() {
			public void run() {
				newTask();
			}
		};
		newTaskAction.setText("New Task");
		newTaskAction.setToolTipText("New Task tooltip");
		newTaskAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
//				showMessage("Double-click detected on "+obj.toString());
			
				if (obj instanceof KindNode) {
					((KindNode)obj).doubleClick(((KindNode)obj).getParent().toString());
					viewer.refresh();
				}else if ( obj instanceof StatusNode){
					((StatusNode)obj).doubleClick(((StatusNode)obj).getParent().toString());
					viewer.refresh();
				}
				
			}
		};	
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Sample View",
			message);
	}

	
	//to get the new task name
	private String newTask() {
		InputDialog id = new InputDialog(viewer.getControl().getShell(), "New Task", "Please enter new task name", "new_task", new IInputValidator() {
			@Override
			public String isValid(String newText) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		
		if (id.open() == Window.OK) {
	        System.out.println( id.getValue());
	        /*
	         * we should check whether the input name has been occupied,
	         * if not occupied, we should add a new task to TaskManager's taskList
	         * else, we should notify the inputer 
	         */
	        boolean isValid = checkTaskName(id.getValue());
	        if(isValid){
	        	//add a new task to TaskManager
	        	littlemylyn.entity.TaskManager.Kind kind = getTaskKind();
	        	System.out.println("getTaskkind£º "+kind);
	        	Task newTask = new Task(id.getValue(),kind);
	        	TaskManager tm = TaskManager.getTaskManeger();
	        	tm.addTask(newTask);
	        	updateView(newTask);
	        }else{
	        	//notify
	        	MessageDialog.openInformation(
	        			viewer.getControl().getShell(),
	        			"LittleMylyn",
	        			"Sorry, this name has been occupied, please enter another name");
	        	newTask();
	        }
	        
		
		} else {
	        System.out.println("NewTaskAction: input dialog open error!");
	    }
		
		return null;
	}
	
	/*
	 * check whether the taskName has been occupied
	 */
	public boolean checkTaskName(String taskName){
		TaskManager tm = TaskManager.getTaskManeger();
		ArrayList<Task> taskList = tm.getTaskList();
		
		System.out.println(taskList.size());
		
		for (Task task : taskList){
			System.out.print(task.getName());
			if(task.getName().equals(taskName)){
				return false;
			}
		}
		return true;
	}
	
	public void updateView(Task task){
		ViewContentProvider vp = (ViewContentProvider) viewer.getContentProvider();
		Object[] elements =(Object[]) vp.getElements(getViewSite());
		for (Object tp:elements){
			if(tp instanceof TreeObject)
			System.out.println(((TreeObject)tp).getName());
		}
		System.out.println("task == null: "+task == null);
		
		TreeParent tp =new TreeParent(task.getName());
		KindNode kn = new KindNode(task.getKind().toString());
		StatusNode sn = new StatusNode(task.getStatus().toString());
		RelatedClassNode rcn = new RelatedClassNode();
		tp.addChild(kn);
		tp.addChild(sn);
		tp.addChild(rcn);
		
		vp.invisibleRoot.addChild(tp);
		viewer.refresh();
	}
	
	/*
	 * Open a wizard page to set the task's kind
	 * then return the new set kind
	 */
	
	public littlemylyn.entity.TaskManager.Kind getTaskKind(){
		TaskWizard tw = new TaskWizard(); 
		WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), tw);
		wizardDialog.open();
		return tw.getKind();
	}
	/*
	 * like the method above, open a wizard page to set the task's status,
	 * then set the new set status
	 * but there are some rule to limit the new set status
	 */
	
	public littlemylyn.entity.TaskManager.Status getStatus(String taskName){
		Task currentTask = TaskManager.getTaskManeger().getTask(taskName);
		littlemylyn.entity.TaskManager.Status currentStatus = currentTask.getStatus();
		
			StatusWizard sw = new StatusWizard(); 
			WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), sw);
			wizardDialog.open();
			littlemylyn.entity.TaskManager.Status newStatus = sw.getStatus();			
			//all status can be activated
			if(newStatus == littlemylyn.entity.TaskManager.Status.ACTIVATED){
				/*
				 * set the activated task to finished
				 */
				ArrayList<Task> taskList = TaskManager.getTaskManeger().getTaskList();
				for(Task task : taskList){
					if (task.getStatus()==littlemylyn.entity.TaskManager.Status.ACTIVATED) {
						task.setStatus(littlemylyn.entity.TaskManager.Status.FINISHED);
						ViewContentProvider vp = (ViewContentProvider) viewer.getContentProvider();
						TreeParent t =(TreeParent) vp.findNode(task.getName());
						TreeObject[] childrenObjects = t.getChildren();
						for (TreeObject treeObject:childrenObjects){
							if (treeObject instanceof StatusNode) {
								treeObject.setName(littlemylyn.entity.TaskManager.Status.FINISHED.toString());
							}
						}
					}
				}			
				TaskManager.getTaskManeger().getTask(taskName).setStatus(littlemylyn.entity.TaskManager.Status.ACTIVATED);
				return littlemylyn.entity.TaskManager.Status.ACTIVATED;
			}else{
			//if new status != activated, then new status == finished
				TaskManager.getTaskManeger().getTask(taskName).setStatus(littlemylyn.entity.TaskManager.Status.FINISHED);
				return littlemylyn.entity.TaskManager.Status.FINISHED;
			}
			
	}
	
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
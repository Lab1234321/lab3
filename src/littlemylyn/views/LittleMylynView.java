package littlemylyn.views;

import java.security.spec.ECField;
import java.util.ArrayList;

import javax.annotation.Resource;

import littlemylyn.detection.Detector;
import littlemylyn.entity.Task;
import littlemylyn.entity.TaskManager;
import littlemylyn.entity.TaskManager.Kind;
import littlemylyn.entity.TaskManager.Status;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ide.IDE;
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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.omg.CORBA.PRIVATE_MEMBER;

public class LittleMylynView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "littlemylyn.views.SampleView";

	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action newTaskAction;
	private Action doubleClickAction;

	class ViewContentProvider implements IStructuredContentProvider,
			ITreeContentProvider {
		private Root invisibleRoot;

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			if (parent.equals(getViewSite())) {
				if (invisibleRoot == null)
					initialize();
				return getChildren(invisibleRoot);
			}
			return getChildren(parent);
		}

		public Object getParent(Object child) {
			if (child instanceof TreeObject) {
				return ((TreeObject) child).getParent();
			}
			return null;
		}

		public Object[] getChildren(Object parent) {
			if (parent instanceof TreeObject) {
				return ((TreeObject) parent).getChildren();
			}
			return new Object[0];
		}

		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeObject)
				return ((TreeObject) parent).hasChildren();
			return false;
		}

		/*
		 * find the node according to the name
		 */

		public TaskNode findTaskNode(String name) {
			TaskNode[] children = (TaskNode[]) invisibleRoot.getChildren();
			for (TaskNode t : children) {
				if (t.getName().equals(name)) {
					return t;
				}
			}
			return null;
		}

		/*
		 * We will set up a dummy model to initialize tree heararchy. In a real
		 * code, you will connect to a real model and expose its hierarchy.
		 */
		private void initialize() {
			invisibleRoot = new Root("");
			ArrayList<Task> taskList = TaskManager.getTaskManeger()
					.getTaskList();
			for (Task task : taskList) {
				TaskNode tn = new TaskNode(task);
				// ArrayList<String[]> relatedClasses = task.getRelatedClass();
				tn.getRelatedClassNode().relatedClasses = task
						.getRelatedClass();
				// for(String[] relatedClass : relatedClasses){
				// ClassLeaf cl = new ClassLeaf(relatedClass[0],
				// relatedClass[1]);
				// tn.getRelatedClassNode().addChild(cl);
				// }
				tn.getRelatedClassNode().updateChildren();
				tn.getRelatedClassNode().updateNum();
				invisibleRoot.addChild(tn);
			}
		}
	}

	class ViewLabelProvider extends LabelProvider {

		public String getText(Object obj) {
			return obj.toString();
		}

		public Image getImage(Object obj) {
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(imageKey);
		}
	}

	class NameSorter extends ViewerSorter {
	}

	class KindNode extends TreeObject implements KindInterface {

		Kind kind;

		public KindNode(String name) {
			super("Kind: " + name);
		}

		public KindNode(Kind kind) {
			super("Kind: " + kind.toString());
			this.kind = kind;
		}

		@Override
		public void doubleClick(String taskName) {
			littlemylyn.entity.TaskManager.Kind kind = LittleMylynView
					.getTaskKind();
			this.setKind(kind);
			System.out.println("Kind to string: " + kind.toString());
			this.setName(kind.toString());
		}

		public void setKind(Kind kind) {
			super.setName("Kind: " + kind.toString());
			this.kind = kind;
			((TaskNode) (this.getParent())).getTask().setKind(kind);
		}

		@Override
		public String getName() {
			return "Kind: " + kind.toString();
		}

	}

	class Root extends TreeObject {
		public Root(String name) {
			super(name);
			// TODO Auto-generated constructor stub
			children = new ArrayList();
		}

		public void addChild(TaskNode child) {
			children.add(child);

		}

		public void removeChild(TaskNode child) {
			children.remove(child);

		}

		public TaskNode[] getChildren() {
			return (TaskNode[]) children.toArray(new TaskNode[children.size()]);
		}

		public boolean hasChildren() {
			return children.size() > 0;
		}
	}

	class TaskNode extends TreeObject {

		private Task task;
		private KindNode kindNode;
		private StatusNode statusNode;
		private RelatedClassNode relatedClassNode;

		public TaskNode(Task task) {
			super(task.getName());
			// TODO Auto-generated constructor stub
			this.task = task;
			kindNode = new KindNode(task.getKind());
			statusNode = new StatusNode(task.getStatus());
			relatedClassNode = new RelatedClassNode();
			this.addChild(kindNode);
			this.addChild(statusNode);
			this.addChild(relatedClassNode);
			// task.setTaskNode(this);
		}

		public KindNode getKindNode() {
			return kindNode;
		}

		public StatusNode getStatusNode() {
			return statusNode;
		}

		public RelatedClassNode getRelatedClassNode() {
			return relatedClassNode;
		}

		public Task getTask() {
			return task;
		}

	}

	class StatusNode extends TreeObject implements StatusInterface {

		Status status;

		public StatusNode(String name) {
			super("Status: " + name);
			// TODO Auto-generated constructor stub
		}

		public StatusNode(Status status) {
			super("Status: " + status.toString());
			this.status = status;
		}

		@Override
		public void doubleClick(String taskName) {
			// TODO Auto-generated method stub
			Task task = TaskManager.getTaskManeger().getTask(taskName);
			if (task == null) {
				System.out.println("Error! the task is null");
			} else {
				modifyStatus(this);
				for (Task t : TaskManager.getTaskManeger().getTaskList()) {
					System.out.println(t.getName() + " "
							+ t.getKind().toString() + " "
							+ t.getStatus().toString());
				}

			}
		}

		public void setStatus(Status status) {
			super.setName("Status: " + status.toString());
			this.status = status;
			((TaskNode) (this.getParent())).getTask().setStatus(status);
		}

		@Override
		public String getName() {
			return "Status: " + status.toString();
		}

	}

	class RelatedClassNode extends TreeObject implements RelatedClassInterface {

		ArrayList<String[]> relatedClasses = new ArrayList<String[]>();

		public RelatedClassNode() {
			super("RelatedClass");
			// TODO Auto-generated constructor stub
		}

		public void updateNum() {
			this.setName("RelatedClass(" + relatedClasses.size() + ")");
		}

		public void addChild(ClassLeaf child) {
			children.add(child);
			child.setParent(this);
		}

		public ClassLeaf[] getChildren() {
			return (ClassLeaf[]) children
					.toArray(new ClassLeaf[children.size()]);
		}

		public void setRelatedClasses(ArrayList<String[]> relatedClasses) {
			this.relatedClasses = relatedClasses;
		}

		public void updateChildren() {
			boolean isContained = false;
			for (String[] relatedClass : relatedClasses) {
				isContained = false;
				for (TreeObject child : children) {
					String childName = child.getName();
					int index = childName.indexOf('(');
					childName = childName.substring(0, index);

					if (childName.equals(relatedClass[0])
							&& ((ClassLeaf) child).getTitleToolTip().equals(
									relatedClass[1])) {
						isContained = true;
					}
				}
				if (!isContained) {
					ClassLeaf leaf = new ClassLeaf(relatedClass[0],
							relatedClass[1]);
					this.addChild(leaf);
				}
			}
		}

		public void removeChild(String className, String path) {

			for (ClassLeaf classLeaf : this.getChildren()) {
				int index = classLeaf.getName().indexOf('(');
				String leafNameString = classLeaf.getName().substring(0, index);
				if (leafNameString.equals(className)
						&& classLeaf.getTitleToolTip().equals(path)) {
					this.removeChild(classLeaf);
					break;
				}

			}
			//
			// for (String[] rc : relatedClasses) {
			// if (rc[0].equals(className) && rc[1].equals(path)) {
			// relatedClasses.remove(rc);
			//
			// break;
			// }
			// }
		}

	}

	class ClassLeaf extends TreeObject {

		String titleToolTip;

		public ClassLeaf(String name, String titleToolTip) {
			super(name + "(" + titleToolTip + ")");
			this.titleToolTip = titleToolTip;
			// TODO Auto-generated constructor stub
		}

		public void doubleClick(String tileToolTip) {
			int index = titleToolTip.indexOf('/');
			String projectName = titleToolTip.substring(0, index);
			String path = titleToolTip.substring(index + 1);
			System.out.println("project name: " + projectName);
			System.out.println("path: " + path);

			try {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				IProject project = ResourcesPlugin.getWorkspace().getRoot()
						.getProject(projectName);
				IFile file = project.getFile(path);
				IDE.openEditor(page, file);
			} catch (CoreException e) {
				e.printStackTrace();
				System.out.println();
			}
		}

		public String getTitleToolTip() {
			return titleToolTip;
		}

	}

	/**
	 * The constructor.
	 */
	public LittleMylynView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(viewer.getControl(), "LittleMylyn.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		Detector detector = new Detector();
		detector.initialize(this);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				LittleMylynView.this.fillContextMenu(manager);
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
		manager.add(new Separator());
		manager.add(newTaskAction);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(newTaskAction);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(newTaskAction);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		newTaskAction = new Action() {
			public void run() {
				newTask();
			}
		};
		newTaskAction.setText("New Task");
		newTaskAction.setToolTipText("New Task tooltip");
		newTaskAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				if (obj instanceof KindNode) {
					((KindNode) obj).doubleClick(((KindNode) obj).getParent()
							.toString());
					// viewer.refresh();
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							viewer.refresh();
						}
					});
				} else if (obj instanceof StatusNode) {
					((StatusNode) obj).doubleClick(((StatusNode) obj)
							.getParent().toString());
					// viewer.refresh();
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							viewer.refresh();
						}
					});
				} else if (obj instanceof ClassLeaf) {
					((ClassLeaf) obj)
							.doubleClick(((ClassLeaf) obj).titleToolTip);
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
		MessageDialog.openInformation(viewer.getControl().getShell(),
				"Sample View", message);
	}

	// to get the new task name
	private String newTask() {
		InputDialog id = new InputDialog(viewer.getControl().getShell(),
				"New Task", "Please enter new task name", "new_task",
				new IInputValidator() {
					@Override
					public String isValid(String newText) {
						// TODO Auto-generated method stub
						return null;
					}
				});

		if (id.open() == Window.OK) {
			System.out.println(id.getValue());
			/*
			 * we should check whether the input name has been occupied, if not
			 * occupied, we should add a new task to TaskManager's taskList
			 * else, we should notify the inputer
			 */
			boolean isValid = checkTaskName(id.getValue());
			if (isValid) {
				// add a new task to TaskManager
				littlemylyn.entity.TaskManager.Kind kind = getTaskKind();
				System.out.println("getTaskkind£º " + kind);
				Task newTask = new Task(id.getValue(), kind);
				TaskManager tm = TaskManager.getTaskManeger();
				tm.addTask(newTask);
				addTaskUpdateView(newTask);
			} else {
				// notify
				MessageDialog
						.openInformation(viewer.getControl().getShell(),
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
	public boolean checkTaskName(String taskName) {
		TaskManager tm = TaskManager.getTaskManeger();
		ArrayList<Task> taskList = tm.getTaskList();

		System.out.println(taskList.size());

		for (Task task : taskList) {
			System.out.print(task.getName());
			if (task.getName().equals(taskName)) {
				return false;
			}
		}
		return true;
	}

	public void addTaskUpdateView(Task task) {
		ViewContentProvider vp = (ViewContentProvider) viewer
				.getContentProvider();
		Object[] elements = (Object[]) vp.getElements(getViewSite());
		for (Object tp : elements) {
			if (tp instanceof TreeObject)
				System.out.println(((TreeObject) tp).getName());
		}
		System.out.println("task == null: " + task == null);

		TaskNode tn = new TaskNode(task);

		vp.invisibleRoot.addChild(tn);
		// viewer.refresh();
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				viewer.refresh();
			}
		});
	}

	/*
	 * Open a wizard page to set the task's kind then return the new set kind
	 */

	public void addRelatedClassUpdateView(Task task) {
		ViewContentProvider vp = (ViewContentProvider) viewer
				.getContentProvider();
		TaskNode t = (TaskNode) vp.findTaskNode(task.getName());
		t.getRelatedClassNode().setRelatedClasses(task.getRelatedClass());
		t.getRelatedClassNode().updateNum();
		t.getRelatedClassNode().updateChildren();
		// viewer.refresh();
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				viewer.refresh();
			}
		});
	}

	/*
	 * should remove the related class no matter which status task is in
	 */
	public void removeRelatedClassUpdateView(Task task, String className,
			String path) {
		ViewContentProvider vp = (ViewContentProvider) viewer
				.getContentProvider();
		TaskNode t = (TaskNode) vp.findTaskNode(task.getName());
		t.getTask().removeRelatedClass(new String[] { className, path });
		//
		t.getRelatedClassNode().setRelatedClasses(task.getRelatedClass());
		t.getRelatedClassNode().updateNum();
		// t.getRelatedClassNode().updateChildren();
		t.getRelatedClassNode().removeChild(className, path);
		// //
		// viewer.refresh();
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				viewer.refresh();
			}
		});
		// TaskNode[] nodeArray = root.getChildren();
		// for (TaskNode tn : nodeArray) {
		// tn.getTask().removeRelatedClass(new String[] { className, path });
		// tn.getRelatedClassNode().setRelatedClasses(tn.getTask().getRelatedClass());
		// tn.getRelatedClassNode().updateNum();
		// tn.getRelatedClassNode().removeChild(className, path);
		// // viewer.refresh();
		// Display.getDefault().asyncExec(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// viewer.refresh();
		// }
		// });
		// // continue;
		// }
	}

	public void changeRelatedClassName(String[] oldClass, String[] newClass) {
		ViewContentProvider vp = (ViewContentProvider) viewer
				.getContentProvider();
		Root root = vp.invisibleRoot;
		TaskNode[] nodeArray = root.getChildren();
		for (TaskNode tn : nodeArray) {
			if (tn.getTask().removeRelatedClass(oldClass)) {
				tn.getTask().addRelatedClass(newClass);
				tn.getRelatedClassNode().setRelatedClasses(tn.getTask().getRelatedClass());
				tn.getRelatedClassNode().removeChild(oldClass[0], oldClass[1]);
				tn.getRelatedClassNode().addChild(new ClassLeaf(newClass[0], newClass[1]));
				
			}
		}
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				viewer.refresh();
			}
		});
	}

	public static littlemylyn.entity.TaskManager.Kind getTaskKind() {
		TaskWizard tw = new TaskWizard();
		WizardDialog wizardDialog = new WizardDialog(Display.getCurrent()
				.getActiveShell(), tw);
		wizardDialog.open();
		return tw.getKind();
	}

	/*
	 * like the method above, open a wizard page to set the task's status, then
	 * set the new set status but there are some rule to limit the new set
	 * status
	 */
	public void modifyStatus(StatusNode statusNode) {
		StatusWizard sw = new StatusWizard();
		WizardDialog wizardDialog = new WizardDialog(Display.getCurrent()
				.getActiveShell(), sw);
		wizardDialog.open();
		littlemylyn.entity.TaskManager.Status newStatus = sw.getStatus();
		// all status can be activated

		/*
		 * set the activated task to finished
		 */
		if (newStatus == littlemylyn.entity.TaskManager.Status.ACTIVATED) {
			ArrayList<Task> taskList = TaskManager.getTaskManeger()
					.getTaskList();
			for (Task task : taskList) {
				if (task.getStatus() == littlemylyn.entity.TaskManager.Status.ACTIVATED) {
					task.setStatus(littlemylyn.entity.TaskManager.Status.FINISHED);

					ViewContentProvider vp = (ViewContentProvider) viewer
							.getContentProvider();
					TaskNode t = (TaskNode) vp.findTaskNode(task.getName());
					t.getStatusNode().setStatus(Status.FINISHED);
				}
			}
			statusNode.setStatus(Status.ACTIVATED);
		} else {
			statusNode.setStatus(Status.FINISHED);

		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public TreeViewer getViewer() {
		return viewer;
	}

}

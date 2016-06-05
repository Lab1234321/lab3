package littlemylyn.detection;

import littlemylyn.entity.Task;
import littlemylyn.entity.TaskManager;
import littlemylyn.views.LittleMylynView;

import org.eclipse.core.internal.preferences.StringPool;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/*
 * The detector is used to listen which file is modified.
 * if a file is modified, the file will be add to the related classes of the activated task
 */

public class Detector {

	public Detector() {

	}

	public void initialize(LittleMylynView view) {

		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		IWorkspace space = ResourcesPlugin.getWorkspace();

		page.addPartListener(new IPartListener() {

			@Override
			public void partOpened(IWorkbenchPart part) {
				detect(view, part);
			}

			@Override
			public void partDeactivated(IWorkbenchPart part) {
				// TODO Auto-generated method stub
			}

			@Override
			public void partClosed(IWorkbenchPart part) {
				/*
				 * when the sample view is closed, save the task to a file
				 */
				if (part instanceof LittleMylynView) {
					TaskManager.getTaskManeger().saveTaskList();
				}

			}

			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
				// TODO Auto-generated method stub
			}

			@Override
			public void partActivated(IWorkbenchPart part) {
				detect(view, part);
			}
		});

		space.addResourceChangeListener(new IResourceChangeListener() {

			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				// TODO Auto-generated method stub
				System.out.println("event get type" + event.getType());
				IResourceDelta delta = event.getDelta();
				IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {
					public boolean visit(IResourceDelta delta) {
						System.out.println("delta.kind " + delta.getKind());
						switch (delta.getKind()) {
						case IResourceDelta.ADDED:
							if (delta.getResource() instanceof IFile) {
								if (isJavaFile(delta.getResource().getName())) {
									String className = delta.getResource()
											.getName();
									String path = delta.getResource()
											.getFullPath().toString();
									path = path.substring(1);
									addRelatedClass(view, className, path);
								}
								System.out.println("Resourced add");
								System.out.println("Added Name is "
										+ delta.getResource().getName());
								System.out.println("path: "
										+ delta.getFullPath());

							}
							break;
						case IResourceDelta.REMOVED:
							if (delta.getResource() instanceof IFile) {
								if (isJavaFile(delta.getResource().getName())) {
									String className = delta.getResource()
											.getName();
									String path = delta.getResource()
											.getFullPath().toString();
									path = path.substring(1);
									removeRelatedClass(view, className, path);
								}

								System.out.println("Resourced removed");
								System.out.println("Removed Name is "
										+ delta.getResource().getName());
								System.out.println("path: "
										+ delta.getFullPath());

							}

							if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0) {
								String newPath = delta.getMovedToPath()
										.toString();
								String newClassName = newPath.substring(newPath
										.lastIndexOf('/') + 1);
								if (isJavaFile(newClassName)) {
									String newClassPath = newPath.substring(1);
									String oldClassName = delta.getResource()
											.getName();
									String oldClassPath = delta.getFullPath()
											.toString().substring(1);

									String[] oldClass = new String[] {
											oldClassName, oldClassPath };
									String[] newClass = new String[] {
											newClassName, newClassPath };

									System.out.println("newClassName"
											+ newClassName);
									System.out.println("newClassPath"
											+ newClassPath);
									System.out.println(oldClassName);
									System.out.println(oldClassPath);
									changeRelatedClassName(view, oldClass, newClass);
								}
							}
							break;
						}

						return true;
					}
				};
				try {
					delta.accept(visitor);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	/*
	 * The method to detect events
	 */
	public void detect(LittleMylynView view, IWorkbenchPart part) {

		if (part instanceof IEditorPart) {

			/*
			 * if the file is opened, add them to the activated task
			 */
			Task actTask = TaskManager.getTaskManeger().getActivatedTask();
			if (null != actTask) {
				String title = ((IEditorPart) part).getTitle();
				String titleToolTip = ((IEditorPart) part).getTitleToolTip();
				String[] classStrings = { title, titleToolTip };
				actTask.addRelatedClass(classStrings);
				view.addRelatedClassUpdateView(actTask);
			}
		}
	}

	public boolean isJavaFile(String fileName) {
		int index = fileName.indexOf('.');
		String suffix = fileName.substring(index + 1);
		if (suffix.equals("java")) {
			return true;
		} else {
			return false;
		}
	}

	public void addRelatedClass(LittleMylynView view, String className,
			String path) {
		Task actTask = TaskManager.getTaskManeger().getActivatedTask();
		if (null != actTask) {
			String[] classStrings = { className, path };
			actTask.addRelatedClass(classStrings);
			view.addRelatedClassUpdateView(actTask);
		}

	}

	public void removeRelatedClass(LittleMylynView view, String className,
			String path) {
		Task actTask = TaskManager.getTaskManeger().getActivatedTask();
		if (null != actTask) {
			String[] classStrings = { className, path };
			actTask.addRelatedClass(classStrings);
			view.removeRelatedClassUpdateView(actTask, className, path);
		}
	}

	public void changeRelatedClassName(LittleMylynView view, String[] oldClass,
			String[] newClass) {
		view.changeRelatedClassName(oldClass, newClass);
	}
}

package littlemylyn.detection;

import littlemylyn.entity.Task;
import littlemylyn.entity.TaskManager;
import littlemylyn.views.LittleMylynView;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
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

	public Detector(){
		
	}
	
	public void initialize(LittleMylynView sampleView){
		
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
//		IWorkspace space = ResourcesPlugin.getWorkspace();
		
		page.addPartListener(new IPartListener() {
			
			@Override
			public void partOpened(IWorkbenchPart part) {
				detect(sampleView, part);
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
				if(part instanceof LittleMylynView){
					TaskManager.getTaskManeger().saveTaskList();
				}
				
			}
			
			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void partActivated(IWorkbenchPart part) {
				detect(sampleView, part);
			}
		});
		
		/*
		space.addResourceChangeListener(new IResourceChangeListener() {
			
			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				// TODO Auto-generated method stub
				System.out.println("event get type"+event.getType());
			}
		});
		*/
	}
	
	/*
	 * The method to detect events
	 */
	public void detect(LittleMylynView sampleView,IWorkbenchPart part){
		
		if (part instanceof IEditorPart) {
			
			/*
			 * if the file is opened, add them to the activated task
			 */
			Task actTask = TaskManager.getTaskManeger().getActivatedTask();
			if(null!=actTask){
				String title = ((IEditorPart)part).getTitle();
				String titleToolTip = ((IEditorPart)part).getTitleToolTip();
				String[] classStrings = {title,titleToolTip};
				actTask.addRelatedClass(classStrings);
				sampleView.addRelatedClassUpdateView(actTask);
			}
			/*
			 * The following code is used to detect whether the file is modified
			 */
//			((IEditorPart)part).addPropertyListener(new IPropertyListener() {
//				@Override
//				public void propertyChanged(Object source, int propId) {
//					
//					if(propId == IEditorPart.PROP_DIRTY){
//						Task actTask = TaskManager.getTaskManeger().getActivatedTask();
//						if(null!=actTask){
//							String title = ((IEditorPart)part).getTitle();
//							String titleToolTip = ((IEditorPart)part).getTitleToolTip();
//							String[] classStrings = {title,titleToolTip};
//							actTask.addRelatedClass(classStrings);
//							sampleView.addRelatedClassUpdateView(actTask);
//						}
//					}
//				}
//			});
		}
	}
}

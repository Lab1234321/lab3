package littlemylyn.detection;

import littlemylyn.entity.Task;
import littlemylyn.entity.TaskManager;
import littlemylyn.views.SampleView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.preferences.PropertyListenerList;

public class Detector {
	public Detector(){
		
	}
	
	public void initialize(SampleView sampleView){
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		
		page.addPartListener(new IPartListener() {
			
			@Override
			public void partOpened(IWorkbenchPart part) {
				// TODO Auto-generated method stub
				System.out.println("partopened");
				detect(sampleView, part);
			}
			
			@Override
			public void partDeactivated(IWorkbenchPart part) {
				// TODO Auto-generated method stub
				System.out.println("pardeactivated");				
			}
			
			@Override
			public void partClosed(IWorkbenchPart part) {
				// TODO Auto-generated method stub
				System.out.println("partclosed");
				System.out.println(part.getClass().toString());
				
				if(part instanceof SampleView){
					TaskManager.getTaskManeger().saveTaskList();
				}
				
			}
			
			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void partActivated(IWorkbenchPart part) {
				// TODO Auto-generated method stub
				System.out.println("part activated");
				System.out.println("partActivated"+part.getTitle());
				detect(sampleView, part);
			}
		});
	}
	
	public void detect(SampleView sampleView,IWorkbenchPart part){
		if (part instanceof IEditorPart) {
			System.out.println("getTitle: "+((IEditorPart)part).getTitle());
			System.out.println("getTitleToolTip: "+((IEditorPart)part).getTitleToolTip());
			((IEditorPart)part).addPropertyListener(new IPropertyListener() {
				@Override
				public void propertyChanged(Object source, int propId) {
					if(propId == ((IEditorPart)part).PROP_DIRTY){
						Task actTask = TaskManager.getTaskManeger().getActivatedTask();
						if(null!=actTask){
							String title = ((IEditorPart)part).getTitle();
							String titleToolTip = ((IEditorPart)part).getTitleToolTip();
							String[] classStrings = {title,titleToolTip};
							actTask.addRelatedClass(classStrings);
							System.out.println("Activated Task: "+actTask.getName());
							sampleView.addRelatedClassUpdateView(actTask);
						}
					}
				}
			});
		}
	}
	
}

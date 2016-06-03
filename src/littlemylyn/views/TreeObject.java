package littlemylyn.views;

import java.util.ArrayList;

import littlemylyn.entity.Task;
import littlemylyn.entity.TaskManager;
import littlemylyn.entity.TaskManager.Kind;
import littlemylyn.entity.TaskManager.Status;
import littlemylyn.views.LittleMylynView.ViewContentProvider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class TreeObject implements IAdaptable {
	private String name;
	private TreeObject parent;
	protected ArrayList<TreeObject> children;
	
	public TreeObject(String name) {
		this.name = name;
		children = new ArrayList();
	}
	
	public String getName() {
		return name;
	}
	public void setParent(TreeObject parent) {
		this.parent = parent;
	}
	public TreeObject getParent() {
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
	public void addChild(TreeObject child) {
		children.add(child);
		child.setParent(this);
	}
	public void removeChild(TreeObject child) {
		children.remove(child);
		child.setParent(null);
	}
	public boolean hasChildren() {
		return children.size()>0;
	}
	public TreeObject [] getChildren() {
		return (TreeObject [])children.toArray(new TreeObject[children.size()]);
	}
}

/*
 * The following interfaces are written by alexzhang, if you have some question, call him soon
 * doubleClick() handle the double click event
 */

interface KindInterface{
	void doubleClick(String taskName);
}
interface StatusInterface{
	void doubleClick(String taskName);
}
interface RelatedClassInterface{
	
}

/*
 * The following classes are written by alexzhang, if you have some question, call him soon
 * KindNode
 * StatusNode
 * RelatedClassNode
 */

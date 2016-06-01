package littlemylyn.views;

import littlemylyn.entity.TaskManager;
import littlemylyn.entity.TaskManager.Kind;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class TaskWizard extends Wizard{

	private KindChoice kindChoice;
	private Kind kind;
	
	public TaskWizard() {
		// TODO Auto-generated constructor stub
		kindChoice = new KindChoice("Kind Set");
		this.addPage(kindChoice);
		this.setWindowTitle("New Task");
	}
	
	
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		System.out.println("performFinish");
		System.out.println(kindChoice.b1.getSelection());
		System.out.println(kindChoice.b2.getSelection());
		System.out.println(kindChoice.b3.getSelection());
		
		//set kind
		if(kindChoice.b1.getSelection()){
			kind = TaskManager.Kind.DEBUG;
		}else if (kindChoice.b2.getSelection()){
			kind = TaskManager.Kind.NEW_FEATURE;
		}else{
			kind = TaskManager.Kind.REFACTOR;
		}
		return true;
	}
	
	public Kind getKind() {
		return kind;
	}

}
class KindChoice extends WizardPage{
	Button b1;
	Button b2;
	Button b3;
	
	protected KindChoice(String pageName) {
		super(pageName);
		// TODO Auto-generated constructor stub
		this.setMessage("Please set the new task's kind ");
	}

	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		Composite composite = new Composite(parent, SWT.NONE);  
        composite.setLayout(new GridLayout(2, false));  
        new Label(composite, SWT.LEFT).setText("A.");  
        b1 =  new Button(composite, SWT.RADIO);  
        b1.setText("Debug");  
        b1.setSelection(true);  
        new Label(composite, SWT.LEFT).setText("B.");  
        b2 = new Button(composite, SWT.RADIO);  
        b2.setText("New feature");  
        new Label(composite, SWT.LEFT).setText("C.");  
        b3 = new Button(composite, SWT.RADIO);  
        b3.setText("Refactor");  
        setControl(composite);  
	}
}
package littlemylyn.views;

import littlemylyn.entity.TaskManager;
import littlemylyn.entity.TaskManager.Kind;
import littlemylyn.entity.TaskManager.Status;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class StatusWizard extends Wizard{

	private StatusChoice statusChoice;
	private Status status;
	
	public StatusWizard() {
		// TODO Auto-generated constructor stub
		statusChoice = new StatusChoice("Status Set");
		this.addPage(statusChoice);
		this.setWindowTitle("Status");
	}
	
	
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		System.out.println("performFinish");
		System.out.println(statusChoice.b2.getSelection());
		System.out.println(statusChoice.b3.getSelection());
		
		//set kind
		if (statusChoice.b2.getSelection()){
			status = TaskManager.Status.ACTIVATED;
		}else{
			status = TaskManager.Status.FINISHED;
		}
		return true;
	}
	
	public Status getStatus() {
		return status;
	}

}
class StatusChoice extends WizardPage{
	Button b2;
	Button b3;
	
	protected StatusChoice(String pageName) {
		super(pageName);
		// TODO Auto-generated constructor stub
		this.setMessage("Please set the task's status");
	}

	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		Composite composite = new Composite(parent, SWT.NONE);  
        composite.setLayout(new GridLayout(2, false));  
        new Label(composite, SWT.LEFT).setText("B.");  
        b2 = new Button(composite, SWT.RADIO);  
        b2.setText("Activated");  
        new Label(composite, SWT.LEFT).setText("C.");  
        b3 = new Button(composite, SWT.RADIO);  
        b3.setText("Finished");  
        setControl(composite);  
	}
}
package at.ac.tuwien.ifs.qse.tdd.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageOne;
import org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageTwo;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;


/**
 * Wizard which use the JUnit wizard pages to create a JUnit test class
 */
public class TddTestCaseWizard extends Wizard implements INewWizard {
	private NewTestCaseWizardPageOne page;
	private NewTestCaseWizardPageTwo page2;
	private String fileName = "TddTestClass";
	private IStructuredSelection selection = null;
	private IJavaProject project = null;
	
	
	public TddTestCaseWizard(String fileName, IJavaProject project) {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("Tdd Testcase Wizard");
		this.fileName = fileName;
		this.project = project;
	}
	
	
	public void addPages() {
		page2 = new NewTestCaseWizardPageTwo();
		page = new TddNewTestCaseWizardPageOne(page2,fileName,project);
		page.init(selection);
		addPage(page);
		addPage(page2);
	}
	
	public boolean performFinish() {
		try {
			page.createType(null);
			JavaUI.openInEditor(page.getCreatedType(),true,true);
		} catch (CoreException e) {
			page.setErrorMessage("Can't create type!");
			return false;
		} catch (InterruptedException e) {
			page.setErrorMessage("Can't create type!");
			return false;			
		}
		return true;
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}
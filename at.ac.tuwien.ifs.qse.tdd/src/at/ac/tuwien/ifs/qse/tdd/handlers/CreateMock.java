package at.ac.tuwien.ifs.qse.tdd.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import at.ac.tuwien.ifs.qse.tdd.exception.SearchException;
import at.ac.tuwien.ifs.qse.tdd.wizard.CreateMockWizard;

public class CreateMock extends TddFileHandler {

	@Override
	public void handleException(SearchException exc, String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWizard wizard = new CreateMockWizard();
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		
		
		try {
		System.out.println("Create");
		dialog.create();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Open");
		dialog.open();
		System.out.println("Finish");
		
		return null;
		
	}

}

package at.ac.tuwien.ifs.qse.tdd.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import at.ac.tuwien.ifs.qse.tdd.exception.SearchException;
import at.ac.tuwien.ifs.qse.tdd.refactoring.ExtractTestcaseSuperclassRefactoring;
import at.ac.tuwien.ifs.qse.tdd.wizard.ExtractTestcaseSuperclassWizard;

public class ExtractTestcaseSuperclass extends TddFileHandler {

	@Override
	public void handleException(SearchException exc, String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		final ICompilationUnit unit = getCompilationUnit();
		
		if(unit == null) return null;
		if(!(unit instanceof ICompilationUnit)) return null;
		
		ExtractTestcaseSuperclassRefactoring refactoring = new ExtractTestcaseSuperclassRefactoring();
		refactoring.setExtractionClass(unit);
		
		run(new ExtractTestcaseSuperclassWizard(refactoring, "Extract Testcase Superclass", unit),"Extract Testcase Superclass");

		return null;
		
	}
	
	public void run(RefactoringWizard wizard, String dialogTitle) {
		try {
			Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			RefactoringWizardOpenOperation operation= new RefactoringWizardOpenOperation(wizard);
			operation.run(parent, dialogTitle);
		} catch (InterruptedException exception) {
			// Do nothing
		}
	}

	
}

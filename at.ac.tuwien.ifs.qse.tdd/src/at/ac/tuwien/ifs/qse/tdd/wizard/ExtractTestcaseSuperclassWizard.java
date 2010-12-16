package at.ac.tuwien.ifs.qse.tdd.wizard;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class ExtractTestcaseSuperclassWizard  extends RefactoringWizard {

	private ICompilationUnit childClass;
	
	public ExtractTestcaseSuperclassWizard(Refactoring refactoring, String pageTitle, ICompilationUnit childClass) {
		super(refactoring, DIALOG_BASED_USER_INTERFACE | NO_PREVIEW_PAGE);
		this.childClass = childClass;
		setDefaultPageTitle(pageTitle);
	}
	
	protected void addUserInputPages() {
		addPage(new ExtractTextcaseSuperClassPageOne("ExtractTestcaseSuperclass", childClass));
	}

}

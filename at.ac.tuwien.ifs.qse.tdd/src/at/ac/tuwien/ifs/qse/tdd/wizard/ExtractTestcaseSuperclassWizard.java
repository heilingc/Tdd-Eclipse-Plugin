package at.ac.tuwien.ifs.qse.tdd.wizard;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.actions.OrganizeImportsAction;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import at.ac.tuwien.ifs.qse.tdd.refactoring.ExtractTestcaseSuperclassRefactoring;

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
	
	public boolean performFinish() {
		boolean returnarg = super.performFinish();

		organizeImports();
		
		return returnarg;
		
	}
	
	private void organizeImports() {
		
		ExtractTestcaseSuperclassRefactoring ref = (ExtractTestcaseSuperclassRefactoring)this.getRefactoring();
		
		IWorkbench wb = PlatformUI.getWorkbench();
		
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		if (win == null && wb.getWorkbenchWindowCount() != 0) {
			win = wb.getWorkbenchWindows()[0];
		}
		else if(win == null) {
			return;
		}
		
		IWorkbenchPage page = win.getActivePage();
		if (page == null && win.getPages().length != 0) {
			page = win.getPages()[0];
		}
		else if (page == null){
			return;
		}
		
		IEditorPart editor = page.getActiveEditor();
		if (editor == null) {
			return;
		}
		
		IWorkbenchPartSite site = editor.getSite();
		if (site == null) {
			return;
		}
		
		OrganizeImportsAction oia = new OrganizeImportsAction(site);
		oia.run(ref.getChildClass());
		oia.run(ref.getParentClass());
		
	}

}

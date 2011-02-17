package at.ac.tuwien.ifs.qse.tdd.propertyTester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import at.ac.tuwien.ifs.qse.tdd.model.TddClass;
import at.ac.tuwien.ifs.qse.tdd.model.TddClassFactory;
import at.ac.tuwien.ifs.qse.tdd.model.TddPluginHandler;
import at.ac.tuwien.ifs.qse.tdd.model.TddSourceClass;
import at.ac.tuwien.ifs.qse.tdd.model.TddTestClass;


public class TddPropertyTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		

		
		if("isClass".equals(property)) {
			
			ICompilationUnit unit = getUnit(receiver);
			if (unit == null) {
				return false;
			}
			
			System.out.print("isClass: ");
			System.out.println((TddClassFactory.get(unit) instanceof TddSourceClass));
			return TddClassFactory.get(unit) instanceof TddSourceClass;
			
		}
		
		if("isTest".equals(property)) {
			
			ICompilationUnit unit = getUnit(receiver);
			if (unit == null) {
				return false;
			}
			
			System.out.print("isTest: ");
			System.out.println((TddClassFactory.get(unit) instanceof TddTestClass));
			return TddClassFactory.get(unit) instanceof TddTestClass;
			
		}
		
		if("hasAssociatedClass".equals(property)) {
			
			ICompilationUnit unit = getUnit(receiver);
			if (unit == null) {
				return false;
			}
			
			TddClass c = TddClassFactory.get(unit);
			if (!(c instanceof TddTestClass)) {
				System.out.println("hasAssociatedClass: false");
				return false;
			}
			
			System.out.print("hasAssociatedClass: ");
			System.out.println((((TddTestClass)c).getSourceClass() != null));			
			return ((TddTestClass)c).getSourceClass() != null;
		}
		
		if("hasAssociatedTest".equals(property)) {
			
			ICompilationUnit unit = getUnit(receiver);
			if (unit == null) {
				return false;
			}
			
			TddClass c = TddClassFactory.get(unit);
			if (!(c instanceof TddSourceClass)) {
				System.out.println("hasAssociatedTest: false");
				return false;
			}
		
			System.out.print("hasAssociatedTest: ");
			System.out.println(((TddSourceClass)c).getTestClass() != null);
			return ((TddSourceClass)c).getTestClass() != null;
		}
		if("hasTddNature".equals(property)) {
			
			IJavaProject project = getProject(receiver);
			if (project == null) {
				return false;
			}

			return TddPluginHandler.getState(project.getProject());
			
		}
		
		return false;
	}
	
	private ICompilationUnit getUnit(Object receiver) {
		
		ICompilationUnit unit = null;
		
		if ((receiver instanceof ICompilationUnit)) {
			unit = (ICompilationUnit)receiver;		
		}
		else if(receiver instanceof TextSelection) {

			IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench == null) return null;

			IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
			IWorkbenchPage page = activeWorkbenchWindow.getActivePage();

			if(page.getActiveEditor() == null || page.getActiveEditor().getEditorInput() == null) {
				return null;
			}
			
			IJavaElement element = JavaUI.getEditorInputJavaElement(page.getActiveEditor().getEditorInput());

			if (element instanceof ICompilationUnit) {
				unit = (ICompilationUnit) element;
			} else {
				return null;
			}
		}
		
		return unit;
	}
	
	private IJavaProject getProject(Object receiver) {
		
		if (receiver instanceof IJavaProject) {
			return (IJavaProject)receiver;
		}
		
		ICompilationUnit unit = getUnit(receiver);
		if (unit != null) {
			return unit.getJavaProject();
		}
		
		return null;
		
	}
	

}

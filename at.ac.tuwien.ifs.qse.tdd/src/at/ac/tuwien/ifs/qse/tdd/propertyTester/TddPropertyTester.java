package at.ac.tuwien.ifs.qse.tdd.propertyTester;

import java.util.Iterator;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import at.ac.tuwien.ifs.qse.tdd.model.*;


public class TddPropertyTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		
		ICompilationUnit unit = null;
		
		if ((receiver instanceof ICompilationUnit)) unit = (ICompilationUnit)receiver;		
		else return false;
		
		if("isClass".equals(property)) {
			
			System.out.print("isClass: ");
			System.out.println((TddClassFactory.get(unit) instanceof TddSourceClass));
			return TddClassFactory.get(unit) instanceof TddSourceClass;
			
		}
		
		if("isTest".equals(property)) {
			
			System.out.print("isTest: ");
			System.out.println((TddClassFactory.get(unit) instanceof TddTestClass));
			return TddClassFactory.get(unit) instanceof TddTestClass;
			
		}
		
		if("hasAssociatedClass".equals(property)) {
			
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
			
			IFile file = (IFile)unit.getResource();
			IProject project = unit.getJavaProject().getProject();

			return TddPluginHandler.getState(project);
			
		}
		
		return false;
	}	

}

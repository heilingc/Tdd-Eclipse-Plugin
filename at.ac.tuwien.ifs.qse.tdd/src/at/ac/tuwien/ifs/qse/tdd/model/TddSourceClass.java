package at.ac.tuwien.ifs.qse.tdd.model;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import at.ac.tuwien.ifs.qse.tdd.finder.TestFinder;

public class TddSourceClass extends TddClass {
	
	private static final long serialVersionUID = -8817804854598636385L;

	public TddSourceClass(ICompilationUnit unit) {
		super(unit);
	}
	
	/**
	 * Return the associated TestClass.
	 * If no TestClass exist null is returned
	 * @return TestClass or null
	 */
	public TddTestClass getTestClass() {
		
		TestFinder finder = new TestFinder(TddTestClass.getPrefix(),TddTestClass.getSuffix());
		try {
			IType type = finder.search(finder.buildTestClassName(unit.getElementName()), unit.getCorrespondingResource().getProject(), TestFinder.SEARCHSCOPE.PROJECT);
			
			if (!(type.getParent() instanceof ICompilationUnit)) {
				return null;
			}
			
			ICompilationUnit u = (ICompilationUnit)type.getParent();
			
			return (TddTestClass)TddClassFactory.get(u);
			
		} catch (Exception e) {
			return null;
		}
		
	}

}

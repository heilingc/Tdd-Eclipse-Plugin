package at.ac.tuwien.ifs.qse.tdd.model;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;

import at.ac.tuwien.ifs.qse.tdd.Activator;
import at.ac.tuwien.ifs.qse.tdd.finder.TestFinder;
import at.ac.tuwien.ifs.qse.tdd.preferences.PreferenceConstants;

public class TddTestClass extends TddClass {
	
	private static final long serialVersionUID = -3499333309229653366L;
	
	public TddTestClass(ICompilationUnit unit) {
		super(unit);
	}
	
	public static String getSuffix() {
		return Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_SUFFIX);
	}
	
	public static String getPrefix() {
		return Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_PREFIX);
	}

	/**
	 * Return the associated SourceClass.
	 * If no SourceClass exists null is returned
	 * @return SourceClass or null
	 */
	public TddSourceClass getSourceClass() {
		
		TestFinder finder = new TestFinder(getPrefix(),getSuffix());
		try {
			IType type = finder.search(finder.buildClassName(unit.getElementName()), unit.getCorrespondingResource().getProject(), TestFinder.SEARCHSCOPE.PROJECT);
			
			if (!(type.getParent() instanceof ICompilationUnit)) {
				return null;
			}
			
			ICompilationUnit u = (ICompilationUnit)type.getParent();

			return (TddSourceClass)TddClassFactory.get(u);
			
		} catch (Exception e) {
			return null;
		}
		
	}	
	

}

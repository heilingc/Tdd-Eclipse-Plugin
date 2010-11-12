package at.ac.tuwien.ifs.qse.tdd.model;

import java.util.HashMap;

import org.eclipse.jdt.core.ICompilationUnit;
import at.ac.tuwien.ifs.qse.tdd.finder.TestFinder;

public class TddClassFactory {

	private static TddClassFactory self = new TddClassFactory();
	private HashMap<ICompilationUnit,TddClass> classMap = new HashMap<ICompilationUnit,TddClass>();
	
	private TddClassFactory() {	
		
	}
	
	public static TddClass get(ICompilationUnit unit) {	
		
		TddClass c = self.classMap.get(unit);
		
		if (c == null) {
			
			TestFinder finder = new TestFinder(TddTestClass.getPrefix(),TddTestClass.getSuffix());
			
			if (TestFinder.FILETYPE.TESTCLASS.equals(finder.getTypeOfSearchName(unit.getElementName()))) {
				c = new TddTestClass(unit);
			}		
			else {
				c = new TddSourceClass(unit);
			}
			
			self.classMap.put(unit, c);
			
		}
		
		return c;
		
	}
	
}

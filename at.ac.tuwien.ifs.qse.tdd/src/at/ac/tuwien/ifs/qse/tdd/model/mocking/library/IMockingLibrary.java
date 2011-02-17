package at.ac.tuwien.ifs.qse.tdd.model.mocking.library;

import at.ac.tuwien.ifs.qse.tdd.model.mocking.MockingConfig;

public interface IMockingLibrary {

	public boolean canMockClasses();
	
	public boolean canHandleAtLeastExpressions();
	
	public boolean canHandleAtLeastOnceExpressions();
	
	public String getCaption();
	
	public String getCode(MockingConfig config);
	
}

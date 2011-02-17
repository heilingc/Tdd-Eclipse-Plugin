package at.ac.tuwien.ifs.qse.tdd.model.mocking.library;

import java.util.ArrayList;
import java.util.List;

public class MockingLibraryRegistration {

	private static List<IMockingLibrary> list;
	
	static {
		list = new ArrayList<IMockingLibrary>();
		list.add(new EasyMockLibrary());
		list.add(new JMockLibrary());
		list.add(new MockitoLibrary());
	}
	
	private MockingLibraryRegistration() {
		
	}
	
	public static List<IMockingLibrary> getInstances() {
		
		return list;
		
	}
}

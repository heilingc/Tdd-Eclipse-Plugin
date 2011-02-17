package at.ac.tuwien.ifs.qse.tdd.model.mocking;

import java.util.ArrayList;
import java.util.List;

public class StubbingConfig {

	private List<Stub> stubs = new ArrayList<Stub>();
	
	public void setStubs(List<Stub> stubs) {
		this.stubs = stubs;
	}
	
	public List<Stub> getStubs() {
		return stubs;
	}
	
	
}

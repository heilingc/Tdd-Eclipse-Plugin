package at.ac.tuwien.ifs.qse.tdd.model.mocking;

import org.eclipse.jdt.core.IMethod;

public class Check {

	private IMethod method;
	private String multiplicity;
	
	public void setMethod(IMethod method) {
		this.method = method;
	}
	
	public IMethod getMethod() {
		return method;
	}
	
	public void setMultiplicity(String multiplicity) {
		this.multiplicity = multiplicity;
	}
	
	public String getMultiplicity() {
		return multiplicity;
	}
	
}

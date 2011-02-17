package at.ac.tuwien.ifs.qse.tdd.model.mocking;

import org.eclipse.jdt.core.IMethod;

public class Stub {

	private IMethod method;
	private StubReturnType returnType = StubReturnType.Null;
	
	public void setMethod(IMethod method) {
		this.method = method;
	}
	
	public IMethod getMethod() {
		return method;
	}
	
	public void setReturnType(StubReturnType returnType) {
		this.returnType = returnType;
	}
	
	public StubReturnType getReturnType() {
		return returnType;
	}
	
}

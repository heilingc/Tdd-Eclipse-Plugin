package at.ac.tuwien.ifs.qse.tdd.model.mocking;

import org.eclipse.jdt.core.IType;

import at.ac.tuwien.ifs.qse.tdd.model.mocking.library.IMockingLibrary;

public class MockingConfig {

	private VerifyConfig verify = new VerifyConfig();
	private StubbingConfig stubbing = new StubbingConfig();
	private IType mockedUnit;
	private String mockName = "mock";
	private IMockingLibrary library;
	
	public void setMockName(String mockName) {
		this.mockName = mockName;
	}
	
	public String getMockName() {
		return mockName;
	}
	
	public void setMockedUnit(IType obj) {
		this.mockedUnit = obj;
	}
	
	public IType getMockedUnit() {
		return mockedUnit;
	}
	
	public StubbingConfig getStubbing() {
		return stubbing;
	}
	
	public VerifyConfig getVerify() {
		return verify;
	}

	public void setLibrary(IMockingLibrary library) {
		this.library = library;
	}

	public IMockingLibrary getLibrary() {
		return library;
	}
	
}

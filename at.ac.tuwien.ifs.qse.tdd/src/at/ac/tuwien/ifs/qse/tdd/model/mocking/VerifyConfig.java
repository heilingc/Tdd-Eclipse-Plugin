package at.ac.tuwien.ifs.qse.tdd.model.mocking;

import java.util.ArrayList;
import java.util.List;

public class VerifyConfig {
	
	private List<Check> checks = new ArrayList<Check>();
	
	public void setChecks(List<Check> checks) {
		this.checks = checks;
	}
	
	public List<Check> getChecks() {
		return checks;
	}

}

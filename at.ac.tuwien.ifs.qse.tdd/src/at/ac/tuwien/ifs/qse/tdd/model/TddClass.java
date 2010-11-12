package at.ac.tuwien.ifs.qse.tdd.model;

import java.io.Serializable;

import org.eclipse.jdt.core.ICompilationUnit;

/**
 * Represent a Class in the TDD Plugin
 */
public abstract class TddClass implements Serializable {

	private static final long serialVersionUID = -6983294411922690660L;	
	
	protected ICompilationUnit unit;
	
	public TddClass(ICompilationUnit unit) {
		this.unit = unit;
	}
	
	public ICompilationUnit getUnit() {
		return unit;
	}
	
	public void setUnit(ICompilationUnit unit) {
		this.unit = unit;
	}

}

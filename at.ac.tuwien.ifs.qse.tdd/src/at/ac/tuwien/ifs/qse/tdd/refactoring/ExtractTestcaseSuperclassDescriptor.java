package at.ac.tuwien.ifs.qse.tdd.refactoring;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

public class ExtractTestcaseSuperclassDescriptor extends RefactoringDescriptor {

	public static final String REFACTORING_ID= "at.ac.tuwien.ifs.qse.tdd.extractTestcaseSuperclass";

	private final Map fArguments;

	public ExtractTestcaseSuperclassDescriptor(String project, String description, String comment, Map arguments) {
		super(REFACTORING_ID, project, description, comment, RefactoringDescriptor.STRUCTURAL_CHANGE | RefactoringDescriptor.MULTI_CHANGE);
		fArguments= arguments;
	}

	@Override
	public Refactoring createRefactoring(RefactoringStatus status) throws CoreException {
		ExtractTestcaseSuperclassRefactoring refactoring= new ExtractTestcaseSuperclassRefactoring();
		status.merge(refactoring.initialize(fArguments));
		return refactoring;
	}

	public Map getArguments() {
		return fArguments;
	}
}
package at.ac.tuwien.ifs.qse.tdd.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;

import at.ac.tuwien.ifs.qse.tdd.exception.SearchException;
import at.ac.tuwien.ifs.qse.tdd.model.TddPluginHandler;

public class EnableTdd extends TddFileHandler {

	@Override
	public void handleException(SearchException exc, String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		final ICompilationUnit unit = getCompilationUnit();
		
		if(unit == null) return null;
		if(!(unit instanceof ICompilationUnit)) return null;
		
		IFile file = (IFile)unit.getResource();
		IProject project = unit.getJavaProject().getProject();

		TddPluginHandler.setState(project, true);
		
		return null;
		
	}

}

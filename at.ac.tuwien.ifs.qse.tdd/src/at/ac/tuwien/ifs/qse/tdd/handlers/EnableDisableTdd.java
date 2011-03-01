package at.ac.tuwien.ifs.qse.tdd.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaProject;

import at.ac.tuwien.ifs.qse.tdd.exception.SearchException;
import at.ac.tuwien.ifs.qse.tdd.model.TddPluginHandler;

public class EnableDisableTdd extends TddFileHandler {

	@Override
	public void handleException(SearchException exc, String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IJavaProject project =  this.getProject();
		
		if (project != null) {
			TddPluginHandler.setState(
					project.getProject(), 
					!TddPluginHandler.getState(project.getProject()));
		}
		
		return null;
		
	}

}

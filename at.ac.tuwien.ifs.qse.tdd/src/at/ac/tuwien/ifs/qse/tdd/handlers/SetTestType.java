package at.ac.tuwien.ifs.qse.tdd.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RadioState;
import at.ac.tuwien.ifs.qse.tdd.exception.SearchException;
import at.ac.tuwien.ifs.qse.tdd.model.TddClassFactory;
import at.ac.tuwien.ifs.qse.tdd.model.TddTestClass;

public class SetTestType extends TddFileHandler {

	@Override
	public void handleException(SearchException exc, String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
	    if(HandlerUtil.matchesRadioState(event))
	        return null; // we are already in the updated state - do nothing
		
		ICompilationUnit unit = getCompilationUnit();
		if (unit == null) return null; //no compilationunit selected
		
		TddTestClass c = (TddTestClass)TddClassFactory.get(unit);
		 
	    String currentState = event.getParameter(RadioState.PARAMETER_ID);
	    System.out.println("currentState: " + currentState);
	    
	    if ("UnitTest".equals(currentState)) c.setType(TddTestClass.TYPE.UNITTEST);
	    else if ("ComponentTest".equals(currentState)) c.setType(TddTestClass.TYPE.COMPONENTTEST);
	    else System.out.println("Unknown Radio State");
	    
	    HandlerUtil.updateRadioState(event.getCommand(), currentState);
	 
	    return null;  
	}

}

package at.ac.tuwien.ifs.qse.tdd.builder;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;

import at.ac.tuwien.ifs.qse.tdd.Activator;
import at.ac.tuwien.ifs.qse.tdd.exception.IHandleException;
import at.ac.tuwien.ifs.qse.tdd.exception.NoTestFound;
import at.ac.tuwien.ifs.qse.tdd.exception.SearchException;
import at.ac.tuwien.ifs.qse.tdd.finder.CoverageBuilderVisitor;
import at.ac.tuwien.ifs.qse.tdd.finder.CoverageExecuter;
import at.ac.tuwien.ifs.qse.tdd.finder.CoverageFullBuilderVisitor;
import at.ac.tuwien.ifs.qse.tdd.finder.TestFinder;
import at.ac.tuwien.ifs.qse.tdd.finder.TestFinder.FILETYPE;
import at.ac.tuwien.ifs.qse.tdd.finder.TestFinder.SEARCHSCOPE;
import at.ac.tuwien.ifs.qse.tdd.model.TddTestClass;
import at.ac.tuwien.ifs.qse.tdd.preferences.PreferenceConstants;


/**
 *	This is the tdd builder which will be called when the project will be build.
 *  The plugin react to incremental build and to full build. </p>  
 */
public class TddBuilder extends IncrementalProjectBuilder implements IHandleException{

	public static final String BUILDER_ID = "at.ac.tuwien.ifs.qse.tdd.tddBuilder";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, @SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor)throws CoreException {
		
		String executeOn = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_EXECUTEON);
		if(executeOn == null)
			return null;

		if(executeOn.equals(PreferenceConstants.P_EXECUTEON_ALL)){
			fullBuild(getProject(), monitor);
		}
		else if(executeOn.equals(PreferenceConstants.P_EXECUTEON_INC)){
			
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {

			} else {
				incrementalBuild(delta, monitor);
			}
			
		}
	
		return null;
	}

	
	/**
	 * Start point of the Tdd Plugin
	 * 	
	 * </p>
	 * <ul>
	 * <li>Find the TestClass</li>
	 * <li>Execute the coverage analyse</li>
	 * </ul> 
	 * @param project contains the changed Resources
	 * @param monitor
	 * @throws CoreException
	 */
	protected void fullBuild(IProject project,IProgressMonitor monitor) throws CoreException {
		
		CoverageFullBuilderVisitor coverageVisitor = new CoverageFullBuilderVisitor();
	    project.accept(coverageVisitor);
	    
	    if (coverageVisitor.getFileList().size() == 0) {
	      return;
	    }

		TestFinder finder = new TestFinder(TddTestClass.getPrefix(),TddTestClass.getSuffix());
	    CoverageExecuter executer = new CoverageExecuter();
	    
	    List<IType> types = new ArrayList<IType>();
	    for (IFile file : coverageVisitor.getFileList()) {
	    	String fileName= null;
	 	    try {
	 	    	fileName = file.getName();
	 	    	
	 	    	if(!finder.getTypeOfSearchName(fileName).equals(FILETYPE.TESTCLASS)){
	 	    		continue;
	 	    	}
	 	    	
	 	    	//Get TestName
	 	    	String testName = testName = file.getName().replace(".java", "");
	 	    	
	 	    	//Get Search Scope
	 	    	String scope = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_SCOPE);
	 	    	
	 	    	//Search the associated TestFile
	 	    	IType type = finder.search(testName, project,SEARCHSCOPE.valueOf(scope));
	 	    	
	 	    	if(type != null) {
	 	    		types.add(type);
	 	    	}
	 	    	
	 	    } catch (SearchException e) {
	 	    	e.printStackTrace();
	 			handleException(e,fileName);
	 			return;
	 		}  
		}
	    //execute the coverage
 	    if(types.size() != 0)	
 	    	executer.executeFileCoverage(types); 	   
	}
		
	/**
	 * Start point of the Tdd Plugin
	 * 	
	 * </p>
	 * <ul>
	 * <li>Find the TestClass</li>
	 * <li>Execute the coverage analyse</li>
	 * </ul> 
	 * @param delta contains the changed Resources
	 * @param monitor
	 * @throws CoreException
	 */
	protected void incrementalBuild(IResourceDelta delta,IProgressMonitor monitor) throws CoreException {
		
		CoverageBuilderVisitor coverageVisitor = new CoverageBuilderVisitor();
	    delta.accept(coverageVisitor);

	    if (coverageVisitor.getFileList().size() == 0) {
	      return;
	    }
	   	        
		TestFinder finder = new TestFinder(TddTestClass.getPrefix(),TddTestClass.getSuffix());
	    CoverageExecuter executer = new CoverageExecuter();
	    
	    List<IType> types = new ArrayList<IType>();
	    for (IFile file : coverageVisitor.getFileList()) {
	    	 String fileName= null;
	 	    try {
	 	    	fileName = file.getName();
	 	    	
	 	    	String testName;
	 	    	
	 	    	if(finder.getTypeOfSearchName(fileName).equals(FILETYPE.TESTCLASS)){
	 	    		testName = file.getName().replace(".java", "");
	 	    	}
	 	    	else {
		 	    	testName = finder.buildTestClassName(file.getName());
	 	    	}
	 	    	
	 	    	//Get Scope
	 	    	String scope = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_SCOPE);
	 	    	
	 	    	//Search the associated TestFile
	 	    	IType type = finder.search(testName, delta.getResource().getProject(),SEARCHSCOPE.valueOf(scope));
	 	    	if(type != null) {
	 	    		types.add(type);
	 	    	}
	 	    	
	 	    		 	    	
	 	    } catch (SearchException e) {
	 			handleException(e,fileName);
	 		}  
		}
	    //execute the coverage
	    if(types.size() == 1)	
 	    	executer.executeFileCoverage(types.get(0)); 	
	    else {
	    	System.err.println("Unexpected: More than one junit test founded by incrementalBuild");
	    }
	}
	
	@Override
	public void handleException(final SearchException exc,final String fileName){
	
	}
	
}

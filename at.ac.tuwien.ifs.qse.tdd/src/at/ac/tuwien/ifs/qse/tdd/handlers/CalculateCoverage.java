package at.ac.tuwien.ifs.qse.tdd.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.dom.CompilationUnit;

import at.ac.tuwien.ifs.qse.tdd.exception.SearchException;
import com.mountainminds.eclemma.core.*;
import com.mountainminds.eclemma.core.launching.CoverageLauncher;
import com.mountainminds.eclemma.core.launching.EclipseLauncher;
import org.eclipse.debug.core.ILaunchConfiguration;

public class CalculateCoverage extends TddFileHandler {

	@Override
	public void handleException(SearchException exc, String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ICompilationUnit unit = getCompilationUnit();
		
		System.out.println("Hallo");
		
		//Search PackageFragmentRoot
		
		CoverageLauncher launcher = new EclipseLauncher();
		ILaunchConfiguration launchConfig;
		
		
		
		
		// IClassFiles classFiles = new ClassFiles(packageFragmentRoot,location);		
		
		//IInstrumentation instrumentation
		//ICoverageSession session = CoverageTools.createCoverageSession("CoverageRun1", instrumentations, coveragedatafiles, launchconfiguration)
		return null;
	}

}

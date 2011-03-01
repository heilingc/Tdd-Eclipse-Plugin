package at.ac.tuwien.ifs.qse.tdd.finder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.mountainminds.eclemma.ui.launching.CoverageLaunchShortcut;

/**
 * The coverage executer launch the coverage analysis. He access the api of eclEmma. 
 */
public class CoverageExecuter {

	public static final String SHORT_CUT = "org.eclipse.jdt.junit.junitShortcut";
	public static final String LAUNCH_MODE = "coverage";

	/**
	 * Starts coverage for the IType.
	 * @param lwType should include a class file which is a Junit test.
	 */
	public void executeFileCoverage(final List<IType> units) {

		if(units == null || units.size() == 0) {
			return;
		}
		Display.getDefault().asyncExec(new Runnable() {

				public void run() {
					
					
					try {
						CoverageLaunchShortcut launchShortcut = new CoverageLaunchShortcut();
						launchShortcut.setInitializationData(null, null,SHORT_CUT);
						
						HashSet<IPackageFragmentRoot> sourcefolders = new HashSet<IPackageFragmentRoot>();
						
						//Collect Sourcefolders out of types
						for (IType unit : units) {
							IPackageFragmentRoot sourcefolder = getPackageFragmentRoot(unit);
							
							if (sourcefolder != null) {
								sourcefolders.add(sourcefolder);
							}
						}
						
						List<IPackageFragmentRoot> sourcefolderslist = new ArrayList<IPackageFragmentRoot>(sourcefolders);
						
						launchShortcut.launch(new StructuredSelection(sourcefolderslist), LAUNCH_MODE); //$NON-NLS-1$
						
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} //$NON-NLS-1$
					
					
					/*
					try {
						
					
					CoverageLaunchShortcut launchShortcut = new CoverageLaunchShortcut();
					//Define which shortcut should be used
					launchShortcut.setInitializationData(null, null,SHORT_CUT); //$NON-NLS-1$

					for (IType type : types) {
					ICompilationUnit lwCompilationUnit = type.getCompilationUnit();
					if(lwCompilationUnit != null)
					//Launch the coverage
					launchShortcut.launch(new StructuredSelection(lwCompilationUnit), LAUNCH_MODE); //$NON-NLS-1$
					}

					} catch (CoreException ex) {

					}
					*/
				}

				private IPackageFragmentRoot getPackageFragmentRoot(
						IJavaElement element) {
					
					if (element == null) {
						return null;
					}
					
					if (element instanceof IPackageFragmentRoot) {
						return (IPackageFragmentRoot) element;
					}
					
					return getPackageFragmentRoot(element.getParent());
					
				}
		}); 

	}



	/**
	 * Starts coverage for the IType.
	 * @param lwType should include a class file which is a Junit test.
	 */
	public void executeFileCoverage(final IType type) {

		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				try {
					ICompilationUnit lwCompilationUnit = type.getCompilationUnit();

					CoverageLaunchShortcut launchShortcut = new CoverageLaunchShortcut();
					//Define which shortcut should be used
					launchShortcut.setInitializationData(null, null,SHORT_CUT); //$NON-NLS-1$

					ISelection selection = new StructuredSelection(lwCompilationUnit);

					//Launch the coverage
					launchShortcut.launch(selection, LAUNCH_MODE); //$NON-NLS-1$
				} catch (CoreException ex) {

				} 
			}
		}); 

	}

	/**
	 * Start the coverage for the currently selected Editor
	 */
	public void executeEditorCoverage() {


		Display.getDefault().asyncExec(new Runnable() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				IWorkbenchWindow iWorkbenchWindow = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
				if (iWorkbenchWindow == null) {
					return;
				}
				IWorkbenchPage iworkbenchpage = iWorkbenchWindow.getActivePage();
				if (iworkbenchpage == null) {
					return;
				}
				IEditorPart iEditorpart = iworkbenchpage.getActiveEditor();

				CoverageLaunchShortcut launchShortcut = new CoverageLaunchShortcut();
				try {
					launchShortcut.setInitializationData(null, null, "org.eclipse.jdt.junit.junitShortcut"); //$NON-NLS-1$
				} catch (CoreException e) {

				}
				launchShortcut.launch(iEditorpart, "coverage");

			}
		});

	}

}

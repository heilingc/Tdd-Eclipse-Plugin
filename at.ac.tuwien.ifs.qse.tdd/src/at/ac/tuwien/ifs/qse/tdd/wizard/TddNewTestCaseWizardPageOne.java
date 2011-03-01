package at.ac.tuwien.ifs.qse.tdd.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageOne;
import org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageTwo;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;

import at.ac.tuwien.ifs.qse.tdd.Activator;
import at.ac.tuwien.ifs.qse.tdd.preferences.PreferenceConstants;

public class TddNewTestCaseWizardPageOne extends NewTestCaseWizardPageOne {

	private String typeName;
	private IJavaProject project;
	
	public TddNewTestCaseWizardPageOne(NewTestCaseWizardPageTwo page2,String typeName, IJavaProject project) {
		super(page2);
		
		this.project = project;
		this.typeName = typeName;
		
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		
		//Set Typename
		this.setTypeName(typeName, true);
		
		//Set PackageFragmentRoot aka Sourcefolder
		IPackageFragmentRoot packageFragment = findPackageFragmentRoot();
		
		if (packageFragment != null) {
			this.setPackageFragmentRoot(packageFragment, true);
		}
	}
	
	private IPackageFragmentRoot findPackageFragmentRoot() {
		
		//Load default value of packagefragmentroot
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	    String defaultPackageFragmentString = store.getString(PreferenceConstants.P_DEFAULT_TEST_SOURCE_FOLDER);
		
		//Search this packagefragmentroot
		try {
			for (IPackageFragmentRoot packageFragment : project.getPackageFragmentRoots()) {
				String packageFragmentString = packageFragment.getElementName();
				if (packageFragment.getElementName().equals(defaultPackageFragmentString)) {
					
					return packageFragment;
					
				}
			}
			
			return null;
		} catch (JavaModelException e) {
			return null;
		}
	}
	
	
	
}

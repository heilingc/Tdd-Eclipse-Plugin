package at.ac.tuwien.ifs.qse.tdd.finder;



import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;

import at.ac.tuwien.ifs.qse.tdd.Activator;
import at.ac.tuwien.ifs.qse.tdd.exception.IllegalProjectType;
import at.ac.tuwien.ifs.qse.tdd.exception.MoreThanOneTestFound;
import at.ac.tuwien.ifs.qse.tdd.exception.NoSettingsFound;
import at.ac.tuwien.ifs.qse.tdd.exception.NoTestFound;
import at.ac.tuwien.ifs.qse.tdd.exception.SearchException;
import at.ac.tuwien.ifs.qse.tdd.preferences.PreferenceConstants;


/**
 * 
 * This class contains the logic to associate the JUnitTestFile to a specific class name. 
 */
public class TestFinder {

	public enum FILETYPE{
		TESTCLASS,CLASS;
	}
	/**
	 * Identify the type of the searchName with the suffix and prefix values
	 * @param name
	 * @return
	 */
	public FILETYPE getTypeOfSearchName(String searchName){
		//Load the preferences
		String prefix = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_PREFIX);
		String suffix = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_SUFFIX);
								
		String name = searchName.replace(".java", "");
		
		if(name.matches(prefix+ ".+" + suffix)) {
			return FILETYPE.TESTCLASS;
		}
		
		return FILETYPE.CLASS;
	}
	
	
	/**
	 * Search for a class name in the specifc project
	 * 
	 * @param className 
	 * @param project the selected search scope
	 * @return the found ClassFile
	 * @throws SearchException 
	 */
	public IType search(String className, final IProject project) throws SearchException {

		SearchPattern pattern = SearchPattern.createPattern(className, IJavaSearchConstants.CLASS,
				IJavaSearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
		
		SearchEngine searchEngine = new SearchEngine();

		IJavaProject jProject = null;
		IProjectNature nature = null;
		IJavaElement[] elements = null;
		IJavaSearchScope scope = null;
		
		String scopePref = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_SCOPE);
		if(scopePref == null)
			throw new NoSettingsFound("No settings found for the scope!");
		
		
		if(scopePref.equals(PreferenceConstants.P_SCOPE_PROJECT)) {
			//Extract IJavaProject
			try {
				nature = project.getNature(JavaCore.NATURE_ID);
			} catch (CoreException e) {
				throw new SearchException(e.getMessage());
			}
			if (nature instanceof IJavaProject) {
				jProject = (IJavaProject) nature;
			} else {
				throw new IllegalProjectType("Error: JProject is not a IJavaProject"); //$NON-NLS-1$
			}
			//Define search Scope
			elements = new IJavaElement[]{ jProject };
			scope = SearchEngine.createJavaSearchScope(elements, true);
		} else if(scopePref.equals(PreferenceConstants.P_SCOPE_WORKSPACE)){
			scope = SearchEngine.createWorkspaceScope();
		} else {
			throw new SearchException("No Scope set!");
		}
				
		//Collector of the search results
		FileSearchRequestor requestor = new FileSearchRequestor();
		
		try {
			SearchParticipant[] parts = { SearchEngine.getDefaultSearchParticipant() };
			searchEngine.search(pattern, parts, scope, requestor, null);

			if (requestor.getTypeList().size() == 0) {
				throw new NoTestFound("Error: " + requestor.getTypeList().size() + " results found!"); //$NON-NLS-1$
			} else if (requestor.getTypeList().size() > 1) {
				throw new MoreThanOneTestFound("Error: " + requestor.getTypeList().size() + " results found!"); //$NON-NLS-1$
			}

			return (IType) requestor.getTypeList().get(0);

		} catch (JavaModelException e) {
			throw new SearchException(e.getMessage());
		} catch (CoreException e) {
			throw new SearchException(e.getMessage());
		}
	}

	/**
	 * Transform the ClassName to the TestClassName 
	 * @param searchName className
	 * @return the name of the TestClassName
	 */
	public String buildTestClassName(String searchName) {
		//Load the preferences
		String prefix = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_PREFIX);
		String suffix = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_SUFFIX);
								
		String name = searchName.replace(".java", "");
		
		return prefix + name + suffix;
	}
	
	
	/**
	 * Transform the TestClassName to the ClassName 
	 * @param searchName className
	 * @return the name of the TestClassName
	 */
	public String buildClassName(String searchName){
		//Load the preferences
		String prefix = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_PREFIX);
		String suffix = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_SUFFIX);
								
		String name = searchName.replace(".java", "");
		
		if(prefix.length() != 0) {
			name = name.substring(prefix.length(), name.length());
		}
		
		if(suffix.length() != 0) {
			name = name.substring(0, name.length() - 1 - suffix.length());
		}
		
		return name;
	}
	
}

package at.ac.tuwien.ifs.qse.tdd.model;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.menus.UIElement;

import at.ac.tuwien.ifs.qse.tdd.builder.TddNature;
import at.ac.tuwien.ifs.qse.tdd.exception.SearchException;


/**
 * Handler for tddPluginEnableCommand.</p>
 * Check if the Tdd nature is enable for the selected project. Set or remove the Nature 
 * from the selected project.
 * 
 */
public class TddPluginHandler{
	
	/**
	 * Check if the nature Tdd nature is set for the project 
	 * @param project IProject
	 * @return true Nature active/false Nature inactive
	 */
	public static boolean getState(IProject project){
		try {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();

			for (int i = 0; i < natures.length; ++i) {
				if (TddNature.NATURE_ID.equals(natures[i])) {
					return true;
				}
			}

		} catch (CoreException e) {
		}
	
		return false;
	}
	
	/**
	 * Set/Remove the Tdd nature from the project
	 * @param project
	 * @param add
	 */
	public static void setState(IProject project, boolean add) {
		try {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();

			for (int i = 0; i < natures.length; ++i) {
				if (TddNature.NATURE_ID.equals(natures[i])) {
					if(add) {
						return;
					} else {
						// Remove the nature
						String[] newNatures = new String[natures.length - 1];
						System.arraycopy(natures, 0, newNatures, 0, i);
						System.arraycopy(natures, i + 1, newNatures, i,
								natures.length - i - 1);
						description.setNatureIds(newNatures);
						project.setDescription(description, null);
						return;
					}
					
				}
			}
			
			if(!add)
				return;

			// Add the nature
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = TddNature.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
		} catch (CoreException e) {
		}
	}	
	
}

package at.ac.tuwien.ifs.qse.tdd.wizard;

import org.eclipse.jface.wizard.Wizard;

import at.ac.tuwien.ifs.qse.tdd.model.mocking.MockingConfig;
import at.ac.tuwien.ifs.qse.tdd.model.mocking.library.IMockingLibrary;

public class CreateMockWizard extends Wizard{

	private MockingConfig config;
	
	public CreateMockWizard() {
		super();
		config = new MockingConfig();
		this.setHelpAvailable(false);
		this.setNeedsProgressMonitor(false);
		this.setWindowTitle("Create Mock");
	}
	
	@Override
	public void addPages() {
		this.addPage(new CreateMockWizardPageOne(config));
		this.addPage(new CreateMockWizardPageTwo(config));
		this.addPage(new CreateMockWizardPageThree(config));
		this.addPage(new CreateMockWizardPageFour(config));
	}
	
	public boolean canFinish() {
		
		return 
			config.getMockedUnit() != null &&
			config.getMockName() != null &&
			config.getLibrary() != null;
		
	}
	
	@Override
	public boolean performFinish() {
		
		try {
			IMockingLibrary lib = this.config.getLibrary();
		
			ShowMockDialog dialog = new ShowMockDialog(getShell(), "Mock", lib.getCode(config));
			dialog.create();
			dialog.open();
			
		} catch (Exception e) {

			e.printStackTrace();
		}
		return true;
	}
	
}

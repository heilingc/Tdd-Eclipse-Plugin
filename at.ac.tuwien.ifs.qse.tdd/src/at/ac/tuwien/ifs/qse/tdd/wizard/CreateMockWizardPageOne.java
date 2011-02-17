package at.ac.tuwien.ifs.qse.tdd.wizard;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import at.ac.tuwien.ifs.qse.tdd.model.mocking.MockingConfig;
import at.ac.tuwien.ifs.qse.tdd.model.mocking.library.IMockingLibrary;
import at.ac.tuwien.ifs.qse.tdd.model.mocking.library.MockingLibraryRegistration;

public class CreateMockWizardPageOne extends WizardPage {

	private MockingConfig config;
	private HashMap<Button,IMockingLibrary> buttonFrameworkMatcher = new HashMap<Button,IMockingLibrary>();
	
	public CreateMockWizardPageOne(MockingConfig config) {
		super("Select Mocking Framework");
		this.config = config;
		this.setTitle("Select Mocking Framework");
	}

	private Button createFrameworkRadioButton(Composite parent, IMockingLibrary lib) {
		Button button = new Button(parent, SWT.RADIO);
		button.setText(lib.getCaption());
		
		if (lib.equals(config.getLibrary())) {
			button.setSelection(true);
		}
		
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				setLibrary(e);
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				setLibrary(e);
			}
			
		});
		
		return button;
	}
	
	@Override
	public void createControl(Composite parent) {
		
		Composite control = new Composite(parent, SWT.NONE);
		this.setControl(control);

		RowLayout rowLayout = new RowLayout(SWT.NONE);
		rowLayout.marginHeight = 5;
		rowLayout.marginWidth = 5;
		rowLayout.spacing = 1;
		
		control.setLayout(rowLayout);
		
		List<IMockingLibrary> libs = MockingLibraryRegistration.getInstances();
		for (IMockingLibrary lib : libs) {
			buttonFrameworkMatcher.put(createFrameworkRadioButton(control, lib),lib);
		}
		
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return (config.getLibrary() != null);
	}
	
	private void setLibrary(SelectionEvent e) {
		IMockingLibrary lib = buttonFrameworkMatcher.get(((Button)e.getSource()));
		config.setLibrary(lib);
		getWizard().getContainer().updateButtons();
	}
	
}

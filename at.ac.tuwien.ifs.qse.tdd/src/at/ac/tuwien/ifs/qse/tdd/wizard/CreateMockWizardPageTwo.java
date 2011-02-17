package at.ac.tuwien.ifs.qse.tdd.wizard;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;

import at.ac.tuwien.ifs.qse.tdd.model.mocking.MockingConfig;

public class CreateMockWizardPageTwo extends WizardPage {

	private MockingConfig config;
	private Text mockedUnit;
	private Button mockedUnitButton;
	private Text mockName;
	
	public CreateMockWizardPageTwo(MockingConfig config) {
		super("Select Mocking Framework");
		this.config = config;
		this.setTitle("General Settings");
	}

	public static GridData createLabelGridData() {
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.BEGINNING;
		gd.verticalAlignment = SWT.BEGINNING;
		gd.widthHint = 150;
		gd.horizontalSpan = 1;
		return gd;
	}
	
	public static GridData createSpanningTextGridData() {
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.BEGINNING;
		gd.horizontalSpan = 2;
		gd.grabExcessHorizontalSpace = true;
		gd.widthHint = 300;
		return gd;
	}
	
	public static GridData createNormalTextGridData() {
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.BEGINNING;
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		gd.widthHint = 200;
		return gd;
	}	
	
	public static GridData createBrowseGridData() {
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.BEGINNING;
		gd.horizontalSpan = 1;
		gd.widthHint = 100;
		return gd;
	}
	
	@Override
	public void createControl(Composite parent) {
		
		Composite control = new Composite(parent, SWT.NULL);
		this.setControl(control);
		
		GridLayout layout= new GridLayout();
		layout.numColumns= 3;
		control.setLayout(layout);
		
		
		
		Label mockNameLabel = new Label(control, SWT.NULL);
		mockNameLabel.setText("Mock Name");
		mockNameLabel.setLayoutData(createLabelGridData());
		
		mockName = new Text(control, SWT.FILL | SWT.BORDER);
		mockName.setLayoutData(createSpanningTextGridData());
		
		
		
		Label mockedUnitLabel = new Label(control, SWT.NULL);
		mockedUnitLabel.setText("Mocked Type");
		mockedUnitLabel.setLayoutData(createLabelGridData());
		
		mockedUnit = new Text(control, SWT.FILL | SWT.BORDER | SWT.READ_ONLY);
		mockedUnit.setLayoutData(createNormalTextGridData());
		
		mockedUnitButton = new Button(control, SWT.FILL | SWT.PUSH);
		mockedUnitButton.setText("Browse ...");
		mockedUnitButton.setLayoutData(createBrowseGridData());
		
		if (config.getMockName() != null) {
			mockName.setText(config.getMockName());
		}
		
		if (config.getMockedUnit() != null) {
			mockedUnit.setText(config.getMockedUnit().getFullyQualifiedName());
		}
		
		addListener();
	}

	private void addListener() {
		
		mockedUnitButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				openTypeBrowser();
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				openTypeBrowser();
			}
			
		});
		
		mockName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				saveMockName();			
			}
			
		});
		
	}
	
	private void openTypeBrowser() {
		
		IJavaSearchScope scope = SearchEngine.createWorkspaceScope(); 
		
		int style;
		if (config.getLibrary().canMockClasses()) {
			style = IJavaElementSearchConstants.CONSIDER_CLASSES_AND_INTERFACES;
		}
		else {
			style = IJavaElementSearchConstants.CONSIDER_INTERFACES;
		}
		
		
		try {
			SelectionDialog typeDialog = JavaUI.createTypeDialog(this.getShell(), null, scope, style, false);
			typeDialog.create();
			typeDialog.open();
			
			if (typeDialog.getReturnCode() == SelectionDialog.OK) {
				IType type = (IType)typeDialog.getResult()[0];
				
				config.setMockedUnit(type);
				
				config.getStubbing().getStubs().clear();
				config.getVerify().getChecks().clear();
				((IRefreshable)this.getWizard().getPages()[2]).refresh();
				((IRefreshable)this.getWizard().getPages()[3]).refresh();
				
				this.mockedUnit.setText(type.getFullyQualifiedName());
				this.getWizard().getContainer().updateButtons();
			}
		} catch (JavaModelException e) {
			
		}
		
	}
	
	private void saveMockName() {
		
		String newMockName = mockName.getText();
		
		if (newMockName.matches("[a-z][A-Za-z0-9]*")) {
			
			config.setMockName(newMockName);
			this.setErrorMessage(null);
			this.getWizard().getContainer().updateButtons();
			
		}
		else {
			
			config.setMockName(null);
			this.setErrorMessage("The Mock Name have to match following pattern [a-z][A-Za-z0-9]*");
			this.getWizard().getContainer().updateButtons();
			
		}
		
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return (config.getMockName() != null &&
				config.getMockedUnit() != null);
	}
	
}

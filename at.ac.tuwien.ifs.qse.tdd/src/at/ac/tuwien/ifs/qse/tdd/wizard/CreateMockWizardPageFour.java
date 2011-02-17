package at.ac.tuwien.ifs.qse.tdd.wizard;

import java.util.ArrayList;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;

import at.ac.tuwien.ifs.qse.tdd.model.mocking.Check;
import at.ac.tuwien.ifs.qse.tdd.model.mocking.MockingConfig;
import at.ac.tuwien.ifs.qse.tdd.model.mocking.Stub;
import at.ac.tuwien.ifs.qse.tdd.model.mocking.library.IMockingLibrary;

public class CreateMockWizardPageFour extends WizardPage implements IRefreshable {

	private final MockingConfig config;
	private TableViewer table;
	private Button addButton;
	private Button removeButton;
	private Composite control;
	
	public CreateMockWizardPageFour(MockingConfig config) {
		super("Select Mocking Framework");
		this.config = config;
		this.setTitle("Verify Settings");
		this.setDescription("Which Method Calls should be checked by the Mock?");
	}
	
	private static GridData createNormalTextGridData() {
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.BEGINNING;
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		gd.widthHint = 200;
		return gd;
	}
	
	private static GridData createTableCompositeGridData() {
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.horizontalSpan = 2;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.widthHint = 350;
		return gd;
	}
	
	private static GridData createButtonCompositeGridData() {
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.BEGINNING;
		gd.widthHint = 80;
		return gd;
	}

	
	private static GridData createButtonGridData() {
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.BEGINNING;
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		return gd;
	}

	private static GridData createTableGridData() {
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.widthHint = 270;
		return gd;
	}
	
	private static GridData createSeperatorGridData() {
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.BEGINNING;
		gd.horizontalSpan = 2;
		gd.grabExcessHorizontalSpace = true;
		gd.widthHint = 350;
		return gd;
	}
	
	
	
	@Override
	public void createControl(Composite parent) {
		
		control = new Composite(parent, SWT.NONE);
		this.setControl(control);
		
		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		control.setLayout(layout);
				
		Composite tableComposite = new Composite(control, SWT.NONE);
		tableComposite.setLayoutData(createTableCompositeGridData());

		layout = new GridLayout();
		layout.numColumns= 2;		
		tableComposite.setLayout(layout);
		
		table = new TableViewer(tableComposite,SWT.FILL | SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		table.getTable().setLayoutData(createTableGridData());
		table.getTable().setHeaderVisible(true);
		table.getTable().setLinesVisible(true);
		table.setContentProvider(ArrayContentProvider.getInstance());
		
		TableViewerColumn methodColumn = new TableViewerColumn(table, SWT.NONE);
		methodColumn.getColumn().setText("Method");
		methodColumn.getColumn().setWidth(100);
		methodColumn.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				
				Check check = (Check)(cell.getElement());
				
				JavaElementLabelProvider javaElementProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_OVERLAY_ICONS);
				String text = javaElementProvider.getText(check.getMethod());
				cell.setText(text);
			}
			
		});
		
		TableViewerColumn returnColumn = new TableViewerColumn(table, SWT.NONE);
		returnColumn.getColumn().setText("Multiplicity");
		returnColumn.getColumn().setWidth(200);
		returnColumn.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				
				Check check = (Check)(cell.getElement());
				
				cell.setText(check.getMultiplicity());
			}
			
		});
		
		returnColumn.setEditingSupport(new EditingSupport(table) {

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				TextCellEditor editor = new TextCellEditor(table.getTable());
				return editor;
				
			}

			@Override
			protected Object getValue(Object element) {
				Check check = (Check)element;
				
				return check.getMultiplicity();
			}

			@Override
			protected void setValue(Object element, Object value) {
				Check check = (Check)element;
				
				//validate input
				String multiplicity = (String)value;
				
				if (!multiplicity.matches("[0-9]+\\.\\.[0-9]+") &&
					!multiplicity.matches("[0-9]+\\.\\.\\*")) {
					CreateMockWizardPageFour.this.setErrorMessage("Only the format [0-9]+..[0-9]+ or [0-9]+..* for the min-max Notation allowed");
					return;
				}
				
				String[] splitted = multiplicity.split("\\.\\.");
				
				String min = splitted[0];
				String max = splitted[1];
				
				if ((min.length() > 1 && min.charAt(0) == '0') ||
					(max.length() > 1 && max.charAt(0) == '0')) {
					CreateMockWizardPageFour.this.setErrorMessage("No leadings Zeros for the min-max Notation allowed");
					return;
				}
				
				if (!max.equals("*")) {
					if (Integer.valueOf(min) > Integer.valueOf(max)) {
						CreateMockWizardPageFour.this.setErrorMessage("min is greater than max in min-max Notation");
						return;						
					}
				}
				
				IMockingLibrary lib = CreateMockWizardPageFour.this.config.getLibrary();
				
				if (!lib.canHandleAtLeastExpressions()) {				
					if (lib.canHandleAtLeastOnceExpressions()) {
						if (max.equals("*") && !min.equals("1")) {
							CreateMockWizardPageFour.this.setErrorMessage("Selected Library do not support stars except \"1..*\" in min-max Notation");
							return;	
						}
					}
					else {
						if (max.equals("*")) {
							CreateMockWizardPageFour.this.setErrorMessage("Selected Library do not support stars in min-max Notation");
							return;	
						}
					}
				}						
				
				check.setMultiplicity(multiplicity);
				CreateMockWizardPageFour.this.setErrorMessage(null);
				
				table.refresh(element);
				
			}
			
		});
		
		Composite buttonComposite = new Composite(tableComposite, SWT.NONE);
		buttonComposite.setLayoutData(createButtonCompositeGridData());
		
		layout = new GridLayout();
		layout.numColumns= 1;		
		buttonComposite.setLayout(layout);
		
		addButton = new Button(buttonComposite,SWT.FILL | SWT.PUSH);
		addButton.setText("Add ...");
		addButton.setLayoutData(createButtonGridData());
		
		removeButton = new Button(buttonComposite,SWT.FILL | SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setLayoutData(createButtonGridData());
		removeButton.setEnabled(false);
		
		refresh();
		addListener();
		
	}

	private void addListener() {
		
		addButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				openMethodSelection();
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				openMethodSelection();
			}
			
		});
		
		removeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				removeSelectedTableItem();
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				removeSelectedTableItem();
			}
			
		});
		
		table.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (table.getTable().getSelectionIndex() != -1) {
					
					int index = table.getTable().getSelectionIndex();
					
					Check check = (Check)table.getElementAt(index);
					IMethod method = check.getMethod();
					
					for (Stub stub : config.getStubbing().getStubs()) {
					
						if (!stub.getMethod().equals(method)) {
							removeButton.setEnabled(false);
							return;
						}
					
					}
					
					removeButton.setEnabled(true);
				}
			}
			
		});
		
	}
	
	private void removeSelectedTableItem() {
		if (table.getTable().getSelectionIndex() != -1) {
			int index = table.getTable().getSelectionIndex();
			
			Object obj = table.getElementAt(index);
			
			table.remove(obj);
			config.getVerify().getChecks().remove(obj);
			
			table.refresh();
			
			removeButton.setEnabled(false);
		}
	}
	
	private void openMethodSelection() {
		
		try {
			SelectionDialog methodSelectionDialog = new ListSelectionDialog(
				this.getShell(), 
				calcuteMethods(), 
				new ArrayContentProvider(), 
				new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_OVERLAY_ICONS), 
				"Select Methods");
			
			methodSelectionDialog.create();
			methodSelectionDialog.open();
			
			if (methodSelectionDialog.getReturnCode() == SelectionDialog.OK) {
				
				for (Object obj : methodSelectionDialog.getResult()) {
					IMethod method = (IMethod)obj;
					Check newCheck = new Check();
					newCheck.setMethod(method);
					newCheck.setMultiplicity("1..1");
					
					table.add(newCheck);
					config.getVerify().getChecks().add(newCheck);
				}

			}
			
			
		} catch (JavaModelException e) {
			
			this.setErrorMessage("Unable to extract Methods out of Mocked Type");
			
		}
		
			
	}
	
	private Object calcuteMethods() throws JavaModelException {
		
		IMethod[] allMethods = this.config.getMockedUnit().getMethods();
		
		java.util.List<IMethod> oldMethods = new ArrayList<IMethod>();
		for (Check check : config.getVerify().getChecks()) {
			oldMethods.add(check.getMethod());
		}
		
		java.util.List<IMethod> newMethods = new ArrayList<IMethod>();
		for (IMethod method : allMethods) {
			
			if (!method.isConstructor() && 
				Flags.isPublic(method.getFlags()) &&
				!Flags.isStatic(method.getFlags()) &&
				!oldMethods.contains(method)) {
				newMethods.add(method);
			}
		}
		
		return newMethods.toArray();
		
		
		
	}

	public void refresh() {
		
		table.setInput(config.getVerify().getChecks());
		table.refresh();
		
	}

}

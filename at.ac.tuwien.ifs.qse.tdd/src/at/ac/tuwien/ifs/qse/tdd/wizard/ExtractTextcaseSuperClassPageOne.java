package at.ac.tuwien.ifs.qse.tdd.wizard;

import java.util.ArrayList;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.ui.dialogs.SelectionDialog;

import at.ac.tuwien.ifs.qse.tdd.refactoring.ExtractTestcaseSuperclassRefactoring;

@SuppressWarnings("restriction")
public class ExtractTextcaseSuperClassPageOne extends UserInputWizardPage implements ModifyListener, ICheckStateListener, SelectionListener {
	
	private Text txtParentClassName;
	private Text txtParentPackageName;
	private Button btnParentPackageNameBrowse;
	
	private Text txtChildClassName;
	private Text txtChildPackageName;
	private Button btnChildPackageNameBrowse;
	
	private CheckboxTableViewer lstParentClassMethods;
	
	private ICompilationUnit childClass;
	private Composite composite;
	
	
	public ExtractTextcaseSuperClassPageOne(String name, ICompilationUnit childClass) {
		super(name);
		this.childClass = childClass;
	}

	public static GridData createLabelGridData() {
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.BEGINNING;
		gd.verticalAlignment = SWT.BEGINNING;
		gd.widthHint = 150;
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
	
	public static GridData createCheckboxTableGridData() {
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.BEGINNING;
		gd.horizontalSpan = 2;
		gd.grabExcessHorizontalSpace = true;
		gd.widthHint = 300;
		return gd;
	}
	
	public static GridData createSeperatorGridData() {
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.BEGINNING;
		gd.widthHint = 450;
		gd.horizontalSpan = 3;
		return gd;
	}
	
	@Override
	public void createControl(Composite parent) {
		
		Label label;
		composite = parent;
		
		Composite result= new Composite(parent, SWT.NONE);

		setControl(result);
		
		GridLayout layout= new GridLayout();
		layout.numColumns= 3;
		result.setLayout(layout);

		label = new Label(result, SWT.NONE);
		label.setText("Superclass package:");
		label.setLayoutData(createLabelGridData());
		
		txtParentPackageName = new Text(result, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		txtParentPackageName.setLayoutData(createNormalTextGridData());
		txtParentPackageName.addModifyListener(this);
		
		btnParentPackageNameBrowse = new Button(result, SWT.PUSH);
		btnParentPackageNameBrowse.setLayoutData(createBrowseGridData());
		btnParentPackageNameBrowse.setText("Browse ...");
		btnParentPackageNameBrowse.addSelectionListener(this);
		
		label= new Label(result, SWT.NONE);
		label.setText("Superclass name:");
		label.setLayoutData(createLabelGridData());
		
		txtParentClassName = new Text(result, SWT.SINGLE | SWT.LEFT | SWT.BORDER);	
		txtParentClassName.setLayoutData(createSpanningTextGridData());
		txtParentClassName.addModifyListener(this);

		Label seperator;
		
		seperator = new Label(result, SWT.SEPARATOR | SWT.HORIZONTAL);
		seperator.setLayoutData(createSeperatorGridData());
		
		label = new Label(result, SWT.NONE);
		label.setText("Testclass package:");
		label.setLayoutData(createLabelGridData());
		
		txtChildPackageName = new Text(result, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		txtChildPackageName.setLayoutData(createNormalTextGridData());
		txtChildPackageName.addModifyListener(this);

		btnChildPackageNameBrowse = new Button(result, SWT.PUSH);
		btnChildPackageNameBrowse.setLayoutData(createBrowseGridData());
		btnChildPackageNameBrowse.setText("Browse ...");
		btnChildPackageNameBrowse.addSelectionListener(this);
		
		label= new Label(result, SWT.NONE);
		label.setText("&Testclass name:");
		label.setLayoutData(createLabelGridData());
		
		txtChildClassName = new Text(result, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		txtChildClassName.setLayoutData(createSpanningTextGridData());
		txtChildClassName.addModifyListener(this);
		
		seperator = new Label(result, SWT.SEPARATOR | SWT.HORIZONTAL);
		seperator.setLayoutData(createSeperatorGridData());		
		
		label = new Label(result, SWT.TOP);
		label.setText("Superclass methods:");
		label.setLayoutData(createLabelGridData());
		
		lstParentClassMethods = CheckboxTableViewer.newCheckList(result, SWT.MULTI | SWT.LEFT | SWT.BORDER);
		lstParentClassMethods.setContentProvider(new ArrayContentProvider());
		lstParentClassMethods.setLabelProvider(new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_OVERLAY_ICONS));
		lstParentClassMethods.setInput(calculateMethods(childClass));	
		lstParentClassMethods.addCheckStateListener(this);		
		lstParentClassMethods.setCheckedElements(calculateSuggestedMethodsSelection(childClass).toArray());
		
		lstParentClassMethods.getTable().setLayoutData(createCheckboxTableGridData());
		
		txtParentPackageName.setText(calculateSuggestedParentPackageName(childClass));		
		txtParentClassName.setText(calculateSuggestedParentClassName(childClass));
		txtChildPackageName.setText(calculateSuggestedChildPackageName(childClass));
		txtChildClassName.setText(calculateSuggestedChildClassName(childClass));
		
		
		saveChanges();
		
	}

	private String calculateSuggestedParentPackageName(
			ICompilationUnit childClass) {
		
		String a = childClass.getParent().getElementName();
		return a;
		
	}
	
	private String calculateSuggestedChildPackageName(
			ICompilationUnit childClass) {
		
		return childClass.getParent().getElementName();
		
	}

	private String calculateSuggestedParentClassName(ICompilationUnit childClass) {
	
		String childName = childClass.getElementName();
		
		String suffix = childName.substring(4);
		suffix = suffix.substring(0,suffix.length() - 5);
		
		return "TestAbstract" + suffix;
		
	}
	
	private String calculateSuggestedChildClassName(ICompilationUnit childClass) {
	
		return childClass.getElementName().substring(0, childClass.getElementName().length() - 5);
		
	}
	
	private IMethod[] calculateMethods(ICompilationUnit childClass) {
			
			try {
				IMethod[] methods = childClass.getTypes()[0].getMethods();
			
				java.util.List<IJavaElement> filteredMethods = new ArrayList<IJavaElement>();
				for (IMethod method : methods) {
					if (!method.isConstructor()) {
						filteredMethods.add(method);
					}
				}
				
				return filteredMethods.toArray(new IMethod[0]);
			} catch (JavaModelException e) {
				return null;
			}		
        
	}
	
	private IStructuredSelection calculateSuggestedMethodsSelection(ICompilationUnit childClass) {
		

		java.util.List<IMethod> selectedMethods = new ArrayList<IMethod>();

		IMethod[] methods = calculateMethods(childClass);
		
		for (IMethod method : methods) {
			if (!method.getAnnotation("Before").exists() &&
				!method.getAnnotation("After").exists() &&
				!method.getAnnotation("BeforeClass").exists() &&
				!method.getAnnotation("AfterClass").exists()) {
				
				selectedMethods.add(method);
				
			}
		}
		
		return new StructuredSelection(selectedMethods);
        
	}

	@Override
	public void modifyText(ModifyEvent e) {
		
		saveChanges();
		
	}

	@Override
	public void checkStateChanged(CheckStateChangedEvent event) {
	
		saveChanges();
		
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		
		saveChanges();
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		
		try {
			
			SelectionDialog dialog = JavaUI.createPackageDialog(composite.getShell(), childClass.getJavaProject(), 0);
			dialog.setMessage("Select Package");
			dialog.setTitle("Package Selection");
			dialog.open();
			
			if (dialog.getReturnCode() == Window.OK) {
				PackageFragment pf = (PackageFragment)dialog.getResult()[0];
				
				if (e.getSource().equals(this.btnParentPackageNameBrowse)) {
					this.txtParentPackageName.setText(pf.getElementName());
				}
				else if(e.getSource().equals(this.btnChildPackageNameBrowse)) {
					this.txtChildPackageName.setText(pf.getElementName());
				}
			}
			
		} catch (JavaModelException e1) {
			System.out.println("Couldn't show Package Dialog");
		}
						
		
	}
	
	private void saveChanges() {
		
		ExtractTestcaseSuperclassRefactoring refactoring = (ExtractTestcaseSuperclassRefactoring)getRefactoring();
		
		refactoring.setParentClassName(this.txtParentClassName.getText());
		refactoring.setParentClassPackage(this.txtParentPackageName.getText());
		
		refactoring.setChildClassName(this.txtChildClassName.getText());
		refactoring.setChildClassPackage(this.txtChildPackageName.getText());
		
		java.util.List<IMethod> parentClassMethods = new ArrayList<IMethod>();
		
		for (Object obj : lstParentClassMethods.getCheckedElements()) {
			IMethod method = (IMethod)obj;
			parentClassMethods.add(method);
		}
		
		refactoring.setParentClassMethods(parentClassMethods);
		
	}

}

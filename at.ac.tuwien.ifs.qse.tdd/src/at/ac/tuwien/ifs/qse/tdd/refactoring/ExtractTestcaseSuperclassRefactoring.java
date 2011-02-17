package at.ac.tuwien.ifs.qse.tdd.refactoring;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.Document;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

/**
 * Will change a selected Class (now childClass) and create a new Class
 * superClass.
 * 
 * childClass extends superClass.
 * 
 * superClass declare all methods except which names are in childClass.
 * 
 * @author Christian
 *
 */
public class ExtractTestcaseSuperclassRefactoring extends Refactoring{

	private List<IMethod> parentClassMethods  = new ArrayList<IMethod>();
	
	private ICompilationUnit childClass;
	private ICompilationUnit parentClass;
	
	private String parentClassName;
	private String childClassName;
	
	private String parentClassPackage;
	private String childClassPackage;

	private HashMap<ICompilationUnit,TextFileChange> textFileChanges = new HashMap<ICompilationUnit,TextFileChange>();

	/**
	 * Only useful after execution of the Refactoring
	 */
	public ICompilationUnit getParentClass() {
		return parentClass;
	}
	
	/**
	 * Only useful after execution of the Refactoring
	 */
	public ICompilationUnit getChildClass() {
		return childClass;
	}
	
	public void setExtractionClass(ICompilationUnit extractionClass) {
		this.childClass = extractionClass;
	}
	
	public void setParentClassName(String superClassName) {
		this.parentClassName = superClassName;
	}
	
	public void setParentClassPackage(String packages) {
		this.parentClassPackage = packages;
	}
	
	public void setChildClassName(String childclassname) {
		this.childClassName = childclassname;
	}
	
	public void setChildClassPackage(String packages) {
		this.childClassPackage = packages;
	}
	
	public void setParentClassMethods(Collection<IMethod> childClassMethods) {
		this.parentClassMethods = new ArrayList<IMethod>();
		this.parentClassMethods.addAll(childClassMethods);
	}
	

	

	
	public RefactoringStatus checkInitialConditions(IProgressMonitor monitor)
			throws CoreException, OperationCanceledException {
		
		RefactoringStatus status = new RefactoringStatus();
		
		try {
			monitor.beginTask("Checking preconditions...", 1);
			
			if (childClass == null) {
				status.merge(RefactoringStatus.createFatalErrorStatus("Method has not been specified."));
			} else if (!childClass.exists()) {
				status.merge(RefactoringStatus.createFatalErrorStatus(MessageFormat.format("Class ''{0}'' does not exist.", new Object[] { childClass.getElementName()})));
			} else if (!childClass.isStructureKnown()) {
				status.merge(RefactoringStatus.createFatalErrorStatus(MessageFormat.format("Class ''{0}'' contains compile errors.", new Object[] { childClass.getElementName()})));
			} else if (!(childClass.getParent() instanceof IPackageFragment)) {
				status.merge(RefactoringStatus.createFatalErrorStatus(MessageFormat.format("Class ''{0}'' parent is not a package fragment.", new Object[] { childClass.getElementName()})));				
			} else if ((childClass.getTypes()[0].getMethods().length == 0)) {
				status.merge(RefactoringStatus.createFatalErrorStatus(MessageFormat.format("Class ''{0}'' has no methods.", new Object[] { childClass.getElementName()})));								
			}
		
		}
		finally {
			monitor.done();
		}
		return status;
		
	}
	
	public RefactoringStatus checkFinalConditions(IProgressMonitor monitor)
			throws CoreException, OperationCanceledException {
		
		RefactoringStatus status = new RefactoringStatus();
		
		if (parentClassName == null || parentClassName.equals("")) {
			status.merge(RefactoringStatus.createErrorStatus("No Name for Superclass specified"));				
		}
		if (childClassName == null || childClassName.equals("")) {
			status.merge(RefactoringStatus.createErrorStatus("No Name for Testclass specified"));				
		} 
		
		monitor.done();
		
		return status;
		
	}

	@Override
	public Change createChange(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		
		childClass = moveClientClass(monitor,childClass,childClassName,childClassPackage);
		
		CompilationUnit childNode = createASTNode(monitor,childClass);
		parentClass = createMinimalParentClass(monitor,parentClassName,parentClassPackage,childNode);
		
		CompilationUnit parentNode = createASTNode(monitor,parentClass);
		ASTRewrite parentRewrite = ASTRewrite.create(parentNode.getAST());

		doClassHeaderChanges(monitor,childNode,childClass);
		childNode = createASTNode(monitor,childClass);
		ASTRewrite childRewrite = ASTRewrite.create(childNode.getAST());
						
		calculateFieldChanges(monitor,childRewrite,parentRewrite,childNode,parentNode);
		calculateMethodChanges(monitor,childRewrite,parentRewrite,childNode,parentNode);
		
		ImportRewrite parentImportRewrite = calculateImportChanges(monitor,childNode,parentNode);
		
		Change change = doChange(monitor,childClass, parentClass, childRewrite,parentRewrite,childNode,parentNode,parentImportRewrite);
		
		monitor.done();
		
		return change;
			
	}



	private ImportRewrite calculateImportChanges(IProgressMonitor monitor,
			CompilationUnit childNode, CompilationUnit parentNode) {

		SubProgressMonitor subMonitor= new SubProgressMonitor(monitor, 1);
		
		subMonitor.beginTask("Create Import Changes", 1);
		
		//Collect childs imports
		List<?> imports = childNode.imports();
		
		//Add childs imports to parent
		ImportRewrite parentImportRewrite = ImportRewrite.create(parentNode, true);
		for (Object obj : imports) {
			ImportDeclaration importDec = (ImportDeclaration)obj;
			if (importDec.isOnDemand()) {
				if (importDec.isStatic()) {
					parentImportRewrite.addStaticImport(importDec.getName().getFullyQualifiedName(), "*", false);
				}
				else {
					parentImportRewrite.addImport(importDec.getName().getFullyQualifiedName() + ".*");
				}
			}
			else {
				if (importDec.isStatic()) {
					parentImportRewrite.addStaticImport(importDec.getName().getFullyQualifiedName(), null, false);
				}
				else {
					parentImportRewrite.addImport(importDec.getName().getFullyQualifiedName());
				}
			}
		}
		
		//Add childs static imports to parent
		
		subMonitor.done();
		
		return parentImportRewrite;
		
	}

	private ICompilationUnit moveClientClass(IProgressMonitor monitor,
			ICompilationUnit childClass, String childClassName, String childClassPackage) throws JavaModelException {
		
		IPackageFragment pf = createOrGetPackageFragment(childClass,childClassPackage);
		
		if (!childClass.getElementName().equals(childClassName + ".java") ||
			!childClass.getParent().equals(pf)) {
		
			SubProgressMonitor subMonitor= new SubProgressMonitor(monitor, 1);
			
			subMonitor.beginTask("Move Child Class", 1);
			
			childClass.move(pf, null, childClassName + ".java", false, monitor);
			
			childClass = pf.getCompilationUnit(childClassName + ".java");
			
			subMonitor.done();
		}
		
		return childClass; 
	}

	private ICompilationUnit createMinimalParentClass(IProgressMonitor monitor, String parentClassName,
			String parentClassPackage, CompilationUnit childNode) throws JavaModelException {
		
		ICompilationUnit parentClass;		
		SubProgressMonitor subMonitor= new SubProgressMonitor(monitor, 1);
		
		subMonitor.beginTask("Create minimal Parent Class", 1);
		
		String parentClassContent = "";
		String extend = "";
		String implement = "";
		
		TypeDeclaration childTypeDec = (TypeDeclaration) childNode.types().get(0);
		
		Type childSuperclass = childTypeDec.getSuperclassType();
		List<?> childInterfaces = childTypeDec.superInterfaceTypes();

		
		if (childSuperclass != null) {
			extend = "extends " + childSuperclass.toString();
		}
		
		if (childInterfaces.size() != 0) {
			implement = "implements ";
			
			for (Object obj : childInterfaces) {
				Type t = (Type)obj;
				implement += t.toString() + ", ";
			}
			
			implement = implement.substring(0, implement.length() - 2);
			
		}
		
		if (!childClass.getParent().getElementName().equals("")) {
			parentClassContent +=
				"package " + parentClassPackage + ";\n" +
				"\n";
		}
			
		parentClassContent += 
		    "public abstract class " + parentClassName + " " + extend + " " + implement + " {\n" +
		    "\n" +
		    "}";
		
		IPackageFragment pf = createOrGetPackageFragment(childClass,parentClassPackage);
		
		parentClass = pf.createCompilationUnit(parentClassName + ".java", parentClassContent, false, subMonitor);
		
		subMonitor.done();
		
		return parentClass;
		
	}

	private IPackageFragment createOrGetPackageFragment(
			ICompilationUnit classes, String packageName) throws JavaModelException {
		
		IPackageFragmentRoot pfr = (IPackageFragmentRoot)classes.getParent().getParent();
		IPackageFragment pf = pfr.getPackageFragment(packageName);
			
		if (pf != null && pf.exists()) {
			return pf;
		}
		else {
			return pfr.createPackageFragment(packageName, true, null);
		}
		
		
	}

	private CompilationUnit createASTNode(IProgressMonitor monitor,
			ICompilationUnit classUnit) {
		
		SubProgressMonitor subMonitor= new SubProgressMonitor(monitor, 1);
		
		subMonitor.beginTask("Create AST Node", 1);
		
		ASTParser parser= ASTParser.newParser(AST.JLS3);
		parser.setProject(childClass.getJavaProject());
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(classUnit);
		
		CompilationUnit node = (CompilationUnit)parser.createAST(subMonitor);
		
		subMonitor.done();
		
		return node;
		
	}
	
	private void calculateMethodChanges(IProgressMonitor monitor,
			ASTRewrite childRewrite, ASTRewrite parentRewrite,
			ASTNode childNode, ASTNode parentNode) {

			SubProgressMonitor subMonitor= new SubProgressMonitor(monitor, 1);
			subMonitor.beginTask("Calculate Method Changes", 1);
			
			boolean differentPackages = !childClassPackage.equals(parentClassPackage);
			
			childNode.accept(new MethodVisitor(childRewrite, parentRewrite, childNode, parentNode, differentPackages, parentClassMethods));		
			
			subMonitor.done();
			
		}	
	
	private void calculateFieldChanges(IProgressMonitor monitor,
		
		ASTRewrite childRewrite, ASTRewrite parentRewrite,
		ASTNode childNode, ASTNode parentNode) {
		
		SubProgressMonitor subMonitor= new SubProgressMonitor(monitor, 1);
		subMonitor.beginTask("Calculate Field Changes", 1);
		
		boolean differentPackages = !childClassPackage.equals(parentClassPackage);
		
		childNode.accept(new FieldVisitor(childRewrite, parentRewrite, childNode, parentNode, differentPackages));
		
		subMonitor.done();
		
	}
	
	private void doClassHeaderChanges(IProgressMonitor monitor, CompilationUnit childNode, ICompilationUnit childClass) throws JavaModelException {
		
		SubProgressMonitor subMonitor= new SubProgressMonitor(monitor, 1);
		subMonitor.beginTask("Calculate Class Header Changes", 1);
		
		childNode.recordModifications();
		TypeDeclaration childTypeDec = (TypeDeclaration) childNode.types().get(0);

		//Set extend from childClass
		
		Name newName = null;
		
		if (parentClassPackage.toString().equals("")) {
			newName = childNode.getAST().newName(parentClassName);
		}
		else {
			newName = childNode.getAST().newName(parentClassPackage + "." + parentClassName);
		}
		
		Type newType = childNode.getAST().newSimpleType(newName);
		childTypeDec.setSuperclassType(newType);
		childTypeDec.superInterfaceTypes().clear();
		
		TextEdit edit = childNode.rewrite(new Document(childClass.getSource()), null);
		childClass.applyTextEdit(edit, subMonitor);

		childClass.getBuffer().setContents(childClass.getSource());
		childClass.getBuffer().save(subMonitor, false);	
		
		subMonitor.done();
	
	}
	
	private Change doChange(IProgressMonitor monitor, ICompilationUnit childClass,
			ICompilationUnit parentClass, ASTRewrite childRewrite,
			ASTRewrite parentRewrite, CompilationUnit childNode, CompilationUnit parentNode, ImportRewrite parentImportRewrite) {
		
		this.rewriteAST(childClass, childRewrite, null, childNode);
		this.rewriteAST(parentClass, parentRewrite, parentImportRewrite, parentNode);
		
		final Collection<TextFileChange> changes= textFileChanges.values();
		CompositeChange change= new CompositeChange(parentClassName + ".java", changes.toArray(new Change[changes.size()])) {

			@Override
			public ChangeDescriptor getDescriptor() {
				return new RefactoringChangeDescriptor(new ExtractTestcaseSuperclassDescriptor("Project", "Description", "Comment", null));
			}
			
		};		
		
		return change;	
	}
	
	private void rewriteAST(ICompilationUnit unit, ASTRewrite astRewrite, ImportRewrite importRewrite, CompilationUnit node) {
		try {
			
			MultiTextEdit edit= new MultiTextEdit();
			TextEdit astEdit= astRewrite.rewriteAST();
			
			if (!isEmptyEdit(astEdit))
				edit.addChild(astEdit);
			
			if (importRewrite != null) {
				TextEdit importEdit= importRewrite.rewriteImports(new NullProgressMonitor());
				if (!isEmptyEdit(importEdit))
					edit.addChild(importEdit);
				if (isEmptyEdit(edit))
					return;
			}

			TextFileChange change= textFileChanges.get(unit);
			if (change == null) {
				change= new TextFileChange(unit.getElementName(), (IFile) unit.getResource());
				change.setTextType("java");
				change.setEdit(edit);
			} else
				change.getEdit().addChild(edit);

			textFileChanges.put(unit, change);
		} catch (MalformedTreeException exception) {
			
		} catch (IllegalArgumentException exception) {
			
		} catch (CoreException exception) {
			
		}
	}

	@Override
	public String getName() {
		return "Extract Testcase Superclass";
	}
	
	private boolean isEmptyEdit(TextEdit edit) {
		return edit.getClass() == MultiTextEdit.class && !edit.hasChildren();
	}
	 
	private class MethodVisitor extends ASTVisitor{
	
		ASTRewrite childRewrite;
		ASTRewrite parentRewrite;
		TypeDeclaration typeDec;
		ASTNode childNode;
		@SuppressWarnings("unused")
		ASTNode parentNode;
		boolean differentPackages;
		private List<IMethod> parentClassMethods;
		
		public MethodVisitor(ASTRewrite childRewrite, ASTRewrite parentRewrite, ASTNode childNode, ASTNode parentNode, boolean differentPackages, List<IMethod> parentClassMethods) {
			
			this.childRewrite = childRewrite;
			this.parentRewrite = parentRewrite;
			this.childNode = childNode;
			this.parentNode = parentNode;
			this.differentPackages = differentPackages;
			this.parentClassMethods = parentClassMethods;
			
			typeDec = (TypeDeclaration) ((CompilationUnit)parentNode).types().get(0);
				
		}
		
		public boolean visit(MethodDeclaration methodDec) {
			
			if (methodDec.isConstructor()) return false;
			
			for (IMethod method : parentClassMethods) {
				
				if (method.getElementName().equals(methodDec.getName().getIdentifier())) {
					refactor(methodDec);
					return true;
				}		
				
			}
			
			return false;
			
		} 
		
		@SuppressWarnings("unchecked")
		public void refactor(MethodDeclaration methodDec) {
			
			ListRewrite list;
			
			//Remove method from parent
			childRewrite.remove(methodDec, null);
			
			//Copy method to parent and change visibility
			list = parentRewrite.getListRewrite(typeDec, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
			int visiblityCounter = 0;
			
			List modifiers = methodDec.modifiers();
			for (Object obj : modifiers) {
				if (obj instanceof Modifier) {
					Modifier modifier = (Modifier)obj;
					
					if (modifier.isPrivate()) {
						modifier.setKeyword(ModifierKeyword.PROTECTED_KEYWORD);
					}
					
					if (modifier.isPrivate() || modifier.isProtected() || modifier.isPublic()) {
						
						
						visiblityCounter++;
						
					}
				}
			}
			
			if (visiblityCounter == 0 && differentPackages) {
				Modifier m = childNode.getAST().newModifier(ModifierKeyword.PUBLIC_KEYWORD);
				
				modifiers.add(m);
			}
			
			list.insertLast(methodDec, null);

		}
	}
	
	private class FieldVisitor extends ASTVisitor{
		
		ASTRewrite childRewrite;
		ASTRewrite parentRewrite;
		TypeDeclaration typeDec;
		boolean differentPackages;
		ASTNode childNode;
		
		public FieldVisitor(ASTRewrite childRewrite, ASTRewrite parentRewrite, ASTNode childNode, ASTNode parentNode, boolean differentPackages) {
			
			this.childRewrite = childRewrite;
			this.parentRewrite = parentRewrite;
			this.differentPackages = differentPackages;
			this.childNode = childNode;
			
			typeDec = (TypeDeclaration) ((CompilationUnit)parentNode).types().get(0);
				
		}
		
		public void endVisit(FieldDeclaration fieldDec) {
			

			List modifiers = fieldDec.modifiers();
			int visiblityCounter = 0;
			
			for (Object obj : modifiers) {
				if (obj instanceof Modifier) {
					Modifier modifier = (Modifier)obj;
					
					if (modifier.isPrivate()) {
						modifier.setKeyword(ModifierKeyword.PROTECTED_KEYWORD);
					}
					
					if (modifier.isPrivate() || modifier.isProtected() || modifier.isPublic()) {
					
						visiblityCounter++;
						
					}
					
				}
			}
			
			if (visiblityCounter == 0 && differentPackages) {
				Modifier m = childNode.getAST().newModifier(ModifierKeyword.PUBLIC_KEYWORD);
				
				modifiers.add(m);
			}
			
			childRewrite.remove(fieldDec, null);
			
			ListRewrite list = parentRewrite.getListRewrite(typeDec, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
			list.insertLast(fieldDec, null);
			
		}
		
	}

	public RefactoringStatus initialize(Map fArguments) {
		// TODO Auto-generated method stub
		return null;
	}
}

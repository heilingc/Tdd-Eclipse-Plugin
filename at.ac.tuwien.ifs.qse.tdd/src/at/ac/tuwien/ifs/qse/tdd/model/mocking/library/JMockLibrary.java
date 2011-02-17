package at.ac.tuwien.ifs.qse.tdd.model.mocking.library;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import at.ac.tuwien.ifs.qse.tdd.model.mocking.Check;
import at.ac.tuwien.ifs.qse.tdd.model.mocking.MockingConfig;
import at.ac.tuwien.ifs.qse.tdd.model.mocking.Stub;
import at.ac.tuwien.ifs.qse.tdd.model.mocking.StubReturnType;

public class JMockLibrary implements IMockingLibrary {

	private MockingConfig currentConfig;
	private Stub currentStub;
	private Check currentCheck;

	@Override
	public boolean canMockClasses() {
		return true;
	}

	@Override
	public String getCaption() {
		return "JMock";
	}

	@Override
	public boolean canHandleAtLeastExpressions() {
		return true;
	}
	
	@Override
	public boolean canHandleAtLeastOnceExpressions() {
		return true;
	}

	@Override
	public String getCode(MockingConfig config) {
		currentConfig = config;
		
		StringBuilder returnArg = new StringBuilder();
		returnArg.append(getImport());
		returnArg.append("\n");
		returnArg.append(getSetup());
		returnArg.append("\n");
		returnArg.append(getConfig());
		returnArg.append("\n");
		returnArg.append("//... write Unit Test with Mock. \n");
		
		return returnArg.toString();
	}
	
	private Object getConfig() {
		
		StringBuilder returnArg = new StringBuilder();
		
		returnArg.append("context.checking(new Expectations() {{\n");
		
		Map<IMethod,Stub> methodStubMatcher = new HashMap<IMethod, Stub>();
		for (Stub s : currentConfig.getStubbing().getStubs()) {
			methodStubMatcher.put(s.getMethod(), s);
		}
		
		for (Check ch : currentConfig.getVerify().getChecks()) {
					
			currentCheck = ch;
			currentStub = methodStubMatcher.get(currentCheck.getMethod());
				
			returnArg.append("\t" + getInvocationCount(currentCheck.getMultiplicity()));
			returnArg.append(" ");
			returnArg.append("(" + currentConfig.getMockName() + "). " + getMethodWithMatcherExpression(currentCheck.getMethod()) + "; ");
			
			if (StubReturnType.Exception.equals(currentStub.getReturnType())) {
				returnArg.append("will(throwException(new Exception())); ");
			}
			
			try {
				if (StubReturnType.Value.equals(currentStub.getReturnType()) &&
					!currentStub.getMethod().getReturnType().equals("V")) {
					
					returnArg.append("will(returnValue(null)); ");	
				}
			} catch (JavaModelException e) {
				//Do nothing
			}
			
			returnArg.append("\n");
		}
				
		returnArg.append("}});\n");

		return returnArg.toString();
		
	}

	private String getMethodWithMatcherExpression(IMethod method) {
		
		StringBuilder returnArg = new StringBuilder();
		
		returnArg.append(method.getElementName() + "(");
		
		if (method.getParameterTypes().length != 0) {
		
			for(String signature : method.getParameterTypes()) {
				
				returnArg.append(signatureToExpression(signature) + ",");
				
			}
			
			returnArg.deleteCharAt(returnArg.length()-1);
			
		}
		returnArg.append(")");
		
		return returnArg.toString();
		
	}
	
	private String signatureToExpression(String signature) {
		
		switch(signature.charAt(0)) {
		case 'B': //byte
			return "with(any(Byte.class))";
		case 'C': //char
			return "with(any(Character.class))";
		case 'D': //double
			return "with(any(Double.class))";
		case 'F': //float
			return "with(any(Float.class))";
		case 'I': //int
			return "with(any(Integer.class))";
		case 'J': //long
			return "with(any(Long.class))";
		case 'S': //short
			return "with(any(Short.class))";
		case 'Z': //boolean
			return "with(any(Boolean.class))";
		case '[': //array
			return "with(any(" + arraySignatureToExpression(signature.substring(1)) + ".class))";
		default: //object
			String searchClassName = signature.substring(1,signature.length()-1);
			
			int openBracketPos = searchClassName.indexOf('<');
			
			if (openBracketPos != -1) {
				searchClassName = searchClassName.substring(0,openBracketPos);
			}
			
			return "with(any(" + searchClassName + ".class))";
			
		}
		
	}
	
	private String arraySignatureToExpression(String signature) {
		switch(signature.charAt(0)) {
		case 'B': //byte
			return "byte[]";
		case 'C': //char
			return "char[]";
		case 'D': //double
			return "double[]";
		case 'F': //float
			return "float[]";
		case 'I': //int
			return "int[]";
		case 'J': //long
			return "long[]";
		case 'S': //short
			return "short[]";
		case 'Z': //boolean
			return "boolean[]";
		case '[': //array	
			return arraySignatureToExpression(signature.substring(1)) + "[]";
		default: //object
			String searchClassName = signature.substring(1);
			return searchClassName + "[]";
		}
	}

	private String getInvocationCount(String multiplicity) {
		
		String[] splitted = multiplicity.split("\\.\\.");
		
		String min = splitted[0];
		String max = splitted[1];
		
		if (min.equals("0") && max.equals("0")) {
			return "never";
		}
		
		if (min.equals("1") && max.equals("1")) {
			return "one";
		}
		
		if (min.equals(max)) {
			return "exactly(" + min + ").of";
		}
		
		else {
			if ( min.equals("0") &&  max.equals("*"))  return "allowing";
			if ( min.equals("0") && !max.equals("*"))  return "atMost(" + max + ").of";
			if (!min.equals("0") &&  max.equals("*"))  return "atLeast(" + min + ").of";
			else                                       return "between(" + min + "," + max + ").of";			
		}
		
	}

	private Object getImport() {
		
		StringBuilder returnArg = new StringBuilder();
		
		returnArg.append("import org.jmock.Expectations;\n");
		returnArg.append("import org.jmock.Mockery;\n");
		returnArg.append("import org.jmock.integration.junit4.JMock;\n");
		returnArg.append("import org.jmock.integration.junit4.JUnit4Mockery;\n");
		
		try {
			if (currentConfig.getMockedUnit().isClass()) {
				returnArg.append("import org.jmock.lib.legacy.ClassImposteriser;");
			}
		} catch (JavaModelException e) {
			//Do nothing
		}
		
		returnArg.append("import " + currentConfig.getMockedUnit().getFullyQualifiedName() + ";\n" );
		return returnArg.toString();
		
	}
	
	private String getSetup() {
		
		StringBuilder returnArg = new StringBuilder();
		
		returnArg.append("//Insert following Annotation before Class Declaration\n");
		returnArg.append("@RunWith(JMock.class)\n");
		returnArg.append("\n");
		returnArg.append("...\n");
		returnArg.append("\n");
		returnArg.append("//Inside Test Method\n");
		
		boolean isClass;
		
		try {
			isClass = currentConfig.getMockedUnit().isClass();
		} catch (JavaModelException e) {
			isClass = false;
		}
		
		if (isClass) {
			returnArg.append("final Mockery context = new JUnit4Mockery() {{ setImposteriser(ClassImposteriser.INSTANCE); }};\n");
		}
		else {
			returnArg.append("final Mockery context = new JUnit4Mockery();\n");
		}
		
		
		returnArg.append("final " + currentConfig.getMockedUnit().getElementName() + " " + currentConfig.getMockName() + " = context.mock(" + currentConfig.getMockedUnit().getElementName() + ".class);\n");
		return returnArg.toString();
		
	}
	
	

}

package at.ac.tuwien.ifs.qse.tdd.model.mocking.library;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import at.ac.tuwien.ifs.qse.tdd.model.mocking.Check;
import at.ac.tuwien.ifs.qse.tdd.model.mocking.MockingConfig;
import at.ac.tuwien.ifs.qse.tdd.model.mocking.Stub;
import at.ac.tuwien.ifs.qse.tdd.model.mocking.StubReturnType;

public class MockitoLibrary implements IMockingLibrary {
	
	private Stub currentStub;
	private Check currentCheck;
	private MockingConfig currentConfig;
	private IMethod currentMethod;
	
	
	@Override
	public boolean canMockClasses() {
		return true;
	}

	@Override
	public String getCaption() {
		return "Mockito";
	}

	public String getImport(MockingConfig c) {
		currentConfig = c;
		
		StringBuilder returnArg = new StringBuilder();
		returnArg.append("import static org.mockito.Mockito.*;\n");
		returnArg.append("import " + currentConfig.getMockedUnit().getFullyQualifiedName() + ";\n" );
		return returnArg.toString();
	}
	
	public String getSetUp(MockingConfig c) {
		currentConfig = c;
		
		StringBuilder returnArg = new StringBuilder();
		returnArg.append(currentConfig.getMockedUnit().getElementName() + " " + currentConfig.getMockName() + " = mock(" + currentConfig.getMockedUnit().getElementName() + ".class);\n");
		
		return returnArg.toString();
		
	}
	
	public String getConfiguration(MockingConfig c) {
		
		currentConfig = c;
		
		StringBuilder returnArg = new StringBuilder();
		
		/*if(StubReturnType.Exception.equals(config.getStubbing().getDefaultReturnType())) {
			
			List<IMethod> toStubMethods = new ArrayList<IMethod>();
			for (Stub stub : config.getStubbing().getStubs()) {
				toStubMethods.add(stub.getMethod());
			}
			
			for (IMethod method : config.getMockedUnit().getMethods()) {
				if (!toStubMethods.contains(method)) {
					
					Stub newStub = new Stub();
					newStub.setMethod(method);
					newStub.setReturnType(StubReturnType.Exception);
					
					config.getStubbing().getStubs().add(newStub);
				}
			}
		}*/
		
		for(Stub s : currentConfig.getStubbing().getStubs()) {

			currentStub = s;
			currentMethod = currentStub.getMethod();
			
			try {
				if (currentMethod.getReturnType().equals("V") && StubReturnType.Value.equals(currentStub.getReturnType())) {
					continue;
				}
			} catch (JavaModelException e) {
				//Do nothing
			}
			
			if (currentMethod.getParameterTypes().length == 0) {	
				returnArg.append(stubVoidMethod());
			}
			else {
				returnArg.append(stubNotVoidMethod());
			}
			
		}
		
		return returnArg.toString();
		
	}

	public String getChecking(MockingConfig c) {
		
		currentConfig = c;
		
		StringBuilder returnArg = new StringBuilder();
		
		for(Check ch : currentConfig.getVerify().getChecks()) { 
		
			currentCheck = ch;
			
			for (String mulitiplicity : mulitiplicityToExpression(currentCheck.getMultiplicity())) {
				returnArg.append("verify(" + currentConfig.getMockName() + ", " + mulitiplicity + ")." + getMethodWithMatcherExpression(currentCheck.getMethod()) + ";\n");
			}
			
		}
		
		return returnArg.toString();
	}

	private String stubVoidMethod() {

		StringBuilder returnArg = new StringBuilder();

		if (StubReturnType.Value.equals(currentStub.getReturnType())) {
			returnArg.append("doReturn(null).");
		}
		else if (StubReturnType.Exception.equals(currentStub.getReturnType())) {
			returnArg.append("doThrow(new Exception()).");
		}
		
		returnArg.append("when(" + currentConfig.getMockName() + ")." + currentStub.getMethod().getElementName() + "();\n");
		
		return returnArg.toString();
	}

	private String stubNotVoidMethod() {
		
		StringBuilder returnArg = new StringBuilder();
		IMethod method = currentStub.getMethod();
		
		returnArg.append("when(" + currentConfig.getMockName() + "." + getMethodWithMatcherExpression(method) + ")");
		
		if (StubReturnType.Value.equals(currentStub.getReturnType())) {
			returnArg.append(".thenReturn(null);\n");
		}
		else if (StubReturnType.Exception.equals(currentStub.getReturnType())) {
			returnArg.append(".thenThrow(new Exception());\n");
		}
		
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
			return "anyByte()";
		case 'C': //char
			return "anyChar()";
		case 'D': //double
			return "anyDouble()";
		case 'F': //float
			return "anyFloat()";
		case 'I': //int
			return "anyInt()";
		case 'J': //long
			return "anyLong()";
		case 'S': //short
			return "anyShort()";
		case 'Z': //boolean
			return "anyBoolean()";
		case '[': //array
			return "any(" + arraySignatureToExpression(signature.substring(1)) + ".class)";
		default: //object
			String searchClassName = signature.substring(1,signature.length()-1);
			
			if (searchClassName.contains("java.lang.String") ||
				searchClassName.contains("java.lang.CharSequence")) {
				return "anyString()";
			}
			
			if (searchClassName.contains("java.util.List")) {
				return "anyList()";
			}
			
			if (searchClassName.contains("java.util.Map")) {
				return "anyMap()";
			}
			
			if (searchClassName.contains("java.util.Collection"))  {
				return "anyCollection()";
			}
			
			if (searchClassName.equals("java.lang.Object")) {
				return "any()";
			}
			
			int openBracketPos = searchClassName.indexOf('<');
			
			if (openBracketPos != -1) {
				searchClassName = searchClassName.substring(0,openBracketPos);
			}
			
			return "any(" + searchClassName + ".class)";
			
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
	
	private String[] mulitiplicityToExpression(String multiplicity) {
		
		if (multiplicity.equals("0..0")) {
			return new String[]{ "never()" };
		}
		
		String[] splitted = multiplicity.split("\\.\\.");
		
		String min = splitted[0];
		String max = splitted[1];
		
		if (min.equals(max)) {
			return new String[] { "times(" + min + ")"};
		}
		else {
			if ( min.equals("0") &&  max.equals("*"))  return new String[0];
			if ( min.equals("0") && !max.equals("*"))  return new String[]{"atMost(" + max + ")"};
			if (!min.equals("0") &&  max.equals("*"))  return new String[]{"atLeast(" + min + ")"};
			else                                       return new String[]{"atLeast(" + min + ")", "atMost(" + max + ")"};			
		}
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
		StringBuilder template = new StringBuilder();
		
		template.append(this.getImport(config));
		template.append("\n//Create Mock\n");
		template.append(this.getSetUp(config));
		template.append("\n//Configure Mock\n");
		template.append(this.getConfiguration(config));
		template.append("\n//... write Unit Test with Mock\n");
		template.append("\n//Check Method Calls\n");
		template.append(this.getChecking(config));
		
		return template.toString();
	}
}

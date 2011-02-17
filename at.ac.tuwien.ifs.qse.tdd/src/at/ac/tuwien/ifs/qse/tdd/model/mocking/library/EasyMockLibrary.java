package at.ac.tuwien.ifs.qse.tdd.model.mocking.library;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import at.ac.tuwien.ifs.qse.tdd.model.mocking.Check;
import at.ac.tuwien.ifs.qse.tdd.model.mocking.MockingConfig;
import at.ac.tuwien.ifs.qse.tdd.model.mocking.Stub;
import at.ac.tuwien.ifs.qse.tdd.model.mocking.StubReturnType;

public class EasyMockLibrary implements IMockingLibrary {
	
	private MockingConfig currentConfig;
	private Check currentCheck;
	private Stub currentStub;

	@Override
	public boolean canMockClasses() {
		return true;
	}
	
	@Override
	public boolean canHandleAtLeastExpressions() {
		return false;
	}

	@Override
	public boolean canHandleAtLeastOnceExpressions() {
		return true;
	}

	@Override
	public String getCaption() {
		return "EasyMock";
	}
	

	public Object getImport(MockingConfig c) {
		currentConfig = c;
		
		StringBuilder returnArg = new StringBuilder();
		returnArg.append("import static org.easymock.EasyMock.*;\n");
		returnArg.append("import " + currentConfig.getMockedUnit().getFullyQualifiedName() + ";\n" );
		return returnArg.toString();
	}

	public String getSetUp(MockingConfig c) {
		currentConfig = c;
		
		StringBuilder returnArg = new StringBuilder();
		returnArg.append(currentConfig.getMockedUnit().getElementName() + " " + currentConfig.getMockName() + " = createMock(" + currentConfig.getMockedUnit().getElementName() + ".class);\n");
		
		return returnArg.toString();

	}

	public String getConfiguration(MockingConfig c) {
		currentConfig = c;
		
		StringBuilder returnArg = new StringBuilder();
		
		Map<IMethod,Stub> methodStubMatcher = new HashMap<IMethod, Stub>();
		for (Stub s : currentConfig.getStubbing().getStubs()) {
			methodStubMatcher.put(s.getMethod(), s);
		}
		
		for (Check ch : currentConfig.getVerify().getChecks()) {
			
			currentCheck = ch;
			currentStub = methodStubMatcher.get(currentCheck.getMethod());
			
			if (!"0..0".equals(currentCheck.getMultiplicity())) {
			
				try {
					if (!currentStub.getMethod().getReturnType().equals("V")) {
						returnArg.append("expect(" + currentConfig.getMockName() + "." + getMethodWithMatcherExpression(currentCheck.getMethod()) + ")");
						
						if (currentStub != null) {
							if (StubReturnType.Exception.equals(currentStub.getReturnType())) {
								returnArg.append(".andThrow(new Exception())");
							} else if (StubReturnType.Value.equals(currentStub.getReturnType())) {
								returnArg.append(".andReturn(null)");
							}
							
							returnArg.append(";\n");
						}						
					}
					else {
						returnArg.append(currentConfig.getMockName() + "." + getMethodWithMatcherExpression(currentCheck.getMethod()) + ";\n");						
						if (StubReturnType.Exception.equals(currentStub.getReturnType())) {
							returnArg.append("expectLastCall().andThrow(new Exception());\n");
						}
					
					}
					
					if (!"1..1".equals(currentCheck.getMultiplicity())) {
						returnArg.append("expectLastCall()." + mulitiplicityToExpression(currentCheck.getMultiplicity()) + ";\n");
					}
				} catch (JavaModelException e) {
					//Do nothing
				}
			}
		}
		
		returnArg.append("replay(" + currentConfig.getMockName() + ");\n");
		return returnArg.toString();
	}
	
	public String getChecking(MockingConfig config) {

		return "verify(" + currentConfig.getMockName() + ");";
		
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
			return "anyObject(" + arraySignatureToExpression(signature.substring(1)) + ".class)";
		default: //object
			String searchClassName = signature.substring(1,signature.length()-1);
			
			int openBracketPos = searchClassName.indexOf('<');
			
			if (openBracketPos != -1) {
				searchClassName = searchClassName.substring(0,openBracketPos);
			}
			
			return "anyObject(" + searchClassName + ".class)";
			
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
	
	private String mulitiplicityToExpression(String multiplicity) {
		
		String[] splitted = multiplicity.split("\\.\\.");
		
		String min = splitted[0];
		String max = splitted[1];
		
		if (min.equals(max)) {
			return "times(" + min + ")";
		}
		else {
			if ( min.equals("0") &&  max.equals("*"))  return "anyTimes()";
			if ( min.equals("0") && !max.equals("*"))  return "times(0," + max + ")";
			if ( min.equals("1") &&  max.equals("*"))  return "atLeastOnce()";
			if (!min.equals("0") &&  max.equals("*"))  return "anyTimes()";
			else                                       return "times(" + min + "," + max + ")";			
		}
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

package fr.echoeslabs.tuto.jdt.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.ThisExpression;

import fr.echoeslabs.tuto.jdt.refactoring.api.AbstractRefactoring;
import fr.echoeslabs.tuto.jdt.refactoring.api.RefactoringException;

/**
 * This refactoring renames locals methods declarations using camel case
 * specification and impacts local method invocations;
 */
public class MethodInvocationCamelCaseRefactoring extends AbstractRefactoring {
	private final static class LocalMethodInvocation extends ASTVisitor {

		private final List<MethodInvocation> methodInvocations = new ArrayList<MethodInvocation>();

		public final List<MethodInvocation> getMethodInvocations() {
			return methodInvocations;
		}

		@Override
		public final boolean visit(final MethodInvocation node) {
			Expression expression = node.getExpression();
			if (expression == null || expression instanceof ThisExpression)
				getMethodInvocations().add(node);
			return super.visit(node);
		}
	}

	private final static class LocalMethodDeclaration extends ASTVisitor {

		private final List<MethodDeclaration> methodDeclarations = new ArrayList<MethodDeclaration>();

		public final List<MethodDeclaration> getMethodDeclarations() {
			return methodDeclarations;
		}

		@Override
		public final boolean visit(final MethodDeclaration node) {
			getMethodDeclarations().add(node);
			return super.visit(node);
		}
	}

	public static final String formatMethodName(String input) {
		input = input.trim();
		
		char[] outputAsChars = new char[input.length()];
		int out = 0;
		
		for (int in = 0; in < input.length(); ++in) {
			switch (input.charAt(in)) {
			case '_':
				if (in == 0 && input.length() >= 2 && Character.isDigit(input.charAt(0))) {
					outputAsChars[out++] = input.charAt(in++);
					outputAsChars[out++] = input.charAt(in);
				}
				if (in + 1 < input.length())	// last '_' as in foo_ is ignored
					outputAsChars[out++] = Character.toUpperCase(input.charAt(++in));
				break;
			default:
				outputAsChars[out++] = input.charAt(in);
			}
		}
		return String.valueOf(outputAsChars, 0, out);
	}

	@Override
	public final void refactor(final CompilationUnit inputAST) throws RefactoringException {
		final LocalMethodDeclaration localMethodDeclaration = new LocalMethodDeclaration();
		inputAST.accept(localMethodDeclaration);

		final List<MethodDeclaration> methodDeclarations = localMethodDeclaration.getMethodDeclarations();
		for (final MethodDeclaration method : methodDeclarations) {
			final SimpleName name = method.getName();
			final String currentIdentifier = name.getIdentifier();
			final String newIdentifier = formatMethodName(currentIdentifier);
			name.setIdentifier(newIdentifier);
		}

		final LocalMethodInvocation localMethodInvocation = new LocalMethodInvocation();
		inputAST.accept(localMethodInvocation);

		final List<MethodInvocation> methodInvocations = localMethodInvocation.getMethodInvocations();
		for (final MethodInvocation method : methodInvocations) {
			final SimpleName name = method.getName();
			final String currentIdentifier = name.getIdentifier();
			final String newIdentifier = formatMethodName(currentIdentifier);
			name.setIdentifier(newIdentifier);
		}

	}

}

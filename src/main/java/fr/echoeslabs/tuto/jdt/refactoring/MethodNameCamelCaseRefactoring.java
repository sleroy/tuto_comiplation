package fr.echoeslabs.tuto.jdt.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;

import com.google.common.base.CaseFormat;

import fr.echoeslabs.tuto.jdt.refactoring.api.AbstractRefactoring;
import fr.echoeslabs.tuto.jdt.refactoring.api.RefactoringException;

public class MethodNameCamelCaseRefactoring extends AbstractRefactoring {

	private final static class MethodCollector extends ASTVisitor {

		private final List<MethodDeclaration> methodDeclarations = new ArrayList<MethodDeclaration>();

		public final List<MethodDeclaration> getMethodDeclarations() {
			return this.methodDeclarations;
		}

		@Override
		public final boolean visit(final MethodDeclaration node) {
			this.getMethodDeclarations().add(node);
			return super.visit(node);
		}
	}

	/**
	 * Format method name.
	 *
	 * @param input
	 *            the input
	 * @return the string
	 */
	public static final String formatMethodName(final String input) {
		String res = input.
		// all upper case chars following a lower char are prefixed with an underscore so that
		// input that are already in camel case are preserved
				replaceAll("(\\p{Ll})(\\p{Lu})", "$1_$2").
				// remove unsupported chars.
				replaceAll("[\n\t\r\\(\\).=';{}]", "").
				// replace spaces and hyphen by underscore
				replaceAll("[ -]", "_").toUpperCase();
		// finally use CaseFormat to have a lower camel case output
		res = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, res);
		// if the first char is a number, prefix the name with an underscore
		if (!Character.isLowerCase(res.charAt(0))) {
			res = "_" + res;
		}
		// since the method length is limited to 255 chars in Java, but the name if needed
		if (res.length() > 250) {
			res = res.substring(0, 250);
		}
		return res;
	}

	@Override
	public final void refactor(final CompilationUnit inputAST) throws RefactoringException {
		// do not change AST
		final MethodCollector methodCollector = new MethodCollector();
		inputAST.accept(methodCollector);

		final List<MethodDeclaration> methodDeclarations = methodCollector.getMethodDeclarations();
		for (final MethodDeclaration method : methodDeclarations) {
			final SimpleName name = method.getName();
			final String currentIdentifier = name.getIdentifier();
			final String newIdentifier = formatMethodName(currentIdentifier);
			name.setIdentifier(newIdentifier);
		}

	}
}

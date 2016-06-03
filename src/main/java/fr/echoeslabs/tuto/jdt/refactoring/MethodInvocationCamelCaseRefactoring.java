package fr.echoeslabs.tuto.jdt.refactoring;

import org.eclipse.jdt.core.dom.CompilationUnit;

import fr.echoeslabs.tuto.jdt.refactoring.api.AbstractRefactoring;
import fr.echoeslabs.tuto.jdt.refactoring.api.RefactoringException;

/**
 * This refactoring renames locals methods declarations using camel case specification
 * and impacts local method invocations;
 */
public class MethodInvocationCamelCaseRefactoring extends AbstractRefactoring {

	@Override
	public final void refactor(final CompilationUnit inputAST) throws RefactoringException {
		// TODO
		System.err.println("TODO: implement " + this.getClass());
	}

}

package fr.echoeslabs.tuto.jdt.refactoring;

import org.eclipse.jdt.core.dom.CompilationUnit;

import fr.echoeslabs.tuto.jdt.refactoring.api.AbstractRefactoring;
import fr.echoeslabs.tuto.jdt.refactoring.api.RefactoringException;

public class NoChangeRefactoring extends AbstractRefactoring {

	@Override
	public final void refactor(final CompilationUnit inputAST) throws RefactoringException {
		// do not change AST
	}

}

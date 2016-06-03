package fr.echoeslabs.tuto.jdt.refactoring;

import org.eclipse.jdt.core.dom.CompilationUnit;

import fr.echoeslabs.tuto.jdt.refactoring.api.AbstractRefactoring;
import fr.echoeslabs.tuto.jdt.refactoring.api.RefactoringException;

/**
 * This refactoring reorders the case blocks within switches in lexicographic order.
 */
public class SwitchOrderingRefactoring extends AbstractRefactoring {

	@Override
	public final void refactor(final CompilationUnit inputAST) throws RefactoringException {
		// TODO
		System.err.println("TODO: implement " + this.getClass());
	}

}

package fr.echoeslabs.tuto.jdt.refactoring.api;

import org.eclipse.jdt.core.dom.CompilationUnit;

public abstract class AbstractRefactoring {

	public abstract void refactor(final CompilationUnit inputAST) throws RefactoringException;

}

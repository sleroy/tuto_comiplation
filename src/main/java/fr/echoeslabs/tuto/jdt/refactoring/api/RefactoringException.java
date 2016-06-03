package fr.echoeslabs.tuto.jdt.refactoring.api;

public class RefactoringException extends Exception {
	private static final long serialVersionUID = -431718016851834962L;

	public RefactoringException(final String _message, final Throwable _cause) {
		super(_message, _cause);
	}
}
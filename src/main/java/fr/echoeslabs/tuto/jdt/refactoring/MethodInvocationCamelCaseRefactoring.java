package fr.echoeslabs.tuto.jdt.refactoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.ThisExpression;

import fr.echoeslabs.tuto.jdt.refactoring.api.AbstractRefactoring;
import fr.echoeslabs.tuto.jdt.refactoring.api.RefactoringException;

/**
 * This refactoring renames locals methods declarations using camel case specification and impacts local method invocations;
 */
public class MethodInvocationCamelCaseRefactoring extends AbstractRefactoring
{

	private final class MethodConverter extends ASTVisitor
	{
		private final List< MethodDeclaration > methodDeclarations = new ArrayList< MethodDeclaration >();

		private final Map< String, String > methodNameCorrelations = new HashMap< String, String >();

		private final List< MethodInvocation > methodInvocations = new ArrayList< MethodInvocation >();

		/**
		 * @see ASTVisitor#visit(MethodDeclaration)
		 */
		@Override
		public final boolean visit(final MethodDeclaration node)
		{
			// collect MethodDeclaration node
			methodDeclarations.add( node );
			return super.visit( node );
		}

		/**
		 * @see ASTVisitor#visit(MethodInvocation)
		 */
		@Override
		public final boolean visit(final MethodInvocation node)
		{
			// collect MethodInvocation node
			methodInvocations.add( node );
			return super.visit( node );
		}

		/**
		 * Convert the method's name
		 */
		public final void convert()
		{
			// MethodDeclarations process
			methodDeclarations.forEach(declaration -> 
			{
				// get current identifier
				SimpleName name = declaration.getName();
				String currentIdentifier = name.getIdentifier();
				// new identifier from conversion
				String newIdentifier = MethodNameCamelCaseRefactoring.formatMethodName( currentIdentifier );
				// if there is a change
				if( !currentIdentifier.equals( newIdentifier ) )
				{
					// change identifier
					name.setIdentifier( newIdentifier );
					// store new identifier
					methodNameCorrelations.put( currentIdentifier, newIdentifier );
				}
			});
			// MethodInvocations process
			methodInvocations.forEach( invocation ->
			{
				// only for method invocation of this class
				if( invocation.getExpression() instanceof ThisExpression )
				{
					// get current identifier
					SimpleName name = invocation.getName();
					String currentIdentifier = name.getIdentifier();
					// if there is a stored change
					if( methodNameCorrelations.containsKey( currentIdentifier ) )
					{
						// get current identifier
						String newIdentifier = methodNameCorrelations.get( currentIdentifier );
						// change identifier
						name.setIdentifier( newIdentifier );
					}
				}
			});
		}
	}

	/**
	 * @see AbstractRefactoring#refactor(CompilationUnit)
	 */
	@Override
	public final void refactor(final CompilationUnit inputAST) throws RefactoringException
	{
		final MethodConverter converter = new MethodConverter();
		// collect usefull nodes
		inputAST.accept( converter );
		// do the conversion
		converter.convert();
	}

}

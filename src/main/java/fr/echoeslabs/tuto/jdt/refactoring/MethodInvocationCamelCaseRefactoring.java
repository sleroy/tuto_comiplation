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

import com.google.common.base.CaseFormat;

import fr.echoeslabs.tuto.jdt.refactoring.api.AbstractRefactoring;
import fr.echoeslabs.tuto.jdt.refactoring.api.RefactoringException;

/**
 * This refactoring renames locals methods declarations using camel case specification and impacts local method invocations;
 */
public class MethodInvocationCamelCaseRefactoring extends AbstractRefactoring
{
	private class MethodConverter extends ASTVisitor
	{
		private final List< MethodDeclaration > methodDeclarations = new ArrayList< MethodDeclaration >();

		private final Map< String, String > methodNameCorrelations = new HashMap< String, String >();

		private final List< MethodInvocation > methodInvocations = new ArrayList< MethodInvocation >();

		/**
		 * @see ASTVisitor#visit(MethodDeclaration)
		 */
		@Override
		public boolean visit(MethodDeclaration node)
		{
			// collect MethodDeclaration node
			methodDeclarations.add( node );
			return super.visit( node );
		}

		/**
		 * @see ASTVisitor#visit(MethodInvocation)
		 */
		@Override
		public boolean visit(MethodInvocation node)
		{
			// collect MethodInvocation node
			methodInvocations.add( node );
			return super.visit( node );
		}

		/**
		 * Convert the method's name
		 */
		public void convert()
		{
			// MethodDeclarations process
			for( MethodDeclaration declaration : methodDeclarations )
			{
				// get current identifier
				SimpleName name = declaration.getName();
				String currentIdentifier = name.getIdentifier();
				// new identifier from conversion
				String newIdentifier = formatMethodName( currentIdentifier );
				// if there is a change
				if( !currentIdentifier.equals( newIdentifier ) )
				{
					// change identifier
					name.setIdentifier( newIdentifier );
					// store new identifier
					methodNameCorrelations.put( currentIdentifier, newIdentifier );
				}
			}
			// MethodInvocations process
			for( MethodInvocation invocation : methodInvocations )
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
			}
		}

		/**
		 * Format method name.
		 *
		 * @param input the input
		 * @return the string
		 */
		private String formatMethodName(String input)
		{
			String res = input.
			// all upper case chars following a lower char are prefixed with an underscore so that
			// input that are already in camel case are preserved
			replaceAll( "(\\p{Ll})(\\p{Lu})", "$1_$2" ).
			// remove unsupported chars.
			replaceAll( "[\n\t\r\\(\\).=';{}]", "" ).
			// replace spaces and hyphen by underscore
			replaceAll( "[ -]", "_" ).toUpperCase();
			// finally use CaseFormat to have a lower camel case output
			res = CaseFormat.UPPER_UNDERSCORE.to( CaseFormat.LOWER_CAMEL, res );
			// if the first char is a number, prefix the name with an underscore
			if( !Character.isLowerCase( res.charAt( 0 ) ) )
			{
				res = "_" + res;
			}
			// since the method length is limited to 255 chars in Java, but the name if needed
			if( res.length() > 250 )
			{
				res = res.substring( 0, 250 );
			}
			return res;
		}
	}

	@Override
	public void refactor(CompilationUnit inputAST) throws RefactoringException
	{
		MethodConverter converter = new MethodConverter();
		// collect usefull nodes
		inputAST.accept( converter );
		// do the conversion
		converter.convert();
	}
}

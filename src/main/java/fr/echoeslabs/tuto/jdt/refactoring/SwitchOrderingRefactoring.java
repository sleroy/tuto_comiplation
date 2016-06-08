package fr.echoeslabs.tuto.jdt.refactoring;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;

import fr.echoeslabs.tuto.jdt.refactoring.api.AbstractRefactoring;
import fr.echoeslabs.tuto.jdt.refactoring.api.RefactoringException;

/**
 * This refactoring reorders the case blocks within switches in lexicographic order.
 */
public class SwitchOrderingRefactoring extends AbstractRefactoring
{
	/**
	 * lexicographic comparator for SwitchCase
	 */
	private class SwitchCaseComparator implements Comparator<SwitchCase>
	{
		@Override
		public int compare(SwitchCase sc1, SwitchCase sc2)
		{
			if (sc1 == null)
				return 1;
			if (sc2 == null)
				return -1;
			Expression e1 = sc1.getExpression();
			Expression e2 = sc2.getExpression();
			if (e1 == null)
				return 1;
			if (e2 == null)
				return -1;
			String toString1 = e1.toString();
			String toString2 = e2.toString();
			return toString1.compareTo(toString2);
		}
	}

	private class SwitchConverter extends ASTVisitor
	{
		private final List<SwitchStatement> switchStatements = new ArrayList<SwitchStatement>();

		/**
		 * @see ASTVisitor#visit(SwitchStatement)
		 */
		@Override
		public boolean visit(SwitchStatement node)
		{
			// collect SwitchStatement node
			switchStatements.add(node);
			return super.visit(node);
		}

		/**
		 * Sort the switch case in lexicographic order
		 */
		public void convert() throws Exception
		{
			SwitchCaseComparator switchCaseComparator = new SwitchCaseComparator();
			// SwitchStatements process
			for (SwitchStatement switchStatement : switchStatements)
			{
				System.out.println("-------- initial ----------");
				System.out.print(switchStatement);
				// list of case expressions
				LinkedList<SwitchCase> switchCases = new LinkedList<SwitchCase>();
				// statements of case expressions
				Map<SwitchCase, List<Object>> switchCaseStatements = new HashMap<SwitchCase, List<Object>>();
				// extract switch cases & statements
				extractSwitchCaseAndStatements(switchStatement, switchCases, switchCaseStatements);
				// sort switch case by lexicographic order
				switchCases.sort(switchCaseComparator);
				// optimize switch case statements
				optimizeSwitchCaseStatements(switchCases, switchCaseStatements);
				// replace switch cases & statements
				replaceSwitchCaseAndStatements(switchStatement, switchCases, switchCaseStatements);
				System.out.println("-------- final ----------");
				System.out.print(switchStatement);
			}
		}

		@SuppressWarnings("unchecked")
		private void extractSwitchCaseAndStatements(SwitchStatement switchStatement, List<SwitchCase> switchCases, Map<SwitchCase, List<Object>> switchCaseStatements)
		{
			// statements process
			for (Object statement : switchStatement.statements())
			{
				// for each switch case
				if (statement instanceof SwitchCase)
				{
					SwitchCase switchCase = (SwitchCase) statement;
					// get all statements for the switch case
					List<Object> switchCaseExpressionStatements = getStatementsUntilBreak(switchStatement.statements(), switchCase);
					// store switch case
					switchCases.add(switchCase);
					// store switch case statements
					switchCaseStatements.put(switchCase, switchCaseExpressionStatements);
				}
			}
		}

		private List<Object> getStatementsUntilBreak(List<Object> statements, SwitchCase start)
		{
			LinkedList<Object> result = new LinkedList<Object>();
			boolean started = false;
			// statements process
			for (Object statement : statements)
			{
				if (started)
				{
					// do not get other switch cases
					if (!(statement instanceof SwitchCase))
						result.addLast(statement);
					// end of statements
					if (statement instanceof BreakStatement)
						break;
				}
				else if (start == statement) // start switch case
					started = true;
			}
			// if there is not a break statement at the end : add one
			if (!(result.getLast() instanceof BreakStatement))
			{
				result.addLast(start.getAST().createInstance(BreakStatement.class));
			}
			return result;
		}

		private void optimizeSwitchCaseStatements(LinkedList<SwitchCase> switchCases, Map<SwitchCase, List<Object>> switchCaseStatements)
		{
			List<Object> previous = null;
			// switch case process
			for (SwitchCase switchCase : switchCases)
			{
				// get current switch statements
				List<Object> current = switchCaseStatements.get(switchCase);
				// if current is identical to the end of previous
				if (previous != null && previousEndsWithCurrent(previous, current))
				{
					// remove end of previous
					removeLasts(previous, current.size());
				}
				previous = current;
				// remove last switch case break statement
				if (switchCase == switchCases.getLast())
				{
					int lastIndex = current.size() - 1;
					Object lastStatement = current.get(lastIndex);
					if (lastStatement instanceof BreakStatement)
						current.remove(lastIndex);
				}
			}
		}

		private boolean previousEndsWithCurrent(List<Object> previous, List<Object> current)
		{
			int currentSize = current.size();
			int deltaSize = previous.size() - currentSize;
			// previous has to be longer than current
			if (deltaSize < 0)
				return false;
			// starting before break, decreasing until statement begining
			for (int index = currentSize - 2; index >= 0; index--)
			{
				if (!previous.get(index + deltaSize).toString().equals(current.get(index).toString()))
				{
					return false;
				}
			}
			return true;
		}

		private void removeLasts(List<Object> list, int sizeToRemove)
		{
			for (int i = 0; i < sizeToRemove; i++)
			{
				// remove last elements
				list.remove(list.size() - 1);
			}
		}

		@SuppressWarnings("unchecked")
		private void replaceSwitchCaseAndStatements(SwitchStatement switchStatement, LinkedList<SwitchCase> switchCases, Map<SwitchCase, List<Object>> switchCaseStatements)
		{
			// remove all current switch statements
			switchStatement.statements().clear();
			// for each sorted switch case
			for (SwitchCase switchCase : switchCases)
			{
				// add switch case
				switchStatement.statements().add(switchCase);
				for (Object switchCaseExpressionStatement : switchCaseStatements.get(switchCase))
				{
					if (switchStatement.statements().contains(switchCaseExpressionStatement))
					{
						// clone node
						switchCaseExpressionStatement = ASTNode.copySubtree(switchStatement.getAST(), (ASTNode) switchCaseExpressionStatement);
					}
					// add switch case statement
					switchStatement.statements().add(switchCaseExpressionStatement);
				}
			}
		}

	}

	@Override
	public final void refactor(final CompilationUnit inputAST) throws RefactoringException
	{
		SwitchConverter converter = new SwitchConverter();
		// collect usefull nodes
		inputAST.accept(converter);
		try
		{
			// do the conversion
			converter.convert();
		}
		catch (Exception e)
		{
			throw new RefactoringException(e.getMessage(), e);
		}
	}
}

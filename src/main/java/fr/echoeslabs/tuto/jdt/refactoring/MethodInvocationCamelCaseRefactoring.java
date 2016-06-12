package fr.echoeslabs.tuto.jdt.refactoring;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
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
	private final class SwitchCaseComparator implements Comparator<SwitchCase>
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

	private final class SwitchConverter extends ASTVisitor
	{
		private final List<SwitchStatement> switchStatements = new ArrayList<SwitchStatement>();

		/**
		 * @see ASTVisitor#visit(SwitchStatement)
		 */
		@Override
		public final boolean visit(final SwitchStatement node)
		{
			// collect SwitchStatement node
			switchStatements.add(node);
			return super.visit(node);
		}

		/**
		 * Sort the switch case in lexicographic order
		 */
		public final void convert() throws Exception
		{
			SwitchCaseComparator switchCaseComparator = new SwitchCaseComparator();
			// SwitchStatements process
			switchStatements.forEach(switchStatement -> {
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
			});
		}

		/**
		 * extract all switch case and their statements
		 * 
		 * @param switchStatement switch statement input
		 * @param switchCases switch case list output
		 * @param switchCaseStatements switch case / statement map output
		 */
		@SuppressWarnings("unchecked")
		private void extractSwitchCaseAndStatements(final SwitchStatement switchStatement, final List<SwitchCase> switchCases, final Map<SwitchCase, List<Object>> switchCaseStatements)
		{
			// statements process
			switchStatement.statements().forEach(statement -> {
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
						});
		}

		/**
		 * return the statements of a switch case until a break or the end (and so add a break)
		 * 
		 * @param statements whole switch statements input
		 * @param start switch statement (case) from which start
		 * @return switch statements
		 */
		private List<Object> getStatementsUntilBreak(final List<Object> statements, final SwitchCase start)
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

		/**
		 * optimize switch statements of switch cases
		 * 
		 * @param switchCases switch cases input
		 * @param switchCaseStatements switch case / statement map input/output
		 */
		private void optimizeSwitchCaseStatements(final LinkedList<SwitchCase> switchCases, final Map<SwitchCase, List<Object>> switchCaseStatements)
		{
			// remove duplications from a switch case to the next one
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
			}
			// remove last break of the last switch case
			{
				List<Object> lastCaseStatements = switchCaseStatements.get(switchCases.getLast());
				int lastIndex = lastCaseStatements.size() - 1;
				Object lastStatement = lastCaseStatements.get(lastIndex);
				// if last statement is break (it should be!)
				if (lastStatement instanceof BreakStatement)
					// remove break statement
					lastCaseStatements.remove(lastIndex);
			}
		}

		/**
		 * Check if a list (previous) ends with another one (current)
		 * 
		 * @param previousList list input
		 * @param currentList list input 
		 * @return true if end of previous list is equal to current list, false otherwise
		 */
		private boolean previousEndsWithCurrent(final List<Object> previousList, final List<Object> currentList)
		{
			int delta = previousList.size() - currentList.size();
			// previous has to be longer than current
			if (delta < 0)
				return false;
			// get end of previous list iterator
			Iterator<Object> previousIt = previousList.subList(delta, previousList.size()).iterator();
			for (Object currentObject : currentList)
			{
				// if current element isn't equal to previous one (from sub list)
				if (!previousIt.next().toString().equals(currentObject.toString()))
				{
					return false;
				}
			}
			return true;
		}

		/**
		 * Remove the last elements of a list
		 * 
		 * @param list list from which some last element will be removed
		 * @param sizeToRemove number of element to remove from the end of the list
		 */
		private void removeLasts(final List<Object> list, int sizeToRemove)
		{
			for (int i = 0; i < sizeToRemove; i++)
			{
				// remove last elements
				list.remove(list.size() - 1);
			}
		}

		/**
		 * Clear and then insert statement in switch
		 * @param switchStatement switch statement output
		 * @param switchCases switch case list input
		 * @param switchCaseStatements switch case / statement map input
		 */
		@SuppressWarnings("unchecked")
		private void replaceSwitchCaseAndStatements(final SwitchStatement switchStatement, final LinkedList<SwitchCase> switchCases, final Map<SwitchCase, List<Object>> switchCaseStatements)
		{
			// remove all current switch statements
			switchStatement.statements().clear();
			// for each sorted switch case
			switchCases.forEach(switchCase -> {
				// add switch case
				switchStatement.statements().add(switchCase);
				switchCaseStatements.get(switchCase).forEach(switchCaseExpressionStatement -> {
					if (switchStatement.statements().contains(switchCaseExpressionStatement))
					{
						// clone node
								switchCaseExpressionStatement = ASTNode.copySubtree(switchStatement.getAST(), (ASTNode) switchCaseExpressionStatement);
							}
							// add switch case statement
							switchStatement.statements().add(switchCaseExpressionStatement);
						});
			});
		}

	}

	/**
	 * @see AbstractRefactoring#refactor(CompilationUnit)
	 */
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

package fr.echoeslabs.tuto.jdt.refactoring;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;

import fr.echoeslabs.tuto.jdt.refactoring.api.AbstractRefactoring;
import fr.echoeslabs.tuto.jdt.refactoring.api.RefactoringException;

/**
 * This refactoring reorders the case blocks within switches in lexicographic
 * order.
 */
public class SwitchOrderingRefactoring extends AbstractRefactoring {
	List<BreakBlock<SwitchCase, List<Statement>>> cases;
	BreakBlock<SwitchCase, List<Statement>> breakBlock;
	List<Statement> caseStatements;
	
	static Comparator<? super BreakBlock<SwitchCase, List<Statement>>> comparator = new BreakBlockComparator();
	
	private final static class SwitchCollector extends ASTVisitor {

		private final List<SwitchStatement> switchStatements = new ArrayList<SwitchStatement>();

		public final List<SwitchStatement> getSwitchStatements() {
			return this.switchStatements;
		}

		@Override
		public final boolean visit(final SwitchStatement node) {
			getSwitchStatements().add(node);
			return super.visit(node);
		}
	}

	private static class LexicalOrderComparator implements Comparator<String> {

		@Override
		public int compare(String o1, String o2) {
			return 0;
		}

	}

	private class BreakBlockConsumer implements Consumer<Statement> {
		@Override
		public void accept(Statement statement) {
				
			if (statement instanceof SwitchCase) {
				SwitchCase switchCase = (SwitchCase) statement;
				if (breakBlock == null) {
					caseStatements = new ArrayList<Statement>();
					breakBlock = new BreakBlock<SwitchCase, List<Statement>>(switchCase, caseStatements);
					cases.add(breakBlock);
				}
				caseStatements.add(statement);
			} else if (statement instanceof BreakStatement) {
				caseStatements.add(statement);
				breakBlock = null;
			} else
				caseStatements.add(statement);
		}
	}

	private static class BreakBlockComparator implements Comparator<BreakBlock<SwitchCase, List<Statement>>> {

		@Override
		public int compare(BreakBlock<SwitchCase, List<Statement>> left, BreakBlock<SwitchCase, List<Statement>> right) {
			Expression le = left.first().getExpression();
			Expression re = right.first().getExpression();
			if (le == null)
				return +1;
			if (re == null)
				return -1;
			return le.toString().compareTo(re.toString());
		}
		
	}
	
	private void reorder(SwitchStatement switchStatement) {// SwitchCase
		AST ast = switchStatement.getAST();
		cases = new ArrayList<BreakBlock<SwitchCase, List<Statement>>>();
		List<Statement> statements = switchStatement.statements();
		Consumer breakBlockConsumer = new BreakBlockConsumer();

		breakBlock = null;caseStatements = null;
		statements.forEach(breakBlockConsumer);
		
		Statement last = caseStatements.get(caseStatements.size() - 1);
		if (!(last instanceof BreakStatement)) {
			caseStatements.add(ast.newBreakStatement());
		}
		
		cases.sort(comparator);
		
		
		statements.clear();
		for(BreakBlock<SwitchCase, List<Statement>> kase : cases) {
			statements.addAll(kase.second());
		}
		last = statements.get(statements.size() - 1);
		if (last instanceof BreakStatement)
			statements.remove(statements.size() - 1);
	}

	@Override
	public final void refactor(final CompilationUnit inputAST) throws RefactoringException {
		// do not change AST
		final SwitchCollector switchCollector = new SwitchCollector();
		inputAST.accept(switchCollector);
		
		final List<SwitchStatement> switchStatements = switchCollector.getSwitchStatements();
		for (final SwitchStatement switchStatement : switchStatements) {
			reorder(switchStatement);
		}

	}
}

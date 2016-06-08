package fr.echoeslabs.tuto.jdt.refactoring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.text.BadLocationException;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import fr.echoeslabs.tuto.jdt.refactoring.api.AbstractRefactoring;
import fr.echoeslabs.tuto.jdt.refactoring.api.RefactoringException;

/**
 * This refactoring reorders the case blocks within switches in lexicographic
 * order.
 */
public class SwitchOrderingRefactoring extends AbstractRefactoring {

	private static Comparator<? super BreakBlock<SwitchCase, List<Statement>>> comparator = new BreakBlockComparator();

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

	private static class BreakBlockComparator implements Comparator<BreakBlock<SwitchCase, List<Statement>>> {

		@Override
		public int compare(BreakBlock<SwitchCase, List<Statement>> left,
				BreakBlock<SwitchCase, List<Statement>> right) {
			Expression le = left.first().getExpression();
			Expression re = right.first().getExpression();
			if (le == null)
				return +1;
			if (re == null)
				return -1;
			return le.toString().compareTo(re.toString());
		}

	}

	@SuppressWarnings("unchecked")
	private void reorder(SwitchStatement switchStatement) {// SwitchCase
		AST ast = switchStatement.getAST();

		List<BreakBlock<SwitchCase, List<Statement>>> cases;
		BreakBlock<SwitchCase, List<Statement>> breakBlock = null;
		List<Statement> caseStatements = null;
		
		cases = new ArrayList<BreakBlock<SwitchCase, List<Statement>>>();

		List<Statement> statements = switchStatement.statements();
		
		Iterator<Statement> statementIterator = statements.iterator();
		while (statementIterator.hasNext()) {
			Statement statement = statementIterator.next();
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

		Statement last = caseStatements.get(caseStatements.size() - 1);
		if (!(last instanceof BreakStatement)) {
			caseStatements.add(ast.newBreakStatement());
		}

		// cases.sort(comparator);
		Collections.sort(cases, comparator);

		statements.clear();
		for (BreakBlock<SwitchCase, List<Statement>> kase : cases) {
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

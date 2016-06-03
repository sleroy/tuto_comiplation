package fr.echoeslabs.tuto.jdt.test;

import java.io.File;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.Test;

import fr.echoeslabs.tuto.jdt.refactoring.CamelCaseMethodName;
import fr.echoeslabs.tuto.jdt.refactoring.NoChangeRefactoring;
import fr.echoeslabs.tuto.jdt.refactoring.api.RefactoringException;
import fr.echoeslabs.tuto.jdt.util.JavaUtils;

public class RefactorTest extends AbstractRefactoringTest {

	@Test
	public void testSample1() throws RefactoringException {
		final String inputJavaFilePath = "src/test/resources/java_input/Sample1.java";
		final File inputJavaFile = new File(inputJavaFilePath);

 		final CompilationUnit parseOriginalAST = JavaUtils.parse(inputJavaFile);
		final String _expectedOutputContent = JavaUtils.formatCode(parseOriginalAST.toString());

		performTest(NoChangeRefactoring.class, inputJavaFile, _expectedOutputContent);
	}

	@Test
	public void testeCamelCaseRefactoring() throws RefactoringException {
		final String inputJavaFilePath = "src/test/resources/java_input/Sample2.java";
		final String exptectedJavaFilePath = "src/test/resources/java_input/CamelCaseRefactoring/Sample2.java.expected";

		setOutputFilePath("src/test/resources/java_input/CamelCaseRefactoring/Sample2.java.output");
		performFileTest(CamelCaseMethodName.class, inputJavaFilePath, exptectedJavaFilePath);
	}

}

package fr.echoeslabs.tuto.jdt.test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.junit.Test;

import fr.echoeslabs.tuto.jdt.refactoring.MethodInvocationCamelCaseRefactoring;
import fr.echoeslabs.tuto.jdt.refactoring.MethodNameCamelCaseRefactoring;
import fr.echoeslabs.tuto.jdt.refactoring.NoChangeRefactoring;
import fr.echoeslabs.tuto.jdt.refactoring.SwitchOrderingRefactoring;
import fr.echoeslabs.tuto.jdt.refactoring.api.RefactoringException;
import fr.echoeslabs.tuto.jdt.util.JavaUtils;

public class RefactorTest extends AbstractRefactoringTest {
	@SuppressWarnings("unchecked")
	private String reformat(final String sourcePath) throws IOException, Exception {
		// take default Eclipse formatting options
		Map<String, String> options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();

		// initialize the compiler settings to be able to format 1.5 code
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_7);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_7);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_7);

		// instantiate the default code formatter with the given options
		final CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(options);

		String source = null;
		source = FileUtils.readFileToString(new File(sourcePath));
		final TextEdit edit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, source, 0, source.length(), 0,
				System.getProperty("line.separator"));

		IDocument document = new Document(source);
		edit.apply(document);
		return document.get();
	}

	@Test
	public void testSample1() throws RefactoringException {
		final String inputJavaFilePath = "src/test/resources/java_input/Sample1.java";
		final File inputJavaFile = new File(inputJavaFilePath);

		final CompilationUnit parseOriginalAST = JavaUtils.parse(inputJavaFile);
		this.performTest(NoChangeRefactoring.class, inputJavaFile, parseOriginalAST.toString());
	}

	@Test
	public void testCamelCaseRefactoring() throws RefactoringException {
		final String inputJavaFilePath = "src/test/resources/java_input/CamelCaseRefactoring/Sample2.java";
		final String exptectedJavaFilePath = "src/test/resources/java_input/CamelCaseRefactoring/Sample2.java.expected";
		final String outputJavaFilePath = "src/test/resources/java_input/CamelCaseRefactoring/Sample2.java.output";

		this.setOutputFilePath(outputJavaFilePath);
		this.setExpectedFilePath(exptectedJavaFilePath);
		this.performFileTest(MethodNameCamelCaseRefactoring.class, inputJavaFilePath);
	}

	@Test
	public void testMethodInvocationToCamelCaseRefactoring() throws RefactoringException {
		final String inputJavaFilePath = "src/test/resources/java_input/CamelCaseRefactoring/Exercise1.java";
		final String exptectedJavaFilePath = "src/test/resources/java_input/CamelCaseRefactoring/Exercise1.java.expected";
		final String outputJavaFilePath = "src/test/resources/java_input/CamelCaseRefactoring/Exercise1.java.output";

		this.setOutputFilePath(outputJavaFilePath);
		// this.setExpectedFilePath(exptectedJavaFilePath);
		this.performFileTest(MethodInvocationCamelCaseRefactoring.class, inputJavaFilePath);

	}

	@Test
	public void testSwitchOrderingRefactoring() throws RefactoringException {
		final String inputJavaFilePath = "src/test/resources/java_input/SwitchOrdering/Exercise2.java";
		final String exptectedJavaFilePath = "src/test/resources/java_input/SwitchOrdering/Exercise2.java.expected";
		final String outputJavaFIlePath = "src/test/resources/java_input/SwitchOrdering/Exercise2.java.output";

		this.setOutputFilePath(outputJavaFIlePath);
		// this.setExpectedFilePath(exptectedJavaFilePath);
		this.performFileTest(SwitchOrderingRefactoring.class, inputJavaFilePath);

		try {
			String reformattedJavaFile = reformat(outputJavaFIlePath);
			FileUtils.write(new File(outputJavaFIlePath), reformattedJavaFile);
		} catch (Exception e) {
			throw new RefactoringException(e.getMessage(), e);
		}
	}
}

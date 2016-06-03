package fr.echoeslabs.tuto.jdt.test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.Assert;

import fr.echoeslabs.tuto.jdt.refactoring.api.AbstractRefactoring;
import fr.echoeslabs.tuto.jdt.refactoring.api.RefactoringException;
import fr.echoeslabs.tuto.jdt.util.JavaUtils;

public abstract class AbstractRefactoringTest {

	private String outputFilePath = null;

	public void performFileTest(final Class<? extends AbstractRefactoring> refactoringClass, final String _inputJavaFilePath,
			final String _expectedOutputFilePath) throws RefactoringException {

		final File _expectedOutputFile = new File(_expectedOutputFilePath);
		final File _inputJavaFile = new File(_inputJavaFilePath);

		final String expectedJavaContent;
		try {
			expectedJavaContent = FileUtils.readFileToString(_expectedOutputFile);
		} catch (final IOException e) {
			throw new RefactoringException("Could not read expected output java content file", e);
		}
		this.performTest(refactoringClass, _inputJavaFile, expectedJavaContent);
	}

	public void performTest(final Class<? extends AbstractRefactoring> refactoringClass, final File _inputJavaFile, final String _expectedOutputContent)
			throws RefactoringException {
		final String inputJavaContent;
		try {
			inputJavaContent = FileUtils.readFileToString(_inputJavaFile);
		} catch (final IOException e) {
			throw new RefactoringException("Could not read input file", e);
		}
		this.performTest(refactoringClass, inputJavaContent, _expectedOutputContent);
	}

	public void performTest(final Class<? extends AbstractRefactoring> refactoringClass, final String _inputJavaContent, final String _expectedOutputContent)
			throws RefactoringException {
		final String finalCode;
		try {
			final Method method = refactoringClass.getMethod("refactor", CompilationUnit.class);
			final CompilationUnit ast = JavaUtils.parse(_inputJavaContent);
			Assert.assertNotNull(ast);
			final AbstractRefactoring newInstance = refactoringClass.newInstance();
			method.invoke(newInstance, ast);
			finalCode = ast.toString();
			if (this.outputFilePath != null) {
				final File outputFile = new File(this.outputFilePath);
				FileUtils.writeStringToFile(outputFile, finalCode);
			}
		} catch (final Exception e) {
			throw new RefactoringException("Could not apply refactoring", e);
		} finally {
			this.outputFilePath = null;
		}
		Assert.assertEquals(_expectedOutputContent, finalCode);
	}

	public void setOutputFilePath(final String _filePath) {
		this.outputFilePath = _filePath;
	}

}

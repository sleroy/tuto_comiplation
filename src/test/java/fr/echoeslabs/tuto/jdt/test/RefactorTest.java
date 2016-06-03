package fr.echoeslabs.tuto.jdt.test;

import java.io.File;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.Test;

import fr.echoeslabs.tuto.jdt.util.JavaUtils;

public class RefactorTest {

	@Test
	public void testSample1() {
		final String inputJavaFilePath = "src/test/resources/java_input/Sample1.java";
		final File inputJavaFile = new File(inputJavaFilePath);
		final CompilationUnit parse = JavaUtils.parse(inputJavaFile);
		final String formatCode = JavaUtils.formatCode(parse.toString());
		System.out.println(formatCode);
	}
}

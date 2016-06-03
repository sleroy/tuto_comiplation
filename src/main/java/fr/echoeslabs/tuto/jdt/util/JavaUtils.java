/*
 * Echoes-Labs.
 */
package fr.echoeslabs.tuto.jdt.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class JavaUtils.
 *
 * @author amorvan
 */
public class JavaUtils {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(JavaUtils.class);

	/**
	 * Mute syserr.
	 *
	 * @return the prints the stream
	 */
	private static final PrintStream muteSyserr() {
		final PrintStream err = System.err;
		System.setErr(new PrintStream(new OutputStream() {

			@Override
			public void write(final int b) throws IOException {
			}
		}));
		return err;
	}

	/**
	 * Parses the content of the given File to obtain a JS AST.
	 *
	 * @param _javaSourceFile
	 *            the _java source file
	 * @return the compilation unit
	 */
	public final static CompilationUnit parse(final File _javaSourceFile) {
		final String javaContent;
		try {
			javaContent = FileUtils.readFileToString(_javaSourceFile);
		} catch (final IOException e) {
			throw new RuntimeException(String.format("Could not read java source file (%s).", _javaSourceFile.getPath()), e);
		}
		return parse(javaContent);
	}

	/**
	 * Parses the content to obtain a JS AST.
	 *
	 * @param _content
	 *            the _script
	 * @return the java script unit
	 */
	public final static CompilationUnit parse(final String _content) {
		LOGGER.trace("JS Parsing {}", _content);
		final ASTParser newParser;
		newParser = ASTParser.newParser(AST.JLS8);
		newParser.setKind(ASTParser.K_COMPILATION_UNIT);
		final Map<String, String> options = new HashMap<String, String>();
		options.put("org.eclipse.jdt.core.compiler.compliance", "1.7");
		options.put("org.eclipse.jdt.core.compiler.source", "1.7");
		options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.7");
		newParser.setCompilerOptions(options);
		newParser.setResolveBindings(true);
		newParser.setBindingsRecovery(true);
		newParser.setStatementsRecovery(true);
		newParser.setSource(_content.toCharArray());
		final PrintStream muteSyserr = muteSyserr();
		final CompilationUnit result = (CompilationUnit) newParser.createAST(null);
		unmuteSyserr(muteSyserr);
		return result;
	}

	/**
	 * Unmute syserr.
	 *
	 * @param err
	 *            the err
	 */
	private static final void unmuteSyserr(final PrintStream err) {
		System.setErr(err);
	}
}

/*
 * Echoes-Labs.
 */
package fr.echoeslabs.tuto.jdt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class JavaScriptParser.
 *
 * @author amorvan
 */
public class JavaParser {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(JavaParser.class);

	/**
	 * Instantiates a new java script parser.
	 */
	public JavaParser() {
		super();
	}

	/**
	 * Parses the content to obtain a JS AST.
	 *
	 * @param content
	 *            the _script
	 * @return the java script unit
	 * @throws ParsingFailedException
	 *             the parsing failed exception
	 */
	public CompilationUnit parse(final String content) {
		LOGGER.debug("JS Parsing {}", content);
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
		newParser.setSource(content.toCharArray());
		final CompilationUnit result = (CompilationUnit) newParser.createAST(null);
		return result;
	}

}

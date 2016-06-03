/*
 * Echoes-Labs.
 */
package fr.echoeslabs.tuto.jdt;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
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
	 * Format code.
	 *
	 * @param code
	 *            the code
	 * @return the string
	 */
	public synchronized static final String formatCode(final String code) {
		final CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(null);
		// mute System.err
		final PrintStream err = System.err;
		System.setErr(new PrintStream(new OutputStream() {
			@Override
			public void write(final int b) throws IOException {
			}
		}));
		final TextEdit textEdit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT | CodeFormatter.F_INCLUDE_COMMENTS, code, 0, code.length(), 0, null);
		// unmute System.err
		System.setErr(err);
		final IDocument doc = new Document(code);
		try {
			textEdit.apply(doc);
		} catch (final Exception e) {
			LOGGER.error(e.getMessage(), e);
			return code;
		}
		return doc.get();
	}
}

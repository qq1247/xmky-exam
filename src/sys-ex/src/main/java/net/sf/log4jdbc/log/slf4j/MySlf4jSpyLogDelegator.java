package net.sf.log4jdbc.log.slf4j;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.log4jdbc.Properties;
import net.sf.log4jdbc.sql.Spy;

public class MySlf4jSpyLogDelegator extends Slf4jSpyLogDelegator {
	private final Logger sqlTimingLogger = LoggerFactory.getLogger("jdbc.sqltiming");
	private static String nl = " ";

	@Override
	public void sqlTimingOccurred(Spy spy, long execTime, String methodCall, String sql) {
		if ("getGeneratedKeys()".equals(methodCall)) {// 这个和insert打印一行重复，不需要
			return;
		}
		
		if (sqlTimingLogger.isErrorEnabled() && (!Properties.isDumpSqlFilteringOn() || shouldSqlBeLogged(sql))) {
			if (Properties.isSqlTimingErrorThresholdEnabled()
					&& execTime >= Properties.getSqlTimingErrorThresholdMsec()) {
				sqlTimingLogger
						.error(buildSqlTimingDump(spy, execTime, methodCall, sql, sqlTimingLogger.isDebugEnabled()));
			} else if (sqlTimingLogger.isWarnEnabled()) {
				if (Properties.isSqlTimingWarnThresholdEnabled()
						&& execTime >= Properties.getSqlTimingWarnThresholdMsec()) {
					sqlTimingLogger
							.warn(buildSqlTimingDump(spy, execTime, methodCall, sql, sqlTimingLogger.isDebugEnabled()));
				} else if (sqlTimingLogger.isDebugEnabled()) {
					sqlTimingLogger.debug(buildSqlTimingDump(spy, execTime, methodCall, sql, true));
				} else if (sqlTimingLogger.isInfoEnabled()) {
					sqlTimingLogger.info(buildSqlTimingDump(spy, execTime, methodCall, sql, false));
				}
			}
		}
	}

	private String buildSqlTimingDump(Spy spy, long execTime, String methodCall, String sql, boolean debugInfo) {
		StringBuffer out = new StringBuffer();

		if (debugInfo) {
			out.append(getDebugInfo());
			out.append(nl);
			out.append(spy.getConnectionNumber());
			out.append(". ");
		}

		// NOTE: if both sql dump and sql timing dump are on, the processSql
		// algorithm will run TWICE once at the beginning and once at the end
		// this is not very efficient but usually
		// only one or the other dump should be on and not both.

		sql = processSql(sql);

		out.append(sql);
		out.append(" {executed in ");
		out.append(execTime);
		out.append(" msec}");

		return out.toString();
	}

	private boolean shouldSqlBeLogged(String sql) {
		if (sql == null) {
			return false;
		}
		sql = sql.trim();

		if (sql.length() < 6) {
			return false;
		}
		sql = sql.substring(0, 6).toLowerCase();
		return (Properties.isDumpSqlSelect() && "select".equals(sql))
				|| (Properties.isDumpSqlInsert() && "insert".equals(sql))
				|| (Properties.isDumpSqlUpdate() && "update".equals(sql))
				|| (Properties.isDumpSqlDelete() && "delete".equals(sql))
				|| (Properties.isDumpSqlCreate() && "create".equals(sql));
	}

	private static String getDebugInfo() {
		Throwable t = new Throwable();
		t.fillInStackTrace();

		StackTraceElement[] stackTrace = t.getStackTrace();

		if (stackTrace != null) {
			String className;

			StringBuffer dump = new StringBuffer();

			/**
			 * The DumpFullDebugStackTrace option is useful in some situations
			 * when we want to see the full stack trace in the debug info- watch
			 * out though as this will make the logs HUGE!
			 */
			if (Properties.isDumpFullDebugStackTrace()) {
				boolean first = true;
				for (int i = 0; i < stackTrace.length; i++) {
					className = stackTrace[i].getClassName();
					if (!className.startsWith("net.sf.log4jdbc")) {
						if (first) {
							first = false;
						} else {
							dump.append("  ");
						}
						dump.append("at ");
						dump.append(stackTrace[i]);
						dump.append(nl);
					}
				}
			} else {
				dump.append(" ");
				int firstLog4jdbcCall = 0;
				int lastApplicationCall = 0;

				for (int i = 0; i < stackTrace.length; i++) {
					className = stackTrace[i].getClassName();
					if (className.startsWith("net.sf.log4jdbc")) {
						firstLog4jdbcCall = i;
					} else if (Properties.isTraceFromApplication()
							&& Pattern.matches(Properties.getDebugStackPrefix(), className)) {
						lastApplicationCall = i;
						break;
					}
				}
				int j = lastApplicationCall;

				if (j == 0) // if app not found, then use whoever was the last
							// guy that called a log4jdbc class.
				{
					j = 1 + firstLog4jdbcCall;
				}

				dump.append(stackTrace[j].getClassName()).append(".").append(stackTrace[j].getMethodName()).append("(")
						.append(stackTrace[j].getFileName()).append(":").append(stackTrace[j].getLineNumber())
						.append(")");
			}

			return dump.toString();
		}
		return null;
	}

	private String processSql(String sql) {
		if (sql == null) {
			return null;
		}

		if (Properties.isSqlTrim()) {
			sql = sql.trim();
		}

		StringBuilder output = new StringBuilder();

		if (Properties.getDumpSqlMaxLineLength() <= 0) {
			output.append(sql);
		} else {
			// insert line breaks into sql to make it more readable
			StringTokenizer st = new StringTokenizer(sql);
			String token;
			int linelength = 0;

			while (st.hasMoreElements()) {
				token = (String) st.nextElement();

				output.append(token);
				linelength += token.length();
				output.append(" ");
				linelength++;
				if (linelength > Properties.getDumpSqlMaxLineLength()) {
					output.append(nl);
					linelength = 0;
				}
			}
		}

		if (Properties.isDumpSqlAddSemicolon()) {
			output.append(";");
		}

		String stringOutput = output.toString();

		if (Properties.isTrimExtraBlankLinesInSql()) {
			LineNumberReader lineReader = new LineNumberReader(new StringReader(stringOutput));

			output = new StringBuilder();

			int contiguousBlankLines = 0;
			try {
				while (true) {
					String line = lineReader.readLine();
					if (line == null) {
						break;
					}

					// is this line blank?
					if (line.trim().length() == 0) {
						contiguousBlankLines++;
						// skip contiguous blank lines
						if (contiguousBlankLines > 1) {
							continue;
						}
					} else {
						contiguousBlankLines = 0;
						output.append(line);
					}
					output.append(nl);
				}
			} catch (IOException e) {
				// since we are reading from a buffer, this isn't likely to
				// happen,
				// but if it does we just ignore it and treat it like its the
				// end of the stream
			}
			stringOutput = output.toString();
		}

		return stringOutput;
	}
}

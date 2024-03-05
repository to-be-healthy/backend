package com.tobe.healthy.config;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import jakarta.annotation.PostConstruct;
import java.util.Locale;
import java.util.Stack;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.springframework.context.annotation.Configuration;

@Configuration
public class P6SpySqlFormatConfig implements MessageFormattingStrategy {

	@PostConstruct
	public void setLogMessageFormat() {
		P6SpyOptions.getActiveInstance().setLogMessageFormat(this.getClass().getName());
	}

	@Override
	public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
		sql = formatSql(category, sql);

		if (sql.trim().isEmpty()) {
			return "";
		}

		return String.format("[%s] | %d ms | %s", category, elapsed, sql + createStack(connectionId, elapsed));
	}

	private String formatSql(String category, String sql) {
		if (sql != null && !sql.trim().isEmpty() && Category.STATEMENT.getName().equals(category)) {
			String trimmedSQL = sql.trim().toLowerCase(Locale.ROOT);
			if (trimmedSQL.startsWith("create") || trimmedSQL.startsWith("alter") || trimmedSQL.startsWith("comment")) {
				sql = FormatStyle.DDL.getFormatter().format(sql);
			} else {
				sql = FormatStyle.BASIC.getFormatter().format(sql);
			}
			return sql;
		}

		return sql;
	}

	// stack 콘솔 표기
	private String createStack(int connectionId, long elapsed) {
		Stack<String> callStack  = new Stack<>();
		StackTraceElement[] stackTrace = new Throwable().getStackTrace();

		for (StackTraceElement stackTraceElement : stackTrace) {
			String trace = stackTraceElement.toString();
			if (trace.startsWith("octopus.backend")) {
				callStack.push(trace);
			}
		}

		StringBuffer sb = new StringBuffer();
		int order = 1;
		while (callStack.size() != 0) {
			sb.append("\n\t\t" + (order++) + "." + callStack.pop());
		}

		return new StringBuffer()
				.append("\n\n\tConnection ID:").append(connectionId).append(" | Excution Time:").append(elapsed).append(" ms\n")
				.append("\n--------------------------------------\n").toString();
	}
}

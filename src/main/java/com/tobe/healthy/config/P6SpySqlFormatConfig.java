package com.tobe.healthy.config;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.Stack;

import static com.p6spy.engine.logging.Category.STATEMENT;
import static com.p6spy.engine.spy.P6SpyOptions.getActiveInstance;
import static java.util.Locale.ROOT;
import static org.hibernate.engine.jdbc.internal.FormatStyle.BASIC;
import static org.hibernate.engine.jdbc.internal.FormatStyle.DDL;
import static org.springframework.util.StringUtils.hasText;

@Configuration
public class P6SpySqlFormatConfig implements MessageFormattingStrategy {

	@PostConstruct
	public void setLogMessageFormat() {
		getActiveInstance().setLogMessageFormat(this.getClass().getName());
	}

	@Override
	public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
		sql = formatSql(category, sql);

		if (!hasText(sql)) {
			return "";
		}

		return String.format("[%s] | %d ms | %s", category, elapsed, sql + createStack(connectionId, elapsed));
	}

	private String formatSql(String category, String sql) {
		if (hasText(sql) && STATEMENT.getName().equals(category)) {
			String trimmedSQL = sql.trim().toLowerCase(ROOT);
			if (trimmedSQL.startsWith("create") || trimmedSQL.startsWith("alter") || trimmedSQL.startsWith("comment")) {
				sql = DDL.getFormatter().format(sql);
			} else {
				sql = BASIC.getFormatter().format(sql);
			}
			return sql;
		}
		return sql;
	}

	// stack 콘솔 표기
	private String createStack(int connectionId, long elapsed) {
		Stack<String> callStack = new Stack<>();
		StackTraceElement[] stackTrace = new Throwable().getStackTrace();

		for (StackTraceElement stackTraceElement : stackTrace) {
			String trace = stackTraceElement.toString();
			if (trace.startsWith("octopus.backend")) {
				callStack.push(trace);
			}
		}

		return "\n\n\tConnection ID: " + connectionId +
                " | Execution Time: " + elapsed + " ms\n" +
                "\n--------------------------------------\n";
	}
}

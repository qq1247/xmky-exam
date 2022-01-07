package com.wcpdoc.core.hibernate;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.stereotype.Component;

/**
 * 默认都大写
 * 
 * v1.0 zhanghc 2022年1月7日下午1:02:49
 */
@Component
public class UpperCaseStrategy extends PhysicalNamingStrategyStandardImpl {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("static-access")
	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
		return name.toIdentifier(name.getText().toUpperCase());
	}

}

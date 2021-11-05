package com.wcpdoc.core.hibernate;

import java.io.Serializable;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.exception.spi.Configurable;
import org.hibernate.id.IdentifierGenerator;

import com.wcpdoc.core.util.IdUtil;

/**
 * 雪花算法
 * 
 * v1.0 chenyun 2020年6月18日上午11:25:25
 */
public class SnowFlakeGenerator implements IdentifierGenerator, Configurable {

	@Override
	public void configure(Properties properties) throws HibernateException {
	}

	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		return IdUtil.getInstance().nextId();
	}
}

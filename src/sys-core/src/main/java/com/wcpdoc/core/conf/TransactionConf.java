package com.wcpdoc.core.conf;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * 事务配置
 * 
 * 1.throw RuntimeException，事务回滚 2.try {throw RuntimeException} catch{}，保存 3.try
 * {throw RuntimeException} catch{throw new RuntimeException}，事务回滚
 * 
 * 4.try {throw Exception} catch{}，保存 5.try {throw Exception} catch{throw new
 * RuntimeException}，事务回滚 6.try {throw Exception} catch{throw new Exception}
 * throws Exception，保存异常前的数据 7.try {throw Exception}
 * catch{TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
 * throw new Exception} throws Exception，事务回滚
 * 
 * v1.0 zhanghc 2019年4月12日下午4:06:03
 */
@Aspect
@Configuration
public class TransactionConf {
	@Autowired
	private TransactionManager transactionManager;

	@Bean
	public TransactionInterceptor txAdvice() {
		DefaultTransactionAttribute defaultAttr = new DefaultTransactionAttribute();
		defaultAttr.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DefaultTransactionAttribute readOnlyAttr = new DefaultTransactionAttribute();
		readOnlyAttr.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		readOnlyAttr.setReadOnly(true);

		NameMatchTransactionAttributeSource attributeSource = new NameMatchTransactionAttributeSource();
		attributeSource.addTransactionalMethod("get*", readOnlyAttr);
		attributeSource.addTransactionalMethod("find*", readOnlyAttr);
		attributeSource.addTransactionalMethod("query*", readOnlyAttr);
		attributeSource.addTransactionalMethod("*", defaultAttr);

		return new TransactionInterceptor(transactionManager, attributeSource);
	}

	@Bean
	public Advisor txAdviceAdvisor() {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression("execution(* *..*Service.*(..))");
		return new DefaultPointcutAdvisor(pointcut, txAdvice());
	}
}

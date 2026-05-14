package com.example.apihealthchecksystem.infrastructure.config;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

@Configuration
@EnableTransactionManagement
public class UseCaseTransactionConfig {

  @Bean
  public Advisor useCaseTransactionAdvisor(PlatformTransactionManager transactionManager) {
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    pointcut.setExpression(
        "execution(public * com.example.apihealthchecksystem.application.usecase..*(..))");
    return new DefaultPointcutAdvisor(pointcut, transactionInterceptor(transactionManager));
  }

  private Advice transactionInterceptor(PlatformTransactionManager transactionManager) {
    NameMatchTransactionAttributeSource attributeSource = new NameMatchTransactionAttributeSource();
    attributeSource.addTransactionalMethod("get*", readOnlyTransaction());
    attributeSource.addTransactionalMethod("find*", readOnlyTransaction());
    attributeSource.addTransactionalMethod("list*", readOnlyTransaction());
    attributeSource.addTransactionalMethod("count*", readOnlyTransaction());
    attributeSource.addTransactionalMethod("*", writeTransaction());

    return new TransactionInterceptor(transactionManager, attributeSource);
  }

  private RuleBasedTransactionAttribute readOnlyTransaction() {
    RuleBasedTransactionAttribute attribute = new RuleBasedTransactionAttribute();
    attribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    attribute.setReadOnly(true);
    return attribute;
  }

  private RuleBasedTransactionAttribute writeTransaction() {
    RuleBasedTransactionAttribute attribute = new RuleBasedTransactionAttribute();
    attribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    return attribute;
  }
}

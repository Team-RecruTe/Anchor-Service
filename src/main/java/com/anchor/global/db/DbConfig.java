package com.anchor.global.db;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
public class DbConfig {

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.hikari.master")
  public DataSource masterDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.hikari.slave")
  public DataSource slaveDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      EntityManagerFactoryBuilder builder,
      DataSource lazyRoutingDataSource) {
    return builder
        .dataSource(lazyRoutingDataSource)
        .packages("com.anchor.domain.**.domain")
        .persistenceUnit("common")
        .build();
  }

  @Bean
  public PlatformTransactionManager transactionManager(
      EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }

  @Bean
  public AbstractRoutingDataSource routingDataSource(
      DataSource masterDataSource,
      DataSource slaveDataSource
  ) {
    AbstractRoutingDataSource routingDataSource = new AbstractRoutingDataSource() {
      @Override
      protected Object determineCurrentLookupKey() {
        return RoutingDataSourceManager.getCurrentDataSourceName();
      }
    };
    Map<Object, Object> targetDataSources = new HashMap<>();
    targetDataSources.put(SetDataSource.DataSourceType.MASTER, masterDataSource);
    targetDataSources.put(SetDataSource.DataSourceType.SLAVE, slaveDataSource);

    routingDataSource.setTargetDataSources(targetDataSources);
    routingDataSource.setDefaultTargetDataSource(masterDataSource);

    return routingDataSource;
  }

  @Bean
  public LazyConnectionDataSourceProxy lazyRoutingDataSource(DataSource routingDataSource) {
    return new LazyConnectionDataSourceProxy(routingDataSource);
  }

  @Bean
  public EntityManagerFactoryBuilder entityManagerFactoryBuilder() {
    return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), new HashMap<>(), null);
  }

  @Bean
  public JPAQueryFactory JPAQueryFactory(EntityManager entityManager) {
    return new JPAQueryFactory(entityManager);
  }

  @Bean
  public JdbcTemplate jdbcTemplate(@Qualifier("routingDataSource") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

}
package com.anchor.global.db;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SetDataSourceAspect {

  @Before("@annotation(com.anchor.global.db.SetDataSource) && @annotation(target)")
  public void setDataSource(SetDataSource target) throws Exception {

    if (target.dataSourceType() == SetDataSource.DataSourceType.MASTER
        || target.dataSourceType() == SetDataSource.DataSourceType.SLAVE) {
      RoutingDataSourceManager.setCurrentDataSourceName(target.dataSourceType());
    } else {
      throw new Exception("Wrong DataSource Type : Should Check Exception");
    }

  }

  @After("@annotation(com.anchor.global.db.SetDataSource)")
  public void clearDataSource() {
    RoutingDataSourceManager.removeCurrentDataSourceName();
  }
}
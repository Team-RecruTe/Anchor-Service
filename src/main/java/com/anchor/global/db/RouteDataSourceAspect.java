package com.anchor.global.db;

import static com.anchor.global.db.RouteDataSource.DataSourceType.MASTER;
import static com.anchor.global.db.RouteDataSource.DataSourceType.SLAVE;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RouteDataSourceAspect {

  @Before("@annotation(com.anchor.global.db.RouteDataSource) && @annotation(target)")
  public void setDataSource(RouteDataSource target) throws Exception {
    if (target.dataSourceType() == MASTER
        || target.dataSourceType() == SLAVE) {
      RouteDataSourceManager.setCurrentDataSourceName(target.dataSourceType());
    } else {
      throw new Exception("Wrong DataSource Type : Should Check Exception");
    }
  }

  @After("@annotation(com.anchor.global.db.RouteDataSource)")
  public void clearDataSource() {
    RouteDataSourceManager.removeCurrentDataSourceName();
  }

}
package com.anchor.global.db;

import static com.anchor.global.db.RouteDataSource.DataSourceType.MASTER;
import static com.anchor.global.db.RouteDataSource.DataSourceType.SLAVE;

import org.springframework.transaction.support.TransactionSynchronizationManager;

public class RouteDataSourceManager {

  private static final ThreadLocal<RouteDataSource.DataSourceType> currentDataSourceName = new ThreadLocal<>();

  public static RouteDataSource.DataSourceType getCurrentDataSourceName() {
    if (currentDataSourceName.get() == null) {
      return TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? SLAVE : MASTER;
    }
    return currentDataSourceName.get();
  }

  public static void setCurrentDataSourceName(RouteDataSource.DataSourceType dataSourceType) {
    currentDataSourceName.set(dataSourceType);
  }

  public static void removeCurrentDataSourceName() {
    currentDataSourceName.remove();
  }

}
package com.anchor.global.db;

public class RoutingDataSourceManager {

  private static final ThreadLocal<SetDataSource.DataSourceType> currentDataSourceName = new ThreadLocal<>();

  public static SetDataSource.DataSourceType getCurrentDataSourceName() {
    if (currentDataSourceName.get() == null) {
      return SetDataSource.DataSourceType.MASTER;
    } else {
      return currentDataSourceName.get();
    }
  }

  public static void setCurrentDataSourceName(SetDataSource.DataSourceType dataSourceType) {
    currentDataSourceName.set(dataSourceType);
  }

  public static void removeCurrentDataSourceName() {
    currentDataSourceName.remove();
  }

}
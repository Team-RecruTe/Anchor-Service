package com.anchor.global.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

  @Value("${rest-template.timeout}")
  private int timeout;

  @Value("${rest-template.max-connection}")
  private int maxConnection;

  @Value("${rest-template.max-per-route}")
  private int maxPerRoute;

  @Bean
  public RestClient restClient() {
    return RestClient.builder()
        .requestFactory(httpRequestFactory())
        .build();
  }

  private ClientHttpRequestFactory httpRequestFactory() {
    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

    CloseableHttpClient httpClient = (CloseableHttpClient) setHttpClient();

    factory.setHttpClient(httpClient);

    return factory;
  }

  private HttpClient setHttpClient() {
    RequestConfig requestConfig = setRequestConfig();
    ConnectionConfig connectionConfig = setConnectionConfig();

    return HttpClientBuilder.create()
        .setDefaultRequestConfig(requestConfig)
        .setConnectionManager(setConnectionManager(connectionConfig))
        .build();
  }

  private RequestConfig setRequestConfig() {
    return RequestConfig.custom()
        .setConnectionRequestTimeout(Timeout.ofSeconds(timeout))
        .build();
  }

  private ConnectionConfig setConnectionConfig() {
    return ConnectionConfig.custom()
        .setConnectTimeout(Timeout.ofSeconds(timeout))
        .setSocketTimeout(Timeout.ofSeconds(timeout))
        .build();
  }

  private HttpClientConnectionManager setConnectionManager(ConnectionConfig connectionConfig) {
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

    connectionManager.setDefaultConnectionConfig(connectionConfig);
    connectionManager.setMaxTotal(maxConnection);
    connectionManager.setDefaultMaxPerRoute(maxPerRoute);

    return connectionManager;
  }
}
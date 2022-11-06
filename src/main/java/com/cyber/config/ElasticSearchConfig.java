package com.cyber.config;

import com.cyber.exception.BusinessException;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;

@Configuration
public class ElasticSearchConfig  {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchConfig.class);

    @Value("${marathon.es.accessKey}")
    protected String accessKey;

    @Value("${marathon.es.secretKey}")
    protected String secretKey;

    @Value("${marathon.es.hosts}")
    protected String[] hosts;

    @Value("${marathon.es.port}")
    protected Integer port;

    @Value("${marathon.es.scheme}")
    protected String scheme;

    @Value("${marathon.es.maxRetryTimeoutMillis:60000}")
    protected Integer timeoutMills;

    @Bean(destroyMethod = "close")
    public RestClient getRestClient() {
        RestClientBuilder builder = getRestClientBuilder();
        return builder.build();
    }

    @Bean(destroyMethod = "close")
    public RestHighLevelClient getRestHighLevelClient() {
        RestClientBuilder builder = getRestClientBuilder();
        return new RestHighLevelClient(builder);
    }

    private RestClientBuilder getRestClientBuilder() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(accessKey, secretKey));
        if(StringUtils.isEmpty(hosts)) {
            throw new BusinessException("Init es client fail, hosts is empty...");
        }
        LOG.info("Init es client, hosts size {} ... ",hosts.length);
        HttpHost[] httpHosts = Arrays.stream(hosts).map(this::newHttpHost).filter(Objects::nonNull).toArray(HttpHost[]::new);
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(timeoutMills)
                .build();

        RestClientBuilder builder = RestClient.builder(httpHosts)
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        httpClientBuilder.disableAuthCaching();
                        httpClientBuilder.setDefaultRequestConfig(requestConfig);
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                })
                .setMaxRetryTimeoutMillis(timeoutMills);
        return builder;
    }


    private HttpHost newHttpHost(String host) {
        if(!StringUtils.isEmpty(host)) {
            return new HttpHost(host, port, scheme);
        }
        return null;
    }
}

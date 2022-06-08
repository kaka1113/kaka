package com.ice.ice.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author sqw
 * @since 2022/06/08
 */
@Configuration
public class SentinelGatewayConfig {

    public SentinelGatewayConfig() {
        GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
                String error = "{\n" +
                        "  \"errCode\": 10004,\n" +
                        "  \"message\": \"请求流量过大\",\n" +
                        "  \"success\": false\n" +
                        "}";
                Mono<ServerResponse> body = ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(error), String.class);
                return body;
            }
        });
    }
}


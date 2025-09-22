package com.github.anqorithm.springopentelemetryjaeger.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {

    @Value("${otel.service.name:spring-opentelemetry-jaeger}")
    private String serviceName;

    @Value("${otel.exporter.otlp.endpoint:http://localhost:4317}")
    private String otlpEndpoint;

    @Bean
    public OpenTelemetry openTelemetry() {
        Resource resource = Resource.getDefault()
                .merge(Resource.builder()
                        .put(AttributeKey.stringKey("service.name"), serviceName)
                        .put(AttributeKey.stringKey("service.version"), "1.0.0")
                        .put(AttributeKey.stringKey("deployment.environment"), "development")
                        .put(AttributeKey.stringKey("service.namespace"), "spring-app")
                        .put(AttributeKey.stringKey("service.instance.id"),
                            System.getProperty("user.name") + "-" + System.currentTimeMillis())
                        .put(AttributeKey.stringKey("host.name"),
                            System.getProperty("os.name"))
                        .put(AttributeKey.stringKey("process.runtime.name"), "Java")
                        .put(AttributeKey.stringKey("process.runtime.version"),
                            System.getProperty("java.version"))
                        .build());

        OtlpGrpcSpanExporter otlpExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint(otlpEndpoint)
                .setCompression("gzip")
                .build();

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(otlpExporter)
                        .setMaxExportBatchSize(512)
                        .build())
                .setResource(resource)
                .setSampler(io.opentelemetry.sdk.trace.samplers.Sampler.alwaysOn())
                .build();

        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .build();
    }

    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer(serviceName, "1.0.0");
    }
}
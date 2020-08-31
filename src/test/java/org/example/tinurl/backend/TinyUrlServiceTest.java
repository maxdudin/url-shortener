package org.example.tinurl.backend;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class TinyUrlServiceTest {

    @Container
    public static CouchbaseContainer couchbaseContainer = new CouchbaseContainer("couchbase/server:6.5.0")
            .withBucket(new BucketDefinition("urlbicket").withPrimaryIndex(false));

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("couchbase.mgmt_port", () -> couchbaseContainer.getMappedPort(8091));
        registry.add("couchbase.kv_port", () -> couchbaseContainer.getMappedPort(11210));
    }

    @Test
    void should_store_provided_url() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        TinyUrlServiceGrpc.TinyUrlServiceBlockingStub stub
                = TinyUrlServiceGrpc.newBlockingStub(channel);

        GetMiniResponse getMiniResponse = stub.getMini(GetMiniRequest.newBuilder().setOriginalUrl("https://twitter.com/home").build());
        GetMaxiResponse getMaxiResponse = stub.getMaxi(GetMaxiRequest.newBuilder().setMinifiedUrl(getMiniResponse.getMinifiedUrl()).build());

        channel.shutdown();

        assertThat(getMaxiResponse.getOriginalUrl()).isEqualTo("https://twitter.com/home");
    }

}
package org.example.tinurl.backend;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static java.util.UUID.randomUUID;

public class LoadTest {
//    @Test
    void should_store_provided_url() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        TinyUrlServiceGrpc.TinyUrlServiceBlockingStub stub
                = TinyUrlServiceGrpc.newBlockingStub(channel);

        IntStream.range(0, 20000).parallel().forEach(n -> {
            GetMiniResponse getMiniResponse = null;
            GetMaxiResponse getMaxiResponse = null;
            for (int i = 0; i < 9; i++) {
                getMiniResponse = stub.getMini(GetMiniRequest.newBuilder().setOriginalUrl("https://twitter.com/home" + randomUUID()).build());
            }

            for (int i = 0; i < 1; i++) {
                getMaxiResponse = stub.getMaxi(GetMaxiRequest.newBuilder().setMinifiedUrl(getMiniResponse.getMinifiedUrl()).build());
            }
        });

        channel.shutdown();
    }
}

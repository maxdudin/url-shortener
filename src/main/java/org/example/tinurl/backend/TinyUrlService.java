package org.example.tinurl.backend;

import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.validator.routines.UrlValidator;
import org.example.tinurl.backend.storage.StorageDAO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static io.grpc.Status.NOT_FOUND;
import static org.example.tinurl.backend.generator.TinyUrlGenerator.generateTinyUrl;

@GrpcService
public class TinyUrlService extends TinyUrlServiceGrpc.TinyUrlServiceImplBase {

    private final UrlValidator urlValidator = new UrlValidator();
    private final StorageDAO storageDAO;

    @Autowired
    public TinyUrlService(StorageDAO storageDAO) {
        this.storageDAO = storageDAO;
    }

    @Override
    public void getMini(GetMiniRequest request, StreamObserver<GetMiniResponse> responseObserver) {
        final String originalUrl = request.getOriginalUrl();

        if (!urlValidator.isValid(originalUrl)) {
            responseObserver.onError(new StatusException(Status.INVALID_ARGUMENT));
            return;
        }

        final String tinyUrl = generateTinyUrl(originalUrl);
        storageDAO.put(tinyUrl, originalUrl);

        final GetMiniResponse resp = GetMiniResponse.newBuilder().setMinifiedUrl(tinyUrl).build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }

    @Override
    public void getMaxi(GetMaxiRequest request, StreamObserver<GetMaxiResponse> responseObserver) {
        final String tinyUrl = request.getMinifiedUrl();
        final Optional<String> orgUrl = storageDAO.originalUrl(tinyUrl);

        if (orgUrl.isPresent()) {
            final GetMaxiResponse resp = GetMaxiResponse.newBuilder().setOriginalUrl(orgUrl.get()).build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new StatusException(NOT_FOUND));
        }
    }
}

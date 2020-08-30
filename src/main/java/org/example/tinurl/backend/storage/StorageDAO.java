package org.example.tinurl.backend.storage;

import java.util.Optional;

public interface StorageDAO {

    Optional<String> originalUrl(String tinyUrl);

    String put(String tinyUrl, String originalUrl);
}

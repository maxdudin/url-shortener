package org.example.tinurl.backend.generator;

import co.elastic.apm.api.CaptureSpan;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;


public class TinyUrlGenerator {

    @CaptureSpan
    public static String generateTinyUrl(String originalUrl) {
        return md5Hex(originalUrl).substring(0, 10);
    }
}

package uk.gov.register.presentation;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Version {
    private final Logger LOGGER = LoggerFactory.getLogger(Version.class);

    @JsonProperty
    public final String hash;
    @JsonProperty("serial-number")
    public final int serialNumber;

    public Version(int serialNumber, String hash) {
        this.hash = hash;
        this.serialNumber = serialNumber;
    }

    public String getUrlEncodedHash() {
        try {
            return URLEncoder.encode(hash, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.toString());
            throw new RuntimeException(e);
        }
    }
}

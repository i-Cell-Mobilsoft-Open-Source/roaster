package hu.icellmobilsoft.roaster.common.util.test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;

public class InputStreamReader {

    private String content;

    public void read(InputStream inputStream) throws BaseException, IOException {
        content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    public String getContent() {
        return content;
    }
}

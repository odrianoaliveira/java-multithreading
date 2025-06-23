package com.adriano.threading.wordcount;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;

public class WordCounter implements Runnable {

    private final ConcurrentHashMap<Character, Integer> aggregation;
    private final String file;

    public WordCounter(ConcurrentHashMap<Character, Integer> aggregation, String file) {
        this.aggregation = aggregation;
        this.file = file;
    }

    @Override
    public void run() {
        var loader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = loader.getResourceAsStream(file)) {
            assert is != null;
            var reader = new InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8);
            int character;
            while ((character = reader.read()) != -1){
                aggregation.merge((char) character, 1, Integer::sum);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

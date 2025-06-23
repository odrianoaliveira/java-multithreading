package com.adriano.threading.wordcount;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WordCountApp {
    public Set<String> getFiles(String path) throws IOException, URISyntaxException {
        var dirURL = Thread.currentThread().getContextClassLoader().getResource(path);
        assert dirURL != null;
        var filenames = new HashSet<String>();
        if (dirURL.getProtocol().equals("file")) {
            var folder = Paths.get(dirURL.toURI());
            try (var ds = Files.newDirectoryStream(folder)) {
                for (Path p : ds) {
                    if (Files.isRegularFile(p)) {
                        filenames.add(p.getFileName().toString());
                    }
                }
            }
        }

        return filenames;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        var aggregation = new ConcurrentHashMap<Character, Integer>();
        var files = new WordCountApp().getFiles("wordcounter");
        try (var executor = java.util.concurrent.Executors.newFixedThreadPool(files.size())) {
            files.stream()
                    .map(filename -> {
                        var counter = new WordCounter(aggregation, "wordcounter/" + filename);
                        return executor.submit(counter);
                    }).forEach(f -> {
                        try {
                            f.get();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
            aggregation.forEach((k, v) -> System.out.println(k + ": " + v));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

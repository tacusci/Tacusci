package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by alewis on 26/01/2017.
 */
public class Tail {

    public static final class RingBuffer {
        private final int limit;
        private final String[] data;
        private int counter = 0;

        RingBuffer(int limit) {
            this.limit = limit;
            this.data = new String[limit];
        }

        void collect(String line) {
            data[counter++ % limit] = line;
        }

        public List<String> contents() {
            return IntStream.range(counter < limit ? 0 : counter - limit, counter)
                    .mapToObj(index -> data[index % limit])
                    .collect(Collectors.toList());
        }

        public String asString_blob() { return contents().stream().collect(Collectors.joining()); }
        public String asString_nLines() { return String.join("\n", contents()); }

    }

    public static RingBuffer tailFile(final Path source, final int limit) throws IOException {

        try (Stream<String> stream = Files.lines(source)) {
            RingBuffer buffer = new RingBuffer(limit);
            stream.forEach(buffer::collect);
            return buffer;
        }
    }
}
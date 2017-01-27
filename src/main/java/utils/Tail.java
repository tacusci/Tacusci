package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Created by alewis on 26/01/2017.
 */
public class Tail {

    public static final class RingBuffer {

        private final long limit;
        private final String[] data;
        private long totalCount = 0;
        private long counter = 0;

        RingBuffer(long totalCount, long limit) {
            this.totalCount = totalCount;
            this.limit = limit;
            if (limit > totalCount) { limit = totalCount; }
            this.data = new String[Math.toIntExact(limit)];
        }

        void collect(String line) { data[Math.toIntExact(counter++ % limit)] = line; }

        public List<String> contents() {
            return LongStream.range(counter < limit ? 0 : counter - limit, counter)
                    .mapToObj(index -> data[Math.toIntExact(index % limit)])
                    .collect(Collectors.toList());
        }

        public String asString_blob() { return contents().stream().collect(Collectors.joining()); }
        public String asString_nLines() { return String.join("\n", contents()); }

    }

    public static RingBuffer tailFile(final Path source, final long limit) throws IOException {
        RingBuffer buffer = null;
        try {
            Stream<String> stream = Files.lines(source);
            buffer = new RingBuffer(stream.count(), limit);
            stream = Files.lines(source);
            stream.forEach(buffer::collect);
            return buffer;
        } catch (Exception e) { e.printStackTrace(); return buffer; }
    }
}
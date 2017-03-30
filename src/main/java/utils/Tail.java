/*
 * # DON'T BE A DICK PUBLIC LICENSE
 *
 * > Version 1.1, December 2016
 *
 * > Copyright (C) 2016 Adam Prakash Lewis
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document.
 *
 * > DON'T BE A DICK PUBLIC LICENSE
 * > TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *  1. Do whatever you like with the original work, just don't be a dick.
 *
 *      Being a dick includes - but is not limited to - the following instances:
 *
 * 	 1a. Outright copyright infringement - Don't just copy this and change the name.
 * 	 1b. Selling the unmodified original with no work done what-so-ever, that's REALLY being a dick.
 * 	 1c. Modifying the original work to contain hidden harmful content. That would make you a PROPER dick.
 *
 *  2. If you become rich through modifications, related works/services, or supporting the original work,
 *  share the love. Only a dick would make loads off this work and not buy the original work's
 *  creator(s) a pint.
 *
 *  3. Code is provided with no warranty. Using somebody else's code and bitching when it goes wrong makes
 *  you a DONKEY dick. Fix the problem yourself. A non-dick would submit the fix back.
 */

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
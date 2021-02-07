package com.hortonworks.labutils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class has been found on: https://rosettacode.org/wiki/Range_expansion#Java
 */
public class RangeExpander  implements Iterator<Integer>, Iterable<Integer> {

    private static final Pattern TOKEN_PATTERN = Pattern.compile("([+-]?\\d+)-([+-]?\\d+)");

    private final Iterator<String> tokensIterator;

    private boolean inRange;
    private int upperRangeEndpoint;
    private int nextRangeValue;

    public RangeExpander(String range) {
        String[] tokens = range.split("\\s*,\\s*");
        this.tokensIterator = Arrays.asList(tokens).iterator();
    }

    @Override
    public boolean hasNext() {
        return hasNextRangeValue() || this.tokensIterator.hasNext();
    }

    private boolean hasNextRangeValue() {
        return this.inRange && this.nextRangeValue <= this.upperRangeEndpoint;
    }

    @Override
    public Integer next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        if (hasNextRangeValue()) {
            return this.nextRangeValue++;
        }

        String token = this.tokensIterator.next();

        Matcher matcher = TOKEN_PATTERN.matcher(token);
        if (matcher.find()) {
            this.inRange = true;
            this.upperRangeEndpoint = Integer.valueOf(matcher.group(2));
            this.nextRangeValue = Integer.valueOf(matcher.group(1));
            return this.nextRangeValue++;
        }

        this.inRange = false;
        return Integer.valueOf(token);
    }

    @Override
    public Iterator<Integer> iterator() {
        return this;
    }

}
package org.crichton.util;

import java.util.regex.Pattern;

public class RegexPatterns {

    public static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)((\\.(\\d+))?)");

    private RegexPatterns() {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

}

package crichton.util;

import java.util.regex.Pattern;

public class RegexPatterns {

    public static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)((\\.(\\d+))?)");
    public static final Pattern PROJECT_PATTERN = Pattern.compile("Project Name,");
    public static final Pattern FILE_PATTERN = Pattern.compile("^Files,.*");
    public static final Pattern UNIT_PATTERN = Pattern.compile("^FilePath,.*");
    public static final Pattern TOTAL_PATTERN = Pattern.compile("^Total,.*");

}

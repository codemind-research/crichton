package crichton.infrastructure.csv;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public abstract class Information<T> {

    private final T info;

    protected Information(T info) {
        this.info = info;
    }

    public T getInfo() {
        return info;
    }

    protected abstract void addInfo(Object... values);

    protected abstract void convert(List<List<String>> lists);

    protected void parser(List<String> lines) {
        List<List<String>> convertList =
                lines.stream()
                     .filter(StringUtils::isNotBlank)
                     .map(this::removeSpacingAndMCDC)
                     .map(pl -> Arrays.stream(pl.split(","))
                                      .toList()
                                      .stream()
                                      .filter(StringUtils::isNotBlank).toList())
                     .filter(pl->!pl.isEmpty())
                     .toList();
        convert(convertList);
    }

    protected String removeSpacingAndMCDC(String value) {
        return value.replaceAll("\\s+","").replaceAll("MC/DC","Pair");
    }

}
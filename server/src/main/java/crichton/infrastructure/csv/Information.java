package crichton.infrastructure.csv;

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

    protected abstract void parser(List<String> lines);

    protected abstract void convert(List<List<String>> lists);

}
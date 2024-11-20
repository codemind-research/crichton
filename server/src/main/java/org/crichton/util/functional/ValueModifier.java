package org.crichton.util.functional;

import java.io.IOException;

// 값 수정 로직을 정의하는 인터페이스
@FunctionalInterface
public interface ValueModifier<T> {
    T modifyValue(T currentValue) throws IOException;
}

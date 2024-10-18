package runner.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassLoaderUtils {

    private ClassLoaderUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Set<Class<?>> findAllClassesAndInterfaces(Class<?> clazz) {
        // 재귀적으로 클래스와 인터페이스를 모두 찾음
        return Stream.concat(
                Stream.of(clazz), // 현재 클래스
                Stream.concat(
                        clazz.getSuperclass() != null ? findAllClassesAndInterfaces(clazz.getSuperclass()).stream() : Stream.empty(), // 상위 클래스 탐색
                        Arrays.stream(clazz.getInterfaces())  // 현재 클래스의 인터페이스들 탐색
                                .flatMap(i -> findAllClassesAndInterfaces(i).stream()) // 재귀적으로 인터페이스의 상위 계층 탐색
                )
        ).collect(Collectors.toSet()); // Set으로 수집하여 중복 제거
    }


}

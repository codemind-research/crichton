package org.crichton.domain.utils.anotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.crichton.domain.utils.enums.UploadAllowFileDefine;
import org.crichton.domain.utils.vaildators.FileTypeValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = FileTypeValidator.class)
public @interface ValidFile {
    String message() default "Invalid file type";
    UploadAllowFileDefine[] allowFileDefines(); // 허용할 파일 타입들 (예: "application/zip", "application/json")
    boolean required() default true; // null 허용 여부 설정
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

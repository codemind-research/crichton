package org.crichton.domain.utils.vaildators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.crichton.domain.utils.anotations.ValidFile;
import org.crichton.domain.utils.enums.UploadAllowFileDefine;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

@Component
@Slf4j
public class FileTypeValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    private ValidFile annotation;

    @Override
    public void initialize(ValidFile constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

        if(!annotation.required() && file.isEmpty()) {
            return true;
        }

        if(file.isEmpty()) {
            context.buildConstraintViolationWithTemplate("업로드 대상 파일이 없습니다. 정확히 선택 업로드해주세요.").addConstraintViolation();
            return false;
        }

        final String fileName = file.getOriginalFilename();

        if(!StringUtils.isBlank(fileName)) {
            context.buildConstraintViolationWithTemplate("업로드 요청한 파일명이 존재하지 않습니다.").addConstraintViolation();
            return false;
        }

        try {
            int targetByte = file.getBytes().length;
            if (targetByte == 0) {
                context.buildConstraintViolationWithTemplate("파일의 용량이 0 byte입니다.").addConstraintViolation();
                return false;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            context.buildConstraintViolationWithTemplate("파일의 용량 확인 중 에러가 발생했습니다.").addConstraintViolation();
            return false;
        }


        final var allowFileDefines = annotation.allowFileDefines();
        final String fileExt = FilenameUtils.getExtension(fileName);

        var allowFileExts = Arrays.stream(allowFileDefines)
                .map(UploadAllowFileDefine::getFileExtensionLowerCase)
                .toArray(String[]::new);

        var allowMimeTypes = Arrays.stream(allowFileDefines)
                .flatMap(type -> Arrays.stream(type.getAllowMimeTypes()))
                .distinct()
                .toArray(String[]::new);


        //파일명의 허용 확장자 검사
        if (!ArrayUtils.contains(allowFileExts, fileExt.toLowerCase())) {
            StringBuilder sb = new StringBuilder();
            sb.append("허용되지 않는 확장자의 파일이며 다음 확장자들만 허용됩니다.");
            sb.append(": ");
            sb.append(Arrays.toString(allowFileExts));
            context.buildConstraintViolationWithTemplate(sb.toString()).addConstraintViolation();

            return false;
        }

        //허용된 파일 확장자 검사
        final String detectedMediaType = this.getMimeTypeByTika(file); //확장자 변조한 파일인지 확인을 위한 mime type 얻기

        //파일 변조 업로드를 막기위한 mime타입 검사(예. exe파일을 csv로 확장자 변경하는 업로드를 막음)
        if (!ArrayUtils.contains(allowMimeTypes, detectedMediaType)) {
            StringBuilder sb = new StringBuilder();
            sb.append("확장자 변조 파일은 허용되지 않습니다.");
            context.buildConstraintViolationWithTemplate(sb.toString()).addConstraintViolation();

            return false;
        }

        return true;
    }


    /**
     * apache Tika라이브러리를 이용해서 파일의 mimeType을 가져옴
     *
     * @param multipartFile
     * @return
     */
    private String getMimeTypeByTika(MultipartFile multipartFile) {
        try {

            Tika tika = new Tika();
            String mimeType = tika.detect(multipartFile.getInputStream());
            log.debug("업로드 요청된 파일 {}의 mimeType:{}", multipartFile.getOriginalFilename(), mimeType);

            return mimeType;

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}

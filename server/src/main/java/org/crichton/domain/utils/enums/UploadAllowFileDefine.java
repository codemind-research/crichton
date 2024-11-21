package org.crichton.domain.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum UploadAllowFileDefine {

    ZIP("zip", new String[]{"application/zip"}),
    ZIP7("7z", new String[]{"application/x-7z-compressed"}),
    TAR("tar", new String[]{"application/x-tar", "application/x-compressed"}),
    GZ("gz", new String[]{"application/gzip"}),
    TGZ("tgz", new String[]{"application/gzip", "application/x-compressed"}),         // tgz 형식
    TAR_GZ("tar.gz", new String[]{"application/gzip", "application/x-compressed"}),   // tar.gz 형식
    GTAR("gtar", new String[]{"application/x-gtar"}),
    JSON("json", new String[]{"application/json"}),
    XZ("xz", new String[]{"application/x-xz"}),
    BZ2("bz2", new String[]{"application/x-bzip2"}),
    RAR("rar", new String[]{"application/x-rar-compressed"});

    private final String fileExtensionLowerCase; // 파일 확장자(소문자)
    private final String[] allowMimeTypes; // 허용하는 MIME 타입들

    /**
     * 주어진 MIME 타입이 열거형에 정의된 MIME 타입 중 하나인지 확인
     */
    public static UploadAllowFileDefine getByMimeType(String mimeType) {
        for (UploadAllowFileDefine define : values()) {
            for (String allowedMimeType : define.getAllowMimeTypes()) {
                if (allowedMimeType.equalsIgnoreCase(mimeType)) {
                    return define;
                }
            }
        }
        return null; // 지원되지 않는 MIME 타입인 경우 null 반환
    }

    /**
     * 주어진 파일 확장자가 열거형에 정의된 확장자와 일치하는지 확인
     */
    public static UploadAllowFileDefine getByFileExtension(String fileExtension) {
        for (UploadAllowFileDefine define : values()) {
            if (define.getFileExtensionLowerCase().equalsIgnoreCase(fileExtension)) {
                return define;
            }
        }
        return null; // 지원되지 않는 파일 확장자인 경우 null 반환
    }

    /**
     * MIME 타입과 파일 확장자를 동시에 확인
     */
    public static UploadAllowFileDefine getByMimeTypeAndExtension(String mimeType, String fileExtension) {
        for (UploadAllowFileDefine define : values()) {
            if (define.getFileExtensionLowerCase().equalsIgnoreCase(fileExtension)) {
                for (String allowedMimeType : define.getAllowMimeTypes()) {
                    if (allowedMimeType.equalsIgnoreCase(mimeType)) {
                        return define;
                    }
                }
            }
        }
        return null; // 둘 다 일치하지 않는 경우 null 반환
    }

}

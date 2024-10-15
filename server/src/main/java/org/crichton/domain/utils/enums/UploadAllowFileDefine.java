package org.crichton.domain.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
}

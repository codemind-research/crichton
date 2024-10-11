package org.crichton.domain.services;

import org.crichton.application.exceptions.CustomException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface StorageService {

    File uploadFile(MultipartFile source) throws CustomException;

}

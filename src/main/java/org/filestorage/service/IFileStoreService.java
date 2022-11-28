package org.filestorage.service;

import org.filestorage.entity.FileMetaDTO;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.UUID;

public interface IFileStoreService {
    String storeFile(byte[] content, String fileName, int subFileType, long userId) throws IOException, NoSuchAlgorithmException;


    byte[] getFile(UUID md5) throws IOException;


    Collection<FileMetaDTO> getMetaFiles(int subtype);

    void deleteFile(UUID md5) throws IOException;

    Integer countUsers(UUID fileHash);
}

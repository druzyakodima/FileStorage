package org.filestorage.entity.interfaces;

import org.filestorage.entity.FileMetaDTO;

import java.util.Collection;
import java.util.UUID;

public interface IFileMetaProvider {

    String checkFileExists(UUID fileHash);

    void saveFileMeta(UUID fileHash, String fileName, int sybType, long userId);

    Collection<FileMetaDTO> getMetaFiles(int subtype);

    void deleteFiles(UUID fileHash);

    public Integer countUsers(UUID fileHash);
}

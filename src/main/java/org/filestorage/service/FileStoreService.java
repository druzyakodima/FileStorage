package org.filestorage.service;

import org.apache.commons.io.FilenameUtils;
import org.filestorage.entity.FileMetaDTO;
import org.filestorage.entity.interfaces.IFileMetaProvider;
import org.filestorage.entity.interfaces.IFileSystemProvider;
import org.filestorage.utils.HashHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.UUID;

@Component
public class FileStoreService implements IFileStoreService {
    private IFileSystemProvider systemProvider;

    private IFileMetaProvider fileMetaProvider;

    @Autowired
    public FileStoreService(IFileSystemProvider systemProvider, IFileMetaProvider fileMetaProvider) {
        this.systemProvider = systemProvider;
        this.fileMetaProvider = fileMetaProvider;
    }

    @Override
    public String storeFile(byte[] content, String fileName, int subFileType, long userId) throws IOException, NoSuchAlgorithmException {
        final UUID md5 = HashHelper.getMd5Hash(content);

        String filename = fileMetaProvider.checkFileExists(md5);
        if (filename == null) {
            fileMetaProvider.saveFileMeta(md5, fileName, subFileType, userId);
            filename = systemProvider.storeFile(content, md5, fileName);
        }

        return filename;
    }

    @Override
    public byte[] getFile(UUID md5) throws IOException {
        String filename = fileMetaProvider.checkFileExists(md5);
        String ext = FilenameUtils.getExtension(filename);
        String fullFileName = md5.toString() + "." + ext;
        return systemProvider.getFile(fullFileName);
    }

    @Override
    public Collection<FileMetaDTO> getMetaFiles(int subtype) {
        return fileMetaProvider.getMetaFiles(subtype);
    }


    public void deleteFile(UUID md5) throws IOException {
        String filename = fileMetaProvider.checkFileExists(md5);
        String ext = FilenameUtils.getExtension(filename);
        String fullFileName = md5.toString() + "." + ext;

        if (filename != null) {
            fileMetaProvider.deleteFiles(md5);
            systemProvider.deleteFile(fullFileName);
        }

    }

    public Integer countUsers(UUID fileHash) {
        return fileMetaProvider.countUsers(fileHash);
    }
}

package org.filestorage.specifications;

import org.filestorage.entity.FileMetaDTO;
import org.filestorage.entity.interfaces.IFileMetaProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
public class FileMetaProvider implements IFileMetaProvider {

    private static final String GET_FILES_META = "select hash, filename from vma.file_info_metadata where sub_type = :subtype";

    private static final String GET_FILE_PATH_BY_HASH = "select filename from vma.file_info_metadata where hash = :hash";

    private static final String DELETE_FILE_PATH_BY_HASH = "delete from vma.file_info_metadata where hash = :hash";

    private static final String SAVE_FILE_META_DATA = "insert into vma.file_info_metadata (hash, filename, sub_type)\n" +
            "values (:hash, :finame, :subtype)";

    private static final String COUNT_USERS_ON_ONE_FILE = "select count (f_u.users_id) from vma.files_users_info_metada f_u where hash = :hash ";
    private static final String SAVE_USER_FILE = "insert into vma.files_users_info_metada (hash, user_id)\n" +
            "values (:hash, :finame, :subtype)";


    private final Sql2o sql2o;

    public FileMetaProvider(@Autowired Sql2o sql2o) {
        this.sql2o = sql2o;
    }


    @Override
    public String checkFileExists(UUID fileHash) {
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(GET_FILE_PATH_BY_HASH, false)
                    .addParameter("hash", fileHash)
                    .executeScalar(String.class);
        }
    }

    @Override
    public void saveFileMeta(UUID fileHash, String fileName, int sybType, long userId) {
        try (Connection connection = sql2o.open()) {

            connection.createQuery(SAVE_USER_FILE)
                    .addParameter("hash", fileHash)
                    .addParameter("user_id", userId)
                    .executeUpdate();

            connection.createQuery(SAVE_FILE_META_DATA)
                    .addParameter("hash", fileHash)
                    .addParameter("finame", fileName)
                    .addParameter("subtype", sybType)
                    .executeUpdate();
        }
    }

    @Override
    public Collection<FileMetaDTO> getMetaFiles(int subtype) {
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(GET_FILES_META, false)
                    .addParameter("subtype", subtype)
                    .executeAndFetch(FileMetaDTO.class);
        }
    }

    @Override
    public void deleteFiles(UUID fileHash) {
        try (Connection connection = sql2o.open()) {
            connection.createQuery(DELETE_FILE_PATH_BY_HASH)
                    .addParameter("hash", fileHash);
        }
    }

    public Integer countUsers(UUID fileHash) {
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(COUNT_USERS_ON_ONE_FILE, false)
                    .addParameter("hash", fileHash)
                    .executeScalar(Integer.class);
        }
    }
}

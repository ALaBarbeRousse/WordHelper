package helper.misc;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

@Slf4j
public class FileHelper {
    /* Удаляем все файлы в указанной папке. Удаляются только файла (не папки), и только на первом уровне, т.е. не вложенные. */
    public static void emptyFolder(File folder) {
        if (folder.isFile()) {
            log.warn("File '{}' isn't a folder.", folder.getAbsolutePath());
        }
        File[] nestedFiles = folder.listFiles();
        if (Objects.nonNull(nestedFiles)) {
            for (File fileToDelete: nestedFiles) {
                if (fileToDelete.isFile()) {
                    fileToDelete.delete();
                }
            }
        }
    }

    /* Получаем массив байт указанного файла */
    public static byte[] getFileBytes(File file) throws IOException {
        if (file.isDirectory()) {
            log.warn("File '{} is a folder.", file.getAbsolutePath());
        }
        return Files.readAllBytes(file.toPath());
    }

    /* Получаем единственный файл в папке. Если он не единственный, бросаем исключение. */
    public static File getTheOnlyFile(File folder) {
        if (folder.isFile()) {
            log.warn("File'{}' isn't a folder.", folder.getAbsolutePath());
            return null;
        }
        File[] files = folder.listFiles();

        assert files != null;
        if (files.length == 0) {
            throw new RuntimeException(String.format("Couldn't find any file in '%s'.", folder.getAbsolutePath()));
        } else if (files.length > 1) {
            throw new RuntimeException(String.format("There are more than 1 file in '%s'.", folder.getAbsolutePath()));
        } else {
            return files[0];
        }
    }
}

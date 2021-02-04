package ru.abenefic.cloudvault.server.storage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.abenefic.cloudvault.common.commands.*;
import ru.abenefic.cloudvault.server.model.User;
import ru.abenefic.cloudvault.server.support.Configuration;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;

/**
 * Утилитный класс для работы с файлами пользователя
 */

public class StorageProvider {

    private static final Logger LOG = LogManager.getLogger(StorageProvider.class);


    static DirectoryTree getUserTree(User user) throws Exception {
        DirectoryTree tree = new DirectoryTree();
        Path userRoot = getUserRoot(user);
        Files.walkFileTree(userRoot,
                new SimpleFileVisitor<>() {

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        if (dir != userRoot) {
                            tree.add(new FileTreeItem(
                                    dir.getFileName().toString(),
                                    userRoot.relativize(dir).toString()));
                        }
                        return super.preVisitDirectory(dir, attrs);
                    }
                }
        );
        return tree;
    }

    private static Path getUserRoot(User user) throws IOException {
        Path rootPath = Path.of(Configuration.getInstance().getSrvRootDirectory());
        if (Files.notExists(rootPath)) {
            Files.createDirectory(rootPath);
        }

        Path userRoot = Path.of(String.valueOf(rootPath), String.valueOf(user.getId()));
        if (Files.notExists(userRoot)) {
            Files.createDirectory(userRoot);
        }
        return userRoot;
    }

    static FilesList getFilesList(User user, String parentPath) throws IOException {
        FilesList list = new FilesList();

        Path directory = Path.of(
                Configuration.getInstance().getSrvRootDirectory(),
                String.valueOf(user.getId()),
                "\\",
                parentPath);

        Files.walkFileTree(directory, Set.of(), 1,
                new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        list.add(FileItem.fromPath(file, attrs.creationTime()));
                        return super.visitFile(file, attrs);
                    }
                }
        );

        return list;
    }

    static boolean writeFilePart(User user, FilePart filePart) {
        if (filePart.isEnd()) {
            return true;
        }
        try {
            Path filePath = getUserRoot(user).resolve(filePart.getFileName());
            Files.createDirectories(filePath.getParent()); // если такой папки ещё нет, чтобы ошибки не было
            Files.write(filePath, filePart.getData(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            LOG.info("FIle part " + filePart.getPartNumber());
            return true;
        } catch (IOException e) {
            LOG.error(e);
            return false;
        }

    }

    static Path getFilePath(User user, String filePath) throws IOException {
        Path userRoot = getUserRoot(user);
        return userRoot.resolve(filePath);
    }

}

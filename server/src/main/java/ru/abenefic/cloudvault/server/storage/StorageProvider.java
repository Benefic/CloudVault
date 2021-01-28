package ru.abenefic.cloudvault.server.storage;

import ru.abenefic.cloudvault.common.commands.*;
import ru.abenefic.cloudvault.server.model.User;
import ru.abenefic.cloudvault.server.support.Configuration;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;

public class StorageProvider {
    static DirectoryTree getUserTree(User user) throws Exception {
        DirectoryTree tree = new DirectoryTree();
        Path rootPath = Path.of(Configuration.getInstance().getSrvRootDirectory());
        if (Files.notExists(rootPath)) {
            Files.createDirectory(rootPath);
        }

        Path userRoot = Path.of(String.valueOf(rootPath), String.valueOf(user.getId()));
        if (Files.notExists(userRoot)) {
            Files.createDirectory(userRoot);
        }
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
        //TODO
        return true;
    }
}

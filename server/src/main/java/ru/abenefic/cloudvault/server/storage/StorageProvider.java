package ru.abenefic.cloudvault.server.storage;

import ru.abenefic.cloudvault.common.commands.DirectoryTree;
import ru.abenefic.cloudvault.server.model.User;
import ru.abenefic.cloudvault.server.support.Configuration;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

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
                        tree.add(new DirectoryTree.TreeItem(dir.getFileName().toString(), dir.toString()));
                        return super.preVisitDirectory(dir, attrs);
                    }
                }
        );
        return tree;
    }
}

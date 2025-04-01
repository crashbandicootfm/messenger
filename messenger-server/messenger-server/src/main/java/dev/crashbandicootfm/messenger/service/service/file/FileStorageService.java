package dev.crashbandicootfm.messenger.service.service.file;

import java.nio.file.Paths;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    private final String uploadDir = "uploads"; // Папка для загрузки файлов
    private final String serverUrl = "http://localhost:8080"; // URL сервера

    public String saveFile(MultipartFile file) {
        try {
            // Создаем папку, если она не существует
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }

            // Генерируем уникальное имя файла
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // Сохраняем файл
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Возвращаем полный URL файла
            return serverUrl + "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }
}

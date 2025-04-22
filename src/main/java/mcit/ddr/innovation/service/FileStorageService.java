package mcit.ddr.innovation.service;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import mcit.ddr.innovation.exception.FileStorageException;

@Service
public class FileStorageService {

    private final Path profileImageStorageLocation;
    private final Path attachmentFileStorageLocation;

    public FileStorageService() {
        this.profileImageStorageLocation = Paths.get("D:\\DDR\\innovation\\user-profile").toAbsolutePath().normalize();
        this.attachmentFileStorageLocation = Paths.get("D:\\DDR\\innovation\\attachmentFile").toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.profileImageStorageLocation);
            Files.createDirectories(this.attachmentFileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create directories for file storage", ex);
        }
    }

    public String saveProfileImage(MultipartFile file, String username) {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = "profile_" + username + "_" + timestamp + "." + extension;

        try {
            Path targetLocation = this.profileImageStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return targetLocation.toString();
        } catch (IOException ex) {
            throw new FileStorageException("Could not store profile image for " + username, ex);
        }
    }

    public String saveFile(MultipartFile file) {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String baseName = StringUtils.cleanPath(file.getOriginalFilename()).replace("." + extension, "");

        // Truncate the base name to 10 characters (to prevent overly long names)
        String safeBaseName = baseName.length() > 10 ? baseName.substring(0, 10) : baseName;

        // Combine base name with timestamp
        String fileName = safeBaseName + "_" + timestamp + "." + extension;

        try {
            Path targetLocation = this.attachmentFileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return targetLocation.toString();
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + file.getOriginalFilename(), ex);
        }
    }


    public void deleteFile(String filePath) {
        try {
            Path pathToDelete = Paths.get(filePath).toAbsolutePath().normalize();
            Files.deleteIfExists(pathToDelete);
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file at " + filePath, ex);
        }
    }
}

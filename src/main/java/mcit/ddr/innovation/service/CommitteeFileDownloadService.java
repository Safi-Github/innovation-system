package mcit.ddr.innovation.service;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import mcit.ddr.innovation.entity.Committee;
import mcit.ddr.innovation.exception.FileStorageException;
import mcit.ddr.innovation.repository.CommitteeRepository;

@Service
public class CommitteeFileDownloadService {

    private final Path fileStorageLocation;
    private final CommitteeRepository committeeRepository;

    @Autowired
    public CommitteeFileDownloadService(CommitteeRepository committeeRepository) {
        this.fileStorageLocation = Paths.get("D:\\DDR\\innovation\\committee\\attachmentFile")
            .toAbsolutePath().normalize();
        this.committeeRepository = committeeRepository;
    }

    public Resource loadCommitteeFileById(Long id) {
        try {
            Optional<Committee> fileRecordOpt = committeeRepository.findById(id);
            if (fileRecordOpt.isEmpty()) {
                throw new FileStorageException("No file found for Committee with ID " + id + ".");
            }

            Committee fileRecord = fileRecordOpt.get();
            String attachmentPath = fileRecord.getAttachment();

            if (attachmentPath == null || attachmentPath.isBlank()) {
                throw new FileStorageException("No attachment path found for record ID: " + id);
            }

            Path filePath = Paths.get(attachmentPath).toAbsolutePath().normalize();

            // Optional security check: ensure the file is within the expected directory
            if (!filePath.startsWith(fileStorageLocation)) {
                throw new FileStorageException("Access to file outside allowed directory is denied.");
            }

            System.out.println("Resolved file path: " + filePath);
            System.out.println("File exists: " + Files.exists(filePath));
            System.out.println("Is readable: " + Files.isReadable(filePath));

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new FileStorageException("File does not exist or is not readable: " + filePath);
            }

            System.out.println("Resource filename: " + resource.getFilename());
            return resource;

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new FileStorageException("Error loading file: " + ex.getMessage(), ex);
        }
    }
}

package mcit.ddr.innovation.service;

import java.nio.file.*;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import mcit.ddr.innovation.entity.Innovation;
import mcit.ddr.innovation.exception.FileStorageException;
import mcit.ddr.innovation.repository.InnovationRepository;

@Service
public class FileDownloadService {

    private final Path fileStorageLocation;
    private final InnovationRepository innovationRepository;

    @Autowired
    public FileDownloadService(InnovationRepository innovationRepository) {
        this.fileStorageLocation = Paths.get("D:\\DDR\\innovation\\attachmentFile").toAbsolutePath().normalize();
        this.innovationRepository = innovationRepository;
    }

    public Resource loadFileById(Long id) {
        try {
            Optional<Innovation> fileRecordOpt = innovationRepository.findById(id);
            if (fileRecordOpt.isEmpty()) {
                throw new FileStorageException("File with ID " + id + " not found in database.");
            }
    
            Innovation fileRecord = fileRecordOpt.get();
            if (fileRecord.getAttachment() == null || fileRecord.getAttachment().isEmpty()) {
                throw new FileStorageException("No attachment filename found for record ID: " + id);
            }
    
            Path filePath = fileStorageLocation.resolve(fileRecord.getAttachment()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new FileStorageException("File does not exist or is not readable: " + fileRecord.getAttachment());
            }
    
            return resource;
        } catch (Exception ex) {
            throw new FileStorageException("Error loading file: " + ex.getMessage(), ex);
        }
    }
}

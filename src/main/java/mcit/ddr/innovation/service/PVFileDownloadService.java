package mcit.ddr.innovation.service;

import java.nio.file.*;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import mcit.ddr.innovation.entity.InnovationHistory;
import mcit.ddr.innovation.exception.FileStorageException;
import mcit.ddr.innovation.repository.InnovationHistoryRepository;

@Service
public class PVFileDownloadService {

    private static final Logger logger = LoggerFactory.getLogger(PVFileDownloadService.class);  // Logger instance

    private final Path fileStorageLocation;
    private final InnovationHistoryRepository innovationHistoryRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON parser

    @Autowired
    public PVFileDownloadService(InnovationHistoryRepository innovationHistoryRepository) {
        // File storage location path
        this.fileStorageLocation = Paths.get("D:\\DDR\\innovation\\attachmentFile").toAbsolutePath().normalize();
        this.innovationHistoryRepository = innovationHistoryRepository;
    }

    public Resource loadFileById(Long id) {
        try {
            // Fetch the record from the repository (InnovationHistory)
            Optional<InnovationHistory> fileRecordOpt = innovationHistoryRepository.findById(id);
            if (fileRecordOpt.isEmpty()) {
                throw new FileStorageException("File with ID " + id + " not found in database.");
            }

            InnovationHistory fileRecord = fileRecordOpt.get();

            // Assuming archivedInnovationData is a JSON string field in the InnovationHistory entity
            String archivedInnovationData = fileRecord.getArchivedInnovationData();
            
            // Parse the JSON string
            JsonNode archivedDataNode = objectMapper.readTree(archivedInnovationData);
            String attachmentPath = archivedDataNode.path("attachment").asText(null); // Extract 'attachment' field

            if (attachmentPath == null || attachmentPath.isEmpty()) {
                throw new FileStorageException("No attachment filename found for record ID: " + id);
            }

            // Ensure the attachment path does not include the base directory if it already has it
            String fileName = attachmentPath.replace("D:\\DDR\\innovation\\attachmentFile\\", "");

            // Construct the correct file path
            Path filePath = fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new FileStorageException("File does not exist or is not readable: " + attachmentPath);
            }

            return resource;
        } catch (Exception ex) {
            throw new FileStorageException("Error loading file: " + ex.getMessage(), ex);
        }
    }
}

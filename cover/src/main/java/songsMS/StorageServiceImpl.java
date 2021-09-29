package songsMS;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class StorageServiceImpl implements StorageService{
    private final Path path;

    public StorageServiceImpl(@Value("$(storage.location}") String path) {
        this.path = Paths.get(path);
    }

    @Override
    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(path);
    }

    @Override
    public String save(MultipartFile file, String filename) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("empty file: " + filename);
        }
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        }
        return filename;
    }

    @Override
    public Resource load(String filename) throws IOException {
        Path file = path.resolve(filename);
        Resource resource = new UrlResource(file.toUri());
        if (!resource.exists())
            throw new FileNotFoundException("unknown file: "+filename);
        return resource;
    }
}

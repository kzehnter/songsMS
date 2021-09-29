package songsMS;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    void init() throws IOException;

    String save(MultipartFile file, String filename) throws IOException;

    Resource load(String filename) throws IOException;
}

package songsMS;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    public void init() throws IOException;
    public String save(MultipartFile file, String filename) throws IOException;
    public Resource load(String filename) throws IOException;
}


package songsMS;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;

@RestController
public class CoverController {
    private StorageService service;

    public CoverController(StorageService service) {
        this.service = service;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Resource> getCover(@PathVariable int id){
        Resource file;
        try {
            file = service.load("cover"+id);
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable int id) {
        if (!Objects.equals(file.getContentType(), "image/jpg"))
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        try {
            service.save(file, "song-"+id);
        }
        catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.created(URI.create("/download/"+id)).contentType(MediaType.TEXT_PLAIN).build();
    }
}

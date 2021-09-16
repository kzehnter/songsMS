package songsMS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.PersistenceException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Random;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {
    private AuthDao dao;
    private ObjectMapper mapper = new ObjectMapper();

    private static HashMap<String /*token*/, Auth /*userId*/> tokenMap = new HashMap<>();

    // Dependency injection
    public AuthController(@Qualifier("authDaoImpl") AuthDao dao){
        this.dao = dao;
    }

    // manages auth post requests
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> login(@RequestBody String userJson) throws IOException {
        try {
            // check for illegal arguments
            if (userJson.toLowerCase().contains("\"firstname\":") || userJson.toLowerCase().contains("\"lastname\":"))
                return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("Wrong request arguments.");
            // create non persisted user
            Auth auth = mapper.readValue(userJson, Auth.class);
            // check for needed arguments
            if (auth.getUserId() == null || auth.getPassword() == null)
                return ResponseEntity.badRequest().body("fields 'userId' and 'password' must be defined");
            // find fitting user by id
            Auth existingAuth = dao.findAuth(auth.getUserId());
            if (existingAuth != null && existingAuth.getPassword().equals(auth.getPassword())) {
                // generate token, put it in hashmap and request that for return body
                String token = generateResponseToken();
                tokenMap.put(token, existingAuth);
                return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(token);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.TEXT_PLAIN).body("Declined: User and password don't match or user doesn't exist!");
            }
        } catch (PersistenceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getStackTrace(e));
        } catch (JsonProcessingException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(getStackTrace(e));
        }
    }

    String generateResponseToken() {
        char[] array = new char[20];
        Random random = new Random();
        for (int i=0; i<array.length; i++) {
            // assigned characters ranging from 33 '!' to 126 '~'
            array[i] = (char) (random.nextInt(93)+33);
        }
        return new String(array);
    }

    static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static HashMap<String, Auth> getTokenMap() {
        return tokenMap;
    }
}

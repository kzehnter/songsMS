package songsMS;

import com.netflix.discovery.DiscoveryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import songsMS.model.SongListsXmlRoot;
import songsMS.model.SongsXmlRoot;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ControllerHelper {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    DiscoveryClient discoveryClient;

    static String convertSongToXml(SongsXmlRoot songs) throws JAXBException {
        return convertToXml(songs, SongsXmlRoot.class);
    }

    static String convertSongListToXml(SongListsXmlRoot songLists) throws JAXBException {
        return convertToXml(songLists, SongListsXmlRoot.class);
    }

    private static String convertToXml(Object jaxbElement, Class<?> class_) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(class_);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter sw = new StringWriter();
        marshaller.marshal(jaxbElement, sw);
        return sw.toString();
    }

    static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    String getUserIdForToken(String token) {
        String url = discoveryClient.getNextServerFromEureka("auth", false).getHomePageUrl();
        ResponseEntity<String> response = restTemplate.getForEntity(url+"/auth/"+token, String.class);
        if (response.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return null;
        else
            return response.getBody();
    }

    boolean doesUserIdExist(String userId) {
        String url = discoveryClient.getNextServerFromEureka("auth", false).getHomePageUrl();
        ResponseEntity<String> response = restTemplate.getForEntity(url+"/auth/id/"+userId, String.class);
        if (response.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return false;
        else
            return true;
    }

    boolean doesTokenExist(String auth) {
        return getUserIdForToken(auth)!=null;
    }

    boolean doesTokenMatchUserId(String auth, String userId) {
        return getUserIdForToken(auth) == userId;
    }
}

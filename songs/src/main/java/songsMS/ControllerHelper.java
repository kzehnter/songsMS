package songsMS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import songsMS.model.SongListsXmlRoot;
import songsMS.model.SongsXmlRoot;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

@Component
public class ControllerHelper {
    @Autowired
    private RestTemplate restTemplate;

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
        String url = "http://localhost:8080";
        return restTemplate.exchange(url + "/auth/token/{token}",
                HttpMethod.GET, null, new ParameterizedTypeReference<String>() {}, token).getBody();
    }

    boolean doesUserIdExist(String userId) {
        String url = "http://localhost:8080";
        String response = restTemplate.exchange(url + "/auth/id/{userId}",
                HttpMethod.GET, null, new ParameterizedTypeReference<String>() {}, userId).getBody();
        return (response != null);

    }

    boolean doesTokenNotExist(String auth) {
        return getUserIdForToken(auth) == null;
    }

    boolean doesTokenMatchUserId(String auth, String userId) {
        return Objects.equals(getUserIdForToken(auth), userId);
    }
}

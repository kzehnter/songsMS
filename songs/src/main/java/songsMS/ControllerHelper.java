package songsMS;

import songsMS.model.SongListsXmlRoot;
import songsMS.model.SongsXmlRoot;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ControllerHelper {

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

//    String getUserIdForToken(String token) {
//        String url = discoveryClient.getNextServerFromEureka("auth-service", false).getHomePageUrl();
//        ResponseEntity<String> response = restTemplate.getForEntity(url+"/auth/"+token, String.class);
//        if (response.getStatusCode().equals(HttpStatus.NOT_FOUND))
//            return null;
//        else
//            return response.getBody();
//    }

    static boolean doesTokenExist(String auth) {
        return UserController.getTokenMap().containsKey(auth);
    }

    static boolean doesTokenMatchUser(String auth, String userId) {
        return UserController.getTokenMap().get(auth).equals(userId);
    }

    public static boolean doesUserIdExist(String userId) {
        boolean doesExist = false;
        for (User u: UserController.getTokenMap().values()) {
            if (u.getUserId().equals(userId))
                doesExist = true;
        }
        return doesExist;
    }

    public static User getUserForToken(String auth) {
        return UserController.getTokenMap().get(auth);
    }
}

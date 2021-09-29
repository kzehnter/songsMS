package songsMS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import songsMS.model.SongList;
import songsMS.model.SongListsXmlRoot;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import songsMS.repo.SongDao;
import songsMS.repo.SongListDao;

import javax.persistence.PersistenceException;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static songsMS.ControllerHelper.convertSongListToXml;
import static songsMS.ControllerHelper.getStackTrace;

@RestController
@RequestMapping(value = "/songLists")
public class SongListController {

    private final SongListDao songListDao;
    private final SongDao songDao;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ControllerHelper helper;

    public SongListController(SongListDao songListDao, SongDao songDao, ControllerHelper helper) {
        this.songListDao = songListDao;
        this.songDao = songDao;
        this.helper = helper;
    }

    @GetMapping
    public ResponseEntity<String> getAllListsByUserId(
            @RequestHeader(HttpHeaders.ACCEPT) String accept,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
            @RequestParam String userId
    ) throws IOException, JAXBException {
        if (helper.doesTokenNotExist(auth)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!helper.doesUserIdExist(userId)) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        List<SongList> songLists;
        if (helper.doesTokenMatchUserId(auth, userId)) {
            songLists = songListDao.findAllListsByUserId(userId);
        } else {
            songLists = songListDao.findAllPublicListsByUserId(userId);
        }

        switch (accept) {
            case MediaType.APPLICATION_JSON_VALUE:
                return new ResponseEntity<>(mapper.writeValueAsString(songLists), HttpStatus.OK);
            case MediaType.APPLICATION_XML_VALUE:
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(convertSongListToXml(new SongListsXmlRoot(songLists)));
            default:
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<String> getListById(
            @RequestHeader(HttpHeaders.ACCEPT) String accept,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
            @PathVariable Integer id
    ) throws IOException, JAXBException {
        if (helper.doesTokenNotExist(auth)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        SongList songList = songListDao.findListById(id);
        if (songList == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        if (!helper.doesTokenMatchUserId(auth, songList.getOwnerId()) && songList.getIsPrivate()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        switch (accept) {
            case MediaType.APPLICATION_JSON_VALUE:
                return new ResponseEntity<>(mapper.writeValueAsString(songList), HttpStatus.OK);
            case MediaType.APPLICATION_XML_VALUE:
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(convertSongListToXml(new SongListsXmlRoot(songList)));
            default:
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postList(@RequestBody String listJson,
                                           @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth) {
        if (helper.doesTokenNotExist(auth)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        int listId;
        try {
            if (listJson.toLowerCase().contains("\"listid\":"))
                return ResponseEntity.badRequest().body("list IDs are not to be manually assigned");
            SongList songList = mapper.readValue(listJson, SongList.class);
            if (songList.getListName() == null)
                throw new IllegalArgumentException("property 'name' must be provided");
            if (doNotAllSongsExist(songList))
                return ResponseEntity.badRequest().body("invalid Song information, please match with database entries");
            songList.setOwnerId(helper.getUserIdForToken(auth));
            listId = songListDao.saveList(songList);
        } catch (PersistenceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getStackTrace(e));
        } catch (JsonProcessingException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(getStackTrace(e));
        }
        return ResponseEntity.created(URI.create("/rest/songLists/" + listId)).contentType(MediaType.TEXT_PLAIN).build();
    }

    private boolean doNotAllSongsExist(SongList songList) {
        return !songDao.findAllSongs().containsAll(songList.getSongs());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteListById(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth, @PathVariable Integer id) {
        if (helper.doesTokenNotExist(auth)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        SongList songList = songListDao.findListById(id);
        if (songList == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        if (!helper.doesTokenMatchUserId(auth, songList.getOwnerId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            songListDao.deleteList(id);
        } catch (IndexOutOfBoundsException e) {
            return ResponseEntity.notFound().build();
        } catch (PersistenceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getStackTrace(e));
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "/{id}")
    public ResponseEntity<String> updateList(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth, @PathVariable Integer id, @RequestBody String listJson) {
        if (helper.doesTokenNotExist(auth)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            SongList newList = mapper.readValue(listJson, SongList.class);

            if (id == 0)
                throw new IllegalArgumentException("property 'id' must be provided and can not be 0");
            if (doNotAllSongsExist(newList))
                return ResponseEntity.badRequest().body("invalid Song information, please match with database entries");

            SongList firstList = songListDao.findListById(id);
            if (!helper.doesTokenMatchUserId(auth, firstList.getOwnerId()))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            newList.setOwnerId(firstList.getOwnerId());
            newList.setListId(id);
            if (!listJson.contains("\"isPrivate\":"))
                newList.setIsPrivate(firstList.getIsPrivate());
            if (newList.getListName() == null)
                newList.setListName(firstList.getListName());
            if (newList.getSongs() == null)
                newList.setSongs(firstList.getSongs());

            songListDao.updateSong(newList);
        } catch (PersistenceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getStackTrace(e));
        } catch (JsonProcessingException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(getStackTrace(e));
        } catch (IndexOutOfBoundsException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}

package songsMS.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;
import java.util.List;

@XmlRootElement(name = "songs")
public class SongsXmlRoot {

    public SongsXmlRoot() {
    }

    public SongsXmlRoot(List<songsMS.model.Song> songs) {
        this.songs = songs;
    }

    public SongsXmlRoot(songsMS.model.Song song) {
        this.songs = new LinkedList<>();
        songs.add(song);
    }

    private List<songsMS.model.Song> songs;

    public void setSongs(List<songsMS.model.Song> songs) {
        this.songs = songs;
    }

    @XmlElement(name = "song")
    public List<songsMS.model.Song> getSongs() {
        return songs;
    }
}

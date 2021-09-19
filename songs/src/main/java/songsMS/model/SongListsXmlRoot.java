package songsMS.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;
import java.util.List;

@XmlRootElement(name = "songlists")
public class SongListsXmlRoot {
    private List<songsMS.model.SongList> songLists;

    public SongListsXmlRoot() {
    }

    public SongListsXmlRoot(List<songsMS.model.SongList> songLists) { this.songLists = songLists; }

    public SongListsXmlRoot(songsMS.model.SongList songList) {
        this.songLists = new LinkedList<>();
        songLists.add(songList);
    }

    public void setSongLists(List<songsMS.model.SongList> songLists) {
        this.songLists = songLists;
    }

    @XmlElement(name = "songlist")
    public List<songsMS.model.SongList> getSongLists() {
        return songLists;
    }
}

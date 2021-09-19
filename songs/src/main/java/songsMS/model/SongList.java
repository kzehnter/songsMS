package songsMS.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.NotNull;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "songlists")
@JsonIgnoreProperties({"listId", "owner", "songsXmlRoot"})
public class SongList {

    @Id
    @NotNull
    @GenericGenerator(name="songlists_gen" , strategy="increment")
    @GeneratedValue(generator="songlists_gen")
    private int listId;

    @NotNull
    @ManyToOne
    @JoinColumn(name="ownerId")
    private User owner;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="songlists_songs",
            joinColumns = @JoinColumn(name = "listId"),
            inverseJoinColumns = @JoinColumn(name = "songId"))
    private List<songsMS.model.Song> songs = new ArrayList<>();

    @NotNull
    private String listName;
    @NotNull
    private boolean isPrivate;

    public SongList() {}

    public SongList(User owner, String listName, boolean isPrivate) {
        this.owner = owner;
        this.listName = listName;
        this.isPrivate = isPrivate;
    }

    @XmlTransient
    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    @XmlTransient
    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @XmlElement(name = "songs")
    public SongsXmlRoot getSongsXmlRoot() {
        return new SongsXmlRoot(getSongs());
    }

    @XmlTransient
    public List<songsMS.model.Song> getSongs() {
        return songs;
    }

    public void setSongs(List<songsMS.model.Song> songs) {
        this.songs = songs;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
}
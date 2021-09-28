package songsMS.repo;

import songsMS.model.SongList;

import javax.persistence.PersistenceException;
import java.util.List;

public interface SongListDao {

    List<SongList> findAllPublicListsByUserId(String id) throws PersistenceException;

    List<SongList> findAllListsByUserId(String id) throws PersistenceException;

    SongList findListById(Integer id) throws PersistenceException;

    void deleteList(Integer id) throws PersistenceException, IndexOutOfBoundsException;

    int saveList(SongList songList) throws PersistenceException;

    void updateSong(SongList list) throws PersistenceException;
}

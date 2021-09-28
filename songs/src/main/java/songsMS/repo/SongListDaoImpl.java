package songsMS.repo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import songsMS.model.SongList;

import javax.persistence.PersistenceException;
import java.util.List;

@Repository
@Transactional
public class SongListDaoImpl implements songsMS.repo.SongListDao {

    @Autowired
    private SessionFactory sessionFactory;

    public SongListDaoImpl() {
    }

    @Override
    public List<SongList> findAllPublicListsByUserId(String id) throws PersistenceException {
        try {
            return sessionFactory.getCurrentSession().createNativeQuery("SELECT * FROM songlists WHERE ownerid = '" + id + "' AND isprivate = false ", SongList.class).getResultList();
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public List<SongList> findAllListsByUserId(String id) throws PersistenceException {
        try {
            return sessionFactory.getCurrentSession().createNativeQuery("SELECT * FROM songlists WHERE ownerid = '" + id + "'", SongList.class).getResultList();
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public SongList findListById(Integer id) throws PersistenceException {
        try {
            return sessionFactory.getCurrentSession().get(SongList.class, id);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void deleteList(Integer id) throws PersistenceException, IndexOutOfBoundsException {
        try {
            if (findListById(id) == null)
                throw new IndexOutOfBoundsException("songList to be deleted does not exist (invalid id: "+id+")");
            Session s = sessionFactory.getCurrentSession();
            s.clear();
            s.delete(s.get(SongList.class, id));
        } catch (IndexOutOfBoundsException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public int saveList(SongList songList) throws PersistenceException {
        try {
            return (int) sessionFactory.getCurrentSession().save(songList);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void updateSong(SongList list) throws PersistenceException {
        try {
            if (findListById(list.getListId()) == null)
                throw new IndexOutOfBoundsException("song to be updated does not exist (invalid id: "+list.getListId()+")");
            sessionFactory.getCurrentSession().clear();
            sessionFactory.getCurrentSession().update(list);
        } catch (IndexOutOfBoundsException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }
}

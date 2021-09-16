package songsMS;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;

@Repository
@Transactional
public class AuthDaoImpl implements AuthDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Auth findAuth(String userId) throws PersistenceException {
        try {
            return sessionFactory.getCurrentSession().get(Auth.class, userId);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }
}

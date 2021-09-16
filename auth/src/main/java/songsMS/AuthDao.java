package songsMS;

import javax.persistence.PersistenceException;

public interface AuthDao {

    /**@param userId the user id
     * @return the User object corresponding to the user id from the database or <code>null</code> if the given id is unassigned
     */
    Auth findAuth(String userId) throws PersistenceException;
}

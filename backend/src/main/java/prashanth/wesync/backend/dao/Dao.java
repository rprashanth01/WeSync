package prashanth.wesync.backend.dao;

import javax.persistence.EntityManager;

import prashanth.wesync.backend.model.User;

public enum Dao {
    INSTANCE;

    public void createUser(String fname, String lname, String email, String accessToken, String refreshToken){
        synchronized (this) {
            EntityManager em = null;
            try {
                em = EMFService.get().createEntityManager();
                User user = new User(fname, lname, email, accessToken, refreshToken);
                em.persist(user);
            } finally {
                em.close();
            }
        }
    }
}
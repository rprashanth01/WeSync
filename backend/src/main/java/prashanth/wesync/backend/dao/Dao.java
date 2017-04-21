package prashanth.wesync.backend.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import prashanth.wesync.backend.model.Event;
import prashanth.wesync.backend.model.User;

public enum Dao {
    INSTANCE;

    public void createUser(String fname, String lname, String email, String accessToken, String refreshToken){
        synchronized (this) {
            EntityManager em = null;
            try {
                em = EMFService.get().createEntityManager();
                Query q = em
                        .createQuery("select t from User t where t.email= :email");
                q.setParameter("email",email);
                List<Event> queryList  = q.getResultList();
                if( queryList.size() <=0 ) {
                    User user = new User(fname, lname, email, accessToken, refreshToken);
                    em.persist(user);
                }
            } finally {
                em.close();
            }
        }
    }

    public void createEvent(String eventName, String user, String location){
        EntityManager em = EMFService.get().createEntityManager();
        Query q = em
                .createQuery("select t from Event t where t.eventName = :eventName");
        q.setParameter("eventName",eventName);
        List<Event> queryList  = q.getResultList();

        if(queryList.size()  > 0 ){
            for( Event ql : queryList) {
                if(ql != null && ql.getEventName().equals(eventName)) {
                    Event e = (Event) queryList.get(0);
                    e.setUsers(user);
                    em.persist(e);
                }
            }

        } else {
            List l = new ArrayList<String>();
            l.add(user);
            Event e = new Event(eventName, l,location);
            em.persist(e);

        }
        em.close();
    }
}
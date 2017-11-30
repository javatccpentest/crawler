/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Crawler.DAO;

import Crawler.DAO.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Crawler.Model.Webpage;
import Crawler.Model.WebpageOff;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Matheus
 */
public class WebpageOffJpaController implements Serializable {

    public WebpageOffJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(WebpageOff webpageOff) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Webpage idWebpage = webpageOff.getIdWebpage();
            if (idWebpage != null) {
                idWebpage = em.getReference(idWebpage.getClass(), idWebpage.getId());
                webpageOff.setIdWebpage(idWebpage);
            }
            em.persist(webpageOff);
            if (idWebpage != null) {
                idWebpage.getWebpageOffCollection().add(webpageOff);
                idWebpage = em.merge(idWebpage);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(WebpageOff webpageOff) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WebpageOff persistentWebpageOff = em.find(WebpageOff.class, webpageOff.getId());
            Webpage idWebpageOld = persistentWebpageOff.getIdWebpage();
            Webpage idWebpageNew = webpageOff.getIdWebpage();
            if (idWebpageNew != null) {
                idWebpageNew = em.getReference(idWebpageNew.getClass(), idWebpageNew.getId());
                webpageOff.setIdWebpage(idWebpageNew);
            }
            webpageOff = em.merge(webpageOff);
            if (idWebpageOld != null && !idWebpageOld.equals(idWebpageNew)) {
                idWebpageOld.getWebpageOffCollection().remove(webpageOff);
                idWebpageOld = em.merge(idWebpageOld);
            }
            if (idWebpageNew != null && !idWebpageNew.equals(idWebpageOld)) {
                idWebpageNew.getWebpageOffCollection().add(webpageOff);
                idWebpageNew = em.merge(idWebpageNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = webpageOff.getId();
                if (findWebpageOff(id) == null) {
                    throw new NonexistentEntityException("The webpageOff with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WebpageOff webpageOff;
            try {
                webpageOff = em.getReference(WebpageOff.class, id);
                webpageOff.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The webpageOff with id " + id + " no longer exists.", enfe);
            }
            Webpage idWebpage = webpageOff.getIdWebpage();
            if (idWebpage != null) {
                idWebpage.getWebpageOffCollection().remove(webpageOff);
                idWebpage = em.merge(idWebpage);
            }
            em.remove(webpageOff);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<WebpageOff> findWebpageOffEntities() {
        return findWebpageOffEntities(true, -1, -1);
    }

    public List<WebpageOff> findWebpageOffEntities(int maxResults, int firstResult) {
        return findWebpageOffEntities(false, maxResults, firstResult);
    }

    private List<WebpageOff> findWebpageOffEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(WebpageOff.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public WebpageOff findWebpageOff(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(WebpageOff.class, id);
        } finally {
            em.close();
        }
    }

    public int getWebpageOffCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<WebpageOff> rt = cq.from(WebpageOff.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

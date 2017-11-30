/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Crawler.DAO;

import Crawler.DAO.exceptions.NonexistentEntityException;
import Crawler.Model.Dominio;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Crawler.Model.Webpage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Matheus
 */
public class DominioJpaController implements Serializable {

    public DominioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Dominio dominio) {
        if (dominio.getWebpageCollection() == null) {
            dominio.setWebpageCollection(new ArrayList<Webpage>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Webpage> attachedWebpageCollection = new ArrayList<Webpage>();
            for (Webpage webpageCollectionWebpageToAttach : dominio.getWebpageCollection()) {
                webpageCollectionWebpageToAttach = em.getReference(webpageCollectionWebpageToAttach.getClass(), webpageCollectionWebpageToAttach.getId());
                attachedWebpageCollection.add(webpageCollectionWebpageToAttach);
            }
            dominio.setWebpageCollection(attachedWebpageCollection);
            em.persist(dominio);
            for (Webpage webpageCollectionWebpage : dominio.getWebpageCollection()) {
                Dominio oldDominioIdOfWebpageCollectionWebpage = webpageCollectionWebpage.getDominioId();
                webpageCollectionWebpage.setDominioId(dominio);
                webpageCollectionWebpage = em.merge(webpageCollectionWebpage);
                if (oldDominioIdOfWebpageCollectionWebpage != null) {
                    oldDominioIdOfWebpageCollectionWebpage.getWebpageCollection().remove(webpageCollectionWebpage);
                    oldDominioIdOfWebpageCollectionWebpage = em.merge(oldDominioIdOfWebpageCollectionWebpage);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Dominio dominio) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Dominio persistentDominio = em.find(Dominio.class, dominio.getId());
            Collection<Webpage> webpageCollectionOld = persistentDominio.getWebpageCollection();
            Collection<Webpage> webpageCollectionNew = dominio.getWebpageCollection();
            Collection<Webpage> attachedWebpageCollectionNew = new ArrayList<Webpage>();
            for (Webpage webpageCollectionNewWebpageToAttach : webpageCollectionNew) {
                webpageCollectionNewWebpageToAttach = em.getReference(webpageCollectionNewWebpageToAttach.getClass(), webpageCollectionNewWebpageToAttach.getId());
                attachedWebpageCollectionNew.add(webpageCollectionNewWebpageToAttach);
            }
            webpageCollectionNew = attachedWebpageCollectionNew;
            dominio.setWebpageCollection(webpageCollectionNew);
            dominio = em.merge(dominio);
            for (Webpage webpageCollectionOldWebpage : webpageCollectionOld) {
                if (!webpageCollectionNew.contains(webpageCollectionOldWebpage)) {
                    webpageCollectionOldWebpage.setDominioId(null);
                    webpageCollectionOldWebpage = em.merge(webpageCollectionOldWebpage);
                }
            }
            for (Webpage webpageCollectionNewWebpage : webpageCollectionNew) {
                if (!webpageCollectionOld.contains(webpageCollectionNewWebpage)) {
                    Dominio oldDominioIdOfWebpageCollectionNewWebpage = webpageCollectionNewWebpage.getDominioId();
                    webpageCollectionNewWebpage.setDominioId(dominio);
                    webpageCollectionNewWebpage = em.merge(webpageCollectionNewWebpage);
                    if (oldDominioIdOfWebpageCollectionNewWebpage != null && !oldDominioIdOfWebpageCollectionNewWebpage.equals(dominio)) {
                        oldDominioIdOfWebpageCollectionNewWebpage.getWebpageCollection().remove(webpageCollectionNewWebpage);
                        oldDominioIdOfWebpageCollectionNewWebpage = em.merge(oldDominioIdOfWebpageCollectionNewWebpage);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = dominio.getId();
                if (findDominio(id) == null) {
                    throw new NonexistentEntityException("The dominio with id " + id + " no longer exists.");
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
            Dominio dominio;
            try {
                dominio = em.getReference(Dominio.class, id);
                dominio.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The dominio with id " + id + " no longer exists.", enfe);
            }
            Collection<Webpage> webpageCollection = dominio.getWebpageCollection();
            for (Webpage webpageCollectionWebpage : webpageCollection) {
                webpageCollectionWebpage.setDominioId(null);
                webpageCollectionWebpage = em.merge(webpageCollectionWebpage);
            }
            em.remove(dominio);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Dominio> findDominioEntities() {
        return findDominioEntities(true, -1, -1);
    }

    public List<Dominio> findDominioEntities(int maxResults, int firstResult) {
        return findDominioEntities(false, maxResults, firstResult);
    }

    private List<Dominio> findDominioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Dominio.class));
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

    public Dominio findDominio(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Dominio.class, id);
        } finally {
            em.close();
        }
    }

    public int getDominioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Dominio> rt = cq.from(Dominio.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

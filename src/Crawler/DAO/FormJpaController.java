/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Crawler.DAO;

import Crawler.DAO.exceptions.NonexistentEntityException;
import Crawler.Model.Form;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Crawler.Model.Webpage;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Matheus
 */
public class FormJpaController implements Serializable {

    public FormJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Form form) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Webpage webpageId = form.getWebpageId();
            if (webpageId != null) {
                webpageId = em.getReference(webpageId.getClass(), webpageId.getId());
                form.setWebpageId(webpageId);
            }
            em.persist(form);
            if (webpageId != null) {
                webpageId.getFormCollection().add(form);
                webpageId = em.merge(webpageId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Form form) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Form persistentForm = em.find(Form.class, form.getId());
            Webpage webpageIdOld = persistentForm.getWebpageId();
            Webpage webpageIdNew = form.getWebpageId();
            if (webpageIdNew != null) {
                webpageIdNew = em.getReference(webpageIdNew.getClass(), webpageIdNew.getId());
                form.setWebpageId(webpageIdNew);
            }
            form = em.merge(form);
            if (webpageIdOld != null && !webpageIdOld.equals(webpageIdNew)) {
                webpageIdOld.getFormCollection().remove(form);
                webpageIdOld = em.merge(webpageIdOld);
            }
            if (webpageIdNew != null && !webpageIdNew.equals(webpageIdOld)) {
                webpageIdNew.getFormCollection().add(form);
                webpageIdNew = em.merge(webpageIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = form.getId();
                if (findForm(id) == null) {
                    throw new NonexistentEntityException("The form with id " + id + " no longer exists.");
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
            Form form;
            try {
                form = em.getReference(Form.class, id);
                form.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The form with id " + id + " no longer exists.", enfe);
            }
            Webpage webpageId = form.getWebpageId();
            if (webpageId != null) {
                webpageId.getFormCollection().remove(form);
                webpageId = em.merge(webpageId);
            }
            em.remove(form);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Form> findFormEntities() {
        return findFormEntities(true, -1, -1);
    }

    public List<Form> findFormEntities(int maxResults, int firstResult) {
        return findFormEntities(false, maxResults, firstResult);
    }

    private List<Form> findFormEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Form.class));
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

    public Form findForm(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Form.class, id);
        } finally {
            em.close();
        }
    }

    public int getFormCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Form> rt = cq.from(Form.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Crawler.DAO;

import Crawler.DAO.exceptions.IllegalOrphanException;
import Crawler.DAO.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Crawler.Model.Dominio;
import Crawler.Model.Form;
import Crawler.Model.Webpage;
import java.util.ArrayList;
import java.util.Collection;
import Crawler.Model.WebpageOff;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Matheus
 */
public class WebpageJpaController implements Serializable {

    public WebpageJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Webpage webpage) {
        if (webpage.getFormCollection() == null) {
            webpage.setFormCollection(new ArrayList<Form>());
        }
        if (webpage.getWebpageOffCollection() == null) {
            webpage.setWebpageOffCollection(new ArrayList<WebpageOff>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Dominio dominioId = webpage.getDominioId();
            if (dominioId != null) {
                dominioId = em.getReference(dominioId.getClass(), dominioId.getId());
                webpage.setDominioId(dominioId);
            }
            Collection<Form> attachedFormCollection = new ArrayList<Form>();
            for (Form formCollectionFormToAttach : webpage.getFormCollection()) {
                formCollectionFormToAttach = em.getReference(formCollectionFormToAttach.getClass(), formCollectionFormToAttach.getId());
                attachedFormCollection.add(formCollectionFormToAttach);
            }
            webpage.setFormCollection(attachedFormCollection);
            Collection<WebpageOff> attachedWebpageOffCollection = new ArrayList<WebpageOff>();
            for (WebpageOff webpageOffCollectionWebpageOffToAttach : webpage.getWebpageOffCollection()) {
                webpageOffCollectionWebpageOffToAttach = em.getReference(webpageOffCollectionWebpageOffToAttach.getClass(), webpageOffCollectionWebpageOffToAttach.getId());
                attachedWebpageOffCollection.add(webpageOffCollectionWebpageOffToAttach);
            }
            webpage.setWebpageOffCollection(attachedWebpageOffCollection);
            em.persist(webpage);
            if (dominioId != null) {
                dominioId.getWebpageCollection().add(webpage);
                dominioId = em.merge(dominioId);
            }
            for (Form formCollectionForm : webpage.getFormCollection()) {
                Webpage oldWebpageIdOfFormCollectionForm = formCollectionForm.getWebpageId();
                formCollectionForm.setWebpageId(webpage);
                formCollectionForm = em.merge(formCollectionForm);
                if (oldWebpageIdOfFormCollectionForm != null) {
                    oldWebpageIdOfFormCollectionForm.getFormCollection().remove(formCollectionForm);
                    oldWebpageIdOfFormCollectionForm = em.merge(oldWebpageIdOfFormCollectionForm);
                }
            }
            for (WebpageOff webpageOffCollectionWebpageOff : webpage.getWebpageOffCollection()) {
                Webpage oldIdWebpageOfWebpageOffCollectionWebpageOff = webpageOffCollectionWebpageOff.getIdWebpage();
                webpageOffCollectionWebpageOff.setIdWebpage(webpage);
                webpageOffCollectionWebpageOff = em.merge(webpageOffCollectionWebpageOff);
                if (oldIdWebpageOfWebpageOffCollectionWebpageOff != null) {
                    oldIdWebpageOfWebpageOffCollectionWebpageOff.getWebpageOffCollection().remove(webpageOffCollectionWebpageOff);
                    oldIdWebpageOfWebpageOffCollectionWebpageOff = em.merge(oldIdWebpageOfWebpageOffCollectionWebpageOff);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Webpage webpage) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Webpage persistentWebpage = em.find(Webpage.class, webpage.getId());
            Dominio dominioIdOld = persistentWebpage.getDominioId();
            Dominio dominioIdNew = webpage.getDominioId();
            Collection<Form> formCollectionOld = persistentWebpage.getFormCollection();
            Collection<Form> formCollectionNew = webpage.getFormCollection();
            Collection<WebpageOff> webpageOffCollectionOld = persistentWebpage.getWebpageOffCollection();
            Collection<WebpageOff> webpageOffCollectionNew = webpage.getWebpageOffCollection();
            List<String> illegalOrphanMessages = null;
            for (WebpageOff webpageOffCollectionOldWebpageOff : webpageOffCollectionOld) {
                if (!webpageOffCollectionNew.contains(webpageOffCollectionOldWebpageOff)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain WebpageOff " + webpageOffCollectionOldWebpageOff + " since its idWebpage field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (dominioIdNew != null) {
                dominioIdNew = em.getReference(dominioIdNew.getClass(), dominioIdNew.getId());
                webpage.setDominioId(dominioIdNew);
            }
            Collection<Form> attachedFormCollectionNew = new ArrayList<Form>();
            for (Form formCollectionNewFormToAttach : formCollectionNew) {
                formCollectionNewFormToAttach = em.getReference(formCollectionNewFormToAttach.getClass(), formCollectionNewFormToAttach.getId());
                attachedFormCollectionNew.add(formCollectionNewFormToAttach);
            }
            formCollectionNew = attachedFormCollectionNew;
            webpage.setFormCollection(formCollectionNew);
            Collection<WebpageOff> attachedWebpageOffCollectionNew = new ArrayList<WebpageOff>();
            for (WebpageOff webpageOffCollectionNewWebpageOffToAttach : webpageOffCollectionNew) {
                webpageOffCollectionNewWebpageOffToAttach = em.getReference(webpageOffCollectionNewWebpageOffToAttach.getClass(), webpageOffCollectionNewWebpageOffToAttach.getId());
                attachedWebpageOffCollectionNew.add(webpageOffCollectionNewWebpageOffToAttach);
            }
            webpageOffCollectionNew = attachedWebpageOffCollectionNew;
            webpage.setWebpageOffCollection(webpageOffCollectionNew);
            webpage = em.merge(webpage);
            if (dominioIdOld != null && !dominioIdOld.equals(dominioIdNew)) {
                dominioIdOld.getWebpageCollection().remove(webpage);
                dominioIdOld = em.merge(dominioIdOld);
            }
            if (dominioIdNew != null && !dominioIdNew.equals(dominioIdOld)) {
                dominioIdNew.getWebpageCollection().add(webpage);
                dominioIdNew = em.merge(dominioIdNew);
            }
            for (Form formCollectionOldForm : formCollectionOld) {
                if (!formCollectionNew.contains(formCollectionOldForm)) {
                    formCollectionOldForm.setWebpageId(null);
                    formCollectionOldForm = em.merge(formCollectionOldForm);
                }
            }
            for (Form formCollectionNewForm : formCollectionNew) {
                if (!formCollectionOld.contains(formCollectionNewForm)) {
                    Webpage oldWebpageIdOfFormCollectionNewForm = formCollectionNewForm.getWebpageId();
                    formCollectionNewForm.setWebpageId(webpage);
                    formCollectionNewForm = em.merge(formCollectionNewForm);
                    if (oldWebpageIdOfFormCollectionNewForm != null && !oldWebpageIdOfFormCollectionNewForm.equals(webpage)) {
                        oldWebpageIdOfFormCollectionNewForm.getFormCollection().remove(formCollectionNewForm);
                        oldWebpageIdOfFormCollectionNewForm = em.merge(oldWebpageIdOfFormCollectionNewForm);
                    }
                }
            }
            for (WebpageOff webpageOffCollectionNewWebpageOff : webpageOffCollectionNew) {
                if (!webpageOffCollectionOld.contains(webpageOffCollectionNewWebpageOff)) {
                    Webpage oldIdWebpageOfWebpageOffCollectionNewWebpageOff = webpageOffCollectionNewWebpageOff.getIdWebpage();
                    webpageOffCollectionNewWebpageOff.setIdWebpage(webpage);
                    webpageOffCollectionNewWebpageOff = em.merge(webpageOffCollectionNewWebpageOff);
                    if (oldIdWebpageOfWebpageOffCollectionNewWebpageOff != null && !oldIdWebpageOfWebpageOffCollectionNewWebpageOff.equals(webpage)) {
                        oldIdWebpageOfWebpageOffCollectionNewWebpageOff.getWebpageOffCollection().remove(webpageOffCollectionNewWebpageOff);
                        oldIdWebpageOfWebpageOffCollectionNewWebpageOff = em.merge(oldIdWebpageOfWebpageOffCollectionNewWebpageOff);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = webpage.getId();
                if (findWebpage(id) == null) {
                    throw new NonexistentEntityException("The webpage with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Webpage webpage;
            try {
                webpage = em.getReference(Webpage.class, id);
                webpage.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The webpage with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<WebpageOff> webpageOffCollectionOrphanCheck = webpage.getWebpageOffCollection();
            for (WebpageOff webpageOffCollectionOrphanCheckWebpageOff : webpageOffCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Webpage (" + webpage + ") cannot be destroyed since the WebpageOff " + webpageOffCollectionOrphanCheckWebpageOff + " in its webpageOffCollection field has a non-nullable idWebpage field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Dominio dominioId = webpage.getDominioId();
            if (dominioId != null) {
                dominioId.getWebpageCollection().remove(webpage);
                dominioId = em.merge(dominioId);
            }
            Collection<Form> formCollection = webpage.getFormCollection();
            for (Form formCollectionForm : formCollection) {
                formCollectionForm.setWebpageId(null);
                formCollectionForm = em.merge(formCollectionForm);
            }
            em.remove(webpage);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Webpage> findWebpageEntities() {
        return findWebpageEntities(true, -1, -1);
    }

    public List<Webpage> findWebpageEntities(int maxResults, int firstResult) {
        return findWebpageEntities(false, maxResults, firstResult);
    }

    private List<Webpage> findWebpageEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Webpage.class));
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

    public Webpage findWebpage(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Webpage.class, id);
        } finally {
            em.close();
        }
    }

    public int getWebpageCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Webpage> rt = cq.from(Webpage.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

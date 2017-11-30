/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Crawler.Model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Matheus
 */
@Entity
@Table(name = "webpage")
@NamedQueries({
    @NamedQuery(name = "Webpage.findAll", query = "SELECT w FROM Webpage w")
    , @NamedQuery(name = "Webpage.findByUrlDom", query = "SELECT w FROM Webpage w WHERE w.url = :url and w.dominioId = :dom")
    ,@NamedQuery(name = "Webpage.findAllByDominio", query = "SELECT w FROM Webpage w WHERE w.dominioId = :dominio")
    , @NamedQuery(name = "Webpage.findById", query = "SELECT w FROM Webpage w WHERE w.id = :id")
    , @NamedQuery(name = "Webpage.findByUrl", query = "SELECT w FROM Webpage w WHERE w.url = :url")
    , @NamedQuery(name = "Webpage.findByContentType", query = "SELECT w FROM Webpage w WHERE w.contentType = :contentType")})
public class Webpage implements Serializable {

    @OneToMany(mappedBy = "webpageId",
            cascade = CascadeType.REMOVE)
    private Collection<Form> formCollection;
    @OneToMany(mappedBy = "idWebpage",
            cascade = CascadeType.REMOVE)
    private Collection<WebpageOff> webpageOffCollection;

    @Column(name = "isDirectory")
    private Boolean isDirectory;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "URL")
    private String url;
    @Basic(optional = false)
    @Column(name = "CONTENT_TYPE")
    private String contentType;
    @JoinColumn(name = "DOMINIO_ID", referencedColumnName = "ID")
    @ManyToOne
    private Dominio dominioId;

    public Webpage() {
    }

    public Webpage(Long id) {
        this.id = id;
    }

    public Webpage(Long id, String contentType) {
        this.id = id;
        this.contentType = contentType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Dominio getDominioId() {
        return dominioId;
    }

    public void setDominioId(Dominio dominioId) {
        this.dominioId = dominioId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Webpage)) {
            return false;
        }
        Webpage other = (Webpage) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return url;
    }

    public Boolean getIsDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(Boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    @XmlTransient
    public Collection<Form> getFormCollection() {
        return formCollection;
    }

    public void setFormCollection(Collection<Form> formCollection) {
        this.formCollection = formCollection;
    }

    @XmlTransient
    public Collection<WebpageOff> getWebpageOffCollection() {
        return webpageOffCollection;
    }

    public void setWebpageOffCollection(Collection<WebpageOff> webpageOffCollection) {
        this.webpageOffCollection = webpageOffCollection;
    }

}

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Matheus
 */
@Entity
@Table(name = "dominio")
@NamedQueries({
    @NamedQuery(name = "Dominio.findAll", query = "SELECT d FROM Dominio d")
    , @NamedQuery(name = "Dominio.findById", query = "SELECT d FROM Dominio d WHERE d.id = :id")
    , @NamedQuery(name = "Dominio.findByDominio", query = "SELECT d FROM Dominio d WHERE d.dominio = :dominio")
    , @NamedQuery(name = "Dominio.findByRemoteAddress", query = "SELECT d FROM Dominio d WHERE d.remoteAddress = :remoteAddress")
    , @NamedQuery(name = "Dominio.findByServer", query = "SELECT d FROM Dominio d WHERE d.server = :server")})
public class Dominio implements Serializable {

    @OneToMany(mappedBy = "dominioId",
            cascade = CascadeType.REMOVE)
    private Collection<Webpage> webpageCollection;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "DOMINIO")
    private String dominio;
    @Basic(optional = false)
    @Column(name = "REMOTE_ADDRESS")
    private String remoteAddress;
    @Basic(optional = false)
    @Column(name = "SERVER")
    private String server;

    public Dominio() {
    }

    public Dominio(Long id) {
        this.id = id;
    }

    public Dominio(Long id, String remoteAddress, String server) {
        this.id = id;
        this.remoteAddress = remoteAddress;
        this.server = server;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDominio() {
        return dominio;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
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
        if (!(object instanceof Dominio)) {
            return false;
        }
        Dominio other = (Dominio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return dominio;
    }

    @XmlTransient
    public Collection<Webpage> getWebpageCollection() {
        return webpageCollection;
    }

    public void setWebpageCollection(Collection<Webpage> webpageCollection) {
        this.webpageCollection = webpageCollection;
    }

}

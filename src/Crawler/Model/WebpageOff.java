/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Crawler.Model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Matheus
 */
@Entity
@Table(name = "webpage_off")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "WebpageOff.findAll", query = "SELECT w FROM WebpageOff w")
    , @NamedQuery(name = "WebpageOff.findById", query = "SELECT w FROM WebpageOff w WHERE w.id = :id")
    , @NamedQuery(name = "WebpageOff.findByUrl", query = "SELECT w FROM WebpageOff w WHERE w.url = :url")
    , @NamedQuery(name = "WebpageOff.findByContentType", query = "SELECT w FROM WebpageOff w WHERE w.contentType = :contentType")})
public class WebpageOff implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @Column(name = "url")
    private String url;
    @Basic(optional = false)
    @Column(name = "content_type")
    private String contentType;
    @JoinColumn(name = "id_webpage", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Webpage idWebpage;

    public WebpageOff() {
    }

    public WebpageOff(Long id) {
        this.id = id;
    }

    public WebpageOff(Long id, String url, String contentType) {
        this.id = id;
        this.url = url;
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

    public Webpage getIdWebpage() {
        return idWebpage;
    }

    public void setIdWebpage(Webpage idWebpage) {
        this.idWebpage = idWebpage;
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
        if (!(object instanceof WebpageOff)) {
            return false;
        }
        WebpageOff other = (WebpageOff) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return url;
    }
    
}

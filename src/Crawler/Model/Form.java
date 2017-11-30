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
@Table(name = "form")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Form.findAll", query = "SELECT f FROM Form f")
    ,@NamedQuery(name = "Form.findAllByWebpage", query = "SELECT f FROM Form f WHERE f.webpageId = :webpage")
    , @NamedQuery(name = "Form.findById", query = "SELECT f FROM Form f WHERE f.id = :id")
    , @NamedQuery(name = "Form.findByForm", query = "SELECT f FROM Form f WHERE f.form = :form")
    , @NamedQuery(name = "Form.findByFormHash", query = "SELECT f FROM Form f WHERE f.formHash = :formHash")})
public class Form implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "FORM",
             length = 20000)
    private String form;
    @Column(name = "FORM_HASH")
    private String formHash;
    @JoinColumn(name = "WEBPAGE_ID", referencedColumnName = "ID")
    @ManyToOne
    private Webpage webpageId;

    public Form() {
    }

    public Form(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getFormHash() {
        return formHash;
    }

    public void setFormHash(String formHash) {
        this.formHash = formHash;
    }

    public Webpage getWebpageId() {
        return webpageId;
    }

    public void setWebpageId(Webpage webpageId) {
        this.webpageId = webpageId;
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
        if (!(object instanceof Form)) {
            return false;
        }
        Form other = (Form) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return form;
    }

}

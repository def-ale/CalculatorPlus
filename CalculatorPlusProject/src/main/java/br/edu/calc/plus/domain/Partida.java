/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.calc.plus.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import br.edu.calc.plus.util.Util;

/**
 *
 * @author daves
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Partida.findAll", query = "SELECT p FROM Partida p")
    , @NamedQuery(name = "Partida.findById", query = "SELECT p FROM Partida p WHERE p.id = :id")
    , @NamedQuery(name = "Partida.findByData", query = "SELECT p FROM Partida p WHERE p.data = :data")
    , @NamedQuery(name = "Partida.findByBonificacao", query = "SELECT p FROM Partida p WHERE p.bonificacao = :bonificacao")})
public class Partida implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
//    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime data;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private double bonificacao;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false, columnDefinition = "INT(11)")
    private long tempo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "partida")
    private List<Jogo> jogoList;
    @JoinColumn(name = "usuario", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Usuario usuario;

    public Partida() {
    }

    public Partida(Integer id) {
        this.id = id;
    }

    public Partida(Integer id, LocalDateTime data, double bonificacao, long tempo) {
        this.id = id;
        this.data = data;
        this.bonificacao = bonificacao;
        this.tempo = tempo;
    }

    public int getAcertos() {
        int tot = 0;
        for (Jogo jogo : jogoList) {
            tot += jogo.estaCerto() ? 1 : 0;
        }
        return tot;
    }

    public int getErros() {
        int tot = 0;
        for (Jogo jogo : jogoList) {
            tot += !jogo.estaCerto() ? 1 : 0;
        }
        return tot;
    }

    public String getDataFormatada() {
        return Util.formatarData(data);
    }

    public String getBonificacaoFormatada() {
        return Util.formatarMoeda(bonificacao);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public double getBonificacao() {
        return bonificacao;
    }

    public void setBonificacao(double bonificacao) {
        this.bonificacao = bonificacao;
    }

    @XmlTransient
    public List<Jogo> getJogoList() {
        return jogoList;
    }

    public void setJogoList(List<Jogo> jogoList) {
        this.jogoList = jogoList;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public long getTempo() {
        return tempo;
    }

    public void setTempo(long tempo) {
        this.tempo = tempo;
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
        if (!(object instanceof Partida)) {
            return false;
        }
        Partida other = (Partida) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.edu.vianna.plus.calculatorplus.domain.Partida[ id=" + id + " ]";
    }

    public void addBonus(double bonus) {
        bonificacao += bonus;
    }

    public void removeBonus(double bonus) {
        bonificacao -= bonus;
    }

}

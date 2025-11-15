/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.calc.plus.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

/**
 *
 * @author daves
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Jogo.findAll", query = "SELECT j FROM Jogo j")
    , @NamedQuery(name = "Jogo.findByIdjogo", query = "SELECT j FROM Jogo j WHERE j.idjogo = :idjogo")
    , @NamedQuery(name = "Jogo.findByValor1", query = "SELECT j FROM Jogo j WHERE j.valor1 = :valor1")
    , @NamedQuery(name = "Jogo.findByValor2", query = "SELECT j FROM Jogo j WHERE j.valor2 = :valor2")
    , @NamedQuery(name = "Jogo.findByOperador", query = "SELECT j FROM Jogo j WHERE j.operador = :operador")
    , @NamedQuery(name = "Jogo.findByResultado", query = "SELECT j FROM Jogo j WHERE j.resultado = :resultado")
    , @NamedQuery(name = "Jogo.findByResposta", query = "SELECT j FROM Jogo j WHERE j.resposta = :resposta")})
public class Jogo implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer idjogo;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private double valor1;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private double valor2;
    @Basic(optional = false)
    @NotNull
//    @Size(min = 1, max = 2)
//    @Column(nullable = false, length = 2)
//    @Enumerated(EnumType.STRING)
    //@Column(columnDefinition = "ENUM('+', '-', '/','*')")
    @Enumerated(EnumType.STRING)
    private EOperator operador;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private double resultado;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private double resposta;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private double bonus;
    @JoinColumn(name = "partida", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Partida partida;

    public Jogo() {
    }

    public Jogo(Integer idjogo) {
        this.idjogo = idjogo;
    }

    public Jogo(Integer idjogo, double valor1, double valor2, EOperator operador, double resultado, double resposta, double bonus) {
        this.idjogo = idjogo;
        this.valor1 = valor1;
        this.valor2 = valor2;
        this.operador = operador;
        this.resultado = resultado;
        this.resposta = resposta;
        this.bonus = bonus;
    }

    public Integer getIdjogo() {
        return idjogo;
    }

    public void setIdjogo(Integer idjogo) {
        this.idjogo = idjogo;
    }

    public double getValor1() {
        return valor1;
    }

    public void setValor1(double valor1) {
        this.valor1 = valor1;
    }

    public double getValor2() {
        return valor2;
    }

    public void setValor2(double valor2) {
        this.valor2 = valor2;
    }

    public EOperator getOperador() {
        return operador;
    }

    public void setOperador(EOperator operador) {
        this.operador = operador;
    }

    public double getResultado() {
        return resultado;
    }

    public void setResultado(double resultado) {
        this.resultado = resultado;
    }

    public double getResposta() {
        return resposta;
    }

    public void setResposta(double resposta) {
        this.resposta = resposta;
    }

    public Partida getPartida() {
        return partida;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.idjogo);
        hash = 61 * hash + (int) (Double.doubleToLongBits(this.valor1) ^ (Double.doubleToLongBits(this.valor1) >>> 32));
        hash = 61 * hash + (int) (Double.doubleToLongBits(this.valor2) ^ (Double.doubleToLongBits(this.valor2) >>> 32));
        hash = 61 * hash + Objects.hashCode(this.operador);
        hash = 61 * hash + (int) (Double.doubleToLongBits(this.resultado) ^ (Double.doubleToLongBits(this.resultado) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Jogo)) {
            return false;
        }
        Jogo other = (Jogo) object;
        if ((this.idjogo == null && other.idjogo != null) || (this.idjogo != null && !this.idjogo.equals(other.idjogo))) {
            return false;
        }
        return true;
    }

    public boolean estaCerto() {
        return resposta == resultado;
    }


    public boolean isCorrect() {
        return resultado == resposta;
    }

}

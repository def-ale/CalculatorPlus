/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.calc.plus.domain;

/**
 *
 * @author daves
 */
public enum EOperator {

    soma("+"),
    subtracao("-"),
    multiplicacao("*"),
    divisao("/");

//    static double calcular(int v1, int v2, EOperator value) {
//
//        if (value == soma) {
//            return v1 + v2;
//        } else if (value == divisao) {
//            return v1 / v2;
//        }
//        if (value == multiplicacao) {
//            return v1 * v2;
//        }
//        if (value == subtracao) {
//            return v1 - v2;
//        }
//        return 0;
//    }
    public String getOperador() {
        return operator;
    }
    public final String operator;

    private EOperator(String operator) {
        this.operator = operator;
    }

    public double calcular(int v1, int v2) {
        if (operator == "+") {
            return v1 + v2;
        } else if (operator == "/") {
            return v1 / v2;
        }
        if (operator == "*") {
            return v1 * v2;
        }
        if (operator == "-") {
            return v1 - v2;
        }
        return 0;
    }

}

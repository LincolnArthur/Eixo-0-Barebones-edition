package com.jogo.personagens;

public class Melee extends Inimigo {

    public Melee(String nome, int vidaMaxima, int dano, double chanceCritico) {
        super(nome, vidaMaxima, new Espada("espada-inimigo", "Espada", "Arma de um inimigo melee", dano, chanceCritico));
    }
}
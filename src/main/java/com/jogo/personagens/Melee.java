package com.jogo.personagens;

public class Melee extends Inimigo {

    private Arma arma;

    public Melee(String nome, int vidaMaxima, Arma arma) {
        super(nome, vidaMaxima);
        this.arma = arma;
    }

    @Override
    public int atacar() {
        return arma.causarDano();
    }
}

package com.jogo.personagens;

public class Melee extends Inimigo {

    private int danoBase;

    public Melee(String nome, int vidaMaxima, int danoBase) {
        super(nome, vidaMaxima);
        this.danoBase = danoBase;
    }

    @Override
    public int atacar() {
        return danoBase;
    }
}

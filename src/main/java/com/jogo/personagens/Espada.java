package com.jogo.personagens;

public class Espada extends Arma {
    private int dano;

    public Espada(int dano, double chanceCritico) {
        super(chanceCritico);
        this.dano = dano;
    }

    @Override
    protected int calcularDanoBase() {
        return dano;
    }
}

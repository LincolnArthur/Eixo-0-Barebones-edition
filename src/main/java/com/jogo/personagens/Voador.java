package com.jogo.personagens;

import java.util.Random;

public class Voador extends Inimigo implements Esquivavel {

    private int danoBase;
    private double chanceEsquiva;
    private Random gerador;

    public Voador(String nome, int vidaMaxima, int danoBase, double chanceEsquiva) {
        super(nome, vidaMaxima);
        this.danoBase = danoBase;
        this.chanceEsquiva = chanceEsquiva;
        this.gerador = new Random();
    }

    @Override 
    public int atacar() {
        return danoBase;
    }

    @Override
    public boolean tentarEsquivar() {
        if (gerador.nextDouble() < chanceEsquiva) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void tomarDano(int quantidade) {
        if (tentarEsquivar()) {
            return; // esquivou -> nenhum dano aplicado
        }
        super.tomarDano(quantidade);
    }
}

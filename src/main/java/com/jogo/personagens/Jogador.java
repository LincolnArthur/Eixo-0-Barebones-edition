package com.jogo.personagens;

import java.util.Random;
//Para implementar a prob. de dano crítico

public class Jogador extends Personagem {
    private int danoBase;
    private double chanceCritico;
    private Random gerador;
    
    public Jogador(String nome, int vidaMaxima, int danoBase, double chanceCritico, Random gerador) {
        super(nome, vidaMaxima);
        this.danoBase =  danoBase;
        this.chanceCritico = chanceCritico;
        this.gerador = new Random(); //Gerar um valor rnd pra ver se crita
    }

    public int atacar() {
        boolean critico = gerador.nextDouble() < chanceCritico;
        if (critico) {
            return danoBase * 2;
        }
        return danoBase;
    }
}

package com.jogo.personagens;

import java.util.Random;
//Esse enemy é oq mais faz coisa
//Ele tem a arma (importado de Arma) que tem chance de critico
//e tem a chance de se esquivar (interface esquivavel) por isso precisa de dois geradores
//Pra evitar que os dois dependam do mesmo numero aleatorio -lincoln
public class Voador extends Inimigo implements Esquivavel {

    private Arma arma;
    private double chanceEsquiva;
    private Random gerador;

    public Voador(String nome, int vidaMaxima, Arma arma, double chanceEsquiva) {
        super(nome, vidaMaxima);
        this.arma = arma;
        this.chanceEsquiva = chanceEsquiva;
        this.gerador = new Random();
    }

    @Override 
    public int atacar() {
        return arma.causarDano();
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
            return; // esquivou -> nenhum dano aplicado -lincoln
        }
        super.tomarDano(quantidade);
    }
}

package com.jogo.personagens;

import java.util.Random;

//Esse enemy é oq mais faz coisa
//Ele tem a arma (agora travada como Espada) que tem chance de critico
//e tem a chance de se esquivar (interface esquivavel) por isso precisa de dois geradores
//Pra evitar que os dois dependam do mesmo numero aleatorio -lincoln
public class Voador extends Inimigo implements Esquivavel {

    private double chanceEsquiva;
    private Random gerador;

    public Voador(String nome, int vidaMaxima, int dano, double chanceCritico, double chanceEsquiva) {
        super(nome, vidaMaxima, new Espada("espada-voador", "Garra", "Arma de um inimigo voador", dano, chanceCritico));
        this.chanceEsquiva = chanceEsquiva;
        this.gerador = new Random();
    }

    @Override
    public boolean tentarEsquivar() {
        return gerador.nextDouble() < chanceEsquiva;
    }

    @Override
    public void tomarDano(int quantidade) {
        if (tentarEsquivar()) {
            return; // esquivou -> nenhum dano aplicado -lincoln
        }
        super.tomarDano(quantidade);
    }
}
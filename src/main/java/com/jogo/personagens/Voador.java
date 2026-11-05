package com.jogo.personagens;

import com.jogo.combate.Espada;
// Inimigo voador. Sem posição nem movimento, o voo ainda não muda nada
// no comportamento; só passa a fazer diferença quando houver engine.
public class Voador extends Inimigo {

    public Voador(String nome, int vidaMaxima, int dano, double chanceCritico) {
        super(nome, vidaMaxima, new Espada("espada-voador", "Garra", "Arma de um inimigo voador", dano, chanceCritico));
    }
}
package com.jogo.personagens;

public class Sniper extends Inimigo {

    public Sniper(String nome, int vidaMaxima, int dano, double chanceErrar, double chanceCritico) {
        super(nome, vidaMaxima, new ArmaDeFogo("arma-fogo-inimigo", "Rifle", "Arma de um inimigo sniper", dano, chanceErrar, chanceCritico));
    }
}
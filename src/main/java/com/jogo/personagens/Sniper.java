package com.jogo.personagens;

import com.jogo.combate.ArmaDeFogo;

public class Sniper extends Inimigo {

    public Sniper(String nome, int vidaMaxima, int dano, double chanceCritico) {
        super(nome, vidaMaxima, new ArmaDeFogo("arma-fogo-inimigo", "Rifle", "Arma de um inimigo sniper", dano, chanceCritico));
    }

    //Quanto XP vale derrotar esse inimigo (atirador, o mais perigoso)
    @Override
    public int getExperienciaConcedida() {
        return 35;
    }
}

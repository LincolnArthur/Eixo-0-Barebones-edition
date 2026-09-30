package com.jogo.personagens;

public class Ataque {

    private final int dano;

    public Ataque(int dano) {
        this.dano = dano;
    }

    public int getDano() {
        return dano;
    }

    public void aplicarEm(Atacavel alvo) {
        if(alvo != null && !alvo.estaMorto()) {
            alvo.tomarDano(dano);
        }
    }
}
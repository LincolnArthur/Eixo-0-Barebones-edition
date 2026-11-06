package com.jogo.combate;

public class Ataque {

    private final int dano;

    public Ataque(int dano) {
        if (dano < 0) {
            throw new IllegalArgumentException("Dano não pode ser negativo: " + dano);
        }
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
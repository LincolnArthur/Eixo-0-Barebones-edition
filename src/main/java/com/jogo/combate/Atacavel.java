package com.jogo.combate;

//Define os metodos de tomar dano e de morte -lincoln

public interface Atacavel {
    void tomarDano(int quantidade);
    boolean estaMorto();
}

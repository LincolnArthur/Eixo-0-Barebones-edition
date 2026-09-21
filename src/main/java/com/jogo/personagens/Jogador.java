package com.jogo.personagens;

public class Jogador extends Personagem implements Atacante {
    //Implementa o metodo de ataque por meio
    //da classe abstrata Arma
    private Arma arma;
    
    public Jogador(String nome, int vidaMaxima, Arma arma) {
        super(nome, vidaMaxima);
        this.arma = arma;
    }

    @Override 
    public int atacar() {
       return arma.causarDano();
    }
}

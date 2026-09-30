package com.jogo.personagens;


//Classe herda metodos e estados da superclasse
//Tipo abstrato, por conta do polimorfismo.
//Cada tipo diferente de inimigo vai atacar de uma forma diferente
//attack() é abstrato por isso. -lincoln
public abstract class Inimigo extends Personagem implements Atacante, ConcedeExperiencia {

    private final Arma arma;
    private int experienciaConcedida;

    public Inimigo(String nome, int vidaMaxima, Arma arma) {
        super(nome, vidaMaxima);
        this.arma = arma;
    }


    // Igual pra QUALQUER inimigo: cria o Ataque a partir da própria arma e aplica -lincoln
    @Override
    public void iniciarAtaque(Personagem vitima) {
        Ataque ataque = new Ataque(arma.causarDano());
        ataque.aplicarEm(vitima);
    }

    
    @Override
    public int getExperienciaConcedida() {
        return experienciaConcedida;
    }
}

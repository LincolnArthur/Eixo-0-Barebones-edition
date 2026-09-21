package com.jogo.personagens;

//Primeiro tipo de inimigo
public  class Sniper extends Inimigo {
    //Só um parâmetro novo, o resto puxa da superclasse
    private Arma arma;

    //Construtor
    public Sniper(String nome, int vidaMaxima, Arma arma) {
        super(nome, vidaMaxima);
        this.arma = arma;
    }

    //@Override é uma anotação, que é uma marcação que se coloca no código afim
    //de dar informação extra à IDE
    //Nesse casp ela diz:
    //"EU TÔ LIGADO QUE TÁ SOBRESCREVENDO E OU EU NÃO LIGO, OU FOI PQ EU QUIS"

    @Override
    //Feito usando a classe projetil (que só devolve dano por agr)
    public int atacar() {
        return arma.causarDano();
    }
}
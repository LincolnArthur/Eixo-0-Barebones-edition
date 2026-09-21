package com.jogo.personagens;

//Primeiro tipo de inimigo
public  class Sniper extends Inimigo {
    //Só um parâmetro novo, o resto puxa da superclasse
    private int danoBase; //Cada tipo de inimigo vai calcular o dmg sozinho


    //Construtor
    public Sniper(String nome, int vidaMaxima, int danoBase) {
        super(nome, vidaMaxima);
        this.danoBase = danoBase;
    }

    //@Override é uma anotação, que é uma marcação que se coloca no código afim
    //de dar informação extra à IDE
    //Nesse casp ela diz:
    //"EU TÔ LIADO QUE TÁ SOBRESCREVENDO E OU EU NÃO LIGO, OU FOI PQ EU QUIS"

    @Override
    //Feito usando a classe projetil (que só devolve dano por agr)
    public int atacar() {
        Projetil projetil = new Projetil(danoBase);
        return projetil.getDano();
    }
}
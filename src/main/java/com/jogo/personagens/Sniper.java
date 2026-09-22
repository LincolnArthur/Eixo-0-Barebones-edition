package com.jogo.personagens;

//Tipo de inimigo com range 
public  class Sniper extends Inimigo {
    //Só um parâmetro novo, o resto puxa da superclasse -lincoln
    private Arma arma;

    //Construtor
    public Sniper(String nome, int vidaMaxima, Arma arma) {
        super(nome, vidaMaxima);
        this.arma = arma;
    }

    //@Override é uma anotação, que é uma marcação que se coloca no código afim
    //de dar informação extra à IDE
    //Nesse caso ela diz:
    //"EU TÔ LIGADO QUE TÁ SOBRESCREVENDO E OU EU NÃO LIGO, OU FOI PQ EU QUIS"
    //-lincoln

    //Usado dentro de arma, como parametro de arma de fogo -lincoln
    @Override
    public int atacar() {
        return arma.causarDano();
    }
}
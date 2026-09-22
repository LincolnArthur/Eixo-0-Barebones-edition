package com.jogo.personagens;
//Arma simples, que só devolve dano, mais nenhum adicional
//Puxa o super de Arma, ent já tem Dano crítico (e java random) por indução
//-lincoln

public class Espada extends Arma {
    private int dano;

    public Espada(int dano, double chanceCritico) {
        super(chanceCritico);
        this.dano = dano;
    }

    @Override
    protected int calcularDanoBase() {
        return dano;
    }
}

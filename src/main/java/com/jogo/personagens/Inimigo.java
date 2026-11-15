package com.jogo.personagens;

import com.jogo.combate.Arma;
import com.jogo.combate.Atacante;
import com.jogo.combate.Ataque;
import com.jogo.combate.ConcedeExperiencia;

//Classe herda metodos e estados da superclasse
//Tipo abstrato, por conta do polimorfismo.
//Cada tipo diferente de inimigo vai atacar de uma forma diferente
//attack() é abstrato por isso. -lincoln
//
//getExperienciaConcedida() (da interface ConcedeExperiencia) não é
//escrito aqui: cada filha é obrigada a dizer quanto vale. Antes era um
//campo que nunca recebia valor, então todo inimigo dava 0 de XP.
public abstract class Inimigo extends Personagem implements Atacante, ConcedeExperiencia {

    private final Arma arma;

    public Inimigo(String nome, int vidaMaxima, Arma arma) {
        super(nome, vidaMaxima);
        this.arma = arma;
    }


    // Igual pra QUALQUER inimigo: cria o Ataque a partir da própria arma e aplica -lincoln
    @Override
    public void iniciarAtaque(Personagem vitima) {
        Ataque ataque = new Ataque(arma.causarDano(vitima));
        ataque.aplicarEm(vitima);
    }

    public Arma getArma() {
        return arma;
    }
}

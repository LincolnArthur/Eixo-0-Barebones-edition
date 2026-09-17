package com.jogo.personagens;


//Classe herda metodos e estados da superclasse
//Tipo abstrato, por conta do polimorfismo.
//Cada tipo diferente de inimigo vai atacar de uma forma diferente
//attack() é abstrato por isso.
public abstract class Inimigo extends Personagem {

    public Inimigo(String nome, int vidaMaxima) {
        super(nome, vidaMaxima);
    }

    public abstract int atacar(); 
                                //Com int o método vira consulta pura
                                //Só calcula quanto o atk causaria sem
                                //tocar em mais nada. Isso abre espaço
                                //pra inserir mais tarde logicas entre
                                //o cálculo e a aplicação, sem precisar
                                //mexer em homicídio
}

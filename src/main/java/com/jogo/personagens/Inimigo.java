package com.jogo.personagens;


//Classe herda metodos e estados da superclasse
//Tipo abstrato, por conta do polimorfismo.
//Cada tipo diferente de inimigo vai atacar de uma forma diferente
//attack() é abstrato por isso. -lincoln
public abstract class Inimigo extends Personagem implements Atacante, ConcedeExperiencia {

    private int experienciaConcedida;

    public Inimigo(String nome, int vidaMaxima) {
        super(nome, vidaMaxima);
    }


    //Sem override pq não está sobrescrevendo
    //só redeclarando -lincoln
    public abstract int atacar();
    //Não precisa saber que "Arma" existe pq ele
    //não possui nenhuma definição de calculo de dano -lincoln

    
    //Com int o método vira consulta pura
    //Só calcula quanto o atk causaria sem
    //tocar em mais nada. Isso abre espaço
    //pra inserir mais tarde logicas entre
    //o cálculo e a aplicação, sem precisar
    //mexer em muita coisa (abstração) -lincoln


    @Override
    public int getExperienciaConcedida() {
        return experienciaConcedida;
    }
}

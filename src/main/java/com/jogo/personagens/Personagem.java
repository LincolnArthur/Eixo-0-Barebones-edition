package com.jogo.personagens;

public class Personagem {

    //O estado que cada personagem carrega individualmente
    private String nome; // private: Ninguém altera fora a classe
    private int vidaAtual;
    private int vidaMaxima;

    //Construtor: Roda quando um new Personagem é criado
    public Personagem(String nome, int vidaMaxima) { 
        this.nome = nome; //this: separa o parâmetro do construtor do da classe
                          //Os dois tem o mesmo nome, então é necessário
        this.vidaAtual = vidaMaxima; // Cmc com vida cheia
        this.vidaMaxima = vidaMaxima;
    }

    //Usa chamando um new
    //Tipo struct
    //Personagem Herói = new Personagem("Herói", 20)

    //Pega o nome do obj gerado com o new
    public String getNome() {
        return nome;
    }

    //Comportamentos: Ações que o personagem sabe executar
    public void tomarDano (int quantidade) {
        vidaAtual = vidaAtual - quantidade;
    }
    
    //verifica se o character está com vida cheia
    public boolean estaSaudavel() {
        return vidaAtual == vidaMaxima;
    }

    //Verifica se o character está morto
    public boolean estaMorto() {
        return vidaAtual <= 0;
    }
    
}

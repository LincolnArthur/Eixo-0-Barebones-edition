package com.jogo.personagens;

public class Personagem implements Atacavel{

    //O estado que cada personagem carrega individualmente
    private String nome; // private: Ninguém altera fora a classe -lincoln
    private int vidaAtual;
    private int vidaMaxima;
    
    

    //Construtor: Roda quando um new Personagem é criado -lincoln
    public Personagem(String nome, int vidaMaxima) { 
        this.nome = nome; //this: separa o parâmetro do construtor do da classe
                          //Os dois tem o mesmo nome, então é necessário
        this.vidaAtual = vidaMaxima; // Cmc com vida cheia -lincoln
        this.vidaMaxima = vidaMaxima;
        
    }

    //Usa chamando um new
    //Tipo struct
    //Personagem Herói = new Personagem("Herói", 20)

    //Pega o nome do obj gerado com o new -lincoln
    public String getNome() {
        return nome;
    }

    //Comportamentos: Ações que o personagem sabe executar

    @Override
    public void tomarDano (int quantidade) {
        if(quantidade<=0){
            return;
        }
        this.vidaAtual=Math.max(0, this.vidaAtual - quantidade); //Garante que vida n fique negativa -Raioni
    }

    //Verifica se o character está morto -lincoln
    @Override 
    public boolean estaMorto() {
        return vidaAtual <= 0;
    }

       //verifica se o character está com vida cheia -lincoln
    public boolean estaSaudavel() {
        return vidaAtual == vidaMaxima;
    }

    // sobrescrita do toString() herdado de Object
    // permite exibir informações do personagem de forma legivel, nemilha tinha pedido pra usar isso aqui na aula passada - hiago
    @Override
    public String toString() {
        return nome + " [Vida: " + vidaAtual + "/" + vidaMaxima + (estaMorto() ? " (Morto)" : "") + "]";
    }

}

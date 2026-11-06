package com.jogo.personagens;

import com.jogo.combate.Atacavel;

public class Personagem implements Atacavel {

    //O estado que cada personagem carrega individualmente
    private String nome; // private: Ninguém altera fora a classe -lincoln
    private int vidaAtual;
    private int vidaMaxima;

    //Construtor: Roda quando um new Personagem é criado -lincoln
    public Personagem(String nome, int vidaMaxima) {
        //Validação: personagem sem nome ou sem vida é erro de quem
        //escreveu o código, não algo que acontece jogando. Por isso
        //IllegalArgumentException (não precisa de try/catch)
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Personagem precisa de um nome");
        }
        if (vidaMaxima <= 0) {
            throw new IllegalArgumentException("Vida máxima deve ser maior que zero: " + vidaMaxima);
        }
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

    //Só leitura: a visão (TelaConsole) e os testes precisam ver a vida.
    //Não tem set: a vida só muda por tomarDano() e curar()
    public int getVidaAtual() {
        return vidaAtual;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    //Comportamentos: Ações que o personagem sabe executar

    @Override
    public void tomarDano(int quantidade) {
        if (quantidade <= 0) {
            return;
        }
        this.vidaAtual = Math.max(0, this.vidaAtual - quantidade); //Garante que vida n fique negativa -Raioni
    }

    //Substitui o antigo setVidaAtual: a vida só sobe por aqui, e o
    //próprio personagem garante as regras (não passa da máxima, não
    //revive morto). Quem cura (ex: ModuloCura) só diz quanto.
    public void curar(int quantidade) {
        if (quantidade <= 0 || estaMorto()) {
            return;
        }
        this.vidaAtual = Math.min(vidaMaxima, this.vidaAtual + quantidade);
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

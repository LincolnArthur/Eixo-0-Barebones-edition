package com.jogo.personagens;


//Classe abstrata que cria as habilidades utilizaveis pelo player -lincoln
public abstract class Habilidade {
    private boolean aprendida;
    private double tempoEspera;
    private double tempoRestante;
    private double duracao; // duracao da habilidade sendo usada até acabar -Raioni
    private int custoEnergia;


    //Construtor do escopo basico de uma habilidade -Raioni
    public Habilidade(boolean aprendida, double tempoEspera, int custoEnergia, double duracao) {
        this(aprendida, tempoEspera, 0.0, custoEnergia, duracao);
    }

    public Habilidade (boolean aprendida, double tempoEspera, double tempoRestante, int custoEnergia, double duracao){
        this.aprendida = aprendida;
        this.tempoEspera= tempoEspera;
        this.custoEnergia = custoEnergia;
        this.duracao = duracao;
        this.tempoRestante = 0.0;
    }

    public void atualizar(double dt) {
        tempoRestante = Math.max(0.0, tempoRestante - dt);
    }

    public double getTempoEspera(){
        return this.tempoEspera;
    }

    public double getDuracao(){
        return this.duracao;
    }

    public int getCustoEnergia(){
        return this.custoEnergia;
    }

    public boolean isAprendida(){ //usa 'is' para retorno boleano no lugar do get -Raioni
        return this.aprendida;
    }
    
    public void setTempoEspera( double tempoEspera){ //usa 'set' pra definir ao invés de retornar -lincoln
        this.tempoEspera=tempoEspera;
    }
    public void setAprendida(boolean aprendida){
        this.aprendida= aprendida;
    }

    public boolean estaDisponivel() {
        return tempoRestante <= 0.0;

    }

    public void setCustoEnergia(int custoEnergia){
        this.custoEnergia=custoEnergia;
    }
    public void setDuracao(double duracao){
        this.duracao=duracao;
    }

    public boolean podeUsar(Jogador player) {
        return isAprendida() && estaDisponivel() && player.energiaSuficiente(custoEnergia);
    }

    public final boolean usar(Jogador player) {
        if (!podeUsar(player)) {
            return false;
        }
        player.consumirEnergia(custoEnergia);
        executar(player);
        tempoRestante = tempoEspera;
        return true;
    }

    public abstract void executar(Jogador usuario);
    
}
package com.jogo.personagens;

public abstract class Habilidade {
    private boolean aprendida;
    private double tempoEspera;
    private double duracao; // duracao da habilidade sendo usada até acabar
    private int custoEnergia;


    //Construtor do escopo basico de uma habilidade
    public Habilidade (boolean aprendida, double tempoEspera, int custoEnergia, double duracao){
        this.aprendida = aprendida;
        this.tempoEspera=tempoEspera;
        this.custoEnergia = custoEnergia;
        this.duracao = duracao;
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

    public boolean isAprendida(){ //usa 'is' para retorno boleano no lugar do get
        return this.aprendida;
    }
    
    public void setTempoEspera( double tempoEspera){
        this.tempoEspera=tempoEspera;
    }
    public void setApredida(boolean aprendida){
        this.aprendida= aprendida;
    }

    public void setCustoEnergia(int custoEnergia){
        this.custoEnergia=custoEnergia;
    }
    public void setDuracao(double duracao){
        this.duracao=duracao;
    }


    public boolean podeUsar(Jogador player){
        if(isAprendida() && player.energiaSuficiente(custoEnergia)){
            return true;
        }else{
            return false;
        }
    }
    public abstract void executar(Jogador usuario);
    
}
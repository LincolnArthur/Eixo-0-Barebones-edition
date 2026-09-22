package com.jogo.personagens;

public class Jogador extends Personagem implements Atacante {
    //Implementa o metodo de ataque por meio
    //da classe abstrata Arma
    private Arma arma;
    private int energiaAtual;
    private int energiaMaxima;
    
    
    public Jogador(String nome, int vidaMaxima, Arma arma, int energiaMaxima) {
        super(nome, vidaMaxima);

        this.energiaAtual= energiaMaxima; //Criei 'energia' para funcionar como a mana
        this.energiaMaxima = energiaMaxima; 
        this.arma = arma;
    }


    public int getEnergiaAtual(){ //manda a energia atual do meu personagem
        return this.energiaAtual;
    }
    public boolean energiaSuficiente(int custoEnergia){ // retorna se tenho energia suficiente pra aquilo
        if(this.energiaAtual >= custoEnergia){
            return true;
        }else{
            return false;
        }
    }
    public void consumirEnergia(int custoEnergia){ //Calcula a energia restante apos usar uma habilidade
        if(energiaAtual <= 0){
            return;
        }
        this.energiaAtual= Math.max(0,energiaAtual - custoEnergia);
    }

    @Override 
    public int atacar() {
       return arma.causarDano();
    }
}

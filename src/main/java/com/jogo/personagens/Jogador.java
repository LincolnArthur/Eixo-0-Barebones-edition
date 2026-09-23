package com.jogo.personagens;

public class Jogador extends Personagem implements Atacante {
    //Implementa o metodo de ataque por meio
    //da classe abstrata Arma -lincoln
    private Arma arma;
    private int energiaAtual;
    private int energiaMaxima;
    private Nivel nivel;
    //private List<Habilidade> habilidades;
    
    private final Inventario inventario; //cada jogador tem seu inventario

    public Jogador(String nome, int vidaMaxima, Arma arma, int energiaMaxima) {
        super(nome, vidaMaxima);

        this.energiaAtual= energiaMaxima; //Criei 'energia' para funcionar como a mana -Raioni
        this.energiaMaxima = energiaMaxima; 
        this.arma = arma;

        this.inventario= new Inventario(); //Vou criar minha classe Inventario que guarda os itens - raioni
        this.inventario.adicionar(arma); //COloquei minha arma inicial no inventario - raioni
    }


    public int getEnergiaAtual(){ //manda a energia atual do meu personagem -Raioni
        return this.energiaAtual;
    }
    public boolean energiaSuficiente(int custoEnergia){ // retorna se tenho energia suficiente pra aquilo -Raioni
        if(this.energiaAtual >= custoEnergia){
            return true;
        }else{
            return false;
        }
    }
    public void consumirEnergia(int custoEnergia){ //Calcula a energia restante apos usar uma habilidade -Raioni
        if(energiaAtual <= 0){
            return;
        }
        this.energiaAtual= Math.max(0,energiaAtual - custoEnergia);
    }

    public void ganharExperiencia(int quantidade) {
        boolean subiuNivel = nivel.ganharExperiencia(quantidade);
        if (subiuNivel) {
            //Logica de habilidade -lincoln
        }
    }

    public int getEnergiaMaxima() {
        return this.energiaMaxima;
    }

    //Causa dano por meio da arma, não de um danoBase definido
    @Override 
    public int atacar() {
       return arma.causarDano();
    }
}

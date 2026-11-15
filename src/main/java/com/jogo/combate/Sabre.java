package com.jogo.combate;

//A cada 3 golpes, o sabre causa 5 de dano adicional.
//Correção: o construtor antigo era (nome, descricao, tipo, dano,
//cooldown) e mandava pra Arma os valores fora de ordem: o nome virava
//id, a descrição virava nome, e o cooldown virava chance de crítico.
//Agora segue o mesmo padrão das outras armas.
public class Sabre extends Arma {
    private int dano;
    private int golpe = 0;

    public Sabre(String id, String nome, String descricao, int dano, double chanceCritico, double tempoEspera) {
        super(id, nome, descricao, chanceCritico, tempoEspera);
        this.dano = dano;
    }

    @Override
    public int calcularDanoBase() {
        golpe++;
        if(golpe%3 == 0){ // a cada 3 golpes, o sabre causa 5 de dano adicional
            return dano +5;
        }
        return dano;
    }
}

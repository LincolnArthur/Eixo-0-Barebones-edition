package com.jogo.personagens;

//Molde pra classe projetil
//Teoricamente, seria implementado com valores posicionais x e y
//Como não tem interface gráfica, implementa só o dano
//Pra ficar separado de outros tipos. -lincoln

import java.util.Random;

public class Projetil {
    private int dano;
    private double chanceErrar;
    private Random gerador;

    public Projetil(int dano, double chanceErrar) {
        this.dano = dano;
        this.chanceErrar = chanceErrar;
        this.gerador = new Random();
  
    }

    public int getDano() {
        if (gerador.nextDouble() < chanceErrar) {
            return 0; //ERROU
        }
        return dano;
    }
}

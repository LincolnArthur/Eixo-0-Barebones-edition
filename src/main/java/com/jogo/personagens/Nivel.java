package com.jogo.personagens;

public class Nivel {
    private int nivelAtual;
    private int experienciaAtual;
    private int experienciaParaProximoNivel;

    public Nivel() {
        this.nivelAtual = 1;
        this.experienciaAtual = 0;
        this.experienciaParaProximoNivel = 100;
    }

    //Devolve true se subiu de nivel
    //Quem chama decide oq fazer com essa info -lincoln
    public boolean ganharExperiencia(int quantidade) {
        experienciaAtual += quantidade;
        if (experienciaAtual >= experienciaParaProximoNivel) {
            subirNivel();
            return true;
        }
        return false;
    }

    private void subirNivel() {
        nivelAtual++;
        experienciaAtual -= experienciaParaProximoNivel;
        experienciaParaProximoNivel = (int)(experienciaParaProximoNivel * 1.5);
    }

    public int getNivelAtual() {
        return nivelAtual;
    }
}

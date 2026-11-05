package com.jogo.combate;
import java.util.List;
import com.jogo.personagens.Personagem;

//Molde pra classe projetil
//Teoricamente, seria implementado com valores posicionais x e y
//Como não tem interface gráfica, a "linha de tiro" é a ordem da lista de alvos
//Quem causa o dano é o projétil, usando Ataque
public class Projetil {
    private int dano;

    public Projetil(int dano) {
        this.dano = dano;
    }

    public int getDano() {
        return dano;
    }

    // Projétil comum: para no primeiro alvo vivo
    public void atingir(List<Personagem> alvos) {
        for (Personagem alvo : alvos) {
            if (!alvo.estaMorto()) {
                new Ataque(dano).aplicarEm(alvo);
                return;
            }
        }
    }
}
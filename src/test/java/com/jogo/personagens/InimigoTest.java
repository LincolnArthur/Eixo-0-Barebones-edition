package com.jogo.personagens;

import com.jogo.combate.ArmaDeFogo;
import com.jogo.combate.Espada;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InimigoTest {

    @Test
    @DisplayName("Cada tipo de inimigo concede o próprio XP (polimorfismo)")
    void experienciaPolimorfica() {
        List<Inimigo> inimigos = List.of(
                new Melee("M", 10, 1, 0.0),
                new Sniper("S", 10, 1, 0.0),
                new Voador("V", 10, 1, 0.0));
        int[] esperado = {20, 35, 25};
        for (int i = 0; i < inimigos.size(); i++) {
            assertEquals(esperado[i], inimigos.get(i).getExperienciaConcedida());
        }
    }

    @Test
    @DisplayName("Cada inimigo nasce com a arma do seu tipo")
    void armas() {
        assertInstanceOf(Espada.class, new Melee("M", 10, 1, 0.0).getArma());
        assertInstanceOf(ArmaDeFogo.class, new Sniper("S", 10, 1, 0.0).getArma());
        assertInstanceOf(Espada.class, new Voador("V", 10, 1, 0.0).getArma());
    }

    @Test
    @DisplayName("iniciarAtaque aplica o dano da arma do inimigo")
    void atacaComArma() {
        Personagem vitima = new Personagem("Vítima", 100);
        new Sniper("S", 10, 12, 0.0).iniciarAtaque(vitima);
        assertEquals(88, vitima.getVidaAtual());
    }

    @Test
    @DisplayName("Não ataca vítima morta")
    void vitimaMorta() {
        Personagem vitima = new Personagem("Vítima", 10);
        vitima.tomarDano(10);
        new Melee("M", 10, 5, 0.0).iniciarAtaque(vitima);
        assertEquals(0, vitima.getVidaAtual());
    }
}

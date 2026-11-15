package com.jogo.habilidades;

import com.jogo.combate.Espada;
import com.jogo.personagens.Jogador;
import com.jogo.personagens.Melee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HabilidadeTest {

    private Jogador jogador;

    @BeforeEach
    void criar() {
        jogador = new Jogador("J", 100, new Espada("e", "E", "", 1, 0.0), 50);
    }

    @Test
    @DisplayName("Nasce disponível; usar gasta energia e liga o cooldown")
    void usar() {
        Apara apara = new Apara(true, 1.5, 5, 0.3);
        assertTrue(apara.estaDisponivel());
        assertTrue(apara.usar(jogador, null));
        assertEquals(45, jogador.getEnergiaAtual());
        assertFalse(apara.estaDisponivel());
    }

    @Test
    @DisplayName("Falha: em cooldown não usa nem gasta energia")
    void emCooldown() {
        Dash dash = new Dash(true, 1.0, 10, 0.2);
        dash.usar(jogador, null);
        assertFalse(dash.usar(jogador, null));
        assertEquals(40, jogador.getEnergiaAtual());
    }

    @Test
    @DisplayName("atualizar desconta o tempo até liberar de novo")
    void atualizar() {
        PuloDuplo pulo = new PuloDuplo(true, 1.0, 0, 0.0);
        pulo.usar(jogador, null);
        pulo.atualizar(0.5);
        assertFalse(pulo.estaDisponivel());
        pulo.atualizar(0.6);
        assertTrue(pulo.estaDisponivel());
        assertTrue(pulo.usar(jogador, null));
    }

    @Test
    @DisplayName("Falha: não aprendida ou sem energia")
    void naoPodeUsar() {
        Apara naoAprendida = new Apara(false, 1.0, 5, 0.3);
        assertFalse(naoAprendida.usar(jogador, null));

        Apara cara = new Apara(true, 1.0, 999, 0.3);
        assertFalse(cara.usar(jogador, null));
        assertEquals(50, jogador.getEnergiaAtual());
    }

    @Test
    @DisplayName("GolpeDeEnergia (BolaDeFogo) causa dano no alvo")
    void golpeDeEnergia() {
        GolpeDeEnergia golpe = new BolaDeFogo(true, 2.0, 15, 0.0, 20);
        Melee alvo = new Melee("Alvo", 30, 1, 0.0);
        assertTrue(golpe.usar(jogador, alvo));
        assertEquals(10, alvo.getVidaAtual());
        assertEquals(35, jogador.getEnergiaAtual());
    }

    @Test
    @DisplayName("GolpeDeEnergia sem alvo não quebra (gasta energia, sem dano)")
    void golpeSemAlvo() {
        GolpeDeEnergia golpe = new BolaDeFogo(true, 2.0, 15, 0.0, 20);
        assertDoesNotThrow(() -> golpe.usar(jogador, null));
    }
}

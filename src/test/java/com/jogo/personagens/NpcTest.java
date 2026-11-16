package com.jogo.personagens;

import com.jogo.combate.Espada;
import com.jogo.controle.Dialogo;
import com.jogo.visao.TelaConsole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NpcTest {

    private Jogador jogador;

    @BeforeEach
    void criar() {
        jogador = new Jogador("J", 100, new Espada("e", "E", "", 1, 0.0), 50);
    }

    @Test
    @DisplayName("Falas giram em ciclo")
    void falas() {
        NpcInformante npc = new NpcInformante("Zé", List.of("a", "b"));
        assertEquals("a", npc.getFalaAtual());
        assertEquals("b", npc.avancarFala());
        assertEquals("a", npc.avancarFala());
    }

    @Test
    @DisplayName("Falha: NPC sem falas ou com fala vazia")
    void semFalas() {
        assertThrows(IllegalArgumentException.class, () -> new NpcInformante("Zé", List.of()));
        assertThrows(IllegalArgumentException.class, () -> new NpcInformante("Zé", List.of(" ")));
    }

    @Test
    @DisplayName("NPC não toma dano")
    void invulneravel() {
        NpcInformante npc = new NpcInformante("Zé", List.of("oi"));
        npc.tomarDano(999);
        assertFalse(npc.estaMorto());
    }

    @Test
    @DisplayName("Curandeiro só gasta cura com o jogador ferido")
    void curandeiro() {
        NpcCurandeiro vega = new NpcCurandeiro("Vega", List.of("oi"), 1);
        vega.interagir(jogador);
        assertEquals(1, vega.getCurasRestantes()); // vida cheia: não gasta
        jogador.tomarDano(60);
        vega.interagir(jogador);
        assertEquals(100, jogador.getVidaAtual());
        assertEquals(0, vega.getCurasRestantes());
        jogador.tomarDano(30);
        vega.interagir(jogador);
        assertEquals(70, jogador.getVidaAtual()); // sem curas
    }

    @Test
    @DisplayName("Dialogo mostra a fala e o efeito na tela")
    void dialogo() {
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        Dialogo dialogo = new Dialogo(jogador, new TelaConsole(new PrintStream(saida)));
        jogador.tomarDano(10);
        dialogo.conversar(new NpcCurandeiro("Vega", List.of("Deixa eu ver isso."), 1));
        String texto = saida.toString();
        assertTrue(texto.contains("Deixa eu ver isso."));
        assertTrue(texto.contains("restaurada"));
    }
}

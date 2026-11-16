package com.jogo.habilidades;

import com.jogo.combate.Espada;
import com.jogo.controle.Batalha;
import com.jogo.personagens.Jogador;
import com.jogo.personagens.Melee;
import com.jogo.visao.TelaConsole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SandevistanTest {

    // Random que sempre devolve o mesmo valor: decide se sai Cyberpsicose
    private static Random fixo(double valor) {
        return new Random() {
            @Override
            public double nextDouble() {
                return valor;
            }
        };
    }

    private Jogador jogador;

    @BeforeEach
    void criar() {
        jogador = new Jogador("J", 100, new Espada("e", "E", "", 1, 0.0), 50);
    }

    @Test
    @DisplayName("Liga e desliga com a mesma ação; ao desligar entra em cooldown")
    void toggle() {
        SandevistanMichyle s = new SandevistanMichyle(true, fixo(0.9));
        assertTrue(s.usar(jogador, null));
        assertTrue(s.estaAtivo());
        assertTrue(s.usar(jogador, null));
        assertFalse(s.estaAtivo());
        assertEquals(SandevistanMichyle.COOLDOWN_POS_DESATIVAR, s.getCooldownRestante());
        assertFalse(s.usar(jogador, null)); // em cooldown
    }

    @Test
    @DisplayName("Gasta 20 de energia por segundo e desliga sozinha sem energia")
    void consomeEnergia() {
        SandevistanMichyle s = new SandevistanMichyle(true, fixo(0.9));
        s.usar(jogador, null);
        s.atualizarSandevistan(jogador, 1.0);
        assertEquals(30, jogador.getEnergiaAtual());
        s.atualizarSandevistan(jogador, 1.5);
        assertEquals(0, jogador.getEnergiaAtual());
        assertFalse(s.estaAtivo());
    }

    @Test
    @DisplayName("Não aprendida ou sem energia: não liga")
    void naoLiga() {
        assertFalse(new SandevistanMichyle(false).usar(jogador, null));
        jogador.consumirEnergia(50);
        assertFalse(new SandevistanMichyle(true).usar(jogador, null));
    }

    @Test
    @DisplayName("Cyberpsicose tira 8 por segundo e para em 50% da vida")
    void cyberpsicose() {
        SandevistanMichyle s = new SandevistanMichyle(true, fixo(0.1)); // 0.1 < 25%: sai Cyberpsicose
        s.usar(jogador, null);
        assertTrue(s.getCyberpsicose().estaAtivo());
        s.getCyberpsicose().atualizar(jogador, 1.0);
        assertEquals(92, jogador.getVidaAtual());
        for (int i = 0; i < 20; i++) {
            s.getCyberpsicose().atualizar(jogador, 1.0);
        }
        assertEquals(50, jogador.getVidaAtual()); // nunca abaixo da metade
    }

    @Test
    @DisplayName("Cyberpsicose continua depois de desligar e acaba quando a energia volta a 50%")
    void cyberpsicoseAcaba() {
        SandevistanMichyle s = new SandevistanMichyle(true, fixo(0.1));
        s.usar(jogador, null);
        jogador.consumirEnergia(40); // 10/50
        s.usar(jogador, null);       // desliga
        s.atualizarSandevistan(jogador, 1.0);
        assertTrue(s.getCyberpsicose().estaAtivo()); // energia abaixo de 50%: continua
        jogador.recuperarEnergia(20); // 30/50
        s.atualizarSandevistan(jogador, 1.0);
        assertFalse(s.getCyberpsicose().estaAtivo());
    }

    @Test
    @DisplayName("Batalha: com a Sandevistan ligada os inimigos só agem 1 turno a cada 4")
    void cameraLentaNaBatalha() {
        jogador = new Jogador("J", 100, new Espada("e", "E", "", 1, 0.0), 100);
        Batalha batalha = new Batalha(jogador, List.of(new Melee("M", 50, 10, 0.0)),
                new TelaConsole(new PrintStream(new ByteArrayOutputStream())));
        assertTrue(batalha.alternarSandevistan(new SandevistanMichyle(true, fixo(0.9))));
        for (int i = 0; i < 3; i++) {
            batalha.turnoDosInimigos();
        }
        assertEquals(100, jogador.getVidaAtual()); // 3 turnos sem apanhar
        batalha.turnoDosInimigos();
        assertEquals(90, jogador.getVidaAtual());  // no 4º o inimigo age
    }
}

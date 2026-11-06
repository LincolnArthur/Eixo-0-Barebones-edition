package com.jogo.controle;

import com.jogo.combate.Espada;
import com.jogo.combate.Marreta;
import com.jogo.combate.RifleSniper;
import com.jogo.habilidades.BolaDeFogo;
import com.jogo.inventario.Categoria;
import com.jogo.inventario.ModuloCura;
import com.jogo.personagens.Inimigo;
import com.jogo.personagens.Jogador;
import com.jogo.personagens.Melee;
import com.jogo.personagens.Sniper;
import com.jogo.personagens.Voador;
import com.jogo.visao.TelaConsole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//Testes de INTEGRAÇÃO: controle + modelo + visão juntos, como no jogo
class BatalhaIntegracaoTest {

    private ByteArrayOutputStream capturado;
    private Jogador jogador;
    private Melee bandido;
    private Sniper atirador;
    private Voador morcego;
    private Batalha batalha;

    @BeforeEach
    void montar() {
        capturado = new ByteArrayOutputStream();
        TelaConsole tela = new TelaConsole(new PrintStream(capturado, true, StandardCharsets.UTF_8));
        jogador = new Jogador("Herói", 100, new Espada("espada", "Espada", "", 10, 0.0), 50);
        bandido = new Melee("Bandido", 10, 8, 0.0);
        atirador = new Sniper("Atirador", 30, 12, 0.0);
        morcego = new Voador("Morcego", 20, 6, 0.0);
        batalha = new Batalha(jogador, List.of(bandido, atirador, morcego), tela);
    }

    private String saida() {
        return capturado.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("Turno dos inimigos: cada um ataca com a própria arma")
    void turnoDosInimigos() {
        batalha.turnoDosInimigos();
        assertEquals(100 - 8 - 12 - 6, jogador.getVidaAtual());
    }

    @Test
    @DisplayName("Inimigo derrotado não ataca mais")
    void derrotadoNaoAtaca() {
        batalha.jogadorAtaca(bandido); //10 de dano mata o bandido
        batalha.turnoDosInimigos();
        assertEquals(100 - 12 - 6, jogador.getVidaAtual());
    }

    @Test
    @DisplayName("Derrotar inimigos dá o XP de cada tipo e sobe de nível")
    void xpENivel() {
        batalha.jogadorAtaca(bandido);              //+20
        batalha.dispararPerfurante(new RifleSniper("r", "Rifle", "", 30, 0.0, 2)); //mata Atirador (+35) e Morcego (+25)
        assertTrue(batalha.jogadorVenceu());
        assertEquals(80, jogador.getNivel().getExperienciaAtual());

        Jogador outro = new Jogador("X", 100, new Espada("e", "E", "", 1000, 0.0), 50);
        Batalha grande = new Batalha(outro, List.of(new Sniper("1", 1, 1, 0.0), new Sniper("2", 1, 1, 0.0),
                new Sniper("3", 1, 1, 0.0)), new TelaConsole(new PrintStream(new ByteArrayOutputStream())));
        for (Inimigo i : grande.getInimigos()) {
            grande.jogadorAtaca(i);
        }
        assertEquals(2, outro.getNivel().getNivelAtual()); //105 XP
    }

    @Test
    @DisplayName("Marreta equipada pelo controle tem bônus contra o Sniper")
    void marretaContraSniper() {
        Marreta marreta = new Marreta("marreta", "Marreta", "", 14, 0.0);
        jogador.coletarItem(marreta);
        assertTrue(batalha.equiparArma(marreta.getId()));
        batalha.jogadorAtaca(atirador);
        assertEquals(30 - 24, atirador.getVidaAtual());
    }

    @Test
    @DisplayName("Habilidade pelo controle: dano, cooldown e XP ao matar")
    void habilidade() {
        BolaDeFogo bola = new BolaDeFogo(true, 2.0, 15, 0.0, 20);
        assertTrue(batalha.usarHabilidade(bola, morcego));
        assertTrue(morcego.estaMorto());
        assertEquals(25, jogador.getNivel().getExperienciaAtual());
        assertFalse(batalha.usarHabilidade(bola, atirador)); //cooldown
        assertTrue(saida().contains("Não deu pra usar BolaDeFogo"));
    }

    @Test
    @DisplayName("Exceções tratadas pelo controle viram mensagem, sem derrubar o jogo")
    void excecoesTratadas() {
        assertFalse(batalha.usarItem("modulo_cura"));
        assertFalse(batalha.equiparArma("nao-existe"));
        jogador.coletarItem(new ModuloCura("modulo_cura", "Cura", "", Categoria.CONSUMIVEL, 30));
        assertFalse(batalha.equiparArma("modulo_cura"));

        assertTrue(saida().contains("[!] Item não encontrado no inventário: modulo_cura"));
        assertTrue(saida().contains("[!] Item não encontrado no inventário: nao-existe"));
        assertTrue(saida().contains("[!] O item 'modulo_cura' não pode ser usado para equipar como arma"));
    }

    @Test
    @DisplayName("Usar Módulo de Cura pelo controle cura e consome")
    void usarCura() {
        jogador.coletarItem(new ModuloCura("modulo_cura", "Cura", "", Categoria.CONSUMIVEL, 30));
        batalha.turnoDosInimigos(); //74 de vida
        assertTrue(batalha.usarItem("modulo_cura"));
        assertEquals(100, jogador.getVidaAtual());
        assertFalse(batalha.usarItem("modulo_cura"));
    }

    @Test
    @DisplayName("Jogador perde quando a vida chega a 0")
    void derrota() {
        Batalha perdida = new Batalha(jogador, List.of(new Melee("Chefe", 999, 200, 0.0)),
                new TelaConsole(new PrintStream(new ByteArrayOutputStream())));
        perdida.turnoDosInimigos();
        assertTrue(perdida.jogadorPerdeu());
        assertFalse(perdida.jogadorVenceu());
    }
}

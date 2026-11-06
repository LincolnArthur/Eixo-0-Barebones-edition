package com.jogo.personagens;

import com.jogo.combate.Espada;
import com.jogo.combate.Marreta;
import com.jogo.excecoes.ItemIncompativelException;
import com.jogo.excecoes.ItemNaoEncontradoException;
import com.jogo.inventario.Categoria;
import com.jogo.inventario.ModuloCura;
import com.jogo.inventario.ModuloEnergia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JogadorTest {

    private Espada espada;
    private Jogador jogador;

    @BeforeEach
    void criar() {
        espada = new Espada("espada", "Espada", "Inicial", 10, 0.0);
        jogador = new Jogador("Aventureiro", 100, espada, 50);
    }

    @Test
    @DisplayName("Começa com energia cheia, a arma equipada e no inventário")
    void estadoInicial() {
        assertEquals(50, jogador.getEnergiaAtual());
        assertSame(espada, jogador.getArma());
        assertTrue(jogador.getInventario().temItem(espada.getId()));
        assertEquals(1, jogador.getNivel().getNivelAtual());
    }

    @Test
    @DisplayName("Consumir e recuperar energia respeitam 0 e o máximo")
    void energia() {
        jogador.consumirEnergia(30);
        assertEquals(20, jogador.getEnergiaAtual());
        assertTrue(jogador.energiaSuficiente(20));
        assertFalse(jogador.energiaSuficiente(21));

        jogador.consumirEnergia(999);
        assertEquals(0, jogador.getEnergiaAtual());

        jogador.recuperarEnergia(15);
        assertEquals(15, jogador.getEnergiaAtual());
        jogador.recuperarEnergia(999);
        assertEquals(50, jogador.getEnergiaAtual());
    }

    @Test
    @DisplayName("Falha: recuperar energia negativa é ignorado")
    void energiaNegativa() {
        jogador.consumirEnergia(10);
        jogador.recuperarEnergia(-5);
        assertEquals(40, jogador.getEnergiaAtual());
    }

    @Test
    @DisplayName("iniciarAtaque aplica o dano da arma equipada")
    void ataca() {
        Melee alvo = new Melee("Alvo", 50, 1, 0.0);
        jogador.iniciarAtaque(alvo);
        assertEquals(40, alvo.getVidaAtual());
    }

    @Test
    @DisplayName("Equipa outra arma que está no inventário")
    void equipaArma() throws Exception {
        Marreta marreta = new Marreta("marreta", "Marreta", "Pesada", 14, 0.0);
        jogador.coletarItem(marreta);
        jogador.equiparArma(marreta.getId());
        assertSame(marreta, jogador.getArma());
    }

    @Test
    @DisplayName("Exceção: equipar id que não existe")
    void equiparInexistente() {
        ItemNaoEncontradoException e = assertThrows(ItemNaoEncontradoException.class,
                () -> jogador.equiparArma("nao-existe"));
        assertEquals("nao-existe", e.getIdItem());
        assertSame(espada, jogador.getArma()); //continua com a arma antiga
    }

    @Test
    @DisplayName("Exceção: equipar um item que não é arma")
    void equiparNaoArma() {
        jogador.coletarItem(new ModuloCura("modulo_cura", "Cura", "", Categoria.CONSUMIVEL, 30));
        assertThrows(ItemIncompativelException.class, () -> jogador.equiparArma("modulo_cura"));
    }

    @Test
    @DisplayName("usarItem aplica o efeito e tira uma unidade do inventário")
    void usarItem() throws Exception {
        jogador.coletarItem(new ModuloCura("modulo_cura", "Cura", "", Categoria.CONSUMIVEL, 30));
        jogador.coletarItem(new ModuloCura("modulo_cura", "Cura", "", Categoria.CONSUMIVEL, 30));
        jogador.tomarDano(50);

        jogador.usarItem("modulo_cura");
        assertEquals(80, jogador.getVidaAtual());
        assertEquals(1, jogador.getInventario().quantidadeDe("modulo_cura"));
    }

    @Test
    @DisplayName("usarItem com Módulo de Energia recupera energia")
    void usarModuloEnergia() throws Exception {
        jogador.coletarItem(new ModuloEnergia("modulo_energia", "Energia", "", Categoria.CONSUMIVEL, 20));
        jogador.consumirEnergia(40);
        jogador.usarItem("modulo_energia");
        assertEquals(30, jogador.getEnergiaAtual());
        assertFalse(jogador.getInventario().temItem("modulo_energia"));
    }

    @Test
    @DisplayName("Exceção: usar item que não tem")
    void usarItemInexistente() {
        assertThrows(ItemNaoEncontradoException.class, () -> jogador.usarItem("modulo_cura"));
    }

    @Test
    @DisplayName("Exceção: usar uma arma como consumível")
    void usarArmaComoConsumivel() {
        assertThrows(ItemIncompativelException.class, () -> jogador.usarItem(espada.getId()));
        assertTrue(jogador.getInventario().temItem(espada.getId())); //não some
    }

    @Test
    @DisplayName("ganharExperiencia repassa pro Nivel")
    void ganharExperiencia() {
        jogador.ganharExperiencia(100);
        assertEquals(2, jogador.getNivel().getNivelAtual());
    }
}

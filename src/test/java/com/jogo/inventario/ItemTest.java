package com.jogo.inventario;

import com.jogo.combate.Espada;
import com.jogo.personagens.Jogador;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    @DisplayName("Item empilhável usa o id base; não empilhável ganha número")
    void ids() {
        ModuloCura cura = new ModuloCura("modulo_cura", "Cura", "Recupera", Categoria.CONSUMIVEL, 30);
        assertEquals("modulo_cura", cura.getId());
        assertEquals("Cura", cura.getNome());
        assertEquals("Recupera", cura.getDescricao());

        ModuloCura upgrade = new ModuloCura("upgrade", "U", "", Categoria.UPGRADE, 1);
        assertTrue(upgrade.getId().startsWith("upgrade-"));
    }

    @Test
    @DisplayName("Categorias: só consumível e chave empilham")
    void categorias() {
        assertTrue(Categoria.CONSUMIVEL.isEmpilhavel());
        assertTrue(Categoria.CHAVE.isEmpilhavel());
        assertFalse(Categoria.ARMA.isEmpilhavel());
        assertFalse(Categoria.UPGRADE.isEmpilhavel());
    }

    @Test
    @DisplayName("Consumíveis aplicam o efeito no jogador (polimorfismo pela interface)")
    void efeitos() {
        Jogador jogador = new Jogador("J", 100, new Espada("e", "E", "", 1, 0.0), 50);
        jogador.tomarDano(50);
        jogador.consumirEnergia(40);

        Consumivel cura = new ModuloCura("c", "Cura", "", Categoria.CONSUMIVEL, 30);
        Consumivel energia = new ModuloEnergia("en", "Energia", "", Categoria.CONSUMIVEL, 25);
        cura.efeito(jogador);
        energia.efeito(jogador);

        assertEquals(80, jogador.getVidaAtual());
        assertEquals(35, jogador.getEnergiaAtual());
    }
}

package com.jogo.inventario;

import com.jogo.combate.Arma;
import com.jogo.combate.Espada;
import com.jogo.combate.Marreta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventarioTest {

    private Inventario inventario;

    @BeforeEach
    void criar() {
        inventario = new Inventario();
    }

    private ModuloCura cura() {
        return new ModuloCura("modulo_cura", "Cura", "", Categoria.CONSUMIVEL, 30);
    }

    @Test
    @DisplayName("Começa vazio; adicionar null falha")
    void vazio() {
        assertTrue(inventario.inventarioVazio());
        assertFalse(inventario.adicionar(null));
        assertTrue(inventario.inventarioVazio());
    }

    @Test
    @DisplayName("Empilháveis com o mesmo id somam na mesma entrada")
    void empilha() {
        inventario.adicionar(cura());
        inventario.adicionar(cura());
        inventario.adicionar(cura());
        assertEquals(1, inventario.quantidadeItem());
        assertEquals(3, inventario.quantidadeDe("modulo_cura"));
    }

    @Test
    @DisplayName("Não empilháveis (armas) ocupam entradas separadas")
    void naoEmpilha() {
        inventario.adicionar(new Espada("espada", "E", "", 1, 0.0));
        inventario.adicionar(new Espada("espada", "E", "", 1, 0.0));
        assertEquals(2, inventario.quantidadeItem());
    }

    @Test
    @DisplayName("Falha: o mesmo objeto não empilhável duas vezes")
    void duplicata() {
        Espada espada = new Espada("espada", "E", "", 1, 0.0);
        assertTrue(inventario.adicionar(espada));
        assertFalse(inventario.adicionar(espada));
    }

    @Test
    @DisplayName("remover tira uma unidade; a última apaga a entrada")
    void remover() {
        inventario.adicionar(cura());
        inventario.adicionar(cura());
        assertTrue(inventario.remover("modulo_cura"));
        assertEquals(1, inventario.quantidadeDe("modulo_cura"));
        assertTrue(inventario.remover("modulo_cura"));
        assertFalse(inventario.temItem("modulo_cura"));
        assertEquals(0, inventario.quantidadeDe("modulo_cura"));
    }

    @Test
    @DisplayName("Falha: remover/buscar id inexistente ou nulo")
    void inexistente() {
        assertFalse(inventario.remover("x"));
        assertFalse(inventario.remover(null));
        assertNull(inventario.buscar("x"));
        assertNull(inventario.buscar(null));
    }

    @Test
    @DisplayName("listarDoTipo (genérico) filtra pelo tipo e mantém a ordem")
    void listarDoTipo() {
        Espada espada = new Espada("espada", "E", "", 1, 0.0);
        Marreta marreta = new Marreta("marreta", "M", "", 1, 0.0);
        inventario.adicionar(espada);
        inventario.adicionar(cura());
        inventario.adicionar(marreta);

        List<Arma> armas = inventario.listarDoTipo(Arma.class);
        assertEquals(List.of(espada, marreta), armas);
        assertEquals(1, inventario.listarDoTipo(Consumivel.class).size());
        assertTrue(inventario.listarDoTipo(ModuloEnergia.class).isEmpty());
    }

    @Test
    @DisplayName("getItens devolve uma cópia que não pode ser alterada")
    void copiaImutavel() {
        inventario.adicionar(cura());
        assertThrows(UnsupportedOperationException.class, () -> inventario.getItens().clear());
    }
}

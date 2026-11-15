package com.jogo.combate;

import com.jogo.inventario.Categoria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

//Regras que valem pra toda Arma (testadas pela Espada, a mais simples)
class ArmaTest {

    @Test
    @DisplayName("Sem crítico, causa exatamente o dano base")
    void danoBase() {
        Espada espada = new Espada("e", "Espada", "", 10, 0.0);
        for (int i = 0; i < 20; i++) {
            assertEquals(10, espada.causarDano());
        }
    }

    @Test
    @DisplayName("Com crítico garantido, causa o dobro")
    void critico() {
        Espada espada = new Espada("e", "Espada", "", 10, 1.0);
        assertEquals(20, espada.causarDano());
    }

    @Test
    @DisplayName("Falha: chance de crítico fora de 0 a 1")
    void chanceInvalida() {
        assertThrows(IllegalArgumentException.class, () -> new Espada("e", "E", "", 10, -0.1));
        assertThrows(IllegalArgumentException.class, () -> new Espada("e", "E", "", 10, 1.5));
    }

    @Test
    @DisplayName("Cada arma ganha um id único e é da categoria ARMA")
    void idUnicoECategoria() {
        Espada a = new Espada("espada", "E", "", 1, 0.0);
        Espada b = new Espada("espada", "E", "", 1, 0.0);
        assertNotEquals(a.getId(), b.getId());
        assertTrue(a.getId().startsWith("espada-"));
        assertEquals(Categoria.ARMA, a.getCategoria());
        assertFalse(a.getCategoria().isEmpilhavel());
    }

    @Test
    @DisplayName("Tempo de espera: 1s por padrão, mínimo 0.01s, e velocidade = 1/tempo")
    void tempoEspera() {
        assertEquals(1.0, new Espada("e", "E", "", 1, 0.0).getTempoEspera());
        Marreta marreta = new Marreta("m", "M", "", 1, 0.0, 0.0);
        assertEquals(0.01, marreta.getTempoEspera());
        AdagaDupla adaga = new AdagaDupla("a", "A", "", 1, 0.0);
        assertEquals(4.0, adaga.getVelocidadeAtaque(), 1e-9);
    }
}

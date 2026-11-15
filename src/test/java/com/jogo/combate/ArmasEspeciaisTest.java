package com.jogo.combate;

import com.jogo.personagens.Melee;
import com.jogo.personagens.Personagem;
import com.jogo.personagens.Sniper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//Cada subclasse de Arma sobrescreve o cálculo de dano do seu jeito
class ArmasEspeciaisTest {

    @Test
    @DisplayName("Marreta: bônus contra Sniper, dano normal contra os outros")
    void marreta() {
        Marreta marreta = new Marreta("m", "Marreta", "", 14, 0.0);
        assertEquals(24, marreta.causarDano(new Sniper("S", 10, 1, 0.0)));
        assertEquals(14, marreta.causarDano(new Melee("M", 10, 1, 0.0)));
        assertEquals(14, marreta.causarDano()); //sem alvo
        assertEquals(1.8, marreta.getTempoEspera());
    }

    @Test
    @DisplayName("AdagaDupla: o 5º golpe tem bônus e o combo recomeça")
    void adagaDupla() {
        AdagaDupla adaga = new AdagaDupla("a", "Adaga", "", 6, 0.0);
        for (int i = 0; i < 4; i++) {
            assertEquals(6, adaga.causarDano());
        }
        assertEquals(21, adaga.causarDano());
        assertEquals(6, adaga.causarDano());
    }

    @Test
    @DisplayName("AdagaDupla: reiniciarCombo zera a contagem")
    void adagaReinicia() {
        AdagaDupla adaga = new AdagaDupla("a", "Adaga", "", 6, 0.0);
        for (int i = 0; i < 4; i++) {
            adaga.causarDano();
        }
        adaga.reiniciarCombo();
        assertEquals(6, adaga.causarDano());
    }

    @Test
    @DisplayName("Sabre: +5 a cada 3 golpes, e os dados chegam na ordem certa")
    void sabre() {
        Sabre sabre = new Sabre("sabre", "Sabre", "Afiado", 9, 0.0, 0.8);
        assertEquals("Sabre", sabre.getNome());
        assertEquals("Afiado", sabre.getDescricao());
        assertEquals(0.8, sabre.getTempoEspera());
        assertEquals(9, sabre.causarDano());
        assertEquals(9, sabre.causarDano());
        assertEquals(14, sabre.causarDano());
    }

    @Test
    @DisplayName("ArmaDeFogo, Shotgun e PistolaDupla: 1, N e 2 projéteis")
    void armasDeFogo() {
        assertEquals(7, new ArmaDeFogo("f", "Fogo", "", 7, 0.0).causarDano());
        assertEquals(20, new Shotgun("s", "Shotgun", "", 4, 5, 0.0).causarDano());
        assertEquals(14, new PistolaDuplaDePlasma("p", "Pistola", "", 7, 0.0).causarDano());
    }

    @Test
    @DisplayName("RifleSniper é Perfurante e atinge até N alvos vivos")
    void rifle() {
        RifleSniper rifle = new RifleSniper("r", "Rifle", "", 15, 0.0, 2);
        Perfurante perfurante = rifle;
        assertEquals(2, perfurante.getAlvosAtravessados());

        Personagem morto = new Personagem("Morto", 10);
        morto.tomarDano(10);
        Personagem a = new Personagem("A", 30);
        Personagem b = new Personagem("B", 30);
        Personagem c = new Personagem("C", 30);
        rifle.disparar(List.of(morto, a, b, c));

        assertEquals(15, a.getVidaAtual());
        assertEquals(15, b.getVidaAtual());
        assertEquals(30, c.getVidaAtual()); //o tiro parou antes
    }
}

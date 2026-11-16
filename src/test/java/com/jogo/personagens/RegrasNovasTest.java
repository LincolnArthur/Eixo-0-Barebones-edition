package com.jogo.personagens;

import com.jogo.combate.Espada;
import com.jogo.habilidades.BuracoNegro;
import com.jogo.habilidades.RaioDeGelo;
import com.jogo.mundo.Mapa;
import com.jogo.mundo.NpcsDasSalas;
import com.jogo.mundo.Sala;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//Regras trazidas do jogo do FXGL: magias por nível, energia por acerto,
//dificuldade, Curandeira com custo e NPCs por sala
class RegrasNovasTest {

    private Jogador jogador;

    @BeforeEach
    void criar() {
        jogador = new Jogador("J", 100, new Espada("e", "E", "", 10, 0.0), 50);
    }

    @Test
    @DisplayName("Subir de nível: +20 de vida máxima, vida cheia e magias liberadas")
    void subirDeNivel() {
        assertFalse(jogador.getGelo().isAprendida());
        jogador.tomarDano(50);
        assertTrue(jogador.ganharExperiencia(100));
        assertEquals(2, jogador.getNivel().getNivelAtual());
        assertEquals(120, jogador.getVidaMaxima());
        assertEquals(120, jogador.getVidaAtual());
        assertTrue(jogador.getGelo().isAprendida());
        assertFalse(jogador.getBuracoNegro().isAprendida());
        jogador.ganharExperiencia(150);
        assertTrue(jogador.getBuracoNegro().isAprendida());
    }

    @Test
    @DisplayName("Energia: só por acerto até o nível 6; depois também recarrega por turno")
    void energia() {
        jogador.consumirEnergia(50);
        jogador.recarregarEnergiaPorTurno();
        assertEquals(0, jogador.getEnergiaAtual());
        jogador.registrarAcerto();
        assertEquals(Jogador.ENERGIA_POR_ACERTO, jogador.getEnergiaAtual());

        jogador.ganharExperiencia(1000); //100+150+200+250+300: nível 6
        assertEquals(6, jogador.getNivel().getNivelAtual());
        assertTrue(jogador.recargaAutomaticaLiberada());
        jogador.recarregarEnergiaPorTurno();
        assertEquals(Jogador.ENERGIA_POR_ACERTO + Jogador.RECARGA_POR_TURNO, jogador.getEnergiaAtual());
    }

    @Test
    @DisplayName("Dificuldade multiplica o dano recebido, e um golpe nunca vira 0")
    void dificuldade() {
        jogador.setDificuldade(Dificuldade.FACIL);
        jogador.tomarDano(10);
        assertEquals(95, jogador.getVidaAtual());
        jogador.tomarDano(1);
        assertEquals(94, jogador.getVidaAtual());
        jogador.setDificuldade(Dificuldade.DIFICIL);
        jogador.tomarDano(10);
        assertEquals(79, jogador.getVidaAtual());
        assertThrows(IllegalArgumentException.class, () -> jogador.setDificuldade(null));
    }

    @Test
    @DisplayName("Raio de Gelo: dano e congelamento; congelado perde turnos")
    void gelo() {
        RaioDeGelo gelo = new RaioDeGelo(true, 3.0, 20, 8, 3.0);
        Melee robo = new Melee("Robô", 50, 5, 0.0);
        assertTrue(gelo.usar(jogador, robo));
        assertEquals(42, robo.getVidaAtual());
        assertTrue(robo.estaCongelado());
        assertTrue(robo.passarTurnoCongelado());
        assertTrue(robo.passarTurnoCongelado());
        assertTrue(robo.passarTurnoCongelado());
        assertFalse(robo.passarTurnoCongelado()); //descongelou
        assertThrows(IllegalArgumentException.class, () -> new RaioDeGelo(true, 1, 1, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> robo.congelar(0));
    }

    @Test
    @DisplayName("Buraco Negro: custa a energia inteira e destrói os inimigos da sala")
    void buracoNegro() {
        BuracoNegro buraco = new BuracoNegro(true, 20);
        assertEquals(50, buraco.custoPara(jogador));
        List<Inimigo> sala = List.of(new Melee("A", 40, 1, 0.0), new Voador("B", 30, 1, 0.0));
        buraco.mirar(sala);
        assertTrue(buraco.usar(jogador, null));
        assertTrue(sala.get(0).estaMorto());
        assertTrue(sala.get(1).estaMorto());
        assertEquals(0, jogador.getEnergiaAtual());
        assertFalse(buraco.podeUsar(jogador)); //sem energia e em cooldown
    }

    @Test
    @DisplayName("Curandeira: a cura custa energia; sem energia, não cura")
    void curandeira() {
        NpcCurandeiro cura = new NpcCurandeiro("Cura", List.of("oi"), 3);
        jogador.tomarDano(40);
        assertTrue(cura.interagir(jogador).contains("restaurada"));
        assertEquals(100, jogador.getVidaAtual());
        assertEquals(30, jogador.getEnergiaAtual());
        assertEquals(2, cura.getCurasRestantes());

        jogador.consumirEnergia(25); //sobra 5
        jogador.tomarDano(40);
        assertTrue(cura.interagir(jogador).contains("energia"));
        assertEquals(60, jogador.getVidaAtual());
        assertEquals(2, cura.getCurasRestantes());
    }

    @Test
    @DisplayName("Informante só na primeira sala, Curandeira só na penúltima")
    void npcsDasSalas() {
        assertTrue(NpcsDasSalas.temInformante(0));
        assertFalse(NpcsDasSalas.temInformante(1));
        assertTrue(NpcsDasSalas.temCurandeira(3, 5));
        assertFalse(NpcsDasSalas.temCurandeira(4, 5));

        Mapa mapa = new Mapa("Teste");
        for (int i = 1; i <= 5; i++) {
            mapa.adicionarSala(new Sala("s" + i, "Sala " + i, ""));
        }
        NpcsDasSalas.colocarNpcs(mapa);
        assertInstanceOf(NpcInformante.class, mapa.buscarSala("s1").getNpcs().get(0));
        assertTrue(mapa.buscarSala("s2").getNpcs().isEmpty());
        assertInstanceOf(NpcCurandeiro.class, mapa.buscarSala("s4").getNpcs().get(0));
        assertTrue(mapa.buscarSala("s5").getNpcs().isEmpty());
    }
}

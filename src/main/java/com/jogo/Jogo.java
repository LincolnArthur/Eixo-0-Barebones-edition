package com.jogo;
//Package diz q uma classe pertence a tal grupo lógico
//Namespace: Evita colisão de nome entre classe de mesmo nome
//Organização: agrupa classes relacionadas -lincoln

import com.jogo.armadilhas.BlocoQueCai;
import com.jogo.armadilhas.DamageTarget;
import com.jogo.armadilhas.SpikesRetrateis;
import com.jogo.armadilhas.Trap;
import com.jogo.armadilhas.TrapFactory;
import com.jogo.armadilhas.TrapListener;
import com.jogo.armadilhas.TrapProceduralGenerator;
import com.jogo.armadilhas.TrapProceduralGenerator.PlatformBounds;
import com.jogo.armadilhas.TrapState;
import com.jogo.mundo.Sala;
import com.jogo.personagens.Personagem;

import java.util.List;

public class Jogo {
    public static void main(String[] args) {
        //Onde são criados os objetos -lincoln
        Personagem heroi = new Personagem("Herói", 100);
        Personagem goblin = new Personagem("goblin", 25);

        //Uso: chamando um comportamento já incluso na classe criada -lincoln
        goblin.tomarDano(15);
        heroi.tomarDano(100);

        //Pra enxergar o resultado de algumas comparações (Com concatenação de strings) -lincoln
        if (!goblin.estaSaudavel()) {
            System.out.println("\n" + goblin.getNome() + " está morto?\n" + goblin.estaMorto() + "\n");
        } else {
            System.out.println(goblin.getNome() + "está saudável.\n");
        }

        if (!heroi.estaSaudavel()) {
            System.out.println("\n" + heroi.getNome() + " está morto?\n" + heroi.estaMorto() + "\n");
        } else {
            System.out.println(heroi.getNome() + "está saudável.\n");
        }

        //teste do toString
        System.out.println("Estado dos personagens com o tostring:\n");
        System.out.println(goblin);
        System.out.println(heroi);

        // =========================================================================
        // DEMONSTRAÇÃO: SISTEMA MODULAR DE ARMADILHAS (POO / SOLID / DESIGN PATTERNS)
        // =========================================================================
        demonstrarSistemaDeArmadilhas();
    }

    private static void demonstrarSistemaDeArmadilhas() {
        System.out.println("\n=================================================================");
        System.out.println("   DEMONSTRAÇÃO: SISTEMA DE ARMADILHAS (SOLID & DESIGN PATTERNS)");
        System.out.println("=================================================================");

        // 1. Geração Procedural Determinística (DEFAULT_SEED = 12345L) para uma Sala
        Sala salaLaboratorio = new Sala(
                "cryolab_01",
                "Corredor Crio-Laboratório",
                "Corredor metálico com armadilhas procedurais ancoradas no teto e no piso."
        );

        List<PlatformBounds> plataformasCenário = List.of(
                new PlatformBounds(0, 0, 3000, 40),      // Teto físico superior (y = 0..40)
                new PlatformBounds(0, 680, 3000, 40),    // Piso principal (y = 680..720)
                new PlatformBounds(1200, 520, 220, 20),  // Plataforma elevada 1
                new PlatformBounds(1900, 500, 220, 20)   // Plataforma elevada 2
        );

        TrapProceduralGenerator gerador = new TrapProceduralGenerator(); // Usa DEFAULT_SEED = 12345L
        List<Trap> armadilhasGeradas = gerador.generateTraps(plataformasCenário, 3000, 720, 400, 420);
        salaLaboratorio.adicionarArmadilhas(armadilhasGeradas);
        salaLaboratorio.descrever();

        // 2. Simulação de Ciclo de Vida Polimórfico + Observer (TrapListener)
        Personagem explorador = new Personagem("Raioni", 100, 100, 616);
        SpikesRetrateis espinhos = TrapFactory.createSpikes(300, 616, 128, 64);
        BlocoQueCai blocoTeto = TrapFactory.createFallingBlock(600, 40, 64, 64, 576);

        TrapListener logListener = new TrapListener() {
            @Override
            public void onStateChanged(Trap trap, TrapState oldState, TrapState newState) {
                System.out.printf("  [Observer] %s mudou de estado: %s -> %s%n", trap.getName(), oldState, newState);
            }

            @Override
            public void onTelegraphStarted(Trap trap, double duration) {
                System.out.printf("  [Telegraphing] %s vibrando em alerta por %.1fs!%n", trap.getName(), duration);
            }

            @Override
            public void onImpact(Trap trap) {
                System.out.printf("  [Impacto] %s atingiu o solo em Y=%.0f! (Camera Shake)%n", trap.getName(), trap.getY());
            }

            @Override
            public void onDamageDealt(Trap trap, DamageTarget target, int damage) {
                System.out.printf("  [Dano] %s causou %d de dano! Alvo em I-Frames (1.0s): %s%n",
                        trap.getName(), damage, target);
            }
        };

        espinhos.addListener(logListener);
        blocoTeto.addListener(logListener);

        Sala salaTeste = new Sala("teste_armadilhas", "Câmara de Testes", "Simulação passo a passo das armadilhas.");
        salaTeste.adicionarArmadilha(espinhos);
        salaTeste.adicionarArmadilha(blocoTeto);

        System.out.println("\n--- Passo 1: Jogador pisa nos Espinhos Retráteis (x=320, y=616) ---");
        explorador.moverPara(320, 616);
        salaTeste.atualizarArmadilhas(0.1, explorador); // Aciona gatilho -> TELEGRAPHING (2.0s)
        salaTeste.atualizarArmadilhas(1.0, explorador); // Ainda em aviso (1.0s de 2.0s)
        salaTeste.atualizarArmadilhas(1.05, explorador); // Completa 2.0s -> ACTIVE e aplica dano + I-Frames!

        System.out.println("\n--- Passo 2: Jogador passa sob o Bloco Esmagador (x=610, y=616) ---");
        explorador.atualizar(1.1); // Avança 1.1s para encerrar a janela de I-Frames anterior
        explorador.moverPara(610, 616);
        salaTeste.atualizarArmadilhas(0.1, explorador); // Aciona gatilho vertical -> TELEGRAPHING (0.5s)
        salaTeste.atualizarArmadilhas(0.3, explorador);
        salaTeste.atualizarArmadilhas(0.25, explorador); // Entra em ACTIVE e começa a cair do teto
        salaTeste.atualizarArmadilhas(0.6, explorador);  // Impacta o chão (Y=616) e atinge o jogador!
        System.out.println("Estado final do jogador: " + explorador);
    }
}

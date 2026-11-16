package com.jogo.habilidades;

import com.jogo.personagens.Jogador;
import com.jogo.personagens.Personagem;

import java.util.Random;

/**
 * Sandevistan: liga e desliga (toggle) com a mesma ação. -Raioni
 * - Consome 20 de energia por segundo enquanto está ligado
 * - Deixa o tempo dos inimigos em 25% (no console: eles só agem 1 turno a cada 4)
 * - 25% de chance de aplicar Cyberpsicose ao ligar
 * - Cooldown de 30s depois de desligar (cancelado ou por falta de energia)
 * - Desliga sozinho quando a energia chega a 0
 * O modelo não imprime nada: quem mostra é a Batalha/TelaConsole.
 */
public class SandevistanMichyle extends Habilidade {

    public static final double CONSUMO_ENERGIA_POR_SEGUNDO = 20.0;
    public static final double ESCALA_TEMPO = 0.25; // inimigos a 25% da velocidade
    public static final double CHANCE_CYBERPSICOSE = 0.25; // 25%
    public static final double COOLDOWN_POS_DESATIVAR = 30.0; // segundos

    private final Random sorteio;
    private final Cyberpsicose cyberpsicose = new Cyberpsicose();
    private boolean ativo = false;
    private double acumuladorConsumo = 0;
    private double cooldownPosDesativar = 0;

    public SandevistanMichyle(boolean aprendida) {
        this(aprendida, new Random());
    }

    // Sobrecarga: os testes passam um Random com semente pra ter resultado fixo
    public SandevistanMichyle(boolean aprendida, Random sorteio) {
        // tempoEspera 0: o cooldown de verdade só começa ao desligar (cooldownPosDesativar)
        super(aprendida, 0.0, 0, 0.0);
        this.sorteio = sorteio;
    }

    @Override
    public boolean podeUsar(Jogador jogador) {
        if (!isAprendida()) {
            return false;
        }
        if (ativo) {
            return true; // ligado: sempre dá pra desligar
        }
        return jogador.getEnergiaAtual() > 0 && cooldownPosDesativar <= 0;
    }

    // Chamado por usar(): alterna entre ligado e desligado. alvo não é usado
    @Override
    public void executar(Jogador usuario, Personagem alvo) {
        if (ativo) {
            desativar(usuario);
        } else {
            ativar(usuario);
        }
    }

    private void ativar(Jogador jogador) {
        ativo = true;
        acumuladorConsumo = 0;
        if (sorteio.nextDouble() < CHANCE_CYBERPSICOSE) {
            cyberpsicose.aplicar(jogador);
        }
    }

    private void desativar(Jogador jogador) {
        ativo = false;
        // a Cyberpsicose NÃO acaba ao desligar: dura até a energia voltar a 50%
        cyberpsicose.marcarFimDoSandevistan();
        cooldownPosDesativar = COOLDOWN_POS_DESATIVAR;
    }

    /** Passa o tempo: gasta energia se ligado, conta o cooldown e atualiza a Cyberpsicose. */
    public void atualizarSandevistan(Jogador jogador, double dt) {
        if (cooldownPosDesativar > 0) {
            cooldownPosDesativar = Math.max(0, cooldownPosDesativar - dt);
        }
        if (ativo) {
            acumuladorConsumo += CONSUMO_ENERGIA_POR_SEGUNDO * dt;
            if (acumuladorConsumo >= 1.0) {
                int pontos = (int) acumuladorConsumo;
                acumuladorConsumo -= pontos;
                jogador.consumirEnergia(pontos);
            }
            if (jogador.getEnergiaAtual() <= 0) {
                desativar(jogador); // desliga sozinho sem energia
            }
        }
        if (cyberpsicose.estaAtivo() && !cyberpsicose.atualizar(jogador, dt)) {
            cyberpsicose.remover(jogador); // terminou naturalmente
        }
    }

    public boolean estaAtivo() {
        return ativo;
    }

    public double getCooldownRestante() {
        return cooldownPosDesativar;
    }

    public Cyberpsicose getCyberpsicose() {
        return cyberpsicose;
    }
}

package com.jogo.habilidades;
import com.jogo.habilidades.Cyberpsicose;
import com.jogo.personagens.Jogador;
import com.jogo.personagens.Personagem;

/**
 * - Consome 20 energia/segundo enquanto ativo
 * - Time scale 0.25x (25% velocidade) para TUDO exceto movimento do jogador
 * - 10% chance de aplicar Cyberpsicose ao ativar
 * - Cooldown de 30s após desativar (por energia acabando ou cancelando)
 * - Para automaticamente quando energia chega a 0
 */
public class SandevistanMichyle extends Habilidade {

    private static final double CONSUMO_ENERGIA_POR_SEGUNDO = 20.0;
    private static final double CHANCE_CYBERPSICOSE = 0.25; // 25%
    private static final double COOLDOWN_POS_DESATIVAR = 30.0; // 30 segundos

    private boolean ativo = false;
    private double acumuladorConsumo = 0;
    private double cooldownPosDesativar = 0;
    private Cyberpsicose cyberpsicose;

    public SandevistanMichyle(boolean aprendida) {
        super(aprendida, COOLDOWN_POS_DESATIVAR, 0, 0); // custo 0 no construtor, duração 0 (toggle)
        this.cyberpsicose = new Cyberpsicose();
    }

    @Override
    public boolean podeUsar(Jogador jogador) {
        if (!isAprendida()) {
            return false;
        }
        // Se está ativo, pode sempre desativar (toggle off)
        if (ativo) {
            return true;
        }
        // Se não ativo: precisa ter energia > 0 e não estar em cooldown pós-desativação
        return estaDisponivel() && jogador.getEnergiaAtual() > 0 && cooldownPosDesativar <= 0;
    }

    private boolean ativar(Jogador jogador) {
        if (jogador.getEnergiaAtual() <= 0) {
            return false;
        }

        ativo = true;
        acumuladorConsumo = 0;

        // 25% chance de Cyberpsicose
        if (Math.random() < CHANCE_CYBERPSICOSE) {
            cyberpsicose.aplicar(jogador);
            System.out.println("[Sandevistan] CYBERPSICOSE ATIVADA!");
            // Feedback de ativação
        } else {
            System.out.println("[Sandevistan] Ativado sem Cyberpsicose (random: " + Math.random() + ")");
        }

        return true;
    }

    private void desativar(Jogador jogador) {
        ativo = false;

        // Remove Cyberpsicose se ativa
        if (cyberpsicose.estaAtivo()) {
            cyberpsicose.remover(jogador);
        }

        // Inicia cooldown pós-desativação
        cooldownPosDesativar = COOLDOWN_POS_DESATIVAR;

    }

    /**
     * Consome energia enquanto ativo e gerencia cooldown pós-desativação.
     */
    public void atualizarSandevistan(Jogador jogador, double tpf) {
        // Atualiza cooldown pós-desativação
        if (cooldownPosDesativar > 0) {
            cooldownPosDesativar -= tpf;
        }

        if (!ativo) {
            return;
        }

        // Consome energia por segundo
        acumuladorConsumo += CONSUMO_ENERGIA_POR_SEGUNDO * tpf;
        if (acumuladorConsumo >= 1.0) {
            int pontos = (int) acumuladorConsumo;
            acumuladorConsumo -= pontos;
            jogador.consumirEnergia(pontos);
        }

        // Atualiza Cyberpsicose se ativa
        if (cyberpsicose.estaAtivo()) {
            if (!cyberpsicose.atualizar(jogador, tpf)) {
                // Cyberpsicose terminou naturalmente
                cyberpsicose.remover(jogador);
            }
        }

        // Desliga automaticamente se energia acabou
        if (jogador.getEnergiaAtual() <= 0) {
            desativar(jogador);
        }
    }

    public Cyberpsicose getCyberpsicose() {
        return cyberpsicose;
    }
}
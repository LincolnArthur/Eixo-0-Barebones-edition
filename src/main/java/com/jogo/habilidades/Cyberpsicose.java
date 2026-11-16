package com.jogo.habilidades;

import com.jogo.personagens.Jogador;
import com.jogo.personagens.Personagem;

/**
 * Debuff Cyberpsicose: chance de 25% ao ligar o SandevistanMichyle. -Raioni
 * - Dano contínuo (8 por segundo) até a vida chegar a 50% da vida que o
 *   jogador tinha quando o efeito começou (nunca mata)
 * - Dura até o Sandevistan acabar E a energia voltar a 50% da máxima.
 *   Continua com o Sandevistan desligado: é o "preço" do implante.
 * Mesmas regras da versão FXGL.
 */
public class Cyberpsicose implements Efeito {
    public static final double DANO_POR_SEGUNDO = 8.0;

    private boolean ativo = false;
    private int vidaLimite; // 50% da vida ATUAL no momento da ativação
    private double timerDano = 0;
    private boolean sandevistanAcabou = false;

    @Override
    public void aplicar(Personagem alvo) {
        if (!(alvo instanceof Jogador jogador) || ativo) {
            return; // só afeta o jogador, e não acumula
        }
        ativo = true;
        vidaLimite = (int) Math.ceil(jogador.getVidaAtual() * 0.5);
        timerDano = 0;
        sandevistanAcabou = false;
    }

    @Override
    public void remover(Personagem alvo) {
        ativo = false;
    }

    // Avisado pelo SandevistanMichyle quando ele desliga
    public void marcarFimDoSandevistan() {
        sandevistanAcabou = true;
    }

    @Override
    public boolean atualizar(Personagem alvo, double dt) {
        if (!ativo || !(alvo instanceof Jogador jogador)) {
            return false;
        }
        // Terminou: Sandevistan já acabou e a energia voltou a 50%
        if (sandevistanAcabou && jogador.getEnergiaAtual() >= jogador.getEnergiaMaxima() * 0.5) {
            return false;
        }
        if (jogador.getVidaAtual() > vidaLimite) {
            timerDano += dt;
            if (timerDano >= 1.0) {
                int dano = (int) Math.ceil(DANO_POR_SEGUNDO * timerDano);
                timerDano = 0; // antes não zerava: o dano crescia a cada atualização
                // não passa do limite de 50%
                jogador.tomarDano(Math.min(dano, jogador.getVidaAtual() - vidaLimite));
            }
        }
        return true;
    }

    @Override
    public boolean estaAtivo() {
        return ativo;
    }

    @Override
    public String getNome() {
        return "Cyberpsicose";
    }

    public boolean estaNoLimiteVida(Jogador jogador) {
        return jogador.getVidaAtual() <= vidaLimite;
    }
}

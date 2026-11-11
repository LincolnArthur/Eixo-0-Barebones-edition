package com.jogo.habilidades;

import com.jogo.personagens.Jogador;
import com.jogo.personagens.Personagem;

/**
 * Debuff Cyberpsicose: chance de 25% ao ativar SandevistanMichyle.
 * - Dano contínuo até a vida chegar a 50% da vida atual no momento da ativação
 * - Dura até a energia zerar E recuperar 50% da energia máxima 
 * -Raioni
 */
public class Cyberpsicose implements Efeito {
    private static final double DANO_POR_SEGUNDO = 8.0; // dano contínuo por segundo

    private boolean ativo = false;
    private double vidaLimite; // 50% da vida atual no momento da ativação
    private double timerDano = 0;
    private boolean energiaZerou = false;

    @Override
    public void aplicar(Personagem alvo) {
        if (!(alvo instanceof Jogador jogador)) {
            return; // Cyberpsicose só afeta o jogador
        }
        if (ativo) {
            return; // já aplicado
        }
        ativo = true;
        vidaLimite = jogador.getVidaAtual() * 0.5; // 50% da vida ATUAL
        energiaZerou = false;
    }

    @Override
    public void remover(Personagem alvo) {
        if (!(alvo instanceof Jogador jogador)) {
            return;
        }

        if (!ativo) {
            return;
        }

        ativo = false;
    }

    @Override
    public boolean atualizar(Personagem alvo, double tpf) {
        if (!ativo || !(alvo instanceof Jogador jogador)) {
            return false;
        }

        // Verifica se energia zerou
        if (jogador.getEnergiaAtual() <= 0) {
            energiaZerou = true;
        }

        // Se energia zerou E recuperou 50% da energia máxima, remove o efeito
        if (energiaZerou && jogador.getEnergiaAtual() >= jogador.getEnergiaMaxima() * 0.5) {
            return false; // sinaliza para remover
        }

        // Aplica dano contínuo até chegar no limite (50% da vida original)
        if (jogador.getVidaAtual() > vidaLimite) {
            timerDano += tpf;
            if (timerDano >= 1.0) { // a cada 1 segundo
                int dano = (int) Math.ceil(DANO_POR_SEGUNDO * timerDano);
                // Math.ceil arredonda pra cima o double para um valor inteiro do tipo double 
                // Usa tomarDano direto (ignora invencibilidade, é dano do debuff)
                jogador.tomarDano(dano);
            }
        }

        return true; // continua ativo
    }

    @Override
    public boolean estaAtivo() {
        return ativo;
    }

    @Override
    public String getNome() {
        return "Cyberpsicose";
    }

    /**
     * Verifica se o jogador está com vida no limite crítico (50%).
     */
    public boolean estaNoLimiteVida(Jogador jogador) {
        return jogador.getVidaAtual() <= vidaLimite;
    }
}
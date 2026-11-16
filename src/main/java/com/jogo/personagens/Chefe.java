package com.jogo.personagens;

import com.jogo.armadilhas.BlocoQueCai;
import com.jogo.armadilhas.DamageTarget;
import com.jogo.armadilhas.Trap;
import com.jogo.armadilhas.TrapFactory;
import com.jogo.combate.ArmaDeFogo;
import com.jogo.combate.Ataque;

import java.util.ArrayList;
import java.util.List;

//Chefe da última sala: NEXUS. Filho de Inimigo (vida, arma, XP, congelar),
//mas com o próprio jeito de atacar, igual ao do FXGL: sempre avisa antes
//e segue a mesma rotação de 4 ataques (ver AtaqueChefe).
//
//No console não dá pra desviar, então cada ataque do chefe ocupa DOIS
//turnos: no primeiro ele só avisa (brilha) qual vem; no segundo ataca.
//E dos tiros e blocos, só uma parte acerta (os outros "erram", como
//quando a jogadora desvia no FXGL).
//
//Com metade da vida ou menos entra na fase 2: mais tiros, mais blocos,
//mais espinhos e uma prensa em cima do jogador.
//
//Sobrescreve também como as magias o afetam (polimorfismo): congela só
//metade do tempo e só perde 25% da vida máxima no Buraco Negro.
public class Chefe extends Inimigo {

    //Arena do FXGL (1280 px) e tamanho dos espinhos/prensa do ataque ARMADILHAS
    private static final double LARGURA_ARENA = 1280;
    private static final double LARGURA_ESPINHOS = 64;
    private static final double ALTURA_ESPINHOS = 32;
    private static final double LADO_PRENSA = 64;
    private static final double ALTURA_DA_PRENSA = 300;
    private static final double ESPACO_ENTRE_ESPINHOS = 150;
    //Passo da simulação das armadilhas (segundos) e tempo máximo de um ciclo
    private static final double PASSO = 0.1;
    private static final double DURACAO_MAXIMA_ARMADILHAS = 7.0;

    private final int dano;
    private AtaqueChefe proximoAtaque = AtaqueChefe.RAJADA;
    private AtaqueChefe ataqueAtual;
    private boolean avisando = false; //true: no próximo turno ele ataca
    private String ultimaAcao = "";
    private final List<Trap> armadilhasDoUltimoAtaque = new ArrayList<>();

    public Chefe(String nome, int vidaMaxima, int dano) {
        super(nome, vidaMaxima, new ArmaDeFogo("canhao-chefe", "Canhão", "Arma do chefe", dano, 0.0));
        if (dano <= 0) {
            throw new IllegalArgumentException("Dano do chefe precisa ser positivo: " + dano);
        }
        this.dano = dano;
    }

    //=== Fases ===

    public int getFase() {
        return getVidaAtual() * 2 <= getVidaMaxima() ? 2 : 1;
    }

    public int getQuantidadeTiros() {
        return getFase() == 1 ? 5 : 7;
    }

    //Quantos tiros da rajada acertam (o resto "o jogador desviou")
    public int getTirosQueAcertam() {
        return getFase() == 1 ? 2 : 3;
    }

    public int getQuantidadeBlocos() {
        return getFase() == 1 ? 4 : 6;
    }

    public int getBlocosQueAcertam() {
        return getFase() == 1 ? 1 : 2;
    }

    public int getQuantidadeEspinhos() {
        return getFase() == 1 ? 3 : 5;
    }

    //A prensa em cima do jogador só vem na fase 2
    public boolean usaPrensa() {
        return getFase() == 2;
    }

    //Espinhos espalhados em volta de um x (um embaixo dele), sem sair da arena
    public List<Double> calcularPosicoesEspinhos(double xCentro) {
        List<Double> posicoes = new ArrayList<>();
        int n = getQuantidadeEspinhos();
        for (int i = 0; i < n; i++) {
            double x = xCentro - LARGURA_ESPINHOS / 2 + (i - n / 2) * ESPACO_ENTRE_ESPINHOS;
            x = Math.max(10, Math.min(LARGURA_ARENA - LARGURA_ESPINHOS - 10, x));
            posicoes.add(x);
        }
        return posicoes;
    }

    //=== Turno do chefe ===

    //Sobrescreve o iniciarAtaque() do Inimigo: em vez de um golpe com a
    //arma, alterna entre avisar e executar o ataque da vez
    @Override
    public void iniciarAtaque(Personagem vitima) {
        if (estaMorto() || vitima == null || vitima.estaMorto()) {
            ultimaAcao = "";
            return;
        }
        if (!avisando) {
            ataqueAtual = proximoAtaque;
            proximoAtaque = proximoAtaque.proximo();
            avisando = true;
            ultimaAcao = getNome() + " brilha e prepara: " + ataqueAtual.getNome() + "!";
            return;
        }
        avisando = false;
        int vidaAntes = vitima.getVidaAtual();
        switch (ataqueAtual) {
            case RAJADA -> {
                aplicarVezes(vitima, getTirosQueAcertam(), dano);
                ultimaAcao = "Rajada: " + getQuantidadeTiros() + " tiros, " + getTirosQueAcertam() + " acertam.";
            }
            case INVESTIDA -> {
                aplicarVezes(vitima, 1, dano * 2);
                ultimaAcao = "Investida: " + getNome() + " mergulha em cima de " + vitima.getNome() + " (dano em dobro).";
            }
            case CHUVA -> {
                aplicarVezes(vitima, getBlocosQueAcertam(), dano);
                ultimaAcao = "Chuva de dados: " + getQuantidadeBlocos() + " blocos caem, "
                        + getBlocosQueAcertam() + " acertam.";
            }
            case ARMADILHAS -> ultimaAcao = plantarArmadilhas(vitima);
        }
        if (vitima.getVidaAtual() == vidaAntes && ataqueAtual != AtaqueChefe.ARMADILHAS) {
            ultimaAcao += " (sem dano)";
        }
    }

    private void aplicarVezes(Personagem vitima, int vezes, int danoPorAcerto) {
        for (int i = 0; i < vezes; i++) {
            new Ataque(danoPorAcerto).aplicarEm(vitima);
        }
    }

    //Usa o sistema de armadilhas de verdade: cria os espinhos (e a prensa
    //na fase 2) já acionados em volta do jogador e roda o ciclo deles
    //(aviso -> ativo -> recarga). Quem estiver em cima quando ficarem
    //ativos toma o dano, com a invencibilidade de 1 s das armadilhas.
    private String plantarArmadilhas(Personagem vitima) {
        armadilhasDoUltimoAtaque.clear();
        double alturaVitima = vitima.getHitbox().getHeight();
        double centroX = vitima.getX() + vitima.getHitbox().getWidth() / 2;
        double ySpikes = vitima.getY() + alturaVitima - ALTURA_ESPINHOS;

        for (double x : calcularPosicoesEspinhos(centroX)) {
            Trap espinhos = TrapFactory.createSpikes(x, ySpikes, LARGURA_ESPINHOS, ALTURA_ESPINHOS);
            espinhos.setDamage(dano);
            armadilhasDoUltimoAtaque.add(espinhos);
        }
        if (usaPrensa()) {
            double xPrensa = centroX - LADO_PRENSA / 2;
            double yTopo = vitima.getY() - ALTURA_DA_PRENSA;
            //Cai até a base dela ficar no pé do jogador
            double queda = (vitima.getY() + alturaVitima - LADO_PRENSA) - yTopo;
            BlocoQueCai prensa = TrapFactory.createFallingBlock(xPrensa, yTopo, LADO_PRENSA, LADO_PRENSA, queda);
            prensa.setDamage(dano * 2);
            armadilhasDoUltimoAtaque.add(prensa);
        }

        int vidaAntes = vitima.getVidaAtual();
        int acertos = simularArmadilhas(armadilhasDoUltimoAtaque, vitima);
        String texto = "Armadilhas: brotam " + getQuantidadeEspinhos() + " espinhos em volta de " + vitima.getNome()
                + (usaPrensa() ? " e uma prensa cai em cima" : "")
                + " (" + acertos + " acerto(s), -" + (vidaAntes - vitima.getVidaAtual()) + ").";
        return texto;
    }

    //Roda as armadilhas em passos pequenos até o ciclo acabar e devolve
    //quantas acertaram. É o mesmo update()/checkCollision() que a Sala usa.
    private int simularArmadilhas(List<Trap> armadilhas, DamageTarget alvo) {
        for (Trap armadilha : armadilhas) {
            armadilha.onTrigger(); //já nascem avisando, sem esperar o jogador chegar perto
        }
        int acertos = 0;
        for (double t = 0; t < DURACAO_MAXIMA_ARMADILHAS; t += PASSO) {
            if (alvo instanceof Personagem p) {
                p.atualizar(PASSO); //a invencibilidade de 1 s também passa
            }
            for (Trap armadilha : armadilhas) {
                armadilha.update(PASSO);
                if (alvo.isAlive() && armadilha.checkCollision(alvo)) {
                    acertos++;
                }
            }
        }
        return acertos;
    }

    //=== O que muda em relação aos outros inimigos ===

    @Override
    public String descreverUltimaAcao() {
        return ultimaAcao;
    }

    @Override
    public String getNomeAtaque() {
        return ataqueAtual == null ? getArma().getNome() : ataqueAtual.getNome();
    }

    //Gelo segura o chefe só metade do tempo
    @Override
    public void congelar(double turnos) {
        super.congelar(turnos / 2);
    }

    //Buraco Negro tira só 25% da vida máxima, em vez de matar
    @Override
    public int danoDeBuracoNegro() {
        return Math.max(1, getVidaMaxima() / 4);
    }

    @Override
    public int getExperienciaConcedida() {
        return 300;
    }

    public AtaqueChefe getProximoAtaque() {
        return proximoAtaque;
    }

    public AtaqueChefe getAtaqueAtual() {
        return ataqueAtual;
    }

    public boolean estaAvisando() {
        return avisando;
    }

    public List<Trap> getArmadilhasDoUltimoAtaque() {
        return List.copyOf(armadilhasDoUltimoAtaque);
    }
}

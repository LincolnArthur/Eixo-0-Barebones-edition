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
import com.jogo.combate.AdagaDupla;
import com.jogo.combate.Arma;
import com.jogo.combate.Espada;
import com.jogo.combate.Marreta;
import com.jogo.combate.Perfurante;
import com.jogo.combate.PistolaDuplaDePlasma;
import com.jogo.combate.RifleSniper;
import com.jogo.combate.Sabre;
import com.jogo.combate.Shotgun;
import com.jogo.controle.Batalha;
import com.jogo.controle.Dialogo;
import com.jogo.excecoes.SalaNaoEncontradaException;
import com.jogo.habilidades.Apara;
import com.jogo.habilidades.BolaDeFogo;
import com.jogo.habilidades.GolpeDeEnergia;
import com.jogo.habilidades.SandevistanMichyle;
import com.jogo.inventario.Categoria;
import com.jogo.inventario.ModuloCura;
import com.jogo.inventario.ModuloEnergia;
import com.jogo.mundo.Mapa;
import com.jogo.mundo.Sala;
import com.jogo.personagens.Inimigo;
import com.jogo.personagens.Jogador;
import com.jogo.personagens.Melee;
import com.jogo.personagens.Npc;
import com.jogo.personagens.NpcCurandeiro;
import com.jogo.personagens.NpcInformante;
import com.jogo.personagens.Personagem;
import com.jogo.personagens.Sniper;
import com.jogo.personagens.Voador;
import com.jogo.visao.TelaConsole;

import java.util.List;
import java.util.Random;

//Ponto de entrada. No MVC, o main só MONTA as peças: cria o modelo
//(personagens, armas, mapa), a visão (TelaConsole) e o controle
//(Batalha), e roda uma demonstração no formato da atividade de RPG:
//personagens, combate e status final.
//
//Quase todas as armas usam chanceCritico 0.0 pra o resultado ser sempre
//o mesmo (crítico é sorteado).
public class Jogo {
    public static void main(String[] args) {
        TelaConsole tela = new TelaConsole();

        //=== Mundo: duas salas ligadas ===
        Mapa mapa = new Mapa("Divisão Criogênica");
        Sala laboratorio = new Sala("lab", "Laboratório", "Tubos de criogenia quebrados por todo lado.");
        Sala corredor = new Sala("corredor", "Corredor Norte", "Um corredor escuro com luzes piscando.");
        laboratorio.conectarBidirecional("norte", "sul", corredor);
        mapa.adicionarSala(laboratorio);
        mapa.adicionarSala(corredor);
        try {
            mapa.definirSalaInicial("sala-que-nao-existe");
        } catch (SalaNaoEncontradaException e) {
            tela.mostrarErro(e.getMessage()); //tratada: o jogo segue na sala padrão
        }

        //=== Jogador e o que ele carrega ===
        Jogador jogador = new Jogador("Aventureiro", 100,
                new Espada("espada", "Espada Longa", "Arma inicial do jogador", 10, 0.0), 50);
        jogador.coletarItem(new Marreta("marreta", "Marreta", "Lenta, forte contra snipers", 14, 0.0));
        jogador.coletarItem(new AdagaDupla("adaga", "Adaga Dupla", "Rápida, combo no 5º golpe", 6, 0.0));
        jogador.coletarItem(new Sabre("sabre", "Sabre", "Bônus a cada 3 golpes", 9, 0.0, 0.8));
        jogador.coletarItem(new Shotgun("shotgun", "Shotgun", "5 balins", 4, 5, 0.0));
        jogador.coletarItem(new PistolaDuplaDePlasma("pistola", "Pistola Dupla de Plasma", "Dois tiros", 7, 0.0));
        jogador.coletarItem(new ModuloCura("modulo_cura", "Módulo de Cura", "Recupera 30 de vida", Categoria.CONSUMIVEL, 30));

        //Lista do tipo da mãe (Inimigo) com objetos de várias filhas
        List<Inimigo> inimigos = List.of(
                new Melee("Bandido", 40, 8, 0.0),
                new Sniper("Atirador", 30, 12, 0.0),
                new Voador("Morcego", 25, 6, 0.0));

        Batalha batalha = new Batalha(jogador, inimigos, tela);

        tela.titulo("Sala inicial");
        tela.mostrarSala(mapa.getSalaInicial());

        tela.titulo("Personagens");
        tela.mostrarJogador(jogador);
        tela.mostrarPersonagens(inimigos);

        //=== Polimorfismo: mesma chamada, comportamento diferente ===
        //listarDoTipo é genérico: já devolve List<Arma>. Pra cada arma,
        //a mesma chamada (atacar um boneco de treino); cada classe
        //calcula o dano do seu jeito (Shotgun soma 5 balins, Pistola
        //soma 2 tiros, Marreta é a mais forte...)
        tela.titulo("Arsenal (mesmo ataque, armas diferentes)");
        for (Arma arma : jogador.getInventario().listarDoTipo(Arma.class)) {
            Personagem boneco = new Personagem("Boneco de treino", 999);
            int vidaAntes = boneco.getVidaAtual();
            batalha.equiparArma(arma.getId());
            jogador.iniciarAtaque(boneco);
            tela.mostrarMensagem("  " + arma.getNome() + " tirou " + (vidaAntes - boneco.getVidaAtual()));
        }

        //=== Combate ===
        tela.titulo("Combate");
        batalha.equiparArma(jogador.getInventario().listarDoTipo(Marreta.class).get(0).getId());
        batalha.jogadorAtaca(inimigos.get(1));   //Marreta contra o Sniper: bônus de dano
        batalha.turnoDosInimigos();               //cada inimigo ataca com a própria arma

        //Variável do tipo da mãe (GolpeDeEnergia) guardando um filho (BolaDeFogo)
        GolpeDeEnergia golpe = new BolaDeFogo(true, 2.0, 15, 0.0, 20);
        batalha.usarHabilidade(golpe, inimigos.get(2));
        batalha.usarHabilidade(golpe, inimigos.get(0)); //ainda em cooldown: não usa
        batalha.usarHabilidade(new Apara(true, 1.5, 5, 0.3), null);

        //Exceções tratadas: os dois erros viram mensagem, o jogo não para
        batalha.usarItem("modulo_cura");
        batalha.usarItem("modulo_cura");          //já usou o único: ItemNaoEncontradoException
        batalha.equiparArma("modulo_cura-x");     //id que não existe

        //Perfurante: o tiro do rifle atravessa até 2 inimigos vivos
        RifleSniper rifle = new RifleSniper("rifle", "Rifle Sniper", "Atravessa até 2 alvos", 30, 0.0, 2);
        Perfurante perfurante = rifle; //a interface também serve de tipo
        tela.mostrarMensagem("Rifle atravessa " + perfurante.getAlvosAtravessados() + " alvos");
        batalha.dispararPerfurante(rifle);

        batalha.equiparArma(jogador.getInventario().listarDoTipo(Espada.class).get(0).getId());
        for (Inimigo inimigo : inimigos) {
            batalha.jogadorAtaca(inimigo);
        }

        //=== Status ===
        tela.titulo("Status");
        tela.mostrarJogador(jogador);
        tela.mostrarPersonagens(inimigos);
        tela.mostrarMensagem(batalha.jogadorVenceu() ? "Vitória!" : "Ainda há inimigos de pé.");

        //=== Sandevistan (câmera lenta) e Cyberpsicose ===
        demonstrarSandevistan(tela);

        //=== NPCs amigáveis da sala (Npc, NpcInformante, NpcCurandeiro e o controle Dialogo) ===
        demonstrarNpcs(tela, jogador, laboratorio);

        // =========================================================================
        // DEMONSTRAÇÃO: SISTEMA MODULAR DE ARMADILHAS (POO / SOLID / DESIGN PATTERNS)
        // =========================================================================
        demonstrarSistemaDeArmadilhas();
    }

    //Sandevistan: liga, os inimigos agem 1 turno a cada 4 (tempo a 25%),
    //a energia acaba e ela desliga sozinha. A semente 4102 faz o sorteio
    //de 25% cair na Cyberpsicose, pra mostrar o efeito sempre
    private static void demonstrarSandevistan(TelaConsole tela) {
        Jogador jogador = new Jogador("Michyle", 100,
                new Espada("espada", "Espada Longa", "", 10, 0.0), 50);
        jogador.coletarItem(new ModuloEnergia("modulo_energia", "Módulo de Energia",
                "Recupera 40 de energia", Categoria.CONSUMIVEL, 40));
        List<Inimigo> inimigos = List.of(new Melee("Robô", 200, 8, 0.0));
        Batalha batalha = new Batalha(jogador, inimigos, tela);
        SandevistanMichyle sandevistan = new SandevistanMichyle(true, new Random(4102));

        tela.titulo("Sandevistan");
        batalha.alternarSandevistan(sandevistan);
        for (int turno = 1; turno <= 3; turno++) {
            batalha.jogadorAtaca(inimigos.get(0));
            batalha.turnoDosInimigos();
            tela.mostrarMensagem("  energia: " + jogador.getEnergiaAtual() + "/" + jogador.getEnergiaMaxima());
        }
        batalha.alternarSandevistan(sandevistan); //em cooldown: não liga
        batalha.usarItem("modulo_energia");       //energia volta a 50%: a Cyberpsicose acaba
        batalha.turnoDosInimigos();               //tempo normal de novo
        tela.mostrarJogador(jogador);
        tela.mostrarMensagem("Cyberpsicose ativa? " + (sandevistan.getCyberpsicose().estaAtivo() ? "sim" : "não"));
    }

    //NPCs amigáveis: mesma divisão do resto do main (monta as peças). Quem
    //conversa é o Dialogo (controle); quem imprime é a TelaConsole (visão).
    private static void demonstrarNpcs(TelaConsole tela, Jogador jogador, Sala sala) {
        NpcInformante ze = new NpcInformante("Zé", List.of(
                "Olá, viajante! Há armas e módulos espalhados pelas salas.",
                "Use a Apara na hora certa pra não tomar dano.",
                "No fim do caminho existe um chefe. Chegue com a vida cheia!"));
        NpcCurandeiro vega = new NpcCurandeiro("Dra. Vega", List.of(
                "Você parece ferido. Fique perto que eu cuido disso.",
                "Só tenho uma cura por sala, então use com sabedoria."), 1);

        //Lista do tipo da mãe (Npc) com objetos de duas filhas: coleção polimórfica
        List<Npc> npcs = List.of(ze, vega);
        Dialogo dialogo = new Dialogo(jogador, tela);

        tela.titulo("NPCs em " + sala.getNome());
        tela.mostrarMensagem("NPCs aqui: " + npcs);

        //Mesma chamada (interagir) pra todos: o Informante só fala, o Curandeiro cura
        tela.titulo("NPCs: conversa com a vida cheia (a cura não é gasta)");
        dialogo.conversarComTodos(npcs);

        tela.titulo("NPCs: conversa depois de ser ferido");
        jogador.tomarDano(60);
        tela.mostrarJogador(jogador);
        dialogo.conversarComTodos(npcs);
        tela.mostrarJogador(jogador);

        tela.titulo("NPCs: sem curas restantes");
        jogador.tomarDano(30);
        dialogo.conversar(vega);

        tela.titulo("NPCs: invulneráveis");
        ze.tomarDano(999);
        tela.mostrarMensagem(ze.getNome() + " tomou 999 de dano e " + (ze.estaMorto() ? "morreu" : "continua de pé"));
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

        List<PlatformBounds> plataformasCenario = List.of(
                new PlatformBounds(0, 0, 3000, 40),      // Teto físico superior (y = 0..40)
                new PlatformBounds(0, 680, 3000, 40),    // Piso principal (y = 680..720)
                new PlatformBounds(1200, 520, 220, 20),  // Plataforma elevada 1
                new PlatformBounds(1900, 500, 220, 20)   // Plataforma elevada 2
        );

        TrapProceduralGenerator gerador = new TrapProceduralGenerator(); // Usa DEFAULT_SEED = 12345L
        List<Trap> armadilhasGeradas = gerador.generateTraps(plataformasCenario, 3000, 720, 400, 420);
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

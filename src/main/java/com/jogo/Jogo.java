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
import com.jogo.inventario.Inventariavel;
import com.jogo.mundo.Mapa;
import com.jogo.mundo.NpcsDasSalas;
import com.jogo.mundo.Sala;
import com.jogo.personagens.Chefe;
import com.jogo.personagens.Inimigo;
import com.jogo.personagens.Jogador;
import com.jogo.personagens.Melee;
import com.jogo.personagens.Npc;
import com.jogo.personagens.NpcChefe;
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
        TelaConsole tela = TelaConsole.doTerminal(); //no terminal, Enter passa as falas

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

        // =========================================================================
        // CAMPANHA: as 5 salas do jogo do FXGL, até a luta com o NEXUS
        // =========================================================================
        demonstrarCampanha(tela);
    }

    //=== Campanha ===
    //Mesmo mapa do jogo do FXGL: 5 salas ligadas, com os inimigos e os itens
    //de cada uma (mesmos nomes, vida e dano dos JSON de lá). Mostra as
    //regras novas na ordem em que aparecem jogando: energia por acerto,
    //Raio de Gelo (nível 2), Buraco Negro (nível 3), a Curandeira e o chefe.
    //Pra demonstração não ficar enorme, nas salas 1 a 4 os inimigos só
    //atacam nos turnos marcados; na arena a luta é completa.
    private static void demonstrarCampanha(TelaConsole tela) {
        Mapa mapa = montarMapaEixo0();
        NpcsDasSalas.colocarNpcs(mapa);
        List<Sala> salas = List.copyOf(mapa.getSalas().values());

        Jogador michyle = new Jogador("Michyle", 100,
                new Espada("espada", "Espada", "Arma inicial", 10, 0.0), 50);
        Dialogo dialogo = new Dialogo(michyle, tela);

        tela.titulo("Campanha: " + mapa.getNome() + " (" + mapa.totalSalas() + " salas)");
        tela.mostrarMensagem("Dificuldade: " + michyle.getDificuldade().getNome()
                + " (Fácil recebe metade do dano, Difícil 1,5x)");

        //--- Sala 1: Informante, energia só por acerto ---
        Sala sala1 = salas.get(0);
        tela.titulo("Sala 1: " + sala1.getNome());
        tela.mostrarSala(sala1);
        dialogo.conversarAteOFim(sala1.getNpcs().get(0));
        coletarItens(michyle, sala1, tela);
        Batalha batalha = new Batalha(michyle, sala1.getInimigos(), tela);
        GolpeDeEnergia bolaDeFogo = new BolaDeFogo(true, 2.0, 15, 0.0, 20);
        batalha.usarHabilidade(bolaDeFogo, sala1.getInimigos().get(1));
        batalha.turnoDosInimigos();
        tela.mostrarMensagem("  energia depois de um turno: " + michyle.getEnergiaAtual()
                + " (não recarrega sozinha antes do nível " + Jogador.NIVEL_RECARGA_AUTOMATICA + ")");
        venceSemContraAtaque(batalha);
        tela.mostrarMensagem("  energia depois dos acertos: " + michyle.getEnergiaAtual()
                + " (+" + Jogador.ENERGIA_POR_ACERTO + " por acerto)");

        //--- Sala 2: nível 2 libera o Raio de Gelo ---
        Sala sala2 = salas.get(1);
        tela.titulo("Sala 2: " + sala2.getNome());
        tela.mostrarSala(sala2);
        coletarItens(michyle, sala2, tela);
        batalha = new Batalha(michyle, sala2.getInimigos(), tela);
        equiparPrimeira(batalha, michyle, Shotgun.class);
        batalha.jogadorAtaca(sala2.getInimigos().get(1)); //derruba o Atirador: sobe pro nível 2
        Inimigo espadachim = sala2.getInimigos().get(2);
        batalha.usarGelo(espadachim);
        batalha.turnoDosInimigos(); //o Espadachim congelado perde o turno
        venceSemContraAtaque(batalha);

        //--- Sala 3: nível 3 libera o Buraco Negro ---
        Sala sala3 = salas.get(2);
        tela.titulo("Sala 3: " + sala3.getNome());
        tela.mostrarSala(sala3);
        coletarItens(michyle, sala3, tela);
        batalha = new Batalha(michyle, sala3.getInimigos(), tela);
        equiparPrimeira(batalha, michyle, PistolaDuplaDePlasma.class);
        venceSemContraAtaque(batalha);

        //--- Sala 4: Buraco Negro em 6 inimigos e a Curandeira ---
        Sala sala4 = salas.get(3);
        tela.titulo("Sala 4: " + sala4.getNome());
        tela.mostrarSala(sala4);
        coletarItens(michyle, sala4, tela);
        batalha = new Batalha(michyle, sala4.getInimigos(), tela);
        batalha.turnoDosInimigos(); //seis inimigos atacam juntos
        tela.mostrarJogador(michyle);
        NpcCurandeiro curandeira = (NpcCurandeiro) sala4.getNpcs().get(0);
        dialogo.conversar(curandeira); //a cura custa 20 de energia
        tela.mostrarJogador(michyle);
        batalha.usarBuracoNegro();             //sem energia cheia: não dá
        batalha.usarItem("modulo_energia");    //+20: energia cheia de novo
        batalha.usarBuracoNegro();             //destrói os seis de uma vez
        tela.mostrarJogador(michyle);

        //--- Sala 5: o NEXUS começa como NPC ---
        Sala arena = salas.get(4);
        tela.titulo("Sala 5: " + arena.getNome());
        tela.mostrarSala(arena);
        coletarItens(michyle, arena, tela);
        NpcChefe nexusNpc = (NpcChefe) arena.getNpcs().get(0);
        dialogo.conversarAteOFim(nexusNpc);
        Chefe nexus = nexusNpc.despertar();
        new Batalha(michyle, List.of(), tela).usarItem("modulo_energia"); //energia pro Raio de Gelo
        lutarContraChefe(new Batalha(michyle, List.of(nexus), tela), michyle, nexus, tela);
    }

    //O jogador ataca o primeiro inimigo vivo até não sobrar nenhum (os
    //inimigos não revidam: só pra encurtar a demonstração)
    private static void venceSemContraAtaque(Batalha batalha) {
        for (int i = 0; i < 60 && !batalha.jogadorVenceu(); i++) {
            for (Inimigo inimigo : batalha.getInimigos()) {
                if (!inimigo.estaMorto()) {
                    batalha.jogadorAtaca(inimigo);
                    break;
                }
            }
        }
    }

    //Luta completa: um turno do jogador, um turno do chefe. A estratégia
    //é simples: cura com pouca vida, gelo quando pode, Buraco Negro com a
    //energia cheia e, no resto, tiro de rifle
    private static void lutarContraChefe(Batalha batalha, Jogador jogador, Chefe chefe, TelaConsole tela) {
        tela.titulo("Luta: " + chefe.getNome());
        equiparPrimeira(batalha, jogador, RifleSniper.class);
        for (int turno = 1; turno <= 60 && !batalha.jogadorVenceu() && !batalha.jogadorPerdeu(); turno++) {
            tela.mostrarMensagem("-- turno " + turno + " (fase " + chefe.getFase() + ") --");
            if (jogador.getVidaAtual() * 3 < jogador.getVidaMaxima()
                    && jogador.getInventario().buscar("modulo_cura") != null) {
                batalha.usarItem("modulo_cura");
            } else if (jogador.getBuracoNegro().podeUsar(jogador)) {
                batalha.usarBuracoNegro();
            } else if (jogador.getGelo().podeUsar(jogador) && !chefe.estaCongelado()) {
                batalha.usarGelo(chefe);
            } else {
                batalha.jogadorAtaca(chefe);
            }
            batalha.turnoDosInimigos();
        }
        tela.mostrarJogador(jogador);
        tela.mostrarPersonagem(chefe);
        tela.mostrarMensagem(batalha.jogadorVenceu() ? "NEXUS derrotado. Fim do jogo!" : "Michyle caiu na arena.");
    }

    //Armas não empilham: no inventário cada uma ganha um id único
    //(rifle-1...), então a busca é pelo tipo, com o listarDoTipo genérico
    private static void equiparPrimeira(Batalha batalha, Jogador jogador, Class<? extends Arma> tipo) {
        batalha.equiparArma(jogador.getInventario().listarDoTipo(tipo).get(0).getId());
    }

    private static void coletarItens(Jogador jogador, Sala sala, TelaConsole tela) {
        for (Inventariavel item : sala.getItens()) {
            jogador.coletarItem(item);
        }
        if (!sala.getItens().isEmpty()) {
            tela.mostrarMensagem("Michyle pega " + sala.getItens().size() + " item(ns) do chão.");
        }
    }

    //As 5 salas do FXGL: mesmos inimigos (nome, vida, dano) e itens dos
    //nivel1..5.json de lá, ligadas leste-oeste
    private static Mapa montarMapaEixo0() {
        Mapa mapa = new Mapa("Eixo 0");
        Sala s1 = new Sala("sala1", "Laboratório Criogênico", "Onde Michyle acorda, entre tubos de criogenia.");
        Sala s2 = new Sala("sala2", "Cidade: Avenida de Neon", "Letreiros piscando e drones no céu.");
        Sala s3 = new Sala("sala3", "Cidade: Viadutos", "Plataformas suspensas sobre a cidade.");
        Sala s4 = new Sala("sala4", "Cidade: Portão da Torre", "A última parada antes da arena.");
        Sala s5 = new Sala("sala5", "Arena do NEXUS", "Uma sala vazia. Algo flutua no centro.");
        List<Sala> ordem = List.of(s1, s2, s3, s4, s5);
        for (int i = 0; i < ordem.size(); i++) {
            mapa.adicionarSala(ordem.get(i));
            if (i > 0) {
                ordem.get(i - 1).conectarBidirecional("leste", "oeste", ordem.get(i));
            }
        }

        s1.adicionarInimigo(new Voador("Morcego", 30, 5, 0.0));
        s1.adicionarInimigo(new Sniper("Atirador", 20, 10, 0.0));
        s1.adicionarInimigo(new Melee("Espadachim", 50, 15, 0.0));
        s1.adicionarItem(cura(30));
        s1.adicionarItem(energia(20));
        s1.adicionarItem(new AdagaDupla("adaga", "Adagas Duplas", "Dois cortes rápidos", 8, 0.0));

        s2.adicionarInimigo(new Voador("Morcego", 30, 5, 0.0));
        s2.adicionarInimigo(new Sniper("Atirador", 20, 10, 0.0));
        s2.adicionarInimigo(new Melee("Espadachim", 50, 15, 0.0));
        s2.adicionarInimigo(new Voador("Morcego Alfa", 40, 8, 0.0));
        s2.adicionarItem(energia(20));
        s2.adicionarItem(cura(30));
        s2.adicionarItem(new Shotgun("shotgun", "Shotgun", "5 balins em leque", 8, 5, 0.0));

        s3.adicionarInimigo(new Voador("Morcego", 30, 6, 0.0));
        s3.adicionarInimigo(new Sniper("Atirador", 22, 11, 0.0));
        s3.adicionarInimigo(new Melee("Espadachim", 55, 16, 0.0));
        s3.adicionarInimigo(new Sniper("Atirador Elite", 28, 14, 0.0));
        s3.adicionarInimigo(new Voador("Morcego Alfa", 42, 9, 0.0));
        s3.adicionarItem(cura(30));
        s3.adicionarItem(energia(20));
        s3.adicionarItem(new PistolaDuplaDePlasma("pistola", "Pistola Dupla de Plasma", "Dois tiros", 9, 0.0));

        s4.adicionarInimigo(new Voador("Morcego Alfa", 45, 9, 0.0));
        s4.adicionarInimigo(new Sniper("Atirador Elite", 28, 14, 0.0));
        s4.adicionarInimigo(new Melee("Espadachim Brutal", 65, 20, 0.0));
        s4.adicionarInimigo(new Voador("Morcego Alfa", 45, 9, 0.0));
        s4.adicionarInimigo(new Sniper("Atirador Elite", 30, 15, 0.0));
        s4.adicionarInimigo(new Melee("Espadachim Brutal", 70, 22, 0.0));
        s4.adicionarItem(cura(30));
        s4.adicionarItem(energia(20));
        s4.adicionarItem(new RifleSniper("rifle", "Rifle Sniper", "Atravessa até 3 alvos", 30, 0.0, 3));

        //O chefe começa como NPC; vira o Chefe depois da conversa
        s5.adicionarNpc(new NpcChefe("NEXUS", List.of(
                "Então você chegou até aqui... Eu sou NEXUS.",
                "Cada sala que você atravessou foi um teste meu.",
                "Os espinhos e as prensas do caminho obedecem a mim.",
                "Chega de conversa. Vamos ver se você sobrevive!"),
                new Chefe("NEXUS", 450, 12)));
        s5.adicionarItem(new ModuloCura("modulo_cura", "Módulo de Cura", "Recupera 40 de vida", Categoria.CONSUMIVEL, 40));
        s5.adicionarItem(new ModuloEnergia("modulo_energia", "Módulo de Energia", "Recupera 25 de energia", Categoria.CONSUMIVEL, 25));
        return mapa;
    }

    private static ModuloCura cura(int quantidade) {
        return new ModuloCura("modulo_cura", "Módulo de Cura", "Recupera " + quantidade + " de vida", Categoria.CONSUMIVEL, quantidade);
    }

    private static ModuloEnergia energia(int quantidade) {
        return new ModuloEnergia("modulo_energia", "Módulo de Energia", "Recupera " + quantidade + " de energia", Categoria.CONSUMIVEL, quantidade);
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

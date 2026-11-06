package com.jogo;
//Package diz q uma classe pertence a tal grupo lógico
//Namespace: Evita colisão de nome entre classe de mesmo nome
//Organização: agrupa classes relacionadas -lincoln

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
import com.jogo.excecoes.SalaNaoEncontradaException;
import com.jogo.habilidades.Apara;
import com.jogo.habilidades.BolaDeFogo;
import com.jogo.habilidades.GolpeDeEnergia;
import com.jogo.inventario.Categoria;
import com.jogo.inventario.ModuloCura;
import com.jogo.mundo.Mapa;
import com.jogo.mundo.Sala;
import com.jogo.personagens.Inimigo;
import com.jogo.personagens.Jogador;
import com.jogo.personagens.Melee;
import com.jogo.personagens.Personagem;
import com.jogo.personagens.Sniper;
import com.jogo.personagens.Voador;
import com.jogo.visao.TelaConsole;

import java.util.List;

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
    }
}

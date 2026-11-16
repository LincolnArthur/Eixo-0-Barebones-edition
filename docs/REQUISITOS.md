# Requisitos — Eixo 0 (versão barebones)

Versão em Java puro (console) do Eixo 0. Serve pra modelar as regras do jogo sem engine: personagens, combate, armas, habilidades, inventário e mapa. A versão jogável, com gráficos, é o projeto FXGL, que reaproveita esse modelo.

Status: ✅ feito e testado · 🟡 modelado, mas sem efeito sem engine.

## Requisitos funcionais

| # | Requisito | Onde está | Teste | Status |
|---|---|---|---|---|
| RF01 | Todo personagem tem nome, vida atual e vida máxima; toma dano até zero e morre com vida 0 | `personagens.Personagem` | `PersonagemTest` | ✅ |
| RF02 | O jogador pode ser curado sem passar da vida máxima e sem curar quem já morreu | `Personagem.curar` | `PersonagemTest` | ✅ |
| RF03 | O jogador tem energia, gasta pelas habilidades. Até o nível 6 ela só volta acertando inimigos (+3 por acerto) ou com Módulo de Energia; a partir do nível 6 também recarrega 1 por turno | `personagens.Jogador`, `controle.Batalha` | `JogadorTest`, `RegrasNovasTest`, `BatalhaIntegracaoTest` | ✅ |
| RF04 | O jogador ganha XP e sobe de nível (100, 150, 200... XP; vários de uma vez, se der). Cada nível dá +20 de vida máxima e vida cheia; o nível 2 libera o Raio de Gelo e o 3 o Buraco Negro | `personagens.Nivel`, `Jogador.ganharExperiencia` | `NivelTest`, `JogadorTest`, `RegrasNovasTest` | ✅ |
| RF05 | Existem três tipos de inimigo (Melee, Sniper, Voador), cada um com a própria arma e o próprio XP (20, 35, 25) | `personagens.Inimigo` e filhas | `InimigoTest` | ✅ |
| RF06 | Ao derrotar um inimigo, o jogador recebe o XP dele | `controle.Batalha` | `BatalhaIntegracaoTest` | ✅ |
| RF07 | O jogador ataca com a arma equipada; cada arma calcula o dano do seu jeito, com chance de crítico | `combate.Arma` e filhas | `ArmaTest`, `ArmasEspeciaisTest` | ✅ |
| RF08 | Armas especiais: Shotgun (5 balins), Pistola Dupla (2 tiros), Marreta (bônus contra Sniper), Adaga Dupla (combo no 5º golpe), Sabre (bônus a cada 3 golpes) | `combate` | `ArmasEspeciaisTest` | ✅ |
| RF09 | O tiro do Rifle Sniper atravessa até N inimigos vivos | `combate.RifleSniper` (`Perfurante`), `Projetil` | `ProjetilTest`, `BatalhaIntegracaoTest` | ✅ |
| RF10 | O jogador usa habilidades com custo de energia e tempo de espera (cooldown) | `habilidades.Habilidade` | `HabilidadeTest` | ✅ |
| RF11 | Golpe de Energia / Bola de Fogo causa dano no alvo | `GolpeDeEnergia`, `BolaDeFogo` | `HabilidadeTest` | ✅ |
| RF12 | Apara, Dash e Pulo Duplo existem como habilidades (gastam energia e entram em cooldown) | `Apara`, `Dash`, `PuloDuplo` | `HabilidadeTest` | 🟡 o efeito depende de posição/tempo real (feito no FXGL) |
| RF13 | O inventário guarda armas e consumíveis; consumíveis iguais empilham, armas não | `inventario.Inventario`, `Categoria` | `InventarioTest` | ✅ |
| RF14 | O jogador equipa uma arma do inventário pelo id | `Jogador.equiparArma` | `JogadorTest` | ✅ |
| RF15 | O jogador usa um consumível (Módulo de Cura/Energia), que sai do inventário | `Jogador.usarItem`, `ModuloCura`, `ModuloEnergia` | `JogadorTest`, `ItemTest` | ✅ |
| RF16 | O mapa tem salas ligadas por direções e uma sala inicial; a campanha da demonstração tem as 5 salas do jogo do FXGL, com os mesmos inimigos e itens | `mundo.Mapa`, `mundo.Sala`, `Jogo.montarMapaEixo0` | `MapaSalaTest`, `JogoIntegracaoTest` | ✅ |
| RF17 | O resultado do combate e o estado dos personagens aparecem no console | `visao.TelaConsole` | `TelaConsoleTest`, `JogoIntegracaoTest` | ✅ |
| RF18 | Raio de Gelo (nível 2): dano 8 e congela o inimigo por 3 turnos; congelado perde os turnos | `habilidades.RaioDeGelo`, `Inimigo.congelar`, `Batalha.turnoDosInimigos` | `RegrasNovasTest`, `BatalhaIntegracaoTest` | ✅ |
| RF19 | Buraco Negro (nível 3): custa a energia inteira e derrota todos os inimigos da sala | `habilidades.BuracoNegro`, `Batalha.usarBuracoNegro` | `RegrasNovasTest`, `BatalhaIntegracaoTest` | ✅ |
| RF20 | Dificuldade Fácil/Normal/Difícil multiplica o dano que o jogador recebe (0,5x / 1x / 1,5x, nunca menos de 1) | `personagens.Dificuldade`, `Jogador.tomarDano` | `RegrasNovasTest` | ✅ |
| RF21 | NPCs invulneráveis conversam (as falas giram); o Informante dá dicas e fica só na primeira sala; a Curandeira fica só na penúltima, tem 3 curas e cada cura custa 20 de energia | `personagens.Npc` e filhas, `mundo.NpcsDasSalas`, `controle.Dialogo` | `NpcTest`, `RegrasNovasTest` | ✅ |
| RF22 | Num terminal, Enter passa as falas da conversa | `TelaConsole.esperarEnter`, `Dialogo.conversarAteOFim` | (manual) | ✅ |
| RF23 | O chefe NEXUS começa como NPC; depois da última fala vira o chefe e a luta começa | `personagens.NpcChefe`, `Chefe` | `ChefeTest`, `JogoIntegracaoTest` | ✅ |
| RF24 | O chefe avisa num turno e ataca no seguinte, na rotação Rajada, Investida (dano em dobro), Chuva de dados e Armadilhas; com metade da vida entra na fase 2 (mais tiros, blocos e espinhos, e uma prensa) | `personagens.Chefe`, `AtaqueChefe` | `ChefeTest`, `BatalhaIntegracaoTest` | ✅ |
| RF25 | O ataque Armadilhas usa o sistema de armadilhas: espinhos (e a prensa) nascem acionados em volta do jogador, e quem estiver em cima quando ficarem ativos toma dano | `Chefe.plantarArmadilhas`, pacote `armadilhas` | `ChefeTest` | ✅ |
| RF26 | O chefe resiste às magias: congela só metade do tempo e perde só 25% da vida no Buraco Negro; vale 300 de XP | `Chefe.congelar`, `Chefe.danoDeBuracoNegro` | `ChefeTest` | ✅ |
| RF27 | Armadilhas (espinhos retráteis e bloco que cai) com o ciclo inativa, aviso, ativa e recarga, geradas por semente fixa | pacote `armadilhas` | `TrapSystemTest` | ✅ |
| RF28 | Sandevistan: câmera lenta (os inimigos agem 1 turno a cada 4), com chance de Cyberpsicose | `habilidades.SandevistanMichyle`, `Cyberpsicose` | `SandevistanTest` | ✅ |

## Tratamento de erros

| # | Situação | Exceção | Tratada em | Teste |
|---|---|---|---|---|
| RE01 | Usar ou equipar item que não está no inventário | `ItemNaoEncontradoException` | `Batalha.usarItem` / `equiparArma` (mostra "[!] ..." e segue) | `JogadorTest`, `BatalhaIntegracaoTest` |
| RE02 | Consumir uma arma ou equipar um consumível | `ItemIncompativelException` | `Batalha` | `JogadorTest` |
| RE03 | Sala inicial que não existe no mapa | `SalaNaoEncontradaException` | `Jogo.main` | `MapaSalaTest`, `JogoIntegracaoTest` |
| RE04 | Valores inválidos (nome vazio, vida ≤ 0, dano negativo, crítico fora de 0..1, XP negativo) | `IllegalArgumentException` | não é tratada: é bug de quem chamou | vários `*Test` |

As três primeiras herdam de `JogoException` (checked), então o compilador obriga a tratar.

## Requisitos não funcionais

| # | Requisito | Como foi atendido |
|---|---|---|
| RNF01 | Java 17, build com Maven | `pom.xml` |
| RNF02 | Separação MVC | modelo (`personagens`, `combate`, `habilidades`, `inventario`, `mundo`, `armadilhas`, `excecoes`), visão (`visao.TelaConsole`), controle (`controle.Batalha`, `controle.Dialogo`); `Jogo` só monta as peças |
| RNF03 | Encapsulamento | atributos `private`; vida e energia só mudam por métodos com regra (`tomarDano`, `curar`, `consumirEnergia`) |
| RNF04 | Testes automatizados | 118 testes JUnit 5 (`mvn test`), unidade e integração |
| RNF05 | Cobertura | 83,6% das linhas e 75,4% dos ramos (JaCoCo, `target/site/jacoco/index.html`), medida depois do chefe e das regras novas |
| RNF06 | Código e nomes em português | todo o projeto |
| RNF07 | Saída previsível na demonstração | armas da demo com `chanceCritico` 0 |

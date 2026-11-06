# Requisitos — Eixo 0 (versão barebones)

Versão em Java puro (console) do Eixo 0. Serve pra modelar as regras do jogo sem engine: personagens, combate, armas, habilidades, inventário e mapa. A versão jogável, com gráficos, é o projeto FXGL, que reaproveita esse modelo.

Status: ✅ feito e testado · 🟡 modelado, mas sem efeito sem engine.

## Requisitos funcionais

| # | Requisito | Onde está | Teste | Status |
|---|---|---|---|---|
| RF01 | Todo personagem tem nome, vida atual e vida máxima; toma dano até zero e morre com vida 0 | `personagens.Personagem` | `PersonagemTest` | ✅ |
| RF02 | O jogador pode ser curado sem passar da vida máxima e sem curar quem já morreu | `Personagem.curar` | `PersonagemTest` | ✅ |
| RF03 | O jogador tem energia, que é gasta pelas habilidades e recuperada sem passar da máxima | `personagens.Jogador` | `JogadorTest` | ✅ |
| RF04 | O jogador ganha XP e sobe de nível (vários de uma vez, se o XP der) | `personagens.Nivel`, `Jogador.ganharExperiencia` | `NivelTest`, `JogadorTest` | ✅ |
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
| RF16 | O mapa tem salas ligadas por direções e uma sala inicial | `mundo.Mapa`, `mundo.Sala` | `MapaSalaTest` | ✅ |
| RF17 | O resultado do combate e o estado dos personagens aparecem no console | `visao.TelaConsole` | `TelaConsoleTest`, `JogoIntegracaoTest` | ✅ |

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
| RNF02 | Separação MVC | modelo (`personagens`, `combate`, `habilidades`, `inventario`, `mundo`, `excecoes`), visão (`visao.TelaConsole`), controle (`controle.Batalha`); `Jogo` só monta as peças |
| RNF03 | Encapsulamento | atributos `private`; vida e energia só mudam por métodos com regra (`tomarDano`, `curar`, `consumirEnergia`) |
| RNF04 | Testes automatizados | 82 testes JUnit 5 (`mvn test`), unidade e integração |
| RNF05 | Cobertura | 93% das linhas, 90% dos ramos (JaCoCo, `target/site/jacoco/index.html`) |
| RNF06 | Código e nomes em português | todo o projeto |
| RNF07 | Saída previsível na demonstração | armas da demo com `chanceCritico` 0 |

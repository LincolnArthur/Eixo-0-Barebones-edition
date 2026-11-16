# Eixo-0-Barebones-version
Barebones ver. do jogo Eixo 0

# Conceito
O intuito é criar uma versão pura do jogo já em produção entitulado "Eixo 0"
afim de aprender e aplicar conceitos aprendidos por meio das aulas universitárias.

# Aplicação
O código é aplicado de maneira não demonstrativa, mas apenas como maneira de aprendizado direto.
Por isso, não há necessidade de executar o código, apenas uma explicação altamente explicativa.

# Como rodar

```
mvn clean compile exec:java   # roda a demonstração (classe com.jogo.Jogo)
mvn test                      # roda os 118 testes JUnit 5 e gera a cobertura em target/site/jacoco/index.html
mvn package                   # gera o executável target/Eixo0-Barebones.jar
java -jar target/Eixo0-Barebones.jar
```

# Documentação

- [`docs/REQUISITOS.md`](docs/REQUISITOS.md): requisitos funcionais, tratamento de erros e não funcionais, com a classe e o teste de cada um.
- [`docs/uml/diagrama-classes.svg`](docs/uml/diagrama-classes.svg): diagrama de classes UML, gerado do código (fonte Mermaid em `docs/uml/diagrama-classes.mmd`). Pra regerar: `python3 docs/uml/gerar_uml.py src/main/java/com/jogo docs/uml/diagrama-classes.mmd` (precisa de `pip install javalang tree-sitter tree-sitter-java`) e `mmdc -i docs/uml/diagrama-classes.mmd -o docs/uml/diagrama-classes.svg`.

# Estrutura (MVC)

| Camada | Pacote | Classes |
|---|---|---|
| Modelo | `personagens` | `Personagem`, `Jogador`, `Inimigo` (abstrata), `Melee`, `Sniper`, `Voador`, `Chefe`, `Nivel`, `Npc` (abstrata), `NpcInformante`, `NpcCurandeiro`, `NpcChefe`, enums `AtaqueChefe` e `Dificuldade` |
| Modelo | `combate` | `Arma` (abstrata), `Espada`, `Marreta`, `AdagaDupla`, `Sabre`, `ArmaDeFogo`, `Shotgun`, `PistolaDuplaDePlasma`, `RifleSniper`, `Ataque`, `Projetil`, interfaces `Atacante`, `Atacavel`, `ConcedeExperiencia`, `Perfurante` |
| Modelo | `habilidades` | `Habilidade` (abstrata), `GolpeDeEnergia` (abstrata), `BolaDeFogo`, `RaioDeGelo`, `BuracoNegro`, `Apara`, `Dash`, `PuloDuplo`, `SandevistanMichyle`, `Cyberpsicose` (interface `Efeito`) |
| Modelo | `inventario` | `Inventario`, `Item` (abstrata), `ModuloCura`, `ModuloEnergia`, `Categoria`, interfaces `Inventariavel`, `Consumivel` |
| Modelo | `mundo` | `Mapa`, `Sala`, `NpcsDasSalas` |
| Modelo | `armadilhas` | `Trap` (abstrata), `SpikesRetrateis`, `BlocoQueCai`, `TrapFactory`, `TrapProceduralGenerator`, `Hitbox`, interfaces `DamageTarget`, `DamageSource`, `TrapListener` |
| Modelo | `excecoes` | `JogoException`, `ItemNaoEncontradoException`, `ItemIncompativelException`, `SalaNaoEncontradaException` |
| Visão | `visao` | `TelaConsole`: tudo que é impresso (personagens, barra de vida, ataques, erros) |
| Controle | `controle` | `Batalha`: turnos, XP de quem morre, Sandevistan, e trata as exceções do modelo; `Dialogo`: conversa com os NPCs |

`Jogo` (o `main`) só monta as peças e roda a demonstração. Nenhuma classe do modelo imprime nada: quem mostra é a `TelaConsole`.

# Exceções

As exceções do jogo herdam de `JogoException`, que herda de `Exception` (checked: o compilador obriga a tratar):

- `ItemNaoEncontradoException`: usar ou equipar um id que não está no inventário (`Jogador.usarItem`, `Jogador.equiparArma`).
- `ItemIncompativelException`: consumir uma arma ou equipar um módulo.
- `SalaNaoEncontradaException`: `Mapa.definirSalaInicial` com um id que não existe.

A `Batalha` captura `JogoException` e mostra `[!] mensagem` na tela, e a demonstração continua. Erros de programação (nome vazio, vida ≤ 0, dano negativo, chance de crítico fora de 0..1) lançam `IllegalArgumentException` nos construtores.

# Testes

118 testes JUnit 5, todos passando. Cobertura do JaCoCo: 83,6% das linhas e 75,4% dos ramos.

- Unitários, um por classe do modelo, com casos de sucesso e de falha (`PersonagemTest`, `JogadorTest`, `InimigoTest`, `NivelTest`, `ArmaTest`, `ArmasEspeciaisTest`, `AtaqueTest`, `ProjetilTest`, `HabilidadeTest`, `InventarioTest`, `ItemTest`, `MapaSalaTest`, `TelaConsoleTest`, `SandevistanTest`, `NpcTest`, `TrapSystemTest`, `ChefeTest`, `RegrasNovasTest`).
- Exceções: `assertThrows` pra cada exceção própria e pras validações dos construtores.
- Integração: `BatalhaIntegracaoTest` (jogador, inimigos, armas, habilidades, inventário e tela juntos) e `JogoIntegracaoTest` (roda o `main` inteiro e confere a saída).

# Mudanças recentes

Trazido do jogo do FXGL, pra as duas versões terem as mesmas regras:
- **Campanha das 5 salas** no fim da demonstração (`Jogo.demonstrarCampanha`): mesmos inimigos (nome, vida, dano) e itens dos JSON do FXGL, até a luta com o chefe.
- **Chefe NEXUS**: começa como NPC (`NpcChefe`); depois da última fala, `despertar()` devolve o `Chefe`. No console ele avisa num turno e ataca no seguinte, na rotação `AtaqueChefe` (Rajada, Investida, Chuva de dados, Armadilhas). Fase 2 com metade da vida. O ataque Armadilhas cria espinhos (e a prensa, na fase 2) com o pacote `armadilhas` e roda o ciclo deles. Resiste às magias (gelo pela metade, Buraco Negro só 25%) e vale 300 de XP.
- **Raio de Gelo** (nível 2: congela por 3 turnos, congelado perde o turno) e **Buraco Negro** (nível 3: custa a energia inteira, derrota todos os inimigos). `Habilidade.custoPara()` permite custo variável.
- **Nível**: 100, 150, 200... de XP (igual ao FXGL); cada nível dá +20 de vida máxima e vida cheia.
- **Energia**: até o nível 6 só volta acertando inimigos (+3 por acerto); a partir do 6, também 1 por turno. O cooldown das habilidades anda 1 s por turno.
- **NPCs**: Informante só na primeira sala, Curandeira só na penúltima (`mundo.NpcsDasSalas`), com 3 curas de 20 de energia cada. Num terminal, Enter passa as falas.
- **Dificuldade** (`Dificuldade`: Fácil, Normal, Difícil) multiplica o dano recebido.

- Sandevistan no console: ligada, os inimigos só agem 1 turno a cada 4 (tempo a 25%); cada turno conta 1 s de energia (20/s) e de Cyberpsicose. Desliga sozinha sem energia e fica 30 s em recarga.
- NPCs amigáveis (`Npc`, `NpcInformante`, `NpcCurandeiro`) e o controle `Dialogo`.
- Armadilhas (`armadilhas`), com os testes rodando no JUnit.

- Separação MVC: a impressão saiu do modelo (`Sala.descrever()` virou `TelaConsole.mostrarSala()`), e a lógica de turno foi pra `Batalha`.
- Exceções próprias no lugar dos `return false` de `usarItem`/`equiparArma`.
- Cada inimigo concede o próprio XP (Melee 20, Voador 25, Sniper 35) por `getExperienciaConcedida()` (da interface `ConcedeExperiencia`), implementado em cada filha.
- `Nivel` sobe vários níveis de uma vez se o XP der, e recusa XP negativo.
- `Sabre`: o construtor recebia os argumentos na ordem errada; corrigido.
- `Inventario.listarDoTipo(Class<T>)`: método genérico que devolve `List<T>` sem cast.

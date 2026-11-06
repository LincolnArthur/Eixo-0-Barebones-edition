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
mvn test                      # roda os 82 testes JUnit 5 e gera a cobertura em target/site/jacoco/index.html
mvn package                   # gera o executável target/Eixo0-Barebones.jar
java -jar target/Eixo0-Barebones.jar
```

# Documentação

- [`docs/REQUISITOS.md`](docs/REQUISITOS.md): requisitos funcionais, tratamento de erros e não funcionais, com a classe e o teste de cada um.
- [`docs/uml/diagrama-classes.svg`](docs/uml/diagrama-classes.svg): diagrama de classes UML, gerado do código (fonte Mermaid em `docs/uml/diagrama-classes.mmd`).

# Estrutura (MVC)

| Camada | Pacote | Classes |
|---|---|---|
| Modelo | `personagens` | `Personagem`, `Jogador`, `Inimigo` (abstrata), `Melee`, `Sniper`, `Voador`, `Nivel` |
| Modelo | `combate` | `Arma` (abstrata), `Espada`, `Marreta`, `AdagaDupla`, `Sabre`, `ArmaDeFogo`, `Shotgun`, `PistolaDuplaDePlasma`, `RifleSniper`, `Ataque`, `Projetil`, interfaces `Atacante`, `Atacavel`, `ConcedeExperiencia`, `Perfurante` |
| Modelo | `habilidades` | `Habilidade` (abstrata), `GolpeDeEnergia` (abstrata), `BolaDeFogo`, `Apara`, `Dash`, `PuloDuplo` |
| Modelo | `inventario` | `Inventario`, `Item` (abstrata), `ModuloCura`, `ModuloEnergia`, `Categoria`, interfaces `Inventariavel`, `Consumivel` |
| Modelo | `mundo` | `Mapa`, `Sala` |
| Modelo | `excecoes` | `JogoException`, `ItemNaoEncontradoException`, `ItemIncompativelException`, `SalaNaoEncontradaException` |
| Visão | `visao` | `TelaConsole`: tudo que é impresso (personagens, barra de vida, ataques, erros) |
| Controle | `controle` | `Batalha`: turnos, XP de quem morre, e trata as exceções do modelo |

`Jogo` (o `main`) só monta as peças e roda a demonstração. Nenhuma classe do modelo imprime nada: quem mostra é a `TelaConsole`.

# Exceções

As exceções do jogo herdam de `JogoException`, que herda de `Exception` (checked: o compilador obriga a tratar):

- `ItemNaoEncontradoException`: usar ou equipar um id que não está no inventário (`Jogador.usarItem`, `Jogador.equiparArma`).
- `ItemIncompativelException`: consumir uma arma ou equipar um módulo.
- `SalaNaoEncontradaException`: `Mapa.definirSalaInicial` com um id que não existe.

A `Batalha` captura `JogoException` e mostra `[!] mensagem` na tela, e a demonstração continua. Erros de programação (nome vazio, vida ≤ 0, dano negativo, chance de crítico fora de 0..1) lançam `IllegalArgumentException` nos construtores.

# Testes

82 testes JUnit 5, todos passando, com 93% de cobertura de linhas e 90% de ramos (JaCoCo):

- Unitários, um por classe do modelo, com casos de sucesso e de falha (`PersonagemTest`, `JogadorTest`, `InimigoTest`, `NivelTest`, `ArmaTest`, `ArmasEspeciaisTest`, `AtaqueTest`, `ProjetilTest`, `HabilidadeTest`, `InventarioTest`, `ItemTest`, `MapaSalaTest`, `TelaConsoleTest`).
- Exceções: `assertThrows` pra cada exceção própria e pras validações dos construtores.
- Integração: `BatalhaIntegracaoTest` (jogador, inimigos, armas, habilidades, inventário e tela juntos) e `JogoIntegracaoTest` (roda o `main` inteiro e confere a saída).

# Mudanças recentes

- Separação MVC: a impressão saiu do modelo (`Sala.descrever()` virou `TelaConsole.mostrarSala()`), e a lógica de turno foi pra `Batalha`.
- Exceções próprias no lugar dos `return false` de `usarItem`/`equiparArma`.
- Cada inimigo concede o próprio XP (Melee 20, Voador 25, Sniper 35) por `getExperienciaConcedida()` (da interface `ConcedeExperiencia`), implementado em cada filha.
- `Nivel` sobe vários níveis de uma vez se o XP der, e recusa XP negativo.
- `Sabre`: o construtor recebia os argumentos na ordem errada; corrigido.
- `Inventario.listarDoTipo(Class<T>)`: método genérico que devolve `List<T>` sem cast.

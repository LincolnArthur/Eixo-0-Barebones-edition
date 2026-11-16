package com.jogo.personagens;

import com.jogo.habilidades.BuracoNegro;
import com.jogo.habilidades.RaioDeGelo;
import com.jogo.combate.Arma;
import com.jogo.combate.Atacante;
import com.jogo.combate.Ataque;
import com.jogo.excecoes.ItemIncompativelException;
import com.jogo.excecoes.ItemNaoEncontradoException;
import com.jogo.inventario.Consumivel;
import com.jogo.inventario.Inventariavel;
import com.jogo.inventario.Inventario;

public class Jogador extends Personagem implements Atacante {
    //Implementa o metodo de ataque por meio
    //da classe abstrata Arma -lincoln
    private Arma arma;
    private int energiaAtual;
    private int energiaMaxima;
    private Nivel nivel;
    //private List<Habilidade> habilidades;
    
    private final Inventario inventario; //cada jogador tem seu inventario

    //Magias liberadas por nível (nascem não aprendidas; subir de nível
    //libera), iguais às do FXGL. Gelo: 20 de energia, dano 8, congela 3
    //turnos, espera 3. Buraco Negro: toda a energia, espera 20.
    public static final int NIVEL_GELO = 2;
    public static final int NIVEL_BURACO_NEGRO = 3;
    private final RaioDeGelo gelo = new RaioDeGelo(false, 3.0, 20, 8, 3.0);
    private final BuracoNegro buracoNegro = new BuracoNegro(false, 20.0);

    //Energia: até o nível 6 ela NÃO recarrega sozinha; só volta acertando
    //inimigos (ENERGIA_POR_ACERTO por acerto) ou com Módulo de Energia. A
    //partir do nível 6 também recarrega RECARGA_POR_TURNO por turno.
    public static final int NIVEL_RECARGA_AUTOMATICA = 6;
    public static final int ENERGIA_POR_ACERTO = 3;
    public static final int RECARGA_POR_TURNO = 1;
    //Ao subir de nível: +20 de vida máxima e vida cheia
    public static final int VIDA_POR_NIVEL = 20;

    private Dificuldade dificuldade = Dificuldade.NORMAL;

    public Jogador(String nome, int vidaMaxima, Arma arma, int energiaMaxima) {
        super(nome, vidaMaxima);

        this.energiaAtual= energiaMaxima; //Criei 'energia' para funcionar como a mana -Raioni
        this.energiaMaxima = energiaMaxima; 
        this.arma = arma;
        this.nivel = new Nivel(); //Nivel n tava sendo inicializado antes ai ficava NULL

        this.inventario= new Inventario(); //Vou criar minha classe Inventario que guarda os itens - raioni
        this.inventario.adicionar(arma); //COloquei minha arma inicial no inventario - raioni
    }
    

    public int getEnergiaAtual(){ //manda a energia atual do meu personagem -Raioni
        return this.energiaAtual;
    }
    //Substitui o antigo setEnergiaAtual: mesma ideia do curar() do
    //Personagem, o Jogador garante que a energia não passa da máxima.
    public void recuperarEnergia(int quantidade){
        if(quantidade <= 0){
            return;
        }
        this.energiaAtual = Math.min(energiaMaxima, energiaAtual + quantidade);
    }
    public boolean energiaSuficiente(int custoEnergia){ // retorna se tenho energia suficiente pra aquilo -Raioni
        if(this.energiaAtual >= custoEnergia){
            return true;
        }else{
            return false;
        }
    }
    public void consumirEnergia(int custoEnergia){ //Calcula a energia restante apos usar uma habilidade -Raioni
        if(energiaAtual <= 0){
            return;
        }
        this.energiaAtual= Math.max(0,energiaAtual - custoEnergia);
    }

    //Devolve true se subiu de nível (um ou mais). A cada nível novo:
    //+20 de vida máxima, vida cheia e libera as magias do nível
    public boolean ganharExperiencia(int quantidade) {
        int nivelAntes = nivel.getNivelAtual();
        boolean subiuNivel = nivel.ganharExperiencia(quantidade);
        if (subiuNivel) {
            aumentarVidaMaxima(VIDA_POR_NIVEL * (nivel.getNivelAtual() - nivelAntes));
            curar(getVidaMaxima());
            liberarMagias();
        }
        return subiuNivel;
    }

    private void liberarMagias() {
        if (nivel.getNivelAtual() >= NIVEL_GELO) {
            gelo.setAprendida(true);
        }
        if (nivel.getNivelAtual() >= NIVEL_BURACO_NEGRO) {
            buracoNegro.setAprendida(true);
        }
    }

    //=== Energia por acerto e recarga a partir do nível 6 ===

    //Chamado pelo controle quando um ataque do jogador acerta um inimigo
    public void registrarAcerto() {
        recuperarEnergia(ENERGIA_POR_ACERTO);
    }

    public boolean recargaAutomaticaLiberada() {
        return nivel.getNivelAtual() >= NIVEL_RECARGA_AUTOMATICA;
    }

    //Chamado pelo controle a cada turno; só faz algo a partir do nível 6
    public void recarregarEnergiaPorTurno() {
        if (recargaAutomaticaLiberada()) {
            recuperarEnergia(RECARGA_POR_TURNO);
        }
    }

    //=== Dificuldade ===

    //Sobrescreve o tomarDano() do Personagem: aplica a dificuldade e
    //deixa o resto (invencibilidade, vida não negativa) com a mãe
    @Override
    public void tomarDano(int quantidade) {
        super.tomarDano(dificuldade.aplicar(quantidade));
    }

    public Dificuldade getDificuldade() {
        return dificuldade;
    }

    public void setDificuldade(Dificuldade dificuldade) {
        if (dificuldade == null) {
            throw new IllegalArgumentException("Dificuldade não pode ser nula");
        }
        this.dificuldade = dificuldade;
    }

    public RaioDeGelo getGelo() {
        return gelo;
    }

    public BuracoNegro getBuracoNegro() {
        return buracoNegro;
    }

    public int getEnergiaMaxima() {
        return this.energiaMaxima;
    }

    //Causa dano por meio da arma, não de um danoBase definido
    @Override  
    public void iniciarAtaque(Personagem vitima) {
        Ataque ataque = new Ataque(arma.causarDano(vitima));
        ataque.aplicarEm(vitima);
    }

    //Antes devolvia false quando dava errado, e quem chamava não sabia
    //o motivo. Agora lança uma exceção diferente pra cada problema: o
    //id não existe, ou existe mas não é uma arma
    public void equiparArma(String idArma) throws ItemNaoEncontradoException, ItemIncompativelException {
        Inventariavel objeto = inventario.buscar(idArma);
        if (objeto == null) {
            throw new ItemNaoEncontradoException(idArma);
        }
        if (!(objeto instanceof Arma armaEncontrada)) {
            throw new ItemIncompativelException(idArma, "equipar como arma");
        }
        this.arma = armaEncontrada;
    }

    //Usa um consumível do inventário pelo id (ex: Módulo de Cura) e
    //tira uma unidade dele. Mesmo esquema de exceções do equiparArma()
    public void usarItem(String id) throws ItemNaoEncontradoException, ItemIncompativelException {
        Inventariavel objeto = inventario.buscar(id);
        if (objeto == null) {
            throw new ItemNaoEncontradoException(id);
        }
        if (!(objeto instanceof Consumivel consumivel)) {
            throw new ItemIncompativelException(id, "consumir");
        }
        consumivel.efeito(this);
        inventario.remover(id);
    }

    public boolean coletarItem(Inventariavel item) {
        return inventario.adicionar(item);
    }

    public Arma getArma() {
        return arma;
    }

    public Nivel getNivel() {
        return nivel;
    }

    public Inventario getInventario() {
        return inventario;
    }

}

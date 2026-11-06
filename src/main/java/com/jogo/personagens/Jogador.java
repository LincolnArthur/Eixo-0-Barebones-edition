package com.jogo.personagens;

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

    public void ganharExperiencia(int quantidade) {
        boolean subiuNivel = nivel.ganharExperiencia(quantidade);
        if (subiuNivel) {
            //Logica de habilidade -lincoln
        }
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

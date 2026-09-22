package com.jogo.personagens;
import java.util.HashMap;
import java.util.Map;

public class GerenciadorHabilidades {
    
    //Cria uma "tabela hash" que liga cada habilidade ao seu tempo de espera -Raioni
    private final Map<Habilidade,Double> tempoEspera;

    public GerenciadorHabilidades(){
        this.tempoEspera = new HashMap<>();
    }

        //Atualiza o tempo de espera em cada frame que for tendo -Raioni
    public void atualizar(double dt){
        for(Map.Entry<Habilidade,Double> entrada: tempoEspera.entrySet()){
            double restante = entrada.getValue() - dt;
            entrada.setValue(Math.max(0.0, restante));
        } 
    }
    //Indica se a habilidade está fora do tempo de espera -Raioni
    public boolean estaDisponivel(Habilidade h) {
        return tempoEspera.getOrDefault(h, 0.0) <= 0.0;
    }
    //Indica quanto tempo falta pro tempo de espera acabar e poder usar habilidade -Raioni
    public double tempoRestante(Habilidade h) {
        return tempoEspera.getOrDefault(h, 0.0);
    }


    //Só pode usar se o player possuir a habilidade && se ela está fora do tempo de cooldown -lincoln
    public boolean usar(Habilidade h, Jogador player){
        if(!estaDisponivel(h)){
            return false;
        }
        if(!h.podeUsar(player)){
            return false;
        }
        //Se puder usar, consome energia do player (equivalente ao custo que consta na tabelha hash)
        //executa e começa o cooldown da hab. (q também tá na tabela hash) -lincoln
        player.consumirEnergia(h.getCustoEnergia());
        h.executar(player);
        tempoEspera.put(h,h.getTempoEspera());
        return true;
    }
}

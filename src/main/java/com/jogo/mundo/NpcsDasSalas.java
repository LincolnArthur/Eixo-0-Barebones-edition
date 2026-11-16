package com.jogo.mundo;

import com.jogo.personagens.NpcCurandeiro;
import com.jogo.personagens.NpcInformante;

import java.util.ArrayList;
import java.util.List;

//Um lugar só com a regra de onde ficam os NPCs, igual ao NpcsDasSalas do FXGL:
// - o Informante só na PRIMEIRA sala, dando as dicas do começo;
// - a Curandeira só na PENÚLTIMA sala, antes da arena do chefe.
//O próprio chefe começa como NPC (NpcChefe), mas quem o coloca é quem
//monta a arena, junto com a luta.
public final class NpcsDasSalas {

    private NpcsDasSalas() {
    }

    public static boolean temInformante(int indiceSala) {
        return indiceSala == 0;
    }

    public static boolean temCurandeira(int indiceSala, int totalSalas) {
        return indiceSala == totalSalas - 2;
    }

    //Percorre as salas na ordem em que foram adicionadas ao mapa
    public static void colocarNpcs(Mapa mapa) {
        List<Sala> salas = new ArrayList<>(mapa.getSalas().values());
        for (int i = 0; i < salas.size(); i++) {
            if (temInformante(i)) {
                salas.get(i).adicionarNpc(new NpcInformante("Informante", List.of(
                        "Olá! Há armas e módulos espalhados pelas salas.",
                        "Acertar inimigos devolve energia. Sozinha, ela só recarrega a partir do nível 6.",
                        "Cuidado com os espinhos e as prensas do caminho.",
                        "Antes do chefe há uma curandeira. Aperte Enter pra passar as falas.")));
            }
            if (temCurandeira(i, salas.size())) {
                salas.get(i).adicionarNpc(new NpcCurandeiro("Curandeira", List.of(
                        "Fique perto que eu cuido dos seus ferimentos.",
                        "Mas cada cura custa " + NpcCurandeiro.CUSTO_ENERGIA_PADRAO + " da sua energia.",
                        "O NEXUS espera depois daquela porta. Tenho " + NpcCurandeiro.CURAS_PADRAO + " curas, use bem."),
                        NpcCurandeiro.CURAS_PADRAO));
            }
        }
    }
}

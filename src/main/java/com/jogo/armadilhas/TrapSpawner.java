package com.jogo.armadilhas;

/**
 * Alias especializado para {@link TrapProceduralGenerator}.
 */
public class TrapSpawner extends TrapProceduralGenerator {

    public TrapSpawner() {
        super();
    }

    public TrapSpawner(long customSeed) {
        super(customSeed);
    }

    public TrapSpawner(boolean useRandomSeed) {
        super(useRandomSeed);
    }

    public TrapSpawner(TrapGenerationConfig config) {
        super(config);
    }
}

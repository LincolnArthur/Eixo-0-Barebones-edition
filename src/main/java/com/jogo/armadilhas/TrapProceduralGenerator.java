package com.jogo.armadilhas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Gerador Procedural de Armadilhas ({@code TrapProceduralGenerator}) — Lógica Pura de Domínio.
 *
 * Totalmente desacoplado de motores gráficos, varre os limites geométricos das plataformas/tetos
 * ({@link PlatformBounds}) e posiciona armadilhas automaticamente respeitando:
 * <ol>
 *   <li><b>Ancoragem Física (Snap-to-Grid/Surface):</b>
 *     <ul>
 *       <li>{@link SpikesRetrateis}: Gerados apoiados exatamente sobre a superfície superior
 *           de plataformas/chãos sólidos ({@code y = plat.y - altura}), com espaço vazio acima.</li>
 *       <li>{@link BlocoQueCai}: Gerados encostados na face inferior de tetos/plataformas altas
 *           ({@code y = plat.y + plat.altura}), priorizando o teto superior ({@code y = 0}) com
 *           toda a altura vertical livre até o piso principal.</li>
 *     </ul>
 *   </li>
 *   <li><b>Regras de Fair Play:</b>
 *     <ul>
 *       <li><b>Zona Segura (Safe Zone):</b> Área livre de armadilhas ao redor do ponto de spawn do jogador.</li>
 *       <li><b>Distância Mínima:</b> Espaçamento mínimo entre armadilhas para nunca bloquear passagens inteiras.</li>
 *       <li><b>Seed Fixa Padrão ({@link #DEFAULT_SEED}):</b> Garante layouts previsíveis e consistentes,
 *           mantendo sobrecargas para seeds customizadas ou aleatórias.</li>
 *     </ul>
 *   </li>
 * </ol>
 */
public class TrapProceduralGenerator {

    public static final long DEFAULT_SEED = 12345L;

    /**
     * Representa os limites geométricos de uma plataforma ou bloco sólido do cenário.
     */
    public static class PlatformBounds {
        private final double x;
        private final double y;
        private final double width;
        private final double height;

        public PlatformBounds(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public double getX() { return x; }
        public double getY() { return y; }
        public double getWidth() { return width; }
        public double getHeight() { return height; }
        public double getMaxX() { return x + width; }
        public double getMaxY() { return y + height; }

        public boolean intersects(double rx, double ry, double rw, double rh) {
            return this.x < (rx + rw)
                    && (this.x + this.width) > rx
                    && this.y < (ry + rh)
                    && (this.y + this.height) > ry;
        }
    }

    /**
     * Descreve uma armadilha posicionada pelo gerador procedural.
     */
    public static class TrapPlacement {
        private final String spawnType;
        private final Trap trap;
        private final double x;
        private final double y;
        private final double width;
        private final double height;
        private final double fallDistance;
        private final PlatformBounds anchorPlatform;

        public TrapPlacement(String spawnType, Trap trap, double x, double y,
                             double width, double height, double fallDistance,
                             PlatformBounds anchorPlatform) {
            this.spawnType = spawnType;
            this.trap = trap;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.fallDistance = fallDistance;
            this.anchorPlatform = anchorPlatform;
        }

        public String getSpawnType() { return spawnType; }
        public Trap getTrap() { return trap; }
        public double getX() { return x; }
        public double getY() { return y; }
        public double getWidth() { return width; }
        public double getHeight() { return height; }
        public double getFallDistance() { return fallDistance; }
        public PlatformBounds getAnchorPlatform() { return anchorPlatform; }
        public double getCenterX() { return x + width / 2.0; }
        public double getCenterY() { return y + height / 2.0; }
    }

    private TrapGenerationConfig config;

    /**
     * Construtor padrão com {@link #DEFAULT_SEED} ({@code 12345L}) para layout consistente.
     */
    public TrapProceduralGenerator() {
        this(new TrapGenerationConfig().withSeed(DEFAULT_SEED));
    }

    /**
     * Construtor sobrecarregado para seed fixa customizada.
     */
    public TrapProceduralGenerator(long customSeed) {
        this(new TrapGenerationConfig().withSeed(customSeed));
    }

    /**
     * Construtor sobrecarregado para escolher entre seed aleatória ({@code true}) ou padrão ({@code false}).
     */
    public TrapProceduralGenerator(boolean useRandomSeed) {
        this(useRandomSeed
                ? new TrapGenerationConfig().withRandomSeed()
                : new TrapGenerationConfig().withSeed(DEFAULT_SEED));
    }

    public TrapProceduralGenerator(TrapGenerationConfig config) {
        this.config = (config != null) ? config : new TrapGenerationConfig().withSeed(DEFAULT_SEED);
    }

    public TrapGenerationConfig getConfig() {
        return config;
    }

    public void setConfig(TrapGenerationConfig config) {
        if (config != null) {
            this.config = config;
        }
    }

    /**
     * Gera diretamente a lista polimórfica de instâncias de {@link Trap} para as plataformas informadas.
     */
    public List<Trap> generateTraps(List<PlatformBounds> platforms,
                                    double levelWidth,
                                    double levelHeight,
                                    double playerX,
                                    double playerY) {
        return generateTraps(platforms, levelWidth, levelHeight, playerX, playerY, config.getSeed());
    }

    /**
     * Método sobrecarregado que gera a lista de {@link Trap} com uma {@code customSeed} específica.
     */
    public List<Trap> generateTraps(List<PlatformBounds> platforms,
                                    double levelWidth,
                                    double levelHeight,
                                    double playerX,
                                    double playerY,
                                    Long customSeed) {
        List<TrapPlacement> placements = generatePlacements(
                platforms, levelWidth, levelHeight, playerX, playerY, customSeed
        );
        List<Trap> traps = new ArrayList<>(placements.size());
        for (TrapPlacement p : placements) {
            traps.add(p.getTrap());
        }
        return traps;
    }

    /**
     * Algoritmo principal de geração procedural utilizando a seed padrão/configurada.
     */
    public List<TrapPlacement> generatePlacements(List<PlatformBounds> platforms,
                                                  double levelWidth,
                                                  double levelHeight,
                                                  double playerX,
                                                  double playerY) {
        return generatePlacements(platforms, levelWidth, levelHeight, playerX, playerY, config.getSeed());
    }

    /**
     * Algoritmo principal de geração procedural sobrecarregado para aceitar uma {@code seedOverride}
     * customizada (ou {@code null} para geração puramente aleatória).
     */
    public List<TrapPlacement> generatePlacements(List<PlatformBounds> platforms,
                                                  double levelWidth,
                                                  double levelHeight,
                                                  double playerX,
                                                  double playerY,
                                                  Long seedOverride) {
        if (platforms == null || platforms.isEmpty() || config.getMaxTraps() <= 0) {
            return Collections.emptyList();
        }

        Random random = (seedOverride != null)
                ? new Random(seedOverride)
                : new Random();

        List<TrapPlacement> ceilingCandidates = findCeilingBlockCandidates(platforms, levelWidth, playerX, playerY);
        List<TrapPlacement> surfaceCandidates = findFloorSpikeCandidates(platforms, levelWidth, playerX, playerY);

        Collections.shuffle(ceilingCandidates, random);
        Collections.shuffle(surfaceCandidates, random);

        List<TrapPlacement> allCandidates = interleaveCandidates(ceilingCandidates, surfaceCandidates);

        List<TrapPlacement> selected = new ArrayList<>();
        List<TrapPlacement> skippedByChance = new ArrayList<>();

        for (TrapPlacement candidate : allCandidates) {
            if (selected.size() >= config.getMaxTraps()) {
                break;
            }
            if (!respectsMinDistance(candidate, selected)) {
                continue;
            }

            if (random.nextDouble() <= config.getSpawnChance()) {
                selected.add(candidate);
            } else {
                skippedByChance.add(candidate);
            }
        }

        if (selected.size() < config.getMinTraps()) {
            for (TrapPlacement fallback : skippedByChance) {
                if (selected.size() >= config.getMinTraps() || selected.size() >= config.getMaxTraps()) {
                    break;
                }
                if (respectsMinDistance(fallback, selected)) {
                    selected.add(fallback);
                }
            }
        }

        return selected;
    }

    private List<TrapPlacement> findCeilingBlockCandidates(List<PlatformBounds> platforms,
                                                           double levelWidth,
                                                           double playerX,
                                                           double playerY) {
        List<TrapPlacement> candidates = new ArrayList<>();
        double blockW = config.getBlockWidth();
        double blockH = config.getBlockHeight();
        double stepX = Math.max(16.0, config.getGridStepX());

        List<PlatformBounds> ceilingPlatforms = new ArrayList<>();
        for (PlatformBounds p : platforms) {
            if (p.getY() <= config.getMaxCeilingTopY() && p.getWidth() >= blockW) {
                ceilingPlatforms.add(p);
            }
        }
        if (ceilingPlatforms.isEmpty()) {
            ceilingPlatforms.addAll(platforms);
        }

        for (PlatformBounds ceiling : ceilingPlatforms) {
            if (ceiling.getWidth() < blockW) {
                continue;
            }

            double anchorY = ceiling.getMaxY();

            List<Double> xPositions = new ArrayList<>();
            double startX = ceiling.getX() + Math.min(config.getPlatformEdgeMargin(), (ceiling.getWidth() - blockW) / 2.0);
            double endX = ceiling.getMaxX() - blockW - Math.min(config.getPlatformEdgeMargin(), (ceiling.getWidth() - blockW) / 2.0);

            for (double x = startX; x <= endX + 0.01; x += stepX) {
                xPositions.add(x);
            }
            if (xPositions.isEmpty()) {
                xPositions.add(ceiling.getX() + (ceiling.getWidth() - blockW) / 2.0);
            }

            for (double x : xPositions) {
                if (!isWithinLevelSafeBounds(x, blockW, levelWidth)) {
                    continue;
                }

                double groundTopY = findMainGroundBelow(platforms, ceiling, x, anchorY, blockW);
                if (groundTopY == Double.MAX_VALUE) {
                    continue;
                }

                double fallDistance = (groundTopY - blockH) - anchorY;
                if (fallDistance < config.getMinFallClearance()) {
                    continue;
                }

                if (!isVerticalCorridorClear(platforms, ceiling, x, anchorY, blockW, groundTopY - anchorY)) {
                    continue;
                }

                if (isInsidePlayerSafeZone(x, anchorY, blockW, groundTopY - anchorY, playerX, playerY)) {
                    continue;
                }

                BlocoQueCai bloco = TrapFactory.createFallingBlock(x, anchorY, blockW, blockH, fallDistance);
                candidates.add(new TrapPlacement(
                        "armadilha_bloco",
                        bloco,
                        x,
                        anchorY,
                        blockW,
                        blockH,
                        fallDistance,
                        ceiling
                ));
            }
        }

        return candidates;
    }

    private List<TrapPlacement> findFloorSpikeCandidates(List<PlatformBounds> platforms,
                                                         double levelWidth,
                                                         double playerX,
                                                         double playerY) {
        List<TrapPlacement> candidates = new ArrayList<>();
        double spikeW = config.getSpikeWidth();
        double spikeH = config.getSpikeHeight();
        double margin = config.getPlatformEdgeMargin();
        double stepX = Math.max(16.0, config.getGridStepX());

        for (PlatformBounds floor : platforms) {
            if (floor.getY() <= config.getMaxCeilingTopY() || (floor.getY() - spikeH) < 0) {
                continue;
            }
            if (floor.getWidth() < spikeW + (margin * 2.0)) {
                continue;
            }

            double anchorY = floor.getY() - spikeH;

            double startX = floor.getX() + margin;
            double endX = floor.getMaxX() - spikeW - margin;

            for (double x = startX; x <= endX + 0.01; x += stepX) {
                if (!isWithinLevelSafeBounds(x, spikeW, levelWidth)) {
                    continue;
                }

                double clearanceHeight = Math.max(spikeH, config.getMinTopClearance());
                if (!isTopClearanceFree(platforms, floor, x, floor.getY() - clearanceHeight, spikeW, clearanceHeight)) {
                    continue;
                }

                if (isInsidePlayerSafeZone(x, anchorY, spikeW, spikeH, playerX, playerY)) {
                    continue;
                }

                SpikesRetrateis spikes = TrapFactory.createSpikes(x, anchorY, spikeW, spikeH);
                candidates.add(new TrapPlacement(
                        "armadilha_espinhos",
                        spikes,
                        x,
                        anchorY,
                        spikeW,
                        spikeH,
                        0.0,
                        floor
                ));
            }
        }

        return candidates;
    }

    private double findMainGroundBelow(List<PlatformBounds> platforms,
                                       PlatformBounds ceiling,
                                       double x,
                                       double startY,
                                       double width) {
        double lowestGroundY = -1.0;
        for (PlatformBounds plat : platforms) {
            if (plat == ceiling) {
                continue;
            }
            if (plat.getY() <= startY) {
                continue;
            }
            boolean coversHorizontally = plat.getX() <= x && plat.getMaxX() >= (x + width);
            if (coversHorizontally && plat.getY() > lowestGroundY) {
                lowestGroundY = plat.getY();
            }
        }
        return lowestGroundY > 0 ? lowestGroundY : Double.MAX_VALUE;
    }

    private boolean isVerticalCorridorClear(List<PlatformBounds> platforms,
                                            PlatformBounds ceiling,
                                            double x,
                                            double startY,
                                            double width,
                                            double corridorHeight) {
        double checkY = startY + 1.0;
        double checkH = Math.max(1.0, corridorHeight - 2.0);
        for (PlatformBounds plat : platforms) {
            if (plat == ceiling) {
                continue;
            }
            if (plat.intersects(x, checkY, width, checkH)) {
                return false;
            }
        }
        return true;
    }

    private boolean isTopClearanceFree(List<PlatformBounds> platforms,
                                       PlatformBounds floor,
                                       double x,
                                       double clearanceY,
                                       double width,
                                       double clearanceHeight) {
        double checkH = Math.max(1.0, clearanceHeight - 1.0);
        for (PlatformBounds plat : platforms) {
            if (plat == floor) {
                continue;
            }
            if (plat.intersects(x, clearanceY, width, checkH)) {
                return false;
            }
        }
        return true;
    }

    private boolean isWithinLevelSafeBounds(double x, double width, double levelWidth) {
        if (levelWidth <= 0) {
            return true;
        }
        double portalMargin = config.getPortalSafeMargin();
        return x >= portalMargin && (x + width) <= (levelWidth - portalMargin);
    }

    private boolean isInsidePlayerSafeZone(double rx,
                                           double ry,
                                           double rw,
                                           double rh,
                                           double playerX,
                                           double playerY) {
        double radius = config.getSafeZoneRadius();
        if (radius <= 0) {
            return false;
        }

        double horizontalDist = 0.0;
        if (playerX < rx) {
            horizontalDist = rx - playerX;
        } else if (playerX > (rx + rw)) {
            horizontalDist = playerX - (rx + rw);
        }
        if (horizontalDist < radius) {
            return true;
        }

        double closestX = Math.max(rx, Math.min(playerX, rx + rw));
        double closestY = Math.max(ry, Math.min(playerY, ry + rh));
        double dist = Math.hypot(playerX - closestX, playerY - closestY);
        return dist < radius;
    }

    private boolean respectsMinDistance(TrapPlacement candidate, List<TrapPlacement> selected) {
        double minDist = config.getMinDistanceBetweenTraps();
        for (TrapPlacement existing : selected) {
            double distCenters = Math.hypot(
                    candidate.getCenterX() - existing.getCenterX(),
                    candidate.getCenterY() - existing.getCenterY()
            );
            if (distCenters < minDist) {
                return false;
            }

            double minHorizontalGap = minDist * 0.55;
            boolean horizontalOverlap = (candidate.getX() - minHorizontalGap) < (existing.getX() + existing.getWidth())
                    && (candidate.getX() + candidate.getWidth() + minHorizontalGap) > existing.getX();
            if (horizontalOverlap) {
                return false;
            }
        }
        return true;
    }

    private List<TrapPlacement> interleaveCandidates(List<TrapPlacement> listA, List<TrapPlacement> listB) {
        List<TrapPlacement> result = new ArrayList<>(listA.size() + listB.size());
        int max = Math.max(listA.size(), listB.size());
        for (int i = 0; i < max; i++) {
            if (i < listA.size()) {
                result.add(listA.get(i));
            }
            if (i < listB.size()) {
                result.add(listB.get(i));
            }
        }
        return result;
    }
}

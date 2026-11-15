package com.jogo.armadilhas;

import com.jogo.mundo.Sala;
import com.jogo.personagens.Personagem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Testes unitários independentes do Sistema Modular de Armadilhas (Trap System)
 * para o repositório Eixo-0-Barebones-edition.
 *
 * 100% Java puro (sem dependências externas de engine ou frameworks), validando:
 * - Pilares de POO (Polimorfismo, Encapsulamento, Herança, Abstração, SOLID)
 * - Ciclo de vida completo (INACTIVE -> TELEGRAPHING -> ACTIVE -> COOLDOWN -> INACTIVE)
 * - SpikesRetrateis com gatilho de proximidade e 2.0s de telegraphing
 * - BlocoQueCai com queda por gravidade, parada exata no solo e dano em 100% da hitbox
 * - TrapFactory e TrapProceduralGenerator (seed fixa 12345L, Safe Zone e ancoragem física)
 * - Invulnerabilidade temporária (I-Frames de 1.0s) e TrapListener (onImpact / onDamageDealt)
 */
public class TrapSystemTest {

    public static class TestDummyTarget implements DamageTarget {
        private double x;
        private double y;
        private final Hitbox hitbox;
        private int currentHealth = 100;
        private double lastKnockbackX = 0;
        private double lastKnockbackY = 0;
        private int hitsReceived = 0;
        private double invulnerabilityTimer = 0.0;

        public TestDummyTarget(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.hitbox = new Hitbox(0, 0, width, height);
        }

        public void update(double deltaTime) {
            if (deltaTime > 0 && invulnerabilityTimer > 0) {
                invulnerabilityTimer = Math.max(0.0, invulnerabilityTimer - deltaTime);
            }
        }

        @Override
        public boolean isInvulnerable() {
            return invulnerabilityTimer > 0;
        }

        @Override
        public void activateInvulnerability(double durationSeconds) {
            this.invulnerabilityTimer = Math.max(this.invulnerabilityTimer, durationSeconds);
        }

        @Override
        public void takeDamage(int damage, DamageSource source) {
            if (isInvulnerable()) {
                return;
            }
            this.currentHealth = Math.max(0, currentHealth - damage);
            this.hitsReceived++;
        }

        @Override
        public void applyKnockback(double forceX, double forceY) {
            this.lastKnockbackX = forceX;
            this.lastKnockbackY = forceY;
        }

        @Override public double getX() { return x; }
        @Override public double getY() { return y; }
        public void setX(double x) { this.x = x; }
        public void setY(double y) { this.y = y; }
        @Override public Hitbox getHitbox() { return hitbox; }
        @Override public boolean isAlive() { return currentHealth > 0; }
        public int getCurrentHealth() { return currentHealth; }
        public int getHitsReceived() { return hitsReceived; }
        public double getLastKnockbackX() { return lastKnockbackX; }
        public double getLastKnockbackY() { return lastKnockbackY; }
    }

    private TestDummyTarget player;

    public void setUp() {
        player = new TestDummyTarget(100, 100, 32, 48);
    }

    public void testHitboxOffsetsAndIntersection() {
        Hitbox hitbox = new Hitbox(5, 5, 40, 40);

        assertEquals(105.0, hitbox.getMinX(100), 0.001);
        assertEquals(145.0, hitbox.getMaxX(100), 0.001);
        assertEquals(105.0, hitbox.getMinY(100), 0.001);
        assertEquals(145.0, hitbox.getMaxY(100), 0.001);

        Hitbox targetHitbox = new Hitbox(0, 0, 2, 40);
        boolean touchesVisual = (100 + 50) > 102;
        boolean touchesRealHitbox = hitbox.intersects(100, 100, targetHitbox, 102, 100);

        assertTrue(touchesVisual, "Visualmente sobreporia");
        assertFalse(touchesRealHitbox, "Hitbox com offset deve perdoar toque de raspão na borda visual!");
    }

    public void testSpikesRetrateisLifecycle() {
        SpikesRetrateis spikes = new SpikesRetrateis(100, 100, 64, 32);

        assertEquals(TrapState.INACTIVE, spikes.getState());
        assertEquals(0.0, spikes.getSpikeExtensionRatio(), 0.001);

        // 1. Sem jogador por perto -> permanece INACTIVE (sem ciclo automático)
        spikes.update(5.0);
        assertEquals(TrapState.INACTIVE, spikes.getState());

        // 2. Jogador pisa na superfície -> TELEGRAPHING (2.0s)
        player.setX(110);
        player.setY(95);
        assertTrue(spikes.checkTrigger(player), "Jogador em cima deve ativar a triggerBox!");
        assertEquals(TrapState.TELEGRAPHING, spikes.getState());
        assertEquals(0.25, spikes.getSpikeExtensionRatio(), 0.01);

        // 3. Durante o aviso: treme e aguarda completar 2.0s
        spikes.update(0.05);
        assertNotEquals(0.0, spikes.getShakeOffsetX() + spikes.getShakeOffsetY());
        spikes.update(1.0);
        assertEquals(TrapState.TELEGRAPHING, spikes.getState());

        // 4. Completa 2.0s -> ACTIVE
        spikes.update(1.0);
        assertEquals(TrapState.ACTIVE, spikes.getState());
        assertEquals(1.0, spikes.getSpikeExtensionRatio(), 0.01);

        // 5. Conclui tempo ativo -> COOLDOWN -> INACTIVE
        spikes.update(2.05);
        assertEquals(TrapState.COOLDOWN, spikes.getState());
        spikes.update(1.55);
        assertEquals(TrapState.INACTIVE, spikes.getState());
        assertEquals(0.0, spikes.getSpikeExtensionRatio(), 0.001);
    }

    public void testSpikesDamageOnlyWhenActive() {
        SpikesRetrateis spikes = new SpikesRetrateis(100, 100, 64, 32);
        player.setX(110);
        player.setY(100);

        assertFalse(spikes.checkCollision(player), "Inativo não deve machucar");
        assertEquals(100, player.getCurrentHealth());

        spikes.onTrigger();
        spikes.update(spikes.getTelegraphDuration() + 0.05);
        assertEquals(TrapState.ACTIVE, spikes.getState());

        assertTrue(spikes.checkCollision(player), "Ativo deve causar dano");
        assertEquals(75, player.getCurrentHealth());
        assertTrue(player.getLastKnockbackY() < 0, "Knockback vertical deve lançar para cima");
    }

    public void testFallingBlockTriggerAndOmnidirectionalDamage() {
        BlocoQueCai block = new BlocoQueCai(200, 50, 64, 64, 300, 0.5, 45);

        // Jogador ao lado não ativa o bloco
        player.setX(150);
        player.setY(150);
        assertFalse(block.checkTrigger(player), "Jogador à esquerda não deve ativar");
        player.setX(270);
        assertFalse(block.checkTrigger(player), "Jogador à direita não deve ativar");

        // Jogador exatamente abaixo ativa o gatilho
        player.setX(210);
        player.setY(200);
        assertTrue(block.checkTrigger(player), "Jogador abaixo deve ativar");
        assertEquals(TrapState.TELEGRAPHING, block.getState());

        block.update(0.3);
        assertEquals(50.0, block.getY(), 0.01);
        block.update(0.25);
        assertEquals(TrapState.ACTIVE, block.getState());

        block.update(0.1);
        assertTrue(block.getY() > 50.0, "Bloco deve cair por gravidade");

        // Hitbox cobre 100% da área do bloco
        assertEquals(64.0, block.getDamageHitbox().getWidth(), 0.001);
        assertEquals(64.0, block.getDamageHitbox().getHeight(), 0.001);

        // Parada exata no solo (y = topoDoChao - alturaDoBloco = 414 - 64 = 350)
        block.alignWithGroundSurface(414.0);
        block.update(1.0);
        assertTrue(block.hasHitGround(), "Bloco deve atingir o solo");
        assertEquals(350.0, block.getY(), 0.01);
        assertEquals(0.0, block.getVelocityY(), 0.001);

        // Contato em todas as faces (esquerda, direita, topo) no solo aplica dano e knockback
        TestDummyTarget dummyLeft = new TestDummyTarget(block.getX() - 32, block.getY() + 8, 32, 48);
        assertTrue(block.checkCollision(dummyLeft), "Face esquerda deve aplicar dano");
        assertTrue(dummyLeft.getLastKnockbackX() < 0, "Deve empurrar para a esquerda");

        TestDummyTarget dummyRight = new TestDummyTarget(block.getX() + block.getWidth(), block.getY() + 8, 32, 48);
        assertTrue(block.checkCollision(dummyRight), "Face direita deve aplicar dano");
        assertTrue(dummyRight.getLastKnockbackX() > 0, "Deve empurrar para a direita");

        TestDummyTarget dummyTop = new TestDummyTarget(block.getX() + 16, block.getY() - 48, 32, 48);
        assertTrue(block.checkCollision(dummyTop), "Face superior deve aplicar dano");
        assertTrue(dummyTop.getLastKnockbackY() < 0, "Deve lançar para cima");
    }

    public void testTrapFactory() {
        Trap spikes = TrapFactory.createSpikes(10, 20, 64, 32);
        assertTrue(spikes instanceof SpikesRetrateis, "Deve instanciar SpikesRetrateis");
        assertEquals(2.0, spikes.getTelegraphDuration(), 0.01);

        Trap block = TrapFactory.createFallingBlock(50, 60, 64, 64, 250);
        assertTrue(block instanceof BlocoQueCai, "Deve instanciar BlocoQueCai");

        Trap fromMap = TrapFactory.createFromTileData("spikes", 0, 0, Map.of("damage", 40));
        assertEquals(40, fromMap.getDamage());
    }

    public void testProceduralTrapGeneratorAnchoringAndFairPlay() {
        List<TrapProceduralGenerator.PlatformBounds> platforms = List.of(
                new TrapProceduralGenerator.PlatformBounds(0, 0, 3000, 40),      // Teto físico superior (y=0..40)
                new TrapProceduralGenerator.PlatformBounds(0, 680, 3000, 40),    // Chão principal (y=680..720)
                new TrapProceduralGenerator.PlatformBounds(300, 560, 180, 20),   // Plataforma na Safe Zone
                new TrapProceduralGenerator.PlatformBounds(1100, 520, 200, 20),  // Plataforma intermediária 1
                new TrapProceduralGenerator.PlatformBounds(1700, 500, 220, 20),  // Plataforma intermediária 2
                new TrapProceduralGenerator.PlatformBounds(2300, 540, 200, 20)   // Plataforma intermediária 3
        );

        double playerX = 400.0;
        double playerY = 420.0;

        TrapProceduralGenerator defaultGen1 = new TrapProceduralGenerator();
        TrapProceduralGenerator defaultGen2 = new TrapProceduralGenerator();
        assertEquals(TrapProceduralGenerator.DEFAULT_SEED, defaultGen1.getConfig().getSeed());

        List<TrapProceduralGenerator.TrapPlacement> placements1 = defaultGen1.generatePlacements(
                platforms, 3000.0, 720.0, playerX, playerY
        );
        List<TrapProceduralGenerator.TrapPlacement> placements2 = defaultGen2.generatePlacements(
                platforms, 3000.0, 720.0, playerX, playerY
        );

        assertEquals(placements1.size(), placements2.size());
        assertTrue(placements1.size() >= defaultGen1.getConfig().getMinTraps(), "Deve respeitar minTraps");

        boolean hasSpikes = false;
        boolean hasFallingBlock = false;

        for (int i = 0; i < placements1.size(); i++) {
            TrapProceduralGenerator.TrapPlacement p = placements1.get(i);
            Trap trap = p.getTrap();
            TrapProceduralGenerator.PlatformBounds anchor = p.getAnchorPlatform();

            assertEquals(p.getSpawnType(), placements2.get(i).getSpawnType());
            assertEquals(p.getX(), placements2.get(i).getX(), 0.001);
            assertEquals(p.getY(), placements2.get(i).getY(), 0.001);

            if (trap instanceof SpikesRetrateis) {
                hasSpikes = true;
                assertEquals(anchor.getY() - p.getHeight(), p.getY(), 0.001);
            } else if (trap instanceof BlocoQueCai) {
                hasFallingBlock = true;
                assertEquals(40.0, p.getY(), 0.001);
                assertEquals(680.0, p.getY() + p.getFallDistance() + p.getHeight(), 0.001);
            }

            // Verifica Safe Zone
            double distToPlayer = Math.hypot(p.getCenterX() - playerX, p.getCenterY() - playerY);
            assertTrue(distToPlayer >= defaultGen1.getConfig().getSafeZoneRadius(), "Deve respeitar a Safe Zone");

            // Verifica distância mínima entre armadilhas
            for (int j = i + 1; j < placements1.size(); j++) {
                TrapProceduralGenerator.TrapPlacement other = placements1.get(j);
                double dist = Math.hypot(p.getCenterX() - other.getCenterX(), p.getCenterY() - other.getCenterY());
                assertTrue(dist >= defaultGen1.getConfig().getMinDistanceBetweenTraps(), "Deve respeitar distância mínima");
            }
        }

        assertTrue(hasSpikes, "Deve gerar SpikesRetrateis");
        assertTrue(hasFallingBlock, "Deve gerar BlocoQueCai");
    }

    public void testInvulnerabilityAndSalaIntegration() {
        Sala sala = new Sala("sala_01", "Corredor de Testes", "Sala barebones integrada com armadilhas");
        Personagem heroi = new Personagem("Arthur", 100, 200, 120);
        BlocoQueCai block = new BlocoQueCai(200, 40, 64, 64, 300, 0.5, 30);
        sala.adicionarArmadilha(block);

        List<String> events = new ArrayList<>();
        block.addListener(new TrapListener() {
            @Override
            public void onImpact(Trap trap) {
                events.add("IMPACT");
            }

            @Override
            public void onDamageDealt(Trap trap, DamageTarget target, int damage) {
                events.add("DAMAGE:" + damage);
            }
        });

        block.onTrigger();
        sala.atualizarArmadilhas(0.30, heroi);
        sala.atualizarArmadilhas(0.21, heroi); // Entra em ACTIVE, acerta o herói e ativa 1.0s de I-Frames
        assertEquals(70, heroi.getVidaAtual());
        assertTrue(heroi.isInvulnerable(), "Herói deve ganhar 1.0s de I-Frames após o dano");
        assertEquals(1, events.size());

        // Durante os 1.0s de I-Frames, novos updates não causam hits repetidos
        sala.atualizarArmadilhas(0.50, heroi);
        assertEquals(70, heroi.getVidaAtual());
        assertTrue(events.contains("IMPACT"), "Bloco deve notificar onImpact ao tocar o chão");
    }

    // === Asserções Java Puro (Zero Dependências Externas) ===

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Falha em assertTrue: " + message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError("Falha em assertFalse: " + message);
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("Esperado: <" + expected + "> mas foi: <" + actual + ">");
        }
    }

    private static void assertEquals(double expected, double actual, double delta) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError("Esperado: <" + expected + "> mas foi: <" + actual + "> (delta=" + delta + ")");
        }
    }

    private static void assertNotEquals(double unexpected, double actual) {
        if (Math.abs(unexpected - actual) <= 1e-9) {
            throw new AssertionError("Valor não deveria ser igual a: <" + unexpected + ">");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== EXECUTANDO SUITE DE TESTES DO TRAP SYSTEM (BAREBONES) ===");
        TrapSystemTest test = new TrapSystemTest();

        runTest("testHitboxOffsetsAndIntersection", () -> {
            test.setUp();
            test.testHitboxOffsetsAndIntersection();
        });

        runTest("testSpikesRetrateisLifecycle", () -> {
            test.setUp();
            test.testSpikesRetrateisLifecycle();
        });

        runTest("testSpikesDamageOnlyWhenActive", () -> {
            test.setUp();
            test.testSpikesDamageOnlyWhenActive();
        });

        runTest("testFallingBlockTriggerAndOmnidirectionalDamage", () -> {
            test.setUp();
            test.testFallingBlockTriggerAndOmnidirectionalDamage();
        });

        runTest("testTrapFactory", () -> {
            test.setUp();
            test.testTrapFactory();
        });

        runTest("testProceduralTrapGeneratorAnchoringAndFairPlay", () -> {
            test.setUp();
            test.testProceduralTrapGeneratorAnchoringAndFairPlay();
        });

        runTest("testInvulnerabilityAndSalaIntegration", () -> {
            test.setUp();
            test.testInvulnerabilityAndSalaIntegration();
        });

        System.out.println(">>> TODOS OS 7 TESTES PASSARAM COM SUCESSO! 100% JAVA PURO <<<");
    }

    private static void runTest(String name, Runnable runnable) {
        try {
            runnable.run();
            System.out.println("  [PASSOU] " + name);
        } catch (Throwable t) {
            System.err.println("  [FALHOU] " + name + ": " + t.getMessage());
            t.printStackTrace();
            System.exit(1);
        }
    }
}

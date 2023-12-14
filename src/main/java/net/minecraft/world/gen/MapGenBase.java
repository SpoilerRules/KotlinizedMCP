package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class MapGenBase {
    protected int generationRange = 8;
    protected Random randomGenerator = new Random();
    protected World worldObject;

    public void generate(World worldIn, int centerX, int centerZ, ChunkPrimer chunkPrimerIn) {
        int range = this.generationRange;
        this.worldObject = worldIn;
        this.randomGenerator.setSeed(worldIn.getSeed());
        long seedX = this.randomGenerator.nextLong();
        long seedZ = this.randomGenerator.nextLong();
        long worldSeed = worldIn.getSeed();

        int minX = centerX - range;
        int maxX = centerX + range;
        int minZ = centerZ - range;
        int maxZ = centerZ + range;

        for (int x = minX; x <= maxX; ++x) {
            long xSeed = (long) x * seedX;
            for (int z = minZ; z <= maxZ; ++z) {
                long zSeed = (long) z * seedZ;
                this.randomGenerator.setSeed(xSeed ^ zSeed ^ worldSeed);
                this.recursiveGenerate(worldIn, x, z, centerX, centerZ, chunkPrimerIn);
            }
        }
    }

    protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int centerX, int centerZ, ChunkPrimer chunkPrimerIn) {
    }
}
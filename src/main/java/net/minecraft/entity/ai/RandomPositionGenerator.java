package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vector3D;

public class RandomPositionGenerator
{
    private static Vector3D staticVector = new Vector3D(0.0D, 0.0D, 0.0D);

    public static Vector3D findRandomTarget(EntityCreature entitycreatureIn, int xz, int y)
    {
        return findRandomTargetBlock(entitycreatureIn, xz, y, (Vector3D)null);
    }

    public static Vector3D findRandomTargetBlockTowards(EntityCreature entitycreatureIn, int xz, int y, Vector3D targetVector3D)
    {
        staticVector = targetVector3D.subtract(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ);
        return findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
    }

    public static Vector3D findRandomTargetBlockAwayFrom(EntityCreature entitycreatureIn, int xz, int y, Vector3D targetVector3D)
    {
        staticVector = (new Vector3D(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ)).subtract(targetVector3D);
        return findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
    }

    private static Vector3D findRandomTargetBlock(EntityCreature entitycreatureIn, int xz, int y, Vector3D targetVector3D)
    {
        Random random = entitycreatureIn.getRNG();
        boolean flag = false;
        int i = 0;
        int j = 0;
        int k = 0;
        float f = -99999.0F;
        boolean flag1;

        if (entitycreatureIn.hasHome())
        {
            double d0 = entitycreatureIn.getHomePosition().distanceSq((double)MathHelper.floor_double(entitycreatureIn.posX), (double)MathHelper.floor_double(entitycreatureIn.posY), (double)MathHelper.floor_double(entitycreatureIn.posZ)) + 4.0D;
            double d1 = (double)(entitycreatureIn.getMaximumHomeDistance() + (float)xz);
            flag1 = d0 < d1 * d1;
        }
        else
        {
            flag1 = false;
        }

        for (int j1 = 0; j1 < 10; ++j1)
        {
            int l = random.nextInt(2 * xz + 1) - xz;
            int k1 = random.nextInt(2 * y + 1) - y;
            int i1 = random.nextInt(2 * xz + 1) - xz;

            if (targetVector3D == null || (double)l * targetVector3D.x + (double)i1 * targetVector3D.z >= 0.0D)
            {
                if (entitycreatureIn.hasHome() && xz > 1)
                {
                    BlockPos blockpos = entitycreatureIn.getHomePosition();

                    if (entitycreatureIn.posX > (double)blockpos.getX())
                    {
                        l -= random.nextInt(xz / 2);
                    }
                    else
                    {
                        l += random.nextInt(xz / 2);
                    }

                    if (entitycreatureIn.posZ > (double)blockpos.getZ())
                    {
                        i1 -= random.nextInt(xz / 2);
                    }
                    else
                    {
                        i1 += random.nextInt(xz / 2);
                    }
                }

                l = l + MathHelper.floor_double(entitycreatureIn.posX);
                k1 = k1 + MathHelper.floor_double(entitycreatureIn.posY);
                i1 = i1 + MathHelper.floor_double(entitycreatureIn.posZ);
                BlockPos blockpos1 = new BlockPos(l, k1, i1);

                if (!flag1 || entitycreatureIn.isWithinHomeDistanceFromPosition(blockpos1))
                {
                    float f1 = entitycreatureIn.getBlockPathWeight(blockpos1);

                    if (f1 > f)
                    {
                        f = f1;
                        i = l;
                        j = k1;
                        k = i1;
                        flag = true;
                    }
                }
            }
        }

        if (flag)
        {
            return new Vector3D((double)i, (double)j, (double)k);
        }
        else
        {
            return null;
        }
    }
}

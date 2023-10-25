package net.minecraft.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vector3D;
import net.minecraft.world.World;
import net.minecraft.world.pathfinder.SwimNodeProcessor;

public class PathNavigateSwimmer extends PathNavigate
{
    public PathNavigateSwimmer(EntityLiving entitylivingIn, World worldIn)
    {
        super(entitylivingIn, worldIn);
    }

    protected PathFinder getPathFinder()
    {
        return new PathFinder(new SwimNodeProcessor());
    }

    protected boolean canNavigate()
    {
        return this.isInLiquid();
    }

    protected Vector3D getEntityPosition()
    {
        return new Vector3D(this.theEntity.posX, this.theEntity.posY + (double)this.theEntity.height * 0.5D, this.theEntity.posZ);
    }

    protected void pathFollow()
    {
        Vector3D vector3D = this.getEntityPosition();
        float f = this.theEntity.width * this.theEntity.width;
        int i = 6;

        if (vector3D.squareDistanceTo(this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex())) < (double)f)
        {
            this.currentPath.incrementPathIndex();
        }

        for (int j = Math.min(this.currentPath.getCurrentPathIndex() + i, this.currentPath.getCurrentPathLength() - 1); j > this.currentPath.getCurrentPathIndex(); --j)
        {
            Vector3D vec31D = this.currentPath.getVectorFromIndex(this.theEntity, j);

            if (vec31D.squareDistanceTo(vector3D) <= 36.0D && this.isDirectPathBetweenPoints(vector3D, vec31D, 0, 0, 0))
            {
                this.currentPath.setCurrentPathIndex(j);
                break;
            }
        }

        this.checkForStuck(vector3D);
    }

    protected void removeSunnyPath()
    {
        super.removeSunnyPath();
    }

    protected boolean isDirectPathBetweenPoints(Vector3D posDVec31, Vector3D posDVec32, int sizeX, int sizeY, int sizeZ)
    {
        MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks(posDVec31, new Vector3D(posDVec32.x, posDVec32.y + (double)this.theEntity.height * 0.5D, posDVec32.z), false, true, false);
        return movingobjectposition == null || movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.MISS;
    }
}

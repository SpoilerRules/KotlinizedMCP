package net.minecraft.pathfinding;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vector3D;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

public abstract class PathNavigate
{
    protected EntityLiving theEntity;
    protected World worldObj;
    protected PathEntity currentPath;
    protected double speed;
    private final IAttributeInstance pathSearchRange;
    private int totalTicks;
    private int ticksAtLastPos;
    private Vector3D lastPosCheck = new Vector3D(0.0D, 0.0D, 0.0D);
    private float heightRequirement = 1.0F;
    private final PathFinder pathFinder;

    public PathNavigate(EntityLiving entitylivingIn, World worldIn)
    {
        this.theEntity = entitylivingIn;
        this.worldObj = worldIn;
        this.pathSearchRange = entitylivingIn.getEntityAttribute(SharedMonsterAttributes.followRange);
        this.pathFinder = this.getPathFinder();
    }

    protected abstract PathFinder getPathFinder();

    public void setSpeed(double speedIn)
    {
        this.speed = speedIn;
    }

    public float getPathSearchRange()
    {
        return (float)this.pathSearchRange.getAttributeValue();
    }

    public final PathEntity getPathToXYZ(double x, double y, double z)
    {
        return this.getPathToPos(new BlockPos(MathHelper.floor_double(x), (int)y, MathHelper.floor_double(z)));
    }

    public PathEntity getPathToPos(BlockPos pos)
    {
        if (!this.canNavigate())
        {
            return null;
        }
        else
        {
            float f = this.getPathSearchRange();
            this.worldObj.theProfiler.startSection("pathfind");
            BlockPos blockpos = new BlockPos(this.theEntity);
            int i = (int)(f + 8.0F);
            ChunkCache chunkcache = new ChunkCache(this.worldObj, blockpos.add(-i, -i, -i), blockpos.add(i, i, i), 0);
            PathEntity pathentity = this.pathFinder.createEntityPathTo(chunkcache, this.theEntity, pos, f);
            this.worldObj.theProfiler.endSection();
            return pathentity;
        }
    }

    public boolean tryMoveToXYZ(double x, double y, double z, double speedIn)
    {
        PathEntity pathentity = this.getPathToXYZ((double)MathHelper.floor_double(x), (double)((int)y), (double)MathHelper.floor_double(z));
        return this.setPath(pathentity, speedIn);
    }

    public void setHeightRequirement(float jumpHeight)
    {
        this.heightRequirement = jumpHeight;
    }

    public PathEntity getPathToEntityLiving(Entity entityIn)
    {
        if (!this.canNavigate())
        {
            return null;
        }
        else
        {
            float f = this.getPathSearchRange();
            this.worldObj.theProfiler.startSection("pathfind");
            BlockPos blockpos = (new BlockPos(this.theEntity)).up();
            int i = (int)(f + 16.0F);
            ChunkCache chunkcache = new ChunkCache(this.worldObj, blockpos.add(-i, -i, -i), blockpos.add(i, i, i), 0);
            PathEntity pathentity = this.pathFinder.createEntityPathTo(chunkcache, this.theEntity, entityIn, f);
            this.worldObj.theProfiler.endSection();
            return pathentity;
        }
    }

    public boolean tryMoveToEntityLiving(Entity entityIn, double speedIn)
    {
        PathEntity pathentity = this.getPathToEntityLiving(entityIn);
        return pathentity != null ? this.setPath(pathentity, speedIn) : false;
    }

    public boolean setPath(PathEntity pathentityIn, double speedIn)
    {
        if (pathentityIn == null)
        {
            this.currentPath = null;
            return false;
        }
        else
        {
            if (!pathentityIn.isSamePath(this.currentPath))
            {
                this.currentPath = pathentityIn;
            }

            this.removeSunnyPath();

            if (this.currentPath.getCurrentPathLength() == 0)
            {
                return false;
            }
            else
            {
                this.speed = speedIn;
                Vector3D vector3D = this.getEntityPosition();
                this.ticksAtLastPos = this.totalTicks;
                this.lastPosCheck = vector3D;
                return true;
            }
        }
    }

    public PathEntity getPath()
    {
        return this.currentPath;
    }

    public void onUpdateNavigation()
    {
        ++this.totalTicks;

        if (!this.noPath())
        {
            if (this.canNavigate())
            {
                this.pathFollow();
            }
            else if (this.currentPath != null && this.currentPath.getCurrentPathIndex() < this.currentPath.getCurrentPathLength())
            {
                Vector3D vector3D = this.getEntityPosition();
                Vector3D vec31D = this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex());

                if (vector3D.y > vec31D.y && !this.theEntity.onGround && MathHelper.floor_double(vector3D.x) == MathHelper.floor_double(vec31D.x) && MathHelper.floor_double(vector3D.z) == MathHelper.floor_double(vec31D.z))
                {
                    this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
                }
            }

            if (!this.noPath())
            {
                Vector3D vec32D = this.currentPath.getPosition(this.theEntity);

                if (vec32D != null)
                {
                    AxisAlignedBB axisalignedbb1 = (new AxisAlignedBB(vec32D.x, vec32D.y, vec32D.z, vec32D.x, vec32D.y, vec32D.z)).expand(0.5D, 0.5D, 0.5D);
                    List<AxisAlignedBB> list = this.worldObj.getCollidingBoundingBoxes(this.theEntity, axisalignedbb1.addCoord(0.0D, -1.0D, 0.0D));
                    double d0 = -1.0D;
                    axisalignedbb1 = axisalignedbb1.offset(0.0D, 1.0D, 0.0D);

                    for (AxisAlignedBB axisalignedbb : list)
                    {
                        d0 = axisalignedbb.calculateYOffset(axisalignedbb1, d0);
                    }

                    this.theEntity.getMoveHelper().setMoveTo(vec32D.x, vec32D.y + d0, vec32D.z, this.speed);
                }
            }
        }
    }

    protected void pathFollow()
    {
        Vector3D vector3D = this.getEntityPosition();
        int i = this.currentPath.getCurrentPathLength();

        for (int j = this.currentPath.getCurrentPathIndex(); j < this.currentPath.getCurrentPathLength(); ++j)
        {
            if (this.currentPath.getPathPointFromIndex(j).yCoord != (int) vector3D.y)
            {
                i = j;
                break;
            }
        }

        float f = this.theEntity.width * this.theEntity.width * this.heightRequirement;

        for (int k = this.currentPath.getCurrentPathIndex(); k < i; ++k)
        {
            Vector3D vec31D = this.currentPath.getVectorFromIndex(this.theEntity, k);

            if (vector3D.squareDistanceTo(vec31D) < (double)f)
            {
                this.currentPath.setCurrentPathIndex(k + 1);
            }
        }

        int j1 = MathHelper.ceiling_float_int(this.theEntity.width);
        int k1 = (int)this.theEntity.height + 1;
        int l = j1;

        for (int i1 = i - 1; i1 >= this.currentPath.getCurrentPathIndex(); --i1)
        {
            if (this.isDirectPathBetweenPoints(vector3D, this.currentPath.getVectorFromIndex(this.theEntity, i1), j1, k1, l))
            {
                this.currentPath.setCurrentPathIndex(i1);
                break;
            }
        }

        this.checkForStuck(vector3D);
    }

    protected void checkForStuck(Vector3D positionVector3D)
    {
        if (this.totalTicks - this.ticksAtLastPos > 100)
        {
            if (positionVector3D.squareDistanceTo(this.lastPosCheck) < 2.25D)
            {
                this.clearPathEntity();
            }

            this.ticksAtLastPos = this.totalTicks;
            this.lastPosCheck = positionVector3D;
        }
    }

    public boolean noPath()
    {
        return this.currentPath == null || this.currentPath.isFinished();
    }

    public void clearPathEntity()
    {
        this.currentPath = null;
    }

    protected abstract Vector3D getEntityPosition();

    protected abstract boolean canNavigate();

    protected boolean isInLiquid()
    {
        return this.theEntity.isInWater() || this.theEntity.isInLava();
    }

    protected void removeSunnyPath()
    {
    }

    protected abstract boolean isDirectPathBetweenPoints(Vector3D posDVec31, Vector3D posDVec32, int sizeX, int sizeY, int sizeZ);
}

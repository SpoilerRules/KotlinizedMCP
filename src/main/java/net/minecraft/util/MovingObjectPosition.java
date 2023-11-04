package net.minecraft.util;

import net.minecraft.entity.Entity;

public class MovingObjectPosition
{
    private BlockPos blockPos;
    public MovingObjectPosition.MovingObjectType typeOfHit;
    public EnumFacing sideHit;
    public Vector3D hitVec;
    public Entity entityHit;

    public MovingObjectPosition(Vector3D hitVecIn, EnumFacing facing, BlockPos blockPosIn)
    {
        this(MovingObjectPosition.MovingObjectType.BLOCK, hitVecIn, facing, blockPosIn);
    }

    public MovingObjectPosition(Vector3D p_i45552_1_, EnumFacing facing)
    {
        this(MovingObjectPosition.MovingObjectType.BLOCK, p_i45552_1_, facing, BlockPos.ORIGIN);
    }

    public MovingObjectPosition(Entity entityIn)
    {
        this(entityIn, new Vector3D(entityIn.posX, entityIn.posY, entityIn.posZ));
    }

    public MovingObjectPosition(MovingObjectPosition.MovingObjectType typeOfHitIn, Vector3D hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn)
    {
        this.typeOfHit = typeOfHitIn;
        this.blockPos = blockPosIn;
        this.sideHit = sideHitIn;
        this.hitVec = new Vector3D(hitVecIn.x, hitVecIn.y, hitVecIn.z);
    }

    public MovingObjectPosition(Entity entityHitIn, Vector3D hitVecIn)
    {
        this.typeOfHit = MovingObjectPosition.MovingObjectType.ENTITY;
        this.entityHit = entityHitIn;
        this.hitVec = hitVecIn;
    }

    public BlockPos getBlockPos()
    {
        return this.blockPos;
    }

    public String toString()
    {
        return "HitResult{type=" + this.typeOfHit + ", blockpos=" + this.blockPos + ", f=" + this.sideHit + ", pos=" + this.hitVec + ", entity=" + this.entityHit + '}';
    }

    public enum MovingObjectType
    {
        MISS,
        BLOCK,
        ENTITY
    }
}

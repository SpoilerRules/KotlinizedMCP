package net.minecraft.util;

public class AxisAlignedBB {
    public final double minX;
    public final double minY;
    public final double minZ;
    public final double maxX;
    public final double maxY;
    public final double maxZ;

    public AxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }

    public AxisAlignedBB(BlockPos pos1, BlockPos pos2) {
        this.minX = pos1.getX();
        this.minY = pos1.getY();
        this.minZ = pos1.getZ();
        this.maxX = pos2.getX();
        this.maxY = pos2.getY();
        this.maxZ = pos2.getZ();
    }

    /**
     * Extends the bounding box if the given coordinates lie outside the current ranges.
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param z Z-coordinate
     * @return A new AxisAlignedBB that includes the given coordinates.
     */
    public AxisAlignedBB addCoord(double x, double y, double z) {
        double newMinX = this.minX + (Math.min(x, 0.0D));
        double newMinY = this.minY + (Math.min(y, 0.0D));
        double newMinZ = this.minZ + (Math.min(z, 0.0D));

        double newMaxX = this.maxX + (Math.max(x, 0.0D));
        double newMaxY = this.maxY + (Math.max(y, 0.0D));
        double newMaxZ = this.maxZ + (Math.max(z, 0.0D));

        return new AxisAlignedBB(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    /**
     * Returns a bounding box expanded by the specified vector. If negative numbers are given, the box will shrink.
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param z Z-coordinate
     * @return A new AxisAlignedBB that includes the expanded dimensions.
     */
    public AxisAlignedBB expand(double x, double y, double z) {
        double newMinX = this.minX - x;
        double newMinY = this.minY - y;
        double newMinZ = this.minZ - z;

        double newMaxX = this.maxX + x;
        double newMaxY = this.maxY + y;
        double newMaxZ = this.maxZ + z;

        return new AxisAlignedBB(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    /**
     * Returns a new bounding box that encompasses both this bounding box and the given one.
     *
     * @param other The other bounding box
     * @return A new AxisAlignedBB that includes both bounding boxes.
     */
    public AxisAlignedBB union(AxisAlignedBB other) {
        double unionMinX = Math.min(this.minX, other.minX);
        double unionMinY = Math.min(this.minY, other.minY);
        double unionMinZ = Math.min(this.minZ, other.minZ);

        double unionMaxX = Math.max(this.maxX, other.maxX);
        double unionMaxY = Math.max(this.maxY, other.maxY);
        double unionMaxZ = Math.max(this.maxZ, other.maxZ);

        return new AxisAlignedBB(unionMinX, unionMinY, unionMinZ, unionMaxX, unionMaxY, unionMaxZ);
    }

    /**
     * Returns a new AxisAlignedBB with the specified corners.
     *
     * @param x1 X-coordinate of the first corner
     * @param y1 Y-coordinate of the first corner
     * @param z1 Z-coordinate of the first corner
     * @param x2 X-coordinate of the second corner
     * @param y2 Y-coordinate of the second corner
     * @param z2 Z-coordinate of the second corner
     * @return A new AxisAlignedBB that includes both corners.
     */
    public static AxisAlignedBB fromBounds(double x1, double y1, double z1, double x2, double y2, double z2) {
        double minX = Math.min(x1, x2);
        double minY = Math.min(y1, y2);
        double minZ = Math.min(z1, z2);

        double maxX = Math.max(x1, x2);
        double maxY = Math.max(y1, y2);
        double maxZ = Math.max(z1, z2);

        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * Returns a new AxisAlignedBB offset by the specified coordinates.
     *
     * @param x X-coordinate offset
     * @param y Y-coordinate offset
     * @param z Z-coordinate offset
     * @return A new AxisAlignedBB that is offset by the given coordinates.
     */
    public AxisAlignedBB offset(double x, double y, double z) {
        double offsetXMin = this.minX + x;
        double offsetYMin = this.minY + y;
        double offsetZMin = this.minZ + z;

        double offsetXMax = this.maxX + x;
        double offsetYMax = this.maxY + y;
        double offsetZMax = this.maxZ + z;

        return new AxisAlignedBB(offsetXMin, offsetYMin, offsetZMin, offsetXMax, offsetYMax, offsetZMax);
    }

    /**
     * If this bounding box and the given bounding box overlap in the Y and Z dimensions,
     * calculate the offset between them in the X dimension. Return the given offset if the
     * bounding boxes do not overlap or if the given offset is closer to 0 than the calculated offset.
     * Otherwise, return the calculated offset.
     *
     * @param other          The other bounding box
     * @param proposedOffset The proposed X offset
     * @return The calculated X offset
     */
    public double calculateXOffset(AxisAlignedBB other, double proposedOffset) {
        if (other.maxY <= this.minY || other.minY >= this.maxY || other.maxZ <= this.minZ || other.minZ >= this.maxZ) {
            return proposedOffset;
        }

        double distanceToLeft = this.minX - other.maxX;
        double distanceToRight = this.maxX - other.minX;

        return proposedOffset > 0.0D && other.maxX <= this.minX ? Math.min(proposedOffset, distanceToLeft)
                : proposedOffset < 0.0D && other.minX >= this.maxX ? Math.max(proposedOffset, distanceToRight)
                : proposedOffset;
    }

    /**
     * If this bounding box and the given bounding box overlap in the X and Z dimensions, calculate the offset between them
     * in the Y dimension. Return the given offset if the bounding boxes do not overlap or if the given offset is closer to 0
     * than the calculated offset. Otherwise, return the calculated offset.
     *
     * @param other          The other bounding box
     * @param proposedOffset The proposed Y offset
     * @return The calculated Y offset
     */
    public double calculateYOffset(AxisAlignedBB other, double proposedOffset) {
        if (other.maxX <= this.minX || other.minX >= this.maxX || other.maxZ <= this.minZ || other.minZ >= this.maxZ) {
            return proposedOffset;
        }

        if (proposedOffset > 0.0D && other.maxY <= this.minY) {
            double distanceToBottom = this.minY - other.maxY;
            return Math.min(proposedOffset, distanceToBottom);
        }

        if (proposedOffset < 0.0D && other.minY >= this.maxY) {
            double distanceToTop = this.maxY - other.minY;
            return Math.max(proposedOffset, distanceToTop);
        }

        return proposedOffset;
    }

    /**
     * If this bounding box and the given bounding box overlap in the Y and X dimensions, calculate the offset between them
     * in the Z dimension. Return the given offset if the bounding boxes do not overlap or if the given offset is closer to 0
     * than the calculated offset. Otherwise, return the calculated offset.
     *
     * @param other          The other bounding box
     * @param proposedOffset The proposed Z offset
     * @return The calculated Z offset
     */
    public double calculateZOffset(AxisAlignedBB other, double proposedOffset) {
        if (other.maxX <= this.minX || other.minX >= this.maxX || other.maxY <= this.minY || other.minY >= this.maxY) {
            return proposedOffset;
        }

        if (proposedOffset > 0.0D && other.maxZ <= this.minZ) {
            double distanceToFront = this.minZ - other.maxZ;
            return Math.min(proposedOffset, distanceToFront);
        }

        if (proposedOffset < 0.0D && other.minZ >= this.maxZ) {
            double distanceToBack = this.maxZ - other.minZ;
            return Math.max(proposedOffset, distanceToBack);
        }

        return proposedOffset;
    }

    /**
     * Checks whether the given bounding box intersects with this one.
     *
     * @param other The other bounding box
     * @return True if the boxes intersect, otherwise false
     */
    public boolean intersectsWith(AxisAlignedBB other) {
        boolean xOverlap = other.maxX > this.minX && other.minX < this.maxX;
        boolean yOverlap = other.maxY > this.minY && other.minY < this.maxY;
        boolean zOverlap = other.maxZ > this.minZ && other.minZ < this.maxZ;
        return xOverlap && yOverlap && zOverlap;
    }

    /**
     * Checks if the supplied Vec3 is completely inside the bounding box.
     *
     * @param vec The Vec3 to check
     * @return True if the Vec3 is inside the box, otherwise false
     */
    public boolean contains(Vector3D vec) {
        boolean xInside = vec.x > this.minX && vec.x < this.maxX;
        boolean yInside = vec.y > this.minY && vec.y < this.maxY;
        boolean zInside = vec.z > this.minZ && vec.z < this.maxZ;
        return xInside && yInside && zInside;
    }

    /**
     * Returns the average length of the edges of the bounding box.
     *
     * @return The average edge length
     */
    public double getAverageEdgeLength() {
        double width = this.maxX - this.minX;
        double height = this.maxY - this.minY;
        double depth = this.maxZ - this.minZ;
        return (width + height + depth) / 3.0D;
    }

    /**
     * Returns a bounding box that is contracted (inset) by the specified amounts.
     *
     * @param x Amount to contract in the X dimension
     * @param y Amount to contract in the Y dimension
     * @param z Amount to contract in the Z dimension
     * @return The contracted bounding box
     */
    public AxisAlignedBB contract(double x, double y, double z) {
        double newMinX = this.minX + x;
        double newMinY = this.minY + y;
        double newMinZ = this.minZ + z;
        double newMaxX = this.maxX - x;
        double newMaxY = this.maxY - y;
        double newMaxZ = this.maxZ - z;
        return new AxisAlignedBB(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public MovingObjectPosition calculateIntercept(Vector3D vecA, Vector3D vecB) {
        Vector3D[] vecs = new Vector3D[]{
                vecA.getIntermediateWithXValue(vecB, this.minX),
                vecA.getIntermediateWithXValue(vecB, this.maxX),
                vecA.getIntermediateWithYValue(vecB, this.minY),
                vecA.getIntermediateWithYValue(vecB, this.maxY),
                vecA.getIntermediateWithZValue(vecB, this.minZ),
                vecA.getIntermediateWithZValue(vecB, this.maxZ)
        };

        double[] distances = new double[6];
        for (int i = 0; i < 6; i++) {
            if (isVecInBox(vecs[i])) distances[i] = vecA.squareDistanceTo(vecs[i]);
            else {
                vecs[i] = null;
                distances[i] = Double.MAX_VALUE;
            }
        }

        int minIndex = getMinIndex(distances);
        if (minIndex == -1) {
            return null;
        }

        EnumFacing enumfacing = getEnumFacing(minIndex);
        return new MovingObjectPosition(vecs[minIndex], enumfacing);
    }

    private boolean isVecInBox(Vector3D vec) {
        return vec != null && vec.x >= this.minX && vec.x <= this.maxX &&
                vec.y >= this.minY && vec.y <= this.maxY &&
                vec.z >= this.minZ && vec.z <= this.maxZ;
    }

    private int getMinIndex(double[] array) {
        int minIndex = -1;
        double minValue = Double.MAX_VALUE;
        for (int i = 0; i < array.length; i++) {
            if (array[i] < minValue) {
                minValue = array[i];
                minIndex = i;
            }
        }
        return minIndex;
    }

    private EnumFacing getEnumFacing(int index) {
        return switch (index) {
            case 0 -> EnumFacing.WEST;
            case 1 -> EnumFacing.EAST;
            case 2 -> EnumFacing.DOWN;
            case 3 -> EnumFacing.UP;
            case 4 -> EnumFacing.NORTH;
            default -> EnumFacing.SOUTH;
        };
    }

    /**
     * Checks if the supplied Vec3D is completely inside the bounding box.
     *
     * @param vec The Vec3D object to be checked. This object represents a point in 3D space.
     * @return true if the point represented by vec is inside the bounding box, false otherwise.
     * The point is considered inside if it lies strictly between the min and max coordinates on all axes (X, Y, Z).
     */
    public boolean isVecInside(Vector3D vec) {
        return vec.x > this.minX && vec.x < this.maxX &&
                vec.y > this.minY && vec.y < this.maxY &&
                vec.z > this.minZ && vec.z < this.maxZ;
    }

    /**
     * Returns a string representation of the bounding box.
     *
     * @return The string representation
     */
    public String toString() {
        return "BoundingBox[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }

    /**
     * Checks if any of the bounding box's coordinates are NaN.
     *
     * @return True if any coordinate is NaN, otherwise false
     */
    public boolean hasNaN() {
        return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX) || Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
    }
}

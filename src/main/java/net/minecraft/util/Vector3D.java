package net.minecraft.util;

public class Vector3D {
    public final double x;
    public final double y;
    public final double z;

    public Vector3D(double x, double y, double z) {
        if (x == -0.0D) {
            x = 0.0D;
        }

        if (y == -0.0D) {
            y = 0.0D;
        }

        if (z == -0.0D) {
            z = 0.0D;
        }

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D(Vec3i vec3i) {
        this(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }

    public Vector3D subtractReverse(Vector3D other) {
        return new Vector3D(other.x - this.x, other.y - this.y, other.z - this.z);
    }

    public Vector3D normalize() {
        double length = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        return length < 1.0E-4D ? new Vector3D(0.0D, 0.0D, 0.0D) : new Vector3D(this.x / length, this.y / length, this.z / length);
    }

    public double dotProduct(Vector3D other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vector3D crossProduct(Vector3D other) {
        return new Vector3D(this.y * other.z - this.z * other.y, this.z * other.x - this.x * other.z, this.x * other.y - this.y * other.x);
    }

    public Vector3D subtract(Vector3D other) {
        return this.subtract(other.x, other.y, other.z);
    }

    public Vector3D subtract(double x, double y, double z) {
        return this.addVector(-x, -y, -z);
    }

    public Vector3D add(Vector3D other) {
        return this.addVector(other.x, other.y, other.z);
    }

    public Vector3D addVector(double x, double y, double z) {
        return new Vector3D(this.x + x, this.y + y, this.z + z);
    }

    public double distanceTo(Vector3D other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        double dz = other.z - this.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public double squareDistanceTo(Vector3D other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        double dz = other.z - this.z;
        return dx * dx + dy * dy + dz * dz;
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public Vector3D getIntermediateWithXValue(Vector3D other, double x) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        double dz = other.z - this.z;

        if (dx * dx < 1.0000000116860974E-7D) {
            return null;
        } else {
            double d = (x - this.x) / dx;
            return d >= 0.0D && d <= 1.0D ? new Vector3D(this.x + dx * d, this.y + dy * d, this.z + dz * d) : null;
        }
    }

    public Vector3D getIntermediateWithYValue(Vector3D other, double y) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        double dz = other.z - this.z;

        if (dy * dy < 1.0000000116860974E-7D) {
            return null;
        } else {
            double d = (y - this.y) / dy;
            return d >= 0.0D && d <= 1.0D ? new Vector3D(this.x + dx * d, this.y + dy * d, this.z + dz * d) : null;
        }
    }

    public Vector3D getIntermediateWithZValue(Vector3D other, double z) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        double dz = other.z - this.z;

        if (dz * dz < 1.0000000116860974E-7D) {
            return null;
        } else {
            double d = (z - this.z) / dz;
            return d >= 0.0D && d <= 1.0D ? new Vector3D(this.x + dx * d, this.y + dy * d, this.z + dz * d) : null;
        }
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    public Vector3D rotatePitch(float pitch) {
        float cos = MathHelper.cos(pitch);
        float sin = MathHelper.sin(pitch);
        double newY = this.y * cos + this.z * sin;
        double newZ = this.z * cos - this.y * sin;
        return new Vector3D(this.x, newY, newZ);
    }

    public Vector3D rotateYaw(float yaw) {
        float cos = MathHelper.cos(yaw);
        float sin = MathHelper.sin(yaw);
        double newX = this.x * cos + this.z * sin;
        double newZ = this.z * cos - this.x * sin;
        return new Vector3D(newX, this.y, newZ);
    }
}
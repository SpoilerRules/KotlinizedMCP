package net.minecraft.util.math

class Matrix4f(matrix: FloatArray) : org.lwjgl.util.vector.Matrix4f() {
    init {
        require(matrix.size == 16) { "Array length must be 16 for a 4x4 matrix" }
        setMatrix(matrix)
    }

    constructor() : this(FloatArray(16))

    private fun setMatrix(matrix: FloatArray) {
        this.m00 = matrix[0]
        this.m01 = matrix[1]
        this.m02 = matrix[2]
        this.m03 = matrix[3]
        this.m10 = matrix[4]
        this.m11 = matrix[5]
        this.m12 = matrix[6]
        this.m13 = matrix[7]
        this.m20 = matrix[8]
        this.m21 = matrix[9]
        this.m22 = matrix[10]
        this.m23 = matrix[11]
        this.m30 = matrix[12]
        this.m31 = matrix[13]
        this.m32 = matrix[14]
        this.m33 = matrix[15]
    }
}
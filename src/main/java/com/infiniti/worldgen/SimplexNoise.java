package com.infiniti.worldgen;

/**
 * SimplexNoise — implementação Java pura (Stefan Gustavson).
 * Suporta 2D, 3D. Seeded via permutação aleatória.
 * Retorna valores no intervalo aproximado [-1, 1].
 */
public class SimplexNoise {

    private final int[] perm = new int[512];
    private final int[] permMod12 = new int[512];

    private static final int[][] GRAD3 = {
        {1,1,0},{-1,1,0},{1,-1,0},{-1,-1,0},
        {1,0,1},{-1,0,1},{1,0,-1},{-1,0,-1},
        {0,1,1},{0,-1,1},{0,1,-1},{0,-1,-1}
    };

    private static final double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
    private static final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;
    private static final double F3 = 1.0 / 3.0;
    private static final double G3 = 1.0 / 6.0;

    public SimplexNoise(long seed) {
        int[] p = new int[256];
        for (int i = 0; i < 256; i++) p[i] = i;
        // Fisher-Yates shuffle usando seed
        java.util.Random rng = new java.util.Random(seed);
        for (int i = 255; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            int tmp = p[i]; p[i] = p[j]; p[j] = tmp;
        }
        for (int i = 0; i < 512; i++) {
            perm[i] = p[i & 255];
            permMod12[i] = perm[i] % 12;
        }
    }

    public double noise2D(double xin, double yin) {
        double s = (xin + yin) * F2;
        int i = fastFloor(xin + s);
        int j = fastFloor(yin + s);
        double t = (i + j) * G2;
        double X0 = i - t, Y0 = j - t;
        double x0 = xin - X0, y0 = yin - Y0;
        int i1, j1;
        if (x0 > y0) { i1 = 1; j1 = 0; } else { i1 = 0; j1 = 1; }
        double x1 = x0 - i1 + G2, y1 = y0 - j1 + G2;
        double x2 = x0 - 1.0 + 2.0 * G2, y2 = y0 - 1.0 + 2.0 * G2;
        int ii = i & 255, jj = j & 255;
        int gi0 = permMod12[ii + perm[jj]];
        int gi1 = permMod12[ii + i1 + perm[jj + j1]];
        int gi2 = permMod12[ii + 1 + perm[jj + 1]];
        double n0 = dot2(GRAD3[gi0], x0, y0, 0.5);
        double n1 = dot2(GRAD3[gi1], x1, y1, 0.5);
        double n2 = dot2(GRAD3[gi2], x2, y2, 0.5);
        return 70.0 * (n0 + n1 + n2);
    }

    public double noise3D(double xin, double yin, double zin) {
        double s = (xin + yin + zin) * F3;
        int i = fastFloor(xin + s);
        int j = fastFloor(yin + s);
        int k = fastFloor(zin + s);
        double t = (i + j + k) * G3;
        double x0 = xin - (i - t), y0 = yin - (j - t), z0 = zin - (k - t);
        int i1, j1, k1, i2, j2, k2;
        if (x0 >= y0) {
            if (y0 >= z0) { i1=1;j1=0;k1=0;i2=1;j2=1;k2=0; }
            else if (x0 >= z0) { i1=1;j1=0;k1=0;i2=1;j2=0;k2=1; }
            else { i1=0;j1=0;k1=1;i2=1;j2=0;k2=1; }
        } else {
            if (y0 < z0) { i1=0;j1=0;k1=1;i2=0;j2=1;k2=1; }
            else if (x0 < z0) { i1=0;j1=1;k1=0;i2=0;j2=1;k2=1; }
            else { i1=0;j1=1;k1=0;i2=1;j2=1;k2=0; }
        }
        double x1=x0-i1+G3, y1=y0-j1+G3, z1=z0-k1+G3;
        double x2=x0-i2+2.0*G3, y2=y0-j2+2.0*G3, z2=z0-k2+2.0*G3;
        double x3=x0-1.0+3.0*G3, y3=y0-1.0+3.0*G3, z3=z0-1.0+3.0*G3;
        int ii=i&255, jj=j&255, kk=k&255;
        int gi0=permMod12[ii+perm[jj+perm[kk]]];
        int gi1=permMod12[ii+i1+perm[jj+j1+perm[kk+k1]]];
        int gi2=permMod12[ii+i2+perm[jj+j2+perm[kk+k2]]];
        int gi3=permMod12[ii+1+perm[jj+1+perm[kk+1]]];
        double n0=dot3(GRAD3[gi0],x0,y0,z0,0.6);
        double n1=dot3(GRAD3[gi1],x1,y1,z1,0.6);
        double n2=dot3(GRAD3[gi2],x2,y2,z2,0.6);
        double n3=dot3(GRAD3[gi3],x3,y3,z3,0.6);
        return 32.0*(n0+n1+n2+n3);
    }

    private double dot2(int[] g, double x, double y, double r) {
        double t = r - x*x - y*y;
        return t < 0 ? 0 : t*t*t*t*(g[0]*x+g[1]*y);
    }

    private double dot3(int[] g, double x, double y, double z, double r) {
        double t = r - x*x - y*y - z*z;
        return t < 0 ? 0 : t*t*t*t*(g[0]*x+g[1]*y+g[2]*z);
    }

    private int fastFloor(double x) { return x > 0 ? (int)x : (int)x - 1; }
}

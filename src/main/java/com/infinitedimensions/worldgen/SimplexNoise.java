package com.infinitedimensions.worldgen;

/**
 * SimplexNoise — implementação pura em Java do algoritmo de Stefan Gustavson.
 * Sem dependências externas. Seeded via permutação aleatória.
 *
 * Suporta 2D, 3D e 4D noise no intervalo aproximado [-1, 1].
 */
public class SimplexNoise {

    private final int[] perm = new int[512];
    private final int[] permMod12 = new int[512];

    private static final int[][] GRAD3 = {
        {1,1,0},{-1,1,0},{1,-1,0},{-1,-1,0},
        {1,0,1},{-1,0,1},{1,0,-1},{-1,0,-1},
        {0,1,1},{0,-1,1},{0,1,-1},{0,-1,-1}
    };

    public SimplexNoise(long seed) {
        int[] p = new int[256];
        for (int i = 0; i < 256; i++) p[i] = i;

        // Fisher-Yates shuffle com seed
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

    // ── 2D ──

    public double eval(double xin, double yin) {
        final double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
        final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;

        double s  = (xin + yin) * F2;
        int i  = fastFloor(xin + s);
        int j  = fastFloor(yin + s);
        double t  = (i + j) * G2;
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

        double n0, n1, n2;
        double t0 = 0.5 - x0*x0 - y0*y0;
        n0 = t0 < 0 ? 0 : (t0 *= t0) * t0 * dot(GRAD3[gi0], x0, y0);
        double t1 = 0.5 - x1*x1 - y1*y1;
        n1 = t1 < 0 ? 0 : (t1 *= t1) * t1 * dot(GRAD3[gi1], x1, y1);
        double t2 = 0.5 - x2*x2 - y2*y2;
        n2 = t2 < 0 ? 0 : (t2 *= t2) * t2 * dot(GRAD3[gi2], x2, y2);

        return 70.0 * (n0 + n1 + n2);
    }

    // ── 3D ──

    public double eval(double xin, double yin, double zin) {
        final double F3 = 1.0 / 3.0;
        final double G3 = 1.0 / 6.0;

        double s  = (xin + yin + zin) * F3;
        int i  = fastFloor(xin + s);
        int j  = fastFloor(yin + s);
        int k  = fastFloor(zin + s);
        double t  = (i + j + k) * G3;
        double x0 = xin - (i - t);
        double y0 = yin - (j - t);
        double z0 = zin - (k - t);

        int i1, j1, k1, i2, j2, k2;
        if (x0 >= y0) {
            if      (y0 >= z0) { i1=1;j1=0;k1=0; i2=1;j2=1;k2=0; }
            else if (x0 >= z0) { i1=1;j1=0;k1=0; i2=1;j2=0;k2=1; }
            else               { i1=0;j1=0;k1=1; i2=1;j2=0;k2=1; }
        } else {
            if      (y0 < z0)  { i1=0;j1=0;k1=1; i2=0;j2=1;k2=1; }
            else if (x0 < z0)  { i1=0;j1=1;k1=0; i2=0;j2=1;k2=1; }
            else               { i1=0;j1=1;k1=0; i2=1;j2=1;k2=0; }
        }

        double x1 = x0-i1+G3, y1 = y0-j1+G3, z1 = z0-k1+G3;
        double x2 = x0-i2+2*G3, y2 = y0-j2+2*G3, z2 = z0-k2+2*G3;
        double x3 = x0-1+3*G3, y3 = y0-1+3*G3, z3 = z0-1+3*G3;

        int ii=i&255, jj=j&255, kk=k&255;
        int gi0 = permMod12[ii+    perm[jj+    perm[kk   ]]];
        int gi1 = permMod12[ii+i1+ perm[jj+j1+ perm[kk+k1]]];
        int gi2 = permMod12[ii+i2+ perm[jj+j2+ perm[kk+k2]]];
        int gi3 = permMod12[ii+1+  perm[jj+1+  perm[kk+1 ]]];

        double n0, n1, n2, n3;
        double t0 = 0.6-x0*x0-y0*y0-z0*z0;
        n0 = t0 < 0 ? 0 : (t0*=t0)*t0*dot(GRAD3[gi0],x0,y0,z0);
        double t1 = 0.6-x1*x1-y1*y1-z1*z1;
        n1 = t1 < 0 ? 0 : (t1*=t1)*t1*dot(GRAD3[gi1],x1,y1,z1);
        double t2 = 0.6-x2*x2-y2*y2-z2*z2;
        n2 = t2 < 0 ? 0 : (t2*=t2)*t2*dot(GRAD3[gi2],x2,y2,z2);
        double t3 = 0.6-x3*x3-y3*y3-z3*z3;
        n3 = t3 < 0 ? 0 : (t3*=t3)*t3*dot(GRAD3[gi3],x3,y3,z3);

        return 32.0 * (n0+n1+n2+n3);
    }

    // ── Helpers ──

    private static int fastFloor(double x) {
        int xi = (int) x;
        return x < xi ? xi - 1 : xi;
    }

    private static double dot(int[] g, double x, double y) {
        return g[0]*x + g[1]*y;
    }

    private static double dot(int[] g, double x, double y, double z) {
        return g[0]*x + g[1]*y + g[2]*z;
    }
}

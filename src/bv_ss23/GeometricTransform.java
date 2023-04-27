// BV Ue2 SS2023 Vorgabe
//
// Copyright (C) 2023 by Klaus Jung
// All rights reserved.
// Date: 2023-03-23


package bv_ss23;


public class GeometricTransform {

    public enum InterpolationType {
        NEAREST("Nearest Neighbour"),
        BILINEAR("Bilinear");

        private final String name;

        private InterpolationType(String s) {
            name = s;
        }

        public String toString() {
            return this.name;
        }
    }

    ;

    public void perspective(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion, InterpolationType interpolation) {
        switch (interpolation) {
            case NEAREST:
                perspectiveNearestNeighbour(src, dst, angle, perspectiveDistortion);
                break;
            case BILINEAR:
                perspectiveBilinear(src, dst, angle, perspectiveDistortion);
                break;
            default:
                break;
        }

    }

    /**
     * @param src                   source image
     * @param dst                   destination Image
     * @param angle                 rotation angle in degrees
     * @param perspectiveDistortion amount of the perspective distortion
     */
    public void perspectiveNearestNeighbour(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion) {

        // TODO: implement the geometric transformation using nearest neighbour image rendering

        double angleRad = Math.toRadians(angle);
        // NOTE: angle contains the angle in degrees, whereas Math trigonometric functions need the angle in radiant
        for (int yd = 0; yd < dst.height; yd++) {
            for (int xd = 0; xd < dst.width; xd++) {

                int ydStrich = yd - dst.height / 2;
                int xdStrich = xd - dst.width / 2;

                double ysStrich = ydStrich / (Math.cos(angleRad) - ydStrich * perspectiveDistortion * Math.sin(angleRad));
                double xsStrich = xdStrich * (perspectiveDistortion * Math.sin(angleRad) * ysStrich + 1);

                int ys = (int)Math.round(ysStrich + (double) src.height / 2);
                int xs = (int)Math.round(xsStrich + (double) src.width / 2);

                int desPos = yd * dst.width + xd;
                int srcPos = ys * src.width + xs;

                if (xs < 0 || xs > (src.width -1 ) || ys < 0 || ys > (src.height -1)) {
                    dst.argb[desPos] = 0xFFFFFFFF;
                } else {
                    dst.argb[desPos] = src.argb[srcPos];
                }
            }
        }
    }


    /**
     * @param src                   source image
     * @param dst                   destination Image
     * @param angle                 rotation angle in degrees
     * @param perspectiveDistortion amount of the perspective distortion
     */
    public void perspectiveBilinear(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion) {

        // TODO: implement the geometric transformation using bilinear interpolation

        // NOTE: angle contains the angle in degrees, whereas Math trigonometric functions need the angle in radiant

    }


}





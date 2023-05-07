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

        double angleRad = Math.toRadians(angle);

        for (int yd = 0; yd < dst.height; yd++) {
            for (int xd = 0; xd < dst.width; xd++) {

                int ydStrich = yd - dst.height / 2;
                int xdStrich = xd - dst.width / 2;

                double ysStrich = ydStrich / (Math.cos(angleRad) - ydStrich * perspectiveDistortion * Math.sin(angleRad));
                double xsStrich = xdStrich * (perspectiveDistortion * Math.sin(angleRad) * ysStrich + 1);

                int ys = (int) Math.round(ysStrich + (double) src.height / 2);
                int xs = (int) Math.round(xsStrich + (double) src.width / 2);

                int desPos = yd * dst.width + xd;
                int srcPos = ys * src.width + xs;

                if (xs < 0 || xs > (src.width - 1) || ys < 0 || ys > (src.height - 1)) {
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

        double angleRad = Math.toRadians(angle);

        int a, b, c, d;
        int widthBounds = src.width - 1;
        int heightBounds = src.height - 1;

        for (int yd = 0; yd < dst.height; yd++) {
            for (int xd = 0; xd < dst.width; xd++) {


                int pos = yd * dst.width + xd;

                int xdStrich = xd - dst.width / 2;
                int ydStrich = yd - dst.height / 2;

                double ysStrich = ydStrich / (Math.cos(angleRad) - ydStrich * perspectiveDistortion * Math.sin(angleRad));
                double xsStrich = xdStrich * (perspectiveDistortion * Math.sin(angleRad) * ysStrich + 1);

                double xs = xsStrich + src.width / 2;
                double ys = ysStrich + src.height / 2;

                //naechster Nachbar
                int xsLeft = (int) xs;
                int xsRight = (int) Math.ceil(xs);
                int ysUp = (int) ys;
                int ysDown = (int) Math.ceil(ys);

                double h = xs - xsLeft;
                double v = ys - ysUp;

                if (xsLeft >= 0 && ysUp >= 0 && xsLeft <= widthBounds && ysUp <= heightBounds) {
                    a = src.argb[ysUp * src.width + xsLeft];
                } else {
                    a = 0xFFFFFFFF;
                }

                if (xsRight >= 0 && ysUp >= 0 && xsRight <= widthBounds && ysUp <= heightBounds) {
                    b = src.argb[ysUp * src.width + xsRight];
                } else {
                    b = 0xFFFFFFFF;
                }

                if (xsLeft >= 0 && ysDown >= 0 && xsLeft <= widthBounds && ysDown <= heightBounds) {
                    c = src.argb[ysDown * src.width + xsLeft];
                } else {
                    c = 0xFFFFFFFF;
                }

                if (xsRight >= 0 && ysDown >= 0 && xsRight <= widthBounds && ysDown <= heightBounds) {
                    d = src.argb[ysDown * src.width + xsRight];
                } else {
                    d = 0xFFFFFFFF;
                }


                //red
                int r = BiliniarInterpolate(h, v, (a >> 16) & 0xff, (b >> 16) & 0xff, (c >> 16) & 0xff, (d >> 16) & 0xff);
                //green
                int g = BiliniarInterpolate(h, v, (a >> 8) & 0xff, (b >> 8) & 0xff, (c >> 8) & 0xff, (d >> 8) & 0xff);
                //blue
                int bl = BiliniarInterpolate(h, v, a & 0xff, b & 0xff, c & 0xff, d & 0xff);


                dst.argb[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + (bl & 0xff);


            }
        }


    }

    private int BiliniarInterpolate(double h, double v, int a, int b, int c, int d) {
        double color = a * (1 - h) * (1 - v) + b * h * (1 - v) + c * (1 - h) * v + d * h * v;
        return (int) color;
    }

}







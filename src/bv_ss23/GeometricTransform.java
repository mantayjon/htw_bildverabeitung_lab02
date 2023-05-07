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

                double r = ((a >> 16) & 0xff) * (1 - v) * (1 - h) + ((b >> 16) & 0xff) * h * (1 - v) + ((c >> 16) & 0xff)
                        * v * (1 - h) + ((d >> 16) & 0xff) * v * h;

                double gr = ((a >> 8) & 0xff) * (1 - v) * (1 - h) + ((b >> 8) & 0xff) * h * (1 - v) + ((c >> 8) & 0xff)
                        * v * (1 - h) + ((d >> 16) & 0xff) * v * h;

                double bl = (a & 0xff) * (1 - v) * (1 - h) + (b & 0xff) * h * (1 - v) + (c & 0xff)
                        * v * (1 - h) + (d & 0xff) * v * h;

                dst.argb[pos] = (0xFF << 24) | (((int) r & 0xff) << 16) | (((int) gr & 0xff) << 8) | ((int) bl & 0xff);

            }
        }
    }
}







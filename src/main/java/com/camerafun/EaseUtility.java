package com.camerafun;

public class EaseUtility {

    public double easeInSine(double x) {
        return 1 - Math.cos((x * Math.PI ) / 2 + 0.5 * Math.PI) - 1;
    }
    public double easeInOutSine(double x) {
        return -(Math.cos(Math.PI * x + 0.5 * Math.PI) - 1) / 2 -0.5;
    }
    public double easeOutBounce(double x) {
        double n1 = 7.5625;
        double d1 = 2.75;
        int flip = (x < 0) ? -1 : 1;
        x = Math.abs(x);
        if (x < 1 / d1) {
            return (n1 * x * x) * flip;
        } else if (x < 2 / d1) {
            return (n1 * (x -= 1.5 / d1) * x + 0.75) * flip;
        } else if (x < 2.5 / d1) {
            return (n1 * (x -= 2.25 / d1) * x + 0.9375) * flip;
        } else {
            return (n1 * (x -= 2.625 / d1) * x + 0.984375) * flip;
        }
    }
    public double easeOutBack(double x) {
        double c1 = 1.70158;
        double c3 = c1 + 1;
        int flip = (x < 0) ? -1 : 1;
        x = Math.abs(x);

        return (1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2)) * flip;
    }
    public double easeOutElastic(double x ) {
        double c4 = (2 * Math.PI) / 3;
        int flip = (x < 0) ? -1 : 1;
        x = Math.abs(x);

        return x == 0 ? 0 * flip
                : x == 1
                ? 1 * flip
                : (Math.pow(2, -10 * x) * Math.sin((x * 10 - 0.75) * c4) + 1) * flip;
    }
}

package dev.lemonclient.addon.utils.render;

public class BezierCurve {
   public double A;
   public double B;
   public double C;
   public double D;
   public float percent = 100.0F;
   public long oldTime = System.currentTimeMillis();

   public BezierCurve(double point1, double point2, double point3, double point4) {
      this.A = point1;
      this.B = point2;
      this.C = point3;
      this.D = point4;
   }

   public double interpolateCubic(double percent) {
      return 1.0 - (this.A * Math.pow(1.0 - percent, 3.0) + 3.0 * this.B * Math.pow(1.0 - percent, 2.0) * percent + 3.0 * this.C * (1.0 - percent) * Math.pow(percent, 2.0) + this.D * Math.pow(percent, 3.0)) + this.A * (1.0 - percent);
   }

   public double interpolateQuadratic(double x) {
      return this.A * Math.pow(1.0 - x, 2.0) + this.B * 2.0 * (1.0 - x) * x + this.C * Math.pow(x, 2.0);
   }

   public double interpolate(double x) {
      return (1.0 - (this.B + 0.5 * x * (this.C - this.A + x * (2.0 * this.A - 5.0 * this.B + 4.0 * this.C - this.D + x * (3.0 * (this.B - this.C) + this.D - this.A)))) * 4.0) * 1.6666666666666667;
   }

   public double get(boolean backwards, int iterations) {
      return Math.max(0.0, Math.abs(this.stepAndInterpolate(backwards, iterations)));
   }

   public double stepAndInterpolate(boolean backwards, int iterations) {
      int x;
      if (backwards) {
         for(x = 0; x < iterations; ++x) {
            if (this.percent < 100.0F) {
               ++this.percent;
            }
         }
      } else {
         for(x = 0; x < iterations; ++x) {
            if (this.percent > 0.0F) {
               --this.percent;
            }
         }
      }

      return this.interpolate((double)this.percent / 100.0);
   }
}

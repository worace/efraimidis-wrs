package wrs;

/**
 * encapsulated double, used for passing parameters by reference
 *  
 * @author pavlos
 *
 */
public class MyDouble {
  private double dbl;

  public MyDouble(double dbl) {
    this.dbl = dbl;
  }

  public double dblValue() {
    return dbl;
  }

  public void setValue(double dbl) {
    this.dbl = dbl;
  }
}

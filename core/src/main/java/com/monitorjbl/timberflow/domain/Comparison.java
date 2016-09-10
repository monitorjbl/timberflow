package com.monitorjbl.timberflow.domain;

public class Comparison {
  private String leftHand;
  private CompareOperation compareOperation;
  private String rightHand;
  private BooleanOperation booleanOperation;
  private Comparison nextComparison;

  public enum CompareOperation {
    EQUALS("=="), NOT_EQUALS("!=");

    String str;

    CompareOperation(String str) {
      this.str = str;
    }

    public String toString() {
      return str;
    }

    public static CompareOperation fromString(String str) {
      if("==".equals(str)) {
        return EQUALS;
      } else if("!=".equals(str)) {
        return NOT_EQUALS;
      } else {
        throw new IllegalArgumentException("Not a valid comparison: " + str);
      }
    }
  }

  public enum BooleanOperation {
    AND, OR;

    public static BooleanOperation fromString(String str) {
      if("and".equals(str)) {
        return AND;
      } else if("or".equals(str)) {
        return OR;
      } else {
        throw new IllegalArgumentException("Not a valid boolean operation: " + str);
      }
    }
  }

  public Comparison(String leftHand, CompareOperation compareOperation, String rightHand) {
    this.leftHand = leftHand;
    this.compareOperation = compareOperation;
    this.rightHand = rightHand;
  }

  public String getLeftHand() {
    return leftHand;
  }

  public CompareOperation getCompareOperation() {
    return compareOperation;
  }

  public String getRightHand() {
    return rightHand;
  }

  public BooleanOperation getBooleanOperation() {
    return booleanOperation;
  }

  void setBooleanOperation(BooleanOperation booleanOperation) {
    this.booleanOperation = booleanOperation;
  }

  public Comparison getNextComparison() {
    return nextComparison;
  }

  void setNextComparison(Comparison nextComparison) {
    this.nextComparison = nextComparison;
  }

  @Override
  public String toString() {
    return leftHand + " " + compareOperation.toString() + " " + rightHand;
  }
}

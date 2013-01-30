package com.perunlabs.tool.jvalgen.java.type;

public enum JAccess {
  PUBLIC {
    @Override
    public String code() {
      return "public ";
    }
  },
  PRIVATE {
    @Override
    public String code() {
      return "private ";
    }
  },
  NONE {
    @Override
    public String code() {
      return "";
    }
  };

  public abstract String code();
}

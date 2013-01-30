package com.perunlabs.tool.jvalgen;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;

import com.perunlabs.tool.jvalgen.var.gen.JValuesGenerator;

public class Main {

  public static void main(String[] args) throws IOException {
    checkArgument(args.length == 1, "Need exactly one argument equal to destination dir.");
    new JValuesGenerator().generate(args[0]);
  }
}

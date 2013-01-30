package com.perunlabs.tool.jvalgen.java.gen;

import java.io.PrintWriter;

public class IndentingWriter {
  public static final String INDENT = "  ";
  public static final String INDENT2 = INDENT + INDENT;
  public static final String INDENT3 = INDENT2 + INDENT;

  private final PrintWriter printWriter;
  private boolean startedNewLine = true;

  public IndentingWriter(PrintWriter printWriter) {
    this.printWriter = printWriter;
  }

  public void println(String string) {
    printIndentIfNeeded();
    printWriter.println(string);
    startedNewLine = true;
  }

  private void printIndentIfNeeded() {
    if (startedNewLine) {
      printWriter.print(INDENT);
      startedNewLine = false;
    }
  }
}

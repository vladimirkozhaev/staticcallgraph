package org.staticcallgraph.dot;

import java.io.*;

/**
 * Encodes general Dot commands.
 */
public class DotGraphCommand implements Renderable{
  String command;

  /**
   * @param cmd a dot dommand string
   */
  public DotGraphCommand(String cmd) {
    this.command = cmd;
  }

  /**
   * Implements Renderable interface.
   * @param out the output stream
   * @param indent the number of indent space 
   * @see Renderable
   */
  public void render(OutputStream out, int indent) throws IOException {
    DotGraphUtility.renderLine(out, command, indent);
  }
}
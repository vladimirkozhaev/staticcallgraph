package org.staticcallgraph.dot;
import java.io.*;
import java.util.*;

/**
 * A Dot graph node with various attributes.
 */  
public class DotGraphNode implements Renderable{
  private String name;
  private List<DotGraphAttribute> attributes;

  public DotGraphNode(String name) {
    this.name = "\""+DotGraphUtility.replaceQuotes(name)+"\"";
  }

  // make any illegal name to be legal
  public String getName(){
    return this.name;
  }

  public void setLabel(String label) {
    label = DotGraphUtility.replaceQuotes(label);
    label = DotGraphUtility.replaceReturns(label);
    this.setAttribute("label", "\""+label+"\"");
  }

  public void setHTMLLabel(String label){
    label = DotGraphUtility.replaceReturns(label);
    this.setAttribute("label", label);
  }
  
  public void setShape(String shape) {
    this.setAttribute("shape", shape);
  }

  public void setStyle(String style) {
    this.setAttribute("style", style);
  }

  public void setAttribute(String id, String value) {
    if (this.attributes == null) {
      this.attributes = new LinkedList<DotGraphAttribute>();
    }
    
    this.setAttribute(new DotGraphAttribute(id, value));    
  }

  public void setAttribute(DotGraphAttribute attr) {
    if (this.attributes == null) {
      this.attributes = new LinkedList<DotGraphAttribute>();
    }
    
    this.attributes.add(attr);    
  }

  public void render(OutputStream out, int indent) throws IOException {
    StringBuffer line = new StringBuffer(this.getName());
    if (this.attributes != null) {
      line.append(" [");
      for (DotGraphAttribute attr : this.attributes) {
	line.append(attr.toString());
	line.append(",");
      }
      line.append("];");
    }
    DotGraphUtility.renderLine(out, new String(line), indent);
  }
}
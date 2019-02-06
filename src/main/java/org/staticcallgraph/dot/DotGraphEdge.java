package org.staticcallgraph.dot;
import java.io.*;
import java.util.*;

/* Graph edges are the major elements of a graph
 * @author Feng Qian 
 */
public class DotGraphEdge implements Renderable {
  private boolean isDirected;
  private DotGraphNode start, end;
  private List<DotGraphAttribute> attributes;

  /**
   * Draws a directed edge.
   * @param src, the source node
   * @param dst, the end node
   */
  public DotGraphEdge(DotGraphNode src, DotGraphNode dst){
    this.start = src;
    this.end   = dst;
    this.isDirected = true;
  }

  /**
   * Draws a graph edge by specifying directed or undirected.
   * @param src, the source node
   * @param dst, the end node
   * @param directed, the edge is directed or not
   */
  public DotGraphEdge(DotGraphNode src, DotGraphNode dst, boolean directed){
    this.start = src;
    this.end   = dst;
    this.isDirected = directed;
  }

  /**
   * Sets the label for the edge.
   * @param label, a label string
   */
  public void setLabel(String label){
    label = DotGraphUtility.replaceQuotes(label);
    label = DotGraphUtility.replaceReturns(label);
    this.setAttribute("label", label);
  }

  /**
   * Sets the edge style.
   * @param style, a style of edge
   * @see DotGraphConstants
   */
  public void setStyle(String style){
    this.setAttribute("style", style);
  }

  /**
   * Sets an edge attribute.
   * @param id, the attribute id to be set
   * @param value, the attribute value
   */
  public void setAttribute(String id, String value) {
    this.setAttribute(new DotGraphAttribute(id, value));    
  }

  /**
   * Sets an edge attribute.
   * @param attr, a {@link DotGraphAttribute} specifying the
   * attribute name and value.
   */
  public void setAttribute(DotGraphAttribute attr) {
    if (this.attributes == null) {
      this.attributes = new LinkedList<DotGraphAttribute>();
    }
    
    this.attributes.add(attr);    
  }

  public void render(OutputStream out, int indent) throws IOException {
    StringBuffer line = new StringBuffer(start.getName());
    line.append((this.isDirected)?"->":"--");
    line.append(end.getName());

    if (this.attributes != null) {
      
      line.append(" [");
      Iterator<DotGraphAttribute> attrIt = this.attributes.iterator();
      while (attrIt.hasNext()) {
	DotGraphAttribute attr = attrIt.next();
	line.append(attr.toString());
	line.append(",");
      }
      line.append("]");
    }

    line.append(";");

    DotGraphUtility.renderLine(out, new String(line), indent);
  }
}
package org.goldenport.atom.text

import scala.collection.mutable.{Stack, HashSet}
import scala.xml._

/*
 * @since   May.  5, 2009
 * @version Oct.  7, 2010
 * @author  ASAMI, Tomoharu
 */
class XmlTextMaker extends TextMaker {
  private val _stack = new Stack[(String, String)]
  private val _setted_ns = new HashSet[(String, String)]

  def xmlNs(prefix: String, namespace: String)(content: => Unit) {
    _stack.push((prefix, namespace))
    content
    _stack.pop
  }

  def element(qname: String, attrs: Seq[(String, String)] = Nil)(content: => Unit) {
//    System.out.println("qname = " + qname)
    print("<")
    print(qname)
    make_ns
    val checkpoint1 = checkpoint
    for ((k, v) <- attrs) {
      print(" ")
      print(k)
      print("=\"")
      print(v)
      print("\"")
    }
    print(">")
    val checkpoint2 = checkpoint
    content
    if (isModified(checkpoint2)) {
      print("</")
      print(qname)
      print(">")
    } else {
      rollback(checkpoint1)
      print("/>")
    }
  }

  def text(string: String) {
    print(string)
  }
  
  def xml(node: Node) {
	node match {
	  case g: Group => g.foreach(c => text(c.toString))
	  case _ => text(node.toString) 
	}
  }

  private def make_ns {
    for (pn <- _stack.reverse) {
      if (!_setted_ns.exists(_ == pn)) {
        print(" xmlns")
        val prefix = pn._1
        val ns = pn._2
        if (prefix != "") {
          print(":")
          print(prefix)
        }
        print("=\"")
        print(ns)
        print("\"")
        _setted_ns += pn
      }
    }
  }
}

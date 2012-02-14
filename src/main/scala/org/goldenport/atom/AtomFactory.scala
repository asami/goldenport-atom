package org.goldenport.atom

import scala.xml.{Elem, XML}

/*
 * @since   
 * @version Mar. 29, 2010
 * @author  ASAMI, Tomoharu
 */
object AtomFactory {
  def unmarshallFeed(string: String): AtomFeed = {
    unmarshallFeed(XML.loadString(string))
  }

  def unmarshallFeed(xml: Elem): AtomFeed = {
    val AtomFeed(feed) = xml
    feed
  }
}

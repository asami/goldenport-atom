package org.goldenport.atom

import scala.collection.mutable.ArrayBuffer
import java.net.URI
import org.goldenport.atom.text.XmlTextMaker

/*
 * @since   May.  5, 2009
 * @version Feb. 16, 2010
 * @author  ASAMI, Tomoharu
 */
abstract class AtomNode extends AtomCommonAttributes {
  def writeXml(maker: XmlTextMaker)
}

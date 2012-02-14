package org.goldenport.atom

import scala.collection.mutable.ArrayBuffer
import com.asamioffice.goldenport.text.UJson
import org.goldenport.atom.text.XmlTextMaker

/*
 * @since   May.  5, 2009
 * @version Jan.  4, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class AppCategories() {
  def writeXml(maker: XmlTextMaker)
  def toJson: String
}

case class InlineAppCategories(fixed: Option[Boolean], scheme: Option[String],
                             categories: List[AtomCategory]) extends AppCategories {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2007/app") {
      maker.xmlNs("atom", "http://www.w3.org/2005/Atom") {
        val attrs = List(fixed.map(f => ("fixed", if (f) "yes" else "no")),
                         scheme.map(s => ("scheme", s))).flatten
        maker.element("categories", attrs) {
          categories.foreach(_.writeXml(maker))
        }
      }
    }
  }

  def toJson = {
    UJson.tuples2Json(
      ("fixed", fixed.map(if (_) "yes" else "no")),
      ("scheme", scheme),
      ("categories", categories))
  }
}

case class OutOfLineAppCategories(href: String) extends AppCategories {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2007/app") {
      maker.xmlNs("atom", "http://www.w3.org/2005/Atom") {
        maker.element("categories", List("href" -> href)) {
        }
      }
    }
  }

  def toJson = {
    UJson.tuples2Json(
      ("href", href))
  }
}

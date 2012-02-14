package org.goldenport.atom

import scala.collection.mutable.ArrayBuffer
import com.asamioffice.goldenport.text.UJson
import org.goldenport.atom.text.XmlTextMaker

/*
 * @since   May.  5, 2009
 * @version Jan.  5, 2011
 * @author  ASAMI, Tomoharu
 */
case class AppService(workspaces: List[AppWorkspace], extensionElements: List[AppExtensionElement] = Nil) extends AppCommonAttributes {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2007/app") {
      maker.xmlNs("atom", "http://www.w3.org/2005/Atom") {
        maker.element("service") {
          workspaces.foreach(_.writeXml(maker))
        }
      }
    }
  }

  def toText: String = {
    val maker = new XmlTextMaker
    writeXml(maker)
//    """<?xml version="1.0" ?>""" + "\n" +
//    """<?xml-stylesheet type="text/xsl" href="/_/xsl/atomfeed.xsl"?>""" + "\n" +
    maker.toString
  }

  def toJson: String = {
    UJson.tuples2Json(
      ("workspace", workspaces.map(_.toJson).toList))
  }
}

case class AppWorkspace(title: AtomTitle, collections: List[AppCollection],
                      extensionSansTitleElements: List[AppExtensionSansTitleElement] = Nil) extends AppCommonAttributes {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2007/app") {
      maker.xmlNs("atom", "http://www.w3.org/2005/Atom") {
        maker.element("workspace") {
          maker.element("atom:title") {
            maker.text(title.value)
          }
          collections.foreach(_.writeXml(maker))
        }
      }
    }
  }

  def toJson: String = {
    UJson.tuples2Json(
      ("title", title.value),
      ("collection", collections.map(_.toJson).toList))
  }
}

case class AppCollection(title: AtomTitle, href: String,
                         accepts: List[AppAccept] = Nil,
                         categories: Option[AppCategories] = None,
                         extensionSansTitleElements: List[AppExtensionSansTitleElement] = Nil) extends AppCommonAttributes {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2007/app") {
      maker.xmlNs("atom", "http://www.w3.org/2005/Atom") {
        maker.element("collection", List("href" -> href)) {
          maker.element("atom:title") {
            maker.text(title.value)
          }
          accepts.foreach(_.writeXml(maker))
          categories.foreach(_.writeXml(maker))
        }
      }
    }
  }

  def toJson: String = {
    UJson.tuples2Json(
      ("title", title.value),
      ("categories", categories.map(_.toJson)))
  }
}

case class AppAccept(value: String) extends AppCommonAttributes {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2007/app") {
      maker.xmlNs("atom", "http://www.w3.org/2005/Atom") {
        maker.element("accept") {
          maker.text(value)
        }
      }
    }
  }
}

case class AppExtensionElement() {
}

case class AppExtensionSansTitleElement() {
}

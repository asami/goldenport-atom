package org.goldenport.atom

import scala.xml.{Elem, Node, XML}
import scala.collection.mutable.ArrayBuffer
import scala.util.control.NonFatal
import java.net.URI
import java.util.Date
import java.io.InputStream
import org.goldenport.atom.text.XmlTextMaker
import org.goldenport.util._

/*
 * @since   May.  4, 2009
 *  version Oct.  6, 2010
 *  version Feb. 14, 2012
 * @version Feb.  4, 2014
 * @author  ASAMI, Tomoharu
 */
class AtomFeed(
  parts: AtomNode*
) extends AtomNode {
  var atomId: AtomId = new AtomId("")
  var atomTitle: AtomTitle = new AtomTitle("")
  var atomUpdated: AtomUpdated = new AtomUpdated()
  var atomSubtitle: Option[AtomSubtitle] = None
  val atomCategories = new ArrayBuffer[AtomCategory]
  val atomAuthors = new ArrayBuffer[AtomAuthor]
  val atomContributers = new ArrayBuffer[AtomContributer]
  var atomRights: Option[AtomRights] = None
  var atomIcon: Option[AtomIcon] = None
  var atomLogo: Option[AtomLogo] = None
  val atomLinks = new ArrayBuffer[AtomLink]
  var atomGenerator: Option[AtomGenerator] = None
  val atomExtensionElements = new ArrayBuffer[AtomExtensionElement]
  val atomEntries = new ArrayBuffer[AtomEntry]

  for (part <- parts) {
    part match {
      case id: AtomId => atomId = id
      case title: AtomTitle => atomTitle = title
      case updated: AtomUpdated => atomUpdated = updated
      case entry: AtomEntry => atomEntries += entry
    }
  }

  def id = atomId.value
  def title = atomTitle.value
  def updated = atomUpdated.datetime
  def subtitle = atomSubtitle.getOrElse(NullAtomSubtitle).value
  def entries = atomEntries.toList
  def length = entries.length

  def addEntry(parts: AtomNode*): AtomEntry = {
    val entry = new AtomEntry(parts:_*)
    atomEntries += entry
    entry
  }

  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("feed") {
        atomId.writeXml(maker)
        atomTitle.writeXml(maker)
        atomUpdated.writeXml(maker)
        atomEntries.foreach(_.writeXml(maker))
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
    entries.map(_.toJson).mkString("[", ",", "]")
  }

  def toCsv: String = {
    entries.map(_.toCsv).mkString("[", ",", "]")
  }

  // Object
  override def toString(): String = { // XXX short form
    val maker = new XmlTextMaker
    writeXml(maker)
    maker.toString
  }
}

object AtomFeed {
  def apply(parts: AtomNode*): AtomFeed = {
    new AtomFeed(parts: _*)
  }

  def apply(parts: List[AtomNode]): AtomFeed = {
    new AtomFeed(parts: _*)
  }

  def apply(is: InputStream): AtomFeed = {
    unmarshall(XML.load(is))
  }

  def unmarshall(xml: Node): AtomFeed = {
    val feed = AtomFeed()
    feed.atomId = new AtomId((xml \ "id").text)
    feed.atomTitle = new AtomTitle((xml \ "title").text)
    feed.atomUpdated = new AtomUpdated((xml \ "updated").text)
    // link
    // updated
    feed.atomSubtitle = Some(new AtomSubtitle((xml \ "subtitle").text))
    feed.atomEntries ++= (xml \ "entry").map(entry => AtomEntry.unmarshall(entry))
    feed
  }

  def unapply(any: Any): Option[AtomFeed] = {
//    println("AtomFeed = " + any)
    any match {
      case atom: AtomFeed => Some(atom)
      case string: String => unapply(string)
      case xml: Elem => unapply(xml)
      case _ => None
    }
  }

  def unapply(string: String): Option[AtomFeed] = {
    try {
     Some(unmarshall(XML.loadString(string)))
    } catch {
      case NonFatal(_) => None
    }
  }

  def unapply(xml: Elem): Option[AtomFeed] = {
    try {
      Some(unmarshall(xml))
    } catch {
      case NonFatal(_) => None
    }
  }
}

case class AtomId(val value: String) extends AtomNode {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("id") {
        maker.text(value)
      }
    }
  }
}

case class AtomTitle(val value: String) extends AtomTextConstruct {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("title") {
        maker.text(value)
      }
    }
  }
}

case class AtomSubtitle(val value:String) extends AtomTextConstruct {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("subtitle") {
        maker.text(value)
      }
    }
  }
}

object NullAtomSubtitle extends AtomSubtitle("")

case class AtomUpdated(val datetime: VDateTime) extends AtomNode {
  def this(string: String) = this(VDateTime(string))
  def this(msec: Long) = this(VDateTime(msec))
  def this() = this(VDateTime())

  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("updated") {
        maker.text(datetime.toString)
      }
    }
  }
}

case class AtomCategory(val category: String) extends AtomNode {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("category") {
        maker.text(category)
      }
    }
  }
}

case class AtomAuthor(val author: String) extends AtomNode with AtomPersonConstruct {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("author") {
        maker.text(author)
      }
    }
  }
}

case class AtomContributer(val contributer: String) extends AtomPersonConstruct {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("contributer") {
        maker.text(contributer)
      }
    }
  }
}

case class AtomRights(rights: String) extends AtomTextConstruct {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("rights") {
        maker.text(rights)
      }
    }
  }
}

case class AtomIcon() extends AtomNode {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("icon") {
      }
    }
  }
}

case class AtomLogo() extends AtomNode {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("logo") {
      }
    }
  }
}

case class AtomLink() extends AtomNode {
  var linkType: String = ""
  var href: String = ""
  var rel: String = ""

  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("link") {
      }
    }
  }
}

object AtomLinkFactory {
  def unmarshall(xml: Node): AtomLink = {
    val link = AtomLink()
    link.linkType = (xml \ "@type").text
    link.href = (xml \ "@href").text
    link.rel = (xml \ "@rel").text
    link
  }
}

case class AtomGenerator() extends AtomNode {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("generator") {
      }
    }
  }
}

case class AtomExtensionElement(urn: String, prefix: String, name: String, value: String) extends AtomNode {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs(prefix, urn) {
      maker.element(name) {
        maker.text(value)
      }
    }
  }
}

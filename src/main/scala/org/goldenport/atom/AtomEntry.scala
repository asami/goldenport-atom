package org.goldenport.atom

import scala.collection.mutable.ArrayBuffer
import scala.xml.{Node, Elem, Text, Group, NodeSeq}
import java.util.Date
import com.asamioffice.goldenport.xml.UXML
import org.goldenport.xml.GXml
import org.goldenport.atom.text.XmlTextMaker
import org.goldenport.util.VDateTime

/*
 * @since   May.  4, 2009
 * @version Jan. 11, 2011
 * @author  ASAMI, Tomoharu
 */
class AtomEntry(
  parts: AtomNode*
) extends AtomNode() {
  var atomId: AtomId = new AtomId("")
  var atomTitle: AtomTitle = new AtomTitle("")
  var atomUpdated: AtomUpdated = new AtomUpdated()
  var atomPublished: Option[AtomPublished] = None
  val atomCategories = new ArrayBuffer[AtomCategory]
  val atomAuthors = new ArrayBuffer[AtomAuthor]
  val atomContributers = new ArrayBuffer[AtomContributer]
  var atomRights: Option[AtomRights] = None
  var atomSource: Option[AtomSource] = None
  var atomSummary: Option[AtomSummary] = None
  var atomContent: AtomContent = NullAtomContent
  val atomLinks = new ArrayBuffer[AtomLink]
  val atomExtensionElements = new ArrayBuffer[AtomExtensionElement]

  for (part <- parts) {
    part match {
      case id: AtomId => atomId = id
      case title: AtomTitle => atomTitle = title
      case updated: AtomUpdated => atomUpdated = updated
      case published: AtomPublished => atomPublished = Some(published)
      case content: AtomContent => atomContent = content
      case link: AtomLink => atomLinks += link
      case extension: AtomExtensionElement => atomExtensionElements += extension
    }
  }

  def id = atomId.value
  def title = atomTitle.value
  def updated = atomUpdated.datetime
  def contentText = atomContent.contentText
  def contentString = atomContent.contentString
  def contentXml = atomContent.contentXml

  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("entry") {
        atomId.writeXml(maker)
        atomTitle.writeXml(maker)
        atomPublished.foreach(_.writeXml(maker))
        atomUpdated.writeXml(maker)
        atomContent.writeXml(maker)
        atomLinks.foreach(_.writeXml(maker))
        atomExtensionElements.foreach(_.writeXml(maker))
      }
    }
  }

  def toText: String = {
    val maker = new XmlTextMaker
    writeXml(maker)
    maker.toString
  }

  def toJson: String = {
    atomContent.mimeType match {
      case "application/json" => atomContent.contentText
      case _ => "***Under construction***"
    }
  }

  def toCsv: String = {
    atomContent.mimeType match {
      case "text/csv" => atomContent.contentText
      case _ => "***Under construction***"
    }
  }

  // Object
  override def toString(): String = { // XXX short form
    val maker = new XmlTextMaker
    writeXml(maker)
    maker.toString
  }
}

object AtomEntry {
  def apply(parts: AtomNode*) = new AtomEntry(parts: _*)

  def unapply(xml: Elem): Option[AtomEntry] = {
    Some(unmarshall(xml))
  }

  def unmarshall(xml: Node): AtomEntry = {
    val entry = new AtomEntry()
    entry.atomId = new AtomId((xml \ "id").text)
    entry.atomTitle = new AtomTitle((xml \ "title").text)
    entry.atomUpdated = new AtomUpdated((xml \ "updated").text)
    entry.atomContent = AtomContent(xml \ "content")
//    println("xxx = " + (xml \ "content").flatMap(x => x.child))
//    log_debug("AtomContent", entry.atomContent.value)
    entry.atomLinks ++= (xml \ "link").map(link => AtomLinkFactory.unmarshall(link))
    entry
  }
}

case class AtomSource(val source: String) extends AtomNode with AtomCommonAttributes {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("source") {
        maker.text(source)
      }
    }
  }
}

case class AtomSummary(val summary: String) extends AtomNode with AtomTextConstruct {
  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("summary") {
        maker.text(summary)
      }
    }
  }
}

abstract class AtomContent extends AtomNode {
  val mimeType: String
  def contentText: String
  def contentString: String
  def contentXml: List[Node]

  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("content", List("type" -> mimeType)) {
        maker.text(UXML.escapeCharData(contentString))
      }
    }
  }
}

class InlineTextAtomContent(val text: String) extends AtomContent {
  val mimeType = "text"
  def contentText = text
  def contentString = text
  def contentXml = List(Text(text))
}

class InlineHtmlAtomContent(val text: String) extends AtomContent {
  val mimeType = "html"
  def contentText = text
  def contentString = text
  def contentXml = List(Text(text))
}

class InlineXHtmlAtomContent(val xhtml: List[Node]) extends AtomContent {
  val mimeType = "xhtml"
  def contentText = xhtml.map(_.text).mkString
  def contentString = xhtml.map(_.toString).mkString
  def contentXml = xhtml
}

class InlineOtherAtomContent(val xml: List[Node], val mimeType: String) extends AtomContent {
  def this(string: String, mimetype: String) = this(List(Text(string)), mimetype)

  def contentText = xml.map(_.text).mkString
  def contentString = xml.map(_.toString).mkString
  def contentXml = xml
}

class OutoflineAtomContent(val src: String, val mimeType: String) extends AtomContent {
  def contentText = ""
  def contentString = ""
  def contentXml = Nil
}

object AtomContent {
  def apply(any: Any): AtomContent = {
    any match {
      case s: String => apply(s)
      case xml: NodeSeq => apply(xml)
      case _ => apply(any.toString)
    }
  }

  def apply(string: String): AtomContent = {
    new InlineTextAtomContent(string)
  }

  def apply(nodes: NodeSeq): AtomContent = {
    if (nodes.length == 0) NullAtomContent
    else apply(nodes.head)
  }

  def apply(node: Node): AtomContent = {
    GXml.attribute(node, "src") match {
      case Some(v) => {
        new OutoflineAtomContent(
          v,
          GXml.attribute(node, "type").getOrElse(""))
      }
      case None => {
        GXml.attribute(node, "type") match {
          case Some("text") => new InlineTextAtomContent(node.text)
          case Some("html") => new InlineHtmlAtomContent(
            node.child.map(_.toString).mkString)
          case Some("xhtml") => new InlineXHtmlAtomContent(node.child.toList)
          case Some(t) => new InlineOtherAtomContent(node.child.toList, t)
          case None => new InlineOtherAtomContent(node.child.toList, "")
        }
      }
    }
  }
}

object NullAtomContent extends InlineTextAtomContent("") {
  override def writeXml(maker: XmlTextMaker) {} 
}

case class AtomPublished(val datetime: VDateTime) extends AtomNode {
  def this(string: String) = this(VDateTime(string))
  def this(msec: Long) = this(VDateTime(msec))
  def this() = this(VDateTime())

  def writeXml(maker: XmlTextMaker) {
    maker.xmlNs("", "http://www.w3.org/2005/Atom") {
      maker.element("published") {
        maker.text(datetime.toString)
      }
    }
  }
}

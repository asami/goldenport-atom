package org.goldenport.atom

import scala.collection.mutable.ArrayBuffer

/*
 * @since   May.  5, 2009
 * @version Mar. 27, 2010
 * @author  ASAMI, Tomoharu
 */
abstract class AtomText {
}

case class AtomTextText() extends AtomText {
}

case class AtomHtmlText() extends AtomText {
}

case class AtomXHtmlText() extends AtomText {
}

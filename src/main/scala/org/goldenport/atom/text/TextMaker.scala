package org.goldenport.atom.text

import java.io.Flushable

/*
 * @since   May.  5, 2009
 * @version Jul.  5, 2009
 * @author  ASAMI, Tomoharu
 */
abstract class TextMaker {
  type MAKER <: TextMaker
  private val _buffer = new StringMaker
  private var _indent = 0
  private var _indent_width = 2
  private var _afterNl = true
  private var _eol = "\n"

  def append(aTemplate: CharSequence, theReplaces: Map[String, String]): MAKER = {
    var text = aTemplate
    for ((regex, target) <- theReplaces.elements) {
      text = regex.r.replaceAllIn(text, target)
    }
    append(text)
    this.asInstanceOf[MAKER]
  }

  def append(cs: CharSequence): MAKER = {
    _buffer.append(cs)
    this.asInstanceOf[MAKER]
  }

  def replace(aRegex: String, aTarget: String) {
    _buffer.replace(aRegex, aTarget)
  }

  private def print_indent() {
    if (_afterNl) {
      for (i <- 0 until _indent; w <- 0 until _indent_width) {
	_buffer.append(' ')
      }
    }
  }

  final def indentUp() {
    _indent += 1
  }

  final def indentDown() {
    _indent -= 1
  }

  final def print(any: Any) {
    print(any.toString)
  }

  def print(cs: CharSequence): MAKER = {
    print_indent()
    _buffer.append(cs)
    _afterNl = false
    this.asInstanceOf[MAKER]
  }

  final def print(c: Char) {
    print_indent()
    _buffer.append(c)
    _afterNl = false
  }    

  final def println(aText: CharSequence) {
    print_indent()
    _buffer.append(aText)
    println()
  }

  final def println(c: Char) {
    print_indent()
    _buffer.append(c)
    println()
  }

  final def println() {
    _buffer.append(_eol)
    _afterNl = true
  }

  final def checkpoint: StringMakerCheckPoint = {
    _buffer.checkpoint
  }

  final def isModified(checkpoint: StringMakerCheckPoint) = {
    _buffer.isModified(checkpoint)
  }

  final def rollback(checkpoint: StringMakerCheckPoint) {
    _buffer.rollback(checkpoint)
  }

  final def popup(mark: String) {
    _buffer.popup(mark)
  }

  override def toString = {
    _buffer.pushback()
    _buffer.toString
  }
}

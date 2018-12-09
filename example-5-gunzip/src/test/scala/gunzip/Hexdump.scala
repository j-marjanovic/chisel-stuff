/*
MIT License

Copyright (c) 2018 Jan Marjanovic

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package gunzip

object Hexdump {
  private val NUM_BYTES_PER_LINE = 8

  private val FMT_RED = "\u001b[31m"
  private val FMT_GREEN = "\u001b[32m"
  private val FMT_ENDC = "\u001b[39m"


  def diff(fst: Array[Byte], snd: Array[Byte]): Unit = {
    val l = if (fst.length < snd.length) fst.length else snd.length

    // print first line
    print("     .")
    for (i <- 0 until NUM_BYTES_PER_LINE) {
      print(f"    ${i}%2d")
    }
    print("\n")

    // print bytes
    for (i <- 0 until l) {
      if (i % NUM_BYTES_PER_LINE == 0 && i != 0) {
        print("\n")
      }

      if (i % NUM_BYTES_PER_LINE == 0) {
        print(f"${i}%4x | ")
      }

      val fst_str =
        if (fst(i) == snd(i)) { FMT_GREEN + "   " }
        else                  { FMT_RED + f"${fst(i)}%02x/" }
      val snd_str = f"${snd(i)}%02x "
      print(s"${fst_str}${snd_str}" + FMT_ENDC)
    }

    // print last \n if it was not printed by for loop
    if (fst.length % NUM_BYTES_PER_LINE != 0) {
      print("\n")
    }

    if (fst.length != snd.length) {
      println(FMT_RED + s"Lengts do not match (${fst.length} != ${snd.length})" + FMT_ENDC)
    }
  }
}

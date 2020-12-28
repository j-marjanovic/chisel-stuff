/*
Copyright (c) 2020 Jan Marjanovic

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

package presence_bits_comp

import bfmtester._

import scala.collection.mutable.ListBuffer

class DecompressorInputAdapterTest(c: DecompressorInputAdapter) extends BfmTester(c) {
  private def seq_to_bigint(xs: Seq[Byte]): BigInt = {
    var tmp: BigInt = 0
    for (x <- xs.reverse) {
      tmp <<= 8
      tmp |= x & 0xff
    }
    tmp
  }

  val DATA_LEN = 32
  val PKT_SIZE = 16 // = 128/8
  val mod_driver = new DecompressorInputAdapterDriver(c.io.from_axi, bfm_peek, bfm_poke, println)
  val mod_monitor =
    new DecompressorInputAdapterMonitor(
      c.io.to_kernel,
      this.rnd,
      DATA_LEN * (PKT_SIZE - 1),
      bfm_peek,
      bfm_poke,
      println
    )

  val exp_resp: ListBuffer[BigInt] = ListBuffer()
  for (y <- 0 until DATA_LEN) {
    val xs: Seq[Byte] = (0 until PKT_SIZE).map(x => (x | ((y & 0xf) << 4)).toByte)
    mod_driver.add_data(xs)
    exp_resp.append(seq_to_bigint(xs))
  }

  step(200)

  val resp: List[Byte] = mod_monitor.get_data()
  for (i <- 0 until DATA_LEN) {
    val xs = seq_to_bigint(resp.slice(i * PKT_SIZE, i * PKT_SIZE + PKT_SIZE))
    println(f"received data = ${xs}%x")
    expect(xs == exp_resp(i), "recv data should match expected data")
  }
}

package com.github.tashoyan.tfidf

import com.github.tashoyan.tfidf.TfIdf._
import org.scalatest.FunSuite

class TfIdfTest extends FunSuite {

  test("calcTfIdf - natural text") {
    val docs = Seq(
      "one flesh one bone one true religion",
      "all flesh is grass",
      "one is all all is one"
    )
      .map(_.split("\\s+").toSeq)

    val result = calcTfIdf(docs)

    val result0 = result.filter(_.docId == 0)
    assert(result0.size === 5, "Number of distinct tokens in doc 0")
    assert(result0.find(_.token == "one").get.tfIdf === 0.8630462173553426, "Doc 0 token 'one'")
    assert(result0.find(_.token == "flesh").get.tfIdf === 0.28768207245178085, "Doc 0 token 'flesh'")

    val result1 = result.filter(_.docId == 1)
    assert(result1.size === 4, "Number of distinct tokens in doc 1")
    assert(result1.find(_.token == "flesh").get.tfIdf === 0.28768207245178085, "Doc 1 token 'flesh'")
    assert(result1.find(_.token == "grass").get.tfIdf === 0.6931471805599453, "Doc 1 token 'grass'")

    val result2 = result.filter(_.docId == 2)
    assert(result2.size === 3, "Number of distinct tokens in doc 2")
    assert(result2.find(_.token == "one").get.tfIdf === 0.5753641449035617, "Doc 1 token 'one'")
    assert(result2.find(_.token == "all").get.tfIdf === 0.5753641449035617, "Doc 1 token 'all'")
  }

  test("calcTfIdf - dummy tokens") {
    val docs = Seq(
      Seq("aa", "bb", "aa", "cc"),
      Seq("aa", "cc", "dd", "dd"),
      Seq("cc")
    )
    val result = calcTfIdf(docs)

    val result0 = result.filter(_.docId == 0)
    assert(result0.size === 3, "Number of distinct tokens in doc 0")
    assert(result0.find(_.token == "aa").get.tfIdf === 0.5753641449035617, "Doc 0 token 'aa'")
    assert(result0.find(_.token == "bb").get.tfIdf === 0.6931471805599453, "Doc 0 token 'bb'")
    assert(result0.find(_.token == "cc").get.tfIdf === 0.0, "Doc 0 token 'cc'")

    val result1 = result.filter(_.docId == 1)
    assert(result1.size === 3, "Number of distinct tokens in doc 1")
    assert(result1.find(_.token == "aa").get.tfIdf === 0.28768207245178085, "Doc 1 token 'aa'")
    assert(result1.find(_.token == "cc").get.tfIdf === 0.0, "Doc 1 token 'cc'")
    assert(result1.find(_.token == "dd").get.tfIdf === 1.3862943611198906, "Doc 1 token 'dd'")

    val result2 = result.filter(_.docId == 2)
    assert(result2.size === 1, "Number of distinct tokens in doc 2")
    assert(result2.find(_.token == "cc").get.tfIdf === 0.0, "Doc 2 token 'cc'")
  }

  test("calcTfIdf - docs is null") {
    intercept[NullPointerException] {
      calcTfIdf(null)
    }
  }

  test("calcTfIdf - docs is empty") {
    val result = calcTfIdf(Seq())
    assert(result.isEmpty, "Empty doc corpus - empty result")
  }

}

package com.github.tashoyan.tfidf

import com.github.tashoyan.tfidf.TfIdf._

import scala.collection.mutable

case class IdDoc(id: Long, doc: Doc)
case class TokenTf(docId: Long, token: String, tf: Long)
case class TokenTfIdf(docId: Long, token: String, tfIdf: Double)

/**
  * TF*IDF calculator.
  */
object TfIdf {
  type Doc = Seq[String]

  /**
    * Calculates TF*IDF for terms in the document corpus.
    *
    * @param docs Document corpus - a collection of bags of words.
    * @return Collection of TF*IDF entries.
    *         Each entry has a TF*IDF value of a term in a document;
    *         a term may have multiple entries corresponding to different documents in the corpus.
    *         Documents are assigned with numeric identifiers;
    *         the order of identifiers is the same as the order of documents in the corpus.
    */
  def calcTfIdf(docs: Seq[Doc]): Seq[TokenTfIdf] = {
    val idDocs = withIdsDocs(docs)
    val tfs = tokenTfs(idDocs)
    val idfs = tokenIdfs(idDocs)
    tokenTfIdfs(tfs, idfs)
  }

  private def withIdsDocs(docs: Seq[Doc]): Seq[IdDoc] = {
    docs.zipWithIndex
      .map { case (doc, idx) => IdDoc(idx.toLong, doc) }
  }

  private def tokenTfs(idDocs: Seq[IdDoc]): Seq[TokenTf] = {
    idDocs.flatMap(docTokenTfs)
  }

  private def docTokenTfs(idDoc: IdDoc): Seq[TokenTf] = {
    val tokenCounters = mutable.Map[String, Long]()
      .withDefaultValue(0)
    val it = idDoc.doc.iterator
    while (it.hasNext) {
      val token = it.next()
      tokenCounters(token) = tokenCounters(token) + 1
    }
    tokenCounters.toSeq
      .map { case (token, tf) => TokenTf(idDoc.id, token, tf) }
  }

  private def tokenDfs(idDocs: Seq[IdDoc]): Map[String, Long] = {
    val docCounters = mutable.Map[String, Long]()
      .withDefaultValue(0)
    val it = idDocs.iterator
    while (it.hasNext) {
      val idDoc = it.next()
      val docTokens = idDoc.doc.distinct
      docTokens.foreach { token =>
        docCounters(token) = docCounters(token) + 1
      }
    }
    docCounters.toMap
  }

  private def tokenIdfs(idDocs: Seq[IdDoc]): Map[String, Double] = {
    val docFreqs = tokenDfs(idDocs)
    val docCorpusSize = idDocs.size.toLong
    docFreqs.mapValues(idf(docCorpusSize))
  }

  private def idf(docCorpusSize: Long)(df: Long): Double = math.log((docCorpusSize.toDouble + 1) / (df.toDouble + 1))

  private def tokenTfIdfs(tfs: Seq[TokenTf], idfs: Map[String, Double]): Seq[TokenTfIdf] = {
    tfs.map(tokenTf => TokenTfIdf(tokenTf.docId, tokenTf.token, tfIdf(tokenTf.tf, idfs(tokenTf.token))))
  }

  private def tfIdf(tf: Long, idf: Double): Double = tf * idf

}

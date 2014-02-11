package models

package object text {

  trait WithTweet {
    def tweet: Tweet
  }

  trait Tokenized {
    def tokens: List[String]
  }

  trait Stemmed {
    def stems: List[String]
  }

  trait Analysed {
    def categories: Map[String, Int]
    def positive: Double
    def negative: Double
  }

  trait AnalysedBis {
    def positive: Double
    def negative: Double
  }
}

package doric
package syntax

import doric.sem.{ColumnNotFound, ColumnTypeError, DoricMultiError}

import org.apache.spark.sql.functions.{col => sparkCol}
import org.apache.spark.sql.types.{IntegerType, StringType}

class AsSpec extends DoricTestElements {

  describe("as method") {
    import spark.implicits._

    val df = List((1, "1")).toDF("int", "str")

    it("should return a SparkError if the column doesn't exist") {
      val originalColumn = sparkCol("error").asDoric[Int]

      intercept[DoricMultiError] {
        df.select(originalColumn)
      } should containAllErrors(
        ColumnNotFound("error", List("int", "str"))
      )
    }

    it("should return a SparkError if the column doesn't match the type") {
      val originalColumn = sparkCol("int").asDoric[String]

      intercept[DoricMultiError] {
        df.select(originalColumn)
      } should containAllErrors(
        ColumnTypeError("int", StringType, IntegerType)
      )
    }
  }

}

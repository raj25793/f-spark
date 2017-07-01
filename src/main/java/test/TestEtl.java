package test;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import org.apache.spark.sql.types.DataTypes;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;

public class TestEtl {

    private static final SimpleDateFormat REDSHIFT_DATE_FORMATTER = new SimpleDateFormat("yyyyMMddHHmm");

    private static final String           DATE_FORMATTER          = "yyyyMMddHHmm";

    public static void main(String[] args) {

        SparkSession spark = SparkSession.builder().master("local").appName("MongoSparkConnectorIntro")
                .config("spark.mongodb.input.uri", "mongodb://54.175.141.216/feed.feed")
                .config("spark.mongodb.output.uri", "mongodb://54.175.141.216/feed.feed").getOrCreate();
        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());
        Date date = new Date(1494645785000L);
        String oid = new ObjectId(date).toHexString();
        JavaMongoRDD<Document> rdd = MongoSpark.load(jsc);
        Dataset<Row> ds = rdd.toDF().select(
                functions.col("children_readings").getField("child_identifier").getItem(0).cast(DataTypes.StringType)
                        .as("child_identifier"),
                functions.col("children_readings").getField("readings").getField("current").getItem(0)
                        .cast(DataTypes.StringType).as("current"),
                functions.col("children_readings").getField("readings").getField("voltage").getItem(0)
                        .cast(DataTypes.StringType).as("voltage"),
                functions.col("children_readings").getField("readings").getField("power").getItem(0)
                        .cast(DataTypes.StringType).as("power"),
                functions.col("_id"));

        ds.printSchema();

        JavaRDD<RawData> javaRdd = ds.filter(e -> {
            return new ObjectId(((GenericRowWithSchema) e.getAs("_id")).getAs("oid").toString()).getDate().after(date);
        }).toJavaRDD().map(p -> {
            Long timeKey = Long.valueOf(
                    REDSHIFT_DATE_FORMATTER.format(
                            new ObjectId(((GenericRowWithSchema) p.getAs("_id")).getAs("oid").toString()).getDate()));
            System.out.println(p.get(0));
            RawData rd = new RawData(
                    p.getAs("child_identifier"),
                    timeKey,
                    Double.valueOf(p.getAs("voltage")),
                    Double.valueOf(0),
                    Double.valueOf(p.getAs("current")),
                    Double.valueOf(p.getAs("power")));
            return rd;
        });

        ds = spark.createDataFrame(javaRdd, RawData.class);

        System.out.println(ds.count());
        System.out.println(ds.first());

    }
}

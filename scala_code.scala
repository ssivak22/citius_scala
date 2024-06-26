import org.apache.spark.sql.sparksession
import org.apache.spark.sql.functions._
import org.apache.commons.io.FileUtils
import java.sql.DriverManager


//Creating a SparkSession
val spark = sparksession.builder().appName("VisitCountPerProvider").getOrCreate()

//splitting the data from given providers csv raw data
val line = "provider_id|provider_specialty|first_name|middle_name|last_name"
val Array(provider_id, provider_specialty, first_name, middle_name, last_name) = line.split("|")

// Writting the splitted column values into new excel sheet as below
val line = "C:/Users/garun/OneDrive/Desktop/ssivak22_Git/citius_scala/prvoutput.csv"
df.write
.format(csv)
.option("header", "true")
.save ("line")

// I am reading the output providers data from a csv/excel file as providersDF
val providersDF = spark.read.option("header", "true").csv("C:/Users/garun/OneDrive/Desktop/ssivak22_Git/citius_scala/prvoutput.csv")

// I am reading given visits data from a csv/excel file as visitsDF
val visitsDF = spark.read.option("header", "true").csv("C:/Users/garun/OneDrive/Desktop/ssivak22_Git/citius_scala/visits.csv")

// Joining the above 2 dataframes to get provider details with visit information (Inner Join operation performed using the provider_id column)
val joinedDF = providersDF.join(visitsDF, Seq("provider_id"))

// After joining the 2 datasets i am grouping the values by provider ID, name, and specialty, summing up the number of visits
val totalVisitsDF = joinedDF.groupBy("provider_id", "provider_name", "specialty").agg(count("*").alias("total_visits"))

// Converting the above DataFrame into JSON format which partitioned by specialty as per problem request
val jsonResult = totalVisitsDF
  .select( "provider_id", "provider_name", "specialty", "total_visits")
  .toJSON 
  .collect() 
  .mkString("\n") 

println(jsonResult) 

// Stop the SparkSession
spark.stop()
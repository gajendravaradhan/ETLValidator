[![MIT License](https://img.shields.io/apm/l/atomic-design-ui.svg?)](https://github.com/tterb/atomic-design-ui/blob/master/LICENSEs)

# ETL Validator

ETL Validator is a tool that can read data files, perform transformations on them and compare them against any similar data sets. It supports Cucumber BDD based steps for writing anything from simple to complex data manipulation processes on data sets of any size. It has a set of prebuilt steps to read, write and compare data sets
It is divided into 4 stages, 
<ul>
<li>Extract</li>
<li>Transform</li>
<li>Report</li>
<li>Validate</li>
</ul>

## Installation

It can be run as a standalone Cucumber Java BDD project or be integrated with your existing framework either as a module or as a JAR. Just clone the project and get going!

## Components

#### Data Extractor

Data extractor is responsible for reading data from json, csv, excel, orc, parquet files or sql tables.

#### Meta Validator

MetaValidator validates the incoming file if it:
<ul>
<li>Exists</li>
<li>Is of the right type</li>
<li>Is not empty</li>
</ul>

#### Data Transformation

This is where all the the business logic of data transformations happen. Any custom transformation you need should be added to the ```DataTransformerImpl``` class and its method signature added to the parent interface. If you are consuming this project as a jar, you should be extending the ```DataTransformerImpl``` class

#### Reporter

This is where the calculated result is written to a file in output directory. You can write outputs to different file formats including:

<ul>
<li>JSON</li>
<li>CSV</li>
<li>EXCEL</li>
<li>PARQUET</li>
<li>ORC</li> 
<li>SQL Tables (Most common databases that have APIs to connect with Java code)</li>  
</ul>

#### Validator

Validator contains all the useful assertion methods for comparing datasets, asserting row count, schema etc. I have provided two ways to compare datasets
<ul>
<li>Compare data row by row</li>
<li>Compare data column by column</li>
</ul>

#### External Data Handling

ETL Validator provides external data support through its json interface ```ETLDataHandler```. Be sure to call its setInstance method with your JSON Data so that the ETL Validator gets all the external data it will need access to.

#### ETLStepDefinitions

This class contains commonly used data manipulation step definitions like:

<ul>
<li>Reading data from files or databases</li>
<li>Compare data</li>
<li>Perform any transformations on data</li>
<li>Write results back to files or databases</li>
</ul>

Apart from existing step definitions, this class can be extended in your automation framework and additional steps can be added
____________________________________________________________________________________________________________________________________

## Sample Project Demo
**Test.feature**

The Test.feature contains a sample scenario that tests an ETL process, where a json file is being merged with a csv file and a filter transformation is being applied on it.
Then we validate the result data set with an expected json file.

## Usage

### As a standalone project
Run the FunctionalCukesTest class under src/test/java after uncommenting the constructor class within ETLStepDefinitions.java

### As an external integration
1. Consume the jar in your test automation framework using build tools like maven, gradle, sbt or add the jar as dependency to your project
2. Setup the ETL instance by calling

    ```ETLDataHandler.setInstance(<your queries file as json string>);```
    
    from your test setup method or test hooks methods. It takes as parameter, all your queries converted into json string. This json data structure is what feeds all the external query data to the tool.
3. For Cucumber based projects, it is recommended to add this line to your before all or before hooks and specify an exclusive tagged hook for running etl tests. That way, your web or api tests will not be interrupted or affected by this etl integration
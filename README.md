[![MIT License](https://img.shields.io/apm/l/atomic-design-ui.svg?)](https://github.com/tterb/atomic-design-ui/blob/master/LICENSEs)

# ETL Validator

ETL Validator is a tool that can read data files, perform transformations on them and compare them against any similar datasets. It uses Google Guice for data injection.
It is divided into 3 stages, 
<ul>
<li>Extract</li>
<li>Transform</li>
<li>Validate</li>
</ul>

## Installation

Clone the project from github.

## Components

#### Data Extractor

Data extractor is responsible for reading data from json,csv,excel, or sql tables. Before reading, the files are validated by the Data Validator.

#### Data Validator

Data Validator validates the incoming file if it:
<ul>
<li>Exists</li>
<li>Is of the right type</li>
<li>Is not empty</li>
</ul>

#### Data Transformation

This is where all the the business logic of data transformations happen. Any custom transformation you need should go in here as methods.

#### Reporter

This is where the calculated result is written to a file in output directory. You can write outputs to different file formats including:

<ul>
<li>JSON</li>
<li>CSV</li>
<li>EXCEL</li>
<li>PARQUET</li>
<li>ORC</li> 
<li>SQL Tables (Most common databases athat have APIs to connect with Java code)</li>  
</ul>

#### Validator

This is where the comparison of two datasets happen. I have provided two ways to compare datasets
<ul>
<li>Compare data row by row</li>
<li>Compare data column by column</li>
</ul>

In both the cases, I show the reports with the primary key so that it will be easier to know which records have mismatch between source and target.

#### Orchestration

All this is orchestrated by the Orchestrator class which orchestrates the *'ETL'* process.

## Guice Injection

#### App Module

the App module class is responsible for the Guice binding of the ETL interfaces to their implementation. It also takes care of the named binding of the properties from config.properties class

#### ETLEngine

This is the main class that provides Guice injection to the Orchestrator and triggers the ETL process.

____________________________________________________________________________________________________________________________________

## Sample Project Demo
**ETL**
Takes two input files one json and another csv. Merges the two based on the id field and groups the amount for each of the transaction codes. Shows the sum only if the code is specifically "CGDLT" or CGDST". Shows null otherwise

**Validation**
We validate this transformation by comparing the result with an expected file **expected.json**

## Usage

Run the ETLEngine class

# Onboarding Calculation Engine

Hadron onboarding project for Spark Calculation Engine. It takes a JSON transaction core file and merges it with the transaction core lot file to produce a transaction core summary file.
It is divided into 3 stages, 
<ul>
<li>Extract</li>
<li>Transform</li>
<li>Load</li>
</ul>

## Installation

git clone the project using:
```gitexclude
git@gitlab.awstrp.net:trpho94/onboarding-oms.git
```

## Components

#### Data Extractor

Data extractor is responsible for reading data from json and csv files using spark. Before reading, the files are validated by the Data Validator.

#### Data Validator

Data Validator validates the incoming file if it:
<ul>
<li>Exists</li>
<li>Is of the right type</li>
<li>Is not empty</li>
</ul>

#### Data Transformation

This is where the business logic of joining and filtering the transaction core and transaction core lot to generate the transaction core summary.

**Calculation Requirement**

**Attribute:** transaction_lot_total_amount

**Filter logic:** transaction_code is 'CGDLT' or 'CGDST'

**Calculation logic:** the sum of the values of transaction_lot_amount

#### Data Loading

This is where the calculated result is written to a file in output directory.

#### Orchestration

All this is orchestrated by the CalculationOrchestrator class which orchestrates the *'ETL'* process.

## Guice Injection

#### App Module

the App module class is responsible for the Guice binding of the ETL interfaces to their implementation. It also takes care of the named binding of the properties from config.properties class

#### CalculationEngine

This is the main class that provides Guice injection to the Orchestrator and triggers the ETL process.

## Usage

```bash
gradle clean
gradle build

```

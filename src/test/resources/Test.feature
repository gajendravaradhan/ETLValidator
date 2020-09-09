Feature: Test feature

  @test
  Scenario: ETL Validation example
    Given I start an etl validation session
    Given I read data from "src/test/resources/data/transaction_core.json" json file
    And I read data from "src/test/resources/data/transaction_core_lot.csv" csv file with a header
    Then I perform transformation on transaction_core dataset and transaction_core_lot dataset using queries.transaction_mapping
    Then I write "result" dataset to "target/output/result.json" file in json format
    Then I read data from "src/test/resources/data/expected.json" json file
    Then I compare expected and result data by column having primary key "transaction_unique_identifier" and store it in "final" dataset
    Then I print "20" rows of "final" dataset onto the screen

# Load tests

Runs load/performance tests for read api

# Steps to run load tests

 - Run gradle task `gradle copyDeps` to fetch all the dependencies
 - Now one can record scenarios using `bin/recorder.sh` script or run the existing scenario using `bin/gatling.sh`
 - The result of tests will be generated inside the `results` directory



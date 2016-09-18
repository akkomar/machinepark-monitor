# Machine park monitor
## Running
Start `pl.akkomar.machinepark.Boot`, alerts will be printed to standard output.

## Cassandra
### Local instance
#### Start
    docker-compose up
    
To remove:
    
    docker-compose rm
    
#### Setup schema
    cat create-schema.cql |  xargs -0 -I "{}" docker exec  machineparkmonitor_cassandra_1 cqlsh -e "{}"
    

## Things to do
* moving averages
* GUI
* Environmental Correlation Analysis
** persistent storage for measurements
** deploy to the cloud
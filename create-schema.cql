CREATE KEYSPACE IF NOT EXISTS machinepark WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };

CREATE TABLE IF NOT EXISTS machinepark.current (
machine_name text,
date text,
event_time timestamp,
current decimal,
PRIMARY KEY ((machine_name,date),event_time)
)

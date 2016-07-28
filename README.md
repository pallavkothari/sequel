# Sequel


A tool for analyzing and indexing SQL queries. 
Several useful nodes in the AST will get indexed separately, and tagged 
with a stable ID, so that index generation is idempotent. 

Speaking of ElasticSearch:
```brew install elasticsearch; elasticsearch;```

No sql scripts are provided -- just place them in /src/main/resources and run SimpleTest. 
Parsed results will be placed in an output folder, along with some useful info on the console. 

For maximum win, hook up kibana to explore the generated index: 
```brew search kibana```

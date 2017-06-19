This demo aims to show how Spring Cloud Dataflow is used to collect event messages from Pivotal CloudCache and bulk upload into AWS S3.

### Step #1 - create new region
* gfsh>create region --name=transactions --type=PARTITION

### Step #2 - start SCDF server
* remember to bind with pcc cluster, check dataflow-manifest.yml for reference.

### Step #3 - register pcc source app
1. redirect to "scdf-pcc-source" folder, build this maven project
2. upload the jar file to a public accessable url, such as *https://s3.amazonaws.com/spring-cloud-data-flow/source/pcc-source.jar* 
3. register this new source app into dataflow server by running command 
**dataflow:> app register --name pcc-demo --type source --uri https://s3.amazonaws.com/spring-cloud-data-flow/source/pcc-source.jar**
* url above is fake

### Step #4 - register s3 sink app
1. redirect to "scdf-s3-sink" folder, build this maven project
2. upload the jar file to a public accessable url, such as *https://s3.amazonaws.com/spring-cloud-data-flow/sink/s3-sink.jar*
3. register this new sink app into dataflow server by running command
**dataflow:> app register --name s3-demo --type sink --uri https://s3.amazonaws.com/spring-cloud-data-flow/sink/s3-sink.jar**
* url above is fake

### Step #5 - deploy SCDF stream
* stream definiton template:
**dataflow:> stream create test --definition "pcc-demo --pcc.cache.region-name=transactions | s3-demo --s3.count=100 --s3.access-key=xxx --s3.secret-key=xxx --s3.bucket-name=xxx --s3.bucket-region=us-east-1" --deploy**

### Step #6 - start to load fake transactions
1. redirect to "transaction-dataloader" repository (*https://github.com/liwang-pivotal/pcc-dataloader*), build this maven project
2. deploy this app into CF environment
3. use the UI to load fake data, input parameter is the batch size to load (100 is recommended)

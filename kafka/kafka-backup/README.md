# Backup of Kafka Data

This project will investigate how a backup of Apache Kafka broker can be performed so that data can be recovered in case a topic is lost due to an error.

## Context

Situations we **need** to solve with this project:

* Someone has accidently deleted a topic
* Someone has produced wrong data into a topic

Situations we **do not have** to solve with this project:

* Disaster Recovery, if a whole data center is no longer available => use a DR setup of Kafka (either stretched cluster or mirroring)
* Consumer want to reconsume messages => use data retention on the topic which is large enough)
* Make sure that some topics can not be deleted on a given environment => maybe use some metadata on the "event catalog" which will be included in the process of deleting topics

## High-Level overview

A online backup on file-level is not possible with Apache Kafka (on the running cluster). 

The idea is to have a "normal" Kafka consumer which also consumes the data in order to write it to a backup media. This should be cost-efficient storage media and the data should be written in a way optimal for restoring it. It is not meant to act as a Data Lake, only as a pure backup, where in case of loosing a topic, the data can be restored from. 

Data in the backup will live in sync with the topics, both on level of partitions as well as on the data retention, with a possible delay in the sync. 

## Requirements

1. Data should be backed-up to an external datastore (possibly S3) as a normal Kafka consumer (possibly using a Kafka connect connector). The data should be written in a way that it can efficiently and selectively be restored upon demand
2. Define a "normalized" data structure for the backup which optimally should be the same for all messages (we do not care about the payload and the format, it should be written to the backup as is)
3. Data restore should be by topic, time (all, range)
4. Data restore should be as fast as possible (using parallelism by partition)
5. Data backup and restore should retain the order of the messages (by partition)
6. If possible, record’s metadata should be restored (timestamp,….). In case, original record timestamp may be provided with an header.
7. Optionally, restored records should be marked with a custom header property (x-restored=recovery timestamp) to inform consumer (just in case they have to be treated differently).
6. Data retention on the topic should propagate to the backup data. If a segment is deleted from Kafka, then the corresponding data on the backup should be removed as well (after another configurable retention time).
7. On Restore, an offset mapping index should be provided (We might store the offset of the original message (by topic, partition) with the backup. When restoring the data, a new offset will be created which can be mapped to the original offset). This allows a consumer to move to the right offset by using the offset and after the restore continue with the last commited offset.  
8. Backup should automatically include new partitions and remove "old" partitions, in situations where a topic is either scaled-up or down. 
9. There needs to be a monitoring of the lag of the backup. It should be always as minimal as possible in order to not lose to many messages (we need an "online" backup).  
9. Solution should be as vender-neutral as possible
 
## References

1. [Surviving Data Loss](https://jobs.zalando.com/tech/blog/backing-up-kafka-zookeeper/index.html?gh_src=4n3gxh1) by Zalando
2. [Kafka Connect S3](https://github.com/spredfast/kafka-connect-s3) by Spredfast
3. [OVO Live Kafka Backu](https://github.com/ovotech/ovo-live-kafka-backup) by Ovotech
4. [Kafka Archiver](https://github.com/UnityTech/kafka-archiver) by Unity Tech

3. 

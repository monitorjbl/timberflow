# Timberflow

Yet another log parsing application with a tree-related name. Built using Akka + Java8, mostly because I wanted to.

# Building

```bash
git clone git@github.com:monitorjbl/timberflow.git
cd timberflow
mvn clean package
```

# Running

```bash
java -jar target/timberflow.jar --config pipeline.conf
```

# Configuration

```
inputs {
  stdin{}
  file {
    path = "/tmp/test1"
    from_beginning = true
    add_fields {"site": "SJC", "env": "prod"}
  }
  file {
    path = "/tmp/test2"
    add_fields {"site": "RTP", "env": "dev"}
  }
}

filters {
  grep {
    match {"message": "%{DATA:timestamp_local}\|%{NUMBER:duration}\|%{WORD:request_type}\|%{IP:clientip}\|%{DATA:username}\|%{WORD:method}\|%{PATH:resource}\|%{DATA:protocol}\|%{NUMBER:statuscode}\|%{NUMBER:bytes}"}
    match {"resource": "/%{DATA:repo}/%{GREEDYDATA:resource_path}"}
    match {"resource_path": "(?<resource_name>[^/]+$)"}
  }

  drop {
    fields = "message"
  }
}

outputs {
  stdout {}
}
```
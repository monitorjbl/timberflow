[![Run Status](https://api.shippable.com/projects/57bbdd43503ad81000c7660c/badge?branch=master)](https://app.shippable.com/projects/57bbdd43503ad81000c7660c)

# Timberflow

Yet another log parsing application with a tree-related name. Built using Akka + Java8, mostly because I wanted to.

# Building

Builds a distributable archive in `build/timberflow.zip` that you can extract to install Timberflow

```bash
git clone git@github.com:monitorjbl/timberflow.git
cd timberflow
./gradlew dist
```

# Running

```bash
bin/timberflow --config /path/to/config.conf
```

# Configuration

Configuration is done through a simple language:

```
inputs {
  stdin {}
  file {
    path = "/tmp/test1"
    from_beginning = true
    add_fields({"site": "SJC", "env": "prod"})
  }
  kafka {
    bootstrap_servers = "localhost:9092"
    group_id = "timberflow"
    topic = "test"
  }
}

filters {
  grep {
    extract({"message": "%{DATA:timestamp_local}\|%{NUMBER:duration}\|%{WORD:request_type}\|%{IP:clientip}\|%{DATA:username}\|%{WORD:method}\|%{PATH:resource}\|%{DATA:protocol}\|%{NUMBER:statuscode}\|%{NUMBER:bytes}"})
    extract({"resource": "/%{DATA:repo}/%{GREEDYDATA:resource_path}"})
    extract({"resource_path": "(?<resource_name>[^/]+$)"})
    add_fields({"grepped": "yep"})
  }

  if(username == "admin") {
    drop {
      fields = "username"
    }
  }

  drop {
    fields = "message"
  }
}

outputs {
  noop{}
  file{
    path = "/tmp/output"
  }
}
```

# Plugins

Timberflow is entirely powered by plugins. Plugins are not complex to create, the only requirement is that authors implement a few interfaces. Other than
that, the can construct their JAR files as they wish.

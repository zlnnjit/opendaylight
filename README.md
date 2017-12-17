## 介绍

### demo


主要为Carbon-sr2版本demo(纯净版demo生成)

maven：

```xml
mvn org.apache.maven.plugins:maven-archetype-plugin:2.4:generate \
 -DarchetypeGroupId=org.opendaylight.controller \
 -DarchetypeArtifactId=opendaylight-startup-archetype \
 -DarchetypeVersion=1.3.2-Carbon \
 -DarchetypeRepository=https://nexus.opendaylight.org/content/repositories/public/ \
 -DarchetypeCatalog=https://nexus.opendaylight.org/content/repositories/public/archetype-catalog.xml
```


编译过程中的输入：
```xml
Define value for property 'groupId': : org.opendaylight.demo
Define value for property 'artifactId': : demo
Define value for property 'version':  0.1.0-SNAPSHOT: : 1.0.0-SNAPSHOT
Define value for property 'package':  org.opendaylight.demo: :
Define value for property 'classPrefix':  Demo: :
Define value for property 'copyright': : zlnnjit
Define value for property 'copyrightYear':  2017: : 2017
Confirm properties configuration:
groupId: org.opendaylight.demo
artifactId: demo
version: 1.0.0-SNAPSHOT
package: org.opendaylight.demo
classPrefix: Demo
copyright: zlnnjit
copyrightYear: 2017
 Y: : Y
```


生成成功：
```
[INFO] ----------------------------------------------------------------------------
[INFO] Using following parameters for creating project from Archetype: opendaylight-startup-archetype:1.3.2-Carbon
[INFO] ----------------------------------------------------------------------------
[INFO] Parameter: groupId, Value: org.opendaylight.demo
[INFO] Parameter: artifactId, Value: demo
[INFO] Parameter: version, Value: 1.0.0-SNAPSHOT
[INFO] Parameter: package, Value: org.opendaylight.demo
[INFO] Parameter: packageInPathFormat, Value: org/opendaylight/demo
[INFO] Parameter: classPrefix, Value: Demo
[INFO] Parameter: package, Value: org.opendaylight.demo
[INFO] Parameter: version, Value: 1.0.0-SNAPSHOT
[INFO] Parameter: copyright, Value: zlnnjit
[INFO] Parameter: groupId, Value: org.opendaylight.demo
[INFO] Parameter: copyrightYear, Value: 2017
[INFO] Parameter: artifactId, Value: demo
[WARNING] Don't override file G:\github\OpenDaylight\demo\pom.xml
[INFO] project created from Archetype in dir: G:\github\OpenDaylight\demo
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 02:15 min
[INFO] Finished at: 2017-12-17T14:09:01+08:00
[INFO] Final Memory: 15M/121M
[INFO] ------------------------------------------------------------------------
```

详情见demo文件夹

编译:demo:mvn clean install

编译不过的话请检查setting.xml以及网络状况，在这里贴出我自己的setting.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- vi: set et smarttab sw=2 tabstop=2: -->
<!--
 Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.

 This program and the accompanying materials are made available under the
 terms of the Eclipse Public License v1.0 which accompanies this distribution,
 and is available at http://www.eclipse.org/legal/epl-v10.html
-->
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
	
	<!-- java仓库
	<localRepository>E:/maven/repo</localRepository>-->
	<localRepository>E:/repository</localRepository>

  
  <profiles>
    <profile>
      <id>opendaylight-release</id>
      <repositories>
        <repository>
          <id>opendaylight-mirror</id>
          <name>opendaylight-mirror</name>
          <url>https://nexus.opendaylight.org/content/repositories/public/</url>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>never</updatePolicy>
          </releases>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <id>opendaylight-mirror</id>
          <name>opendaylight-mirror</name>
          <url>https://nexus.opendaylight.org/content/repositories/public/</url>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>never</updatePolicy>
          </releases>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
        </pluginRepository>
      </pluginRepositories>
    </profile>

    <profile>
      <id>opendaylight-snapshots</id>
      <repositories>
        <repository>
          <id>opendaylight-snapshot</id>
          <name>opendaylight-snapshot</name>
          <url>https://nexus.opendaylight.org/content/repositories/opendaylight.snapshot/</url>
          <releases>
            <enabled>false</enabled>
          </releases>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <id>opendaylight-snapshot</id>
          <name>opendaylight-snapshot</name>
          <url>https://nexus.opendaylight.org/content/repositories/opendaylight.snapshot/</url>
          <releases>
            <enabled>false</enabled>
          </releases>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </pluginRepository>
      </pluginRepositories>
    </profile>
  </profiles>

  <activeProfiles>
    <activeProfile>opendaylight-release</activeProfile>
    <activeProfile>opendaylight-snapshots</activeProfile>
  </activeProfiles>
</settings>
```


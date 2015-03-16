### Pre-Requirements ###
This guide assumes you have downloaded and unpacked wikinet bundle (or built it from scratch). Also you need several environment configurations to be made before you can actually use it:<br>
<ol><li>You must have Ubuntu or any other Linux OS. (As a matter of fact it's not a strict requirement and wikinet should work fine under Windows or Mac OS. But it hasn't been tested using such environments and thus only .sh files are provided)<br>
</li><li>You should download Apache ActiveMQ from <a href='http://activemq.apache.org'>http://activemq.apache.org</a>, unpack it and run <i>${ACTIVEMQ_HOME}/bin/activemq</i>, assuming <i>${ACTIVEMQ_HOME}</i> points to ActiveMQ home directory. This step is necessary only for wikinet extender and mapper modules.<br>
</li><li>You need set up database. Currently wikinet use MySQL as DB server. Default configuration is to use database named "wikinet" under user "root" with no password. Supposing you don't have such database already, you should be able to create it using<br>mysql --user=root --password=<br>CREATE DATABASE wikinet CHARACTER SET utf8 COLLATE utf8_bin;<br>If you need other database name, user login or password you can specify different one using <i>${WIKINET_HOME}/conf/db.properties</i> file.<br>
</li><li>Lastly, you need JRE (Java Runtime Environment) version 1.6 or later.</li></ol>

<h3>Wordnet import</h3>
<ol><li>Download wordnet from <a href='http://wordnet.princeton.edu'>http://wordnet.princeton.edu</a> and unpack it. Lets assume you decompressed archive to <i>${WORDNET_HOME}</i>.<br>
</li><li>Run <i>${WIKINET_HOME}/bin/w2db.sh -d ${WORDNET_HOME}/dict</i>.</li></ol>

<h3>Wikipedia import</h3>
<ol><li>Download wikipedia pages xml-dump from <a href='http://dumps.wikimedia.org/enwiki'>http://dumps.wikimedia.org/enwiki</a>. (Note: File should be named something like enwiki-<code>&lt;date&gt;</code>-pages-articles.xml.bz2 and have size more than 5GB)<br>
</li><li>Run 
# Requirements #
  1. jdk 1.6 (or later)
  1. maven
  1. ant

# Steps #

  1. Checkout sources<br>svn checkout <a href='http://wikinet.googlecode.com/svn/trunk/'>http://wikinet.googlecode.com/svn/trunk/</a> wikinet<br>
<ol><li>Change working directory to wikinet<br>cd wikinet<br>
</li><li>Clean up build directories if any<br>mvn clean<br>
</li><li>Build project<br>mvn package<br>NOTE: if you want to skip tests use "mvn package -Dmaven.test.skip=true" instead.<br>
</li><li>Assemble modules and create wikinet zip-archive<br>ant</li></ol>

Now you should get wikinet.zip in wikinet/target directory.
# Persistence Layer #

<img src='http://yuml.me/9483833.jpg'>

<h3><a href='http://yuml.me'>http://yuml.me</a> class diagram code</h3>

<pre><code>[Word|- word (string) PK]*&lt;-&gt;*[Synset|- id (long) PK;- description (string);- type (enum);- lexFileNum (string)]<br>
[Synset]1&lt;-&gt;*[Connection|- connectionType (enum)]<br>
[Synset]1&lt;-&gt;*[Connection]<br>
[Synset]*&lt;-&gt;*[Page|- id (long);- word (string);- disambiguation (string);- firstParagraph (string);- text (string)]<br>
[Page]1&lt;-&gt;* redirect[Page]<br>
[Page]1 have...&lt;-&gt;0..*[LinkedPage|- id (long);- startPos (int);- length (int)]<br>
[Page]1&lt;-&gt;0..* point to...[LinkedPage]<br>
[Page]*&lt;-&gt;*[Category|- id (long);- name (string)]<br>
[Category]*&lt;-&gt;parent of *[Category]<br>
[Page]*&lt;-&gt;*[LocalizedPage|- title (string);- locale (enum)]<br>
</code></pre>
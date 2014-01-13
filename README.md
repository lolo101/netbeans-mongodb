NetBeans MongoDB
================

A [NetBeans](http://netbeans.org) plugin for accessing [MongoDB](http://mongodb.org). It adds
a node to the Services tab. Right click it to add connections.

You can:
 * Connect to MongoDB using mongo standard uri
 * Browse collections
 * Create/Rename/Delete collections
 * Browse documents (a json criteria can be specified)
 * Add/Edit/Delete documents as json

![NetBeans MongoDB Plugin Screen Shot](screenshot.png "NetBeans MongoDB Plugin Screen Shot")


Status
------
Stable and works.

Features in progress
--------------------
 * Documents are displayed as expendable tree in query result list.
 * Add projection and sort for querying
 * Export/Import as json.

TODO
----
 * Secure system collections (read-only, "system." prefix forbidden for new collections)

Build And Run / Downloads
-------------------------
It's a Maven project built using the NBM Maven Plugin - just check out and build.
In NetBeans, install using Tools | Plugins on the Downloaded tab.


License
-------
MIT license

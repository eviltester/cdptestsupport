A prototype code base for experimenting with the Chrome Debugging Protocol and using it to support Exploratory Testing.

Chrome Debugging Protocol Docs:

- https://chromedevtools.github.io/devtools-protocol/


Using the Java Library:

- https://github.com/kklisura/chrome-devtools-java-client

This is still a prototype and doesn't pick up all events reliably yet.

## Monitoring Message Content

Currently the tool monitors message content to find entity values in the text.

e.g.

- is my email address in the messages?
- are there references to my twitter handle?

This can help 'monitor' for data leaks as we test, and possibly detect error messages that we might not spot on screen.

## Command Line Usage

Assuming the `.jar` file is named `cdptestsupport-1-0.jar` (which it probably isn't, but it is faster to write).

`java -jar cdptestsupport-1-0.jar  --config=monitor.properties`

The configuration is via a `.properties` file

Properties file uses the following format:

~~~~~~~~
email_1=bob@mailinator.com
email_2=eris@mailinator.com
twitter_1=@eviltester
url_1=eviltester.com
~~~~~~~~

This would monitor for two email addresses, a twitter handle and a specific url.

`--url=https://eviltester.com`

You can also configure the default url that will be opened by passing in the `url` parameter.

## About

Written by:

- Alan Richardson

- https://www.eviltester.com - Software Testing Blogs and Articles
- https://www.compendiumdev.co.uk - Consultancy & Training
- https://digitalonlinetactics.com - Online Marketing
- https://uk.linkedin.com/in/eviltester
- https://twitter.com/eviltester
- https://patreon.com/eviltester 
# What's this
Two way relay server application between IRC and Slack.
Zero size space put after username's first char for ignore notification.

## IRC message/notice to Slack message
ex.
```
23:55 ircslackrelay: u​sername: message
23:55 ircslackrelay: _ u​sername: notice is sanded by undersocre _
```

## Slack message to IRC notice
ex.
```
23:55 ircslackrelay: u​sername: message
```

# How to use
## Install Java (upper 6)
Download from [Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html).
After install, please check by type `java -version` on your console.

## Install SBT
Please install [SBT](http://www.scala-sbt.org/).

## Build
```
sbt assembly
```
build file is
```
target/scala-2.11/ircslackrelay-assembly-X.X.X.jar
```

## Create config file
put `ircslackrelay.conf` file on the same folder of `ircslackrelay-assembly-X.X.X.jar`

```
irc.address="irchostname.com"
irc.nickname="ircslackrelay"
irc.username="ircslackrelay"
irc.password=""
irc.port=6667
irc.use_ssl=false
irc.charset="UTF-8"
slack.irc.address="hostname.irc.slack.com"
slack.irc.nickname="fuga"
slack.irc.username="fuga"
slack.irc.password="hege.k314df9aKefaj"
slack.irc.port=6667
slack.irc.use_ssl=true
slack.irc.charset="UTF-8"
slack.api.username="ircslackrelay"
slack.api.token="aaaaa-999999999-99999999-9999999-99999999"
slack.api.icon_url="https://pbs.twimg.com/profile_images/2193228277/scalachan.jpg"
relays = [
  {
    irc_channel: "#irc_channel_1"
    slack_channel: "#slack_channel_1"
  },
  {
    irc_channel: "#irc_channel_2"
    slack_channel: "#slack_channel_2"
  },
  {
    irc_channel: "#irc_channel_3"
    slack_channel: "#slack_channel_3"
  }
]
```
[slack.irc config](https://slack.zendesk.com/hc/en-us/articles/201727913-Connecting-to-Slack-over-IRC-and-XMPP) and [slack api token](https://api.slack.com/) provided at slack website.


## Start server
```
java -jar -server ircslackrelay-assembly-X.X.X.jar
```
If you want to demonize, please use screen or tmux or nohup command.

# LICENSE
MIT License


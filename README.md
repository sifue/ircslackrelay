# What's this
Two way relay server application between IRC and Slack.
Zero size space put after username's first char for ignore notification.

## IRC message/notice to Slack message
ex.
```
23:55 ircslackrelay: :u_sername: message
23:55 ircslackrelay: _ :u_sername: notice is sanded by undersocre _
```
If you configure :u_sername: as Emoji,  :u_sername: looks like a picture.
And username is converted to lower case.

## Slack message to IRC message
ex.
```
23:55 ircslackrelay: (u​s​e​r​n​a​m​e) message
```

# How to use
## Install Java (upper 7)
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
[slack.irc config](https://my.slack.com/account/gateways) and [slack api token](https://api.slack.com/) provided at slack website.


## Start server
```
java -jar -server ircslackrelay-assembly-X.X.X.jar
```
If you want to demonize, please use screen or tmux or nohup command.

## Start on heroku

```
heroku create # git remote add heroku dokku@dokku.me:myrelay 
cp ircslackrelay_template.conf ircslackrelay.conf
vi ircslackrelay.conf
git add -f ircslackrelay.conf
git commit -m "ircslackrelay.conf"
git push heroku master
```

# LICENSE
MIT License


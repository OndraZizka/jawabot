# Usage #

```
jawabot[:|,] <command> <params> ...
```


# Commands #

## Resource management ##


  * `find [[<from>] <to>]` - finds which machines are free in the given period.

  * `take/keep <resource>[,<resource>] [[<from>] <to>] [# Action comment]`
    * `<resource>` see list.
    * `<from>` and `<to>`:
> > > Either date `YYYY-MM-DD` or offset since today: `+1` is tomorrow etc.
> > > or "today" or "tmrw"
> > > TODO: Handle day names.


> If `<from>` and/or `<to>` are ommited, they are replaced with today's date.
> Ignores nick suffixes after "-", "_" etc.
> Merges overlapping reservations of the same user.
> The "Action comment" part is shown in the mailing-list announcement body.
> (TODO: Merge marginal reservations of the same user.)
> (TODO: Expiration reminders [PM or channel?](on.md))_


  * `leave <resource>` - leaves the resource; can be done only by the one who has made current reservation.
  * `leave all` - leave all resources reserved for the "calling" user.

  * `list` - lists the resources.
  * `list <resource>` - lists the reservations on the given machine.
  * `list <user>` - list the reservations for the given user.
  * `[list] my` - list the reservations for the current user.


## Bot administration ##

  * `join #channel` - join the channel
> > (also, the bot accepts IRC invitations - /invite JawaBot )

  * `please leave` - quit the channel

  * `quit <password>` - shut down.

  * `help` - shows basic help and a link to this page.

  * `save` - saves the current state.


Some of the commands can be performed via personal message.


# Examples #

## Find example: ##

```
(07:12:54) ozizka: JawaBot: find
 (07:12:54) JawaBot: Free for 2009-06-16 - 2009-06-16: xen19, jawa18, jawa09 (SOA), xen64, jawa07 (SOA), jawa08 (SOA), jawa14, jawa05 (Productization), jawa15 (EWS), jawa06 (SOA), jawa16 (OpenJDK), jawa03 (EAP), jawa17, jawa04 (EAP), jawa01 (EAP), jawa10, jawa02 (EAP), jawa11, jawa12, jawa13
 (07:12:59) ozizka: JawaBot: take jawa18
 (07:12:59) JawaBot: jawa18 was succesfully reserved for ozizka from 2009-06-16 to 2009-06-16.
 (07:13:09) ozizka: JawaBot: find
 (07:13:09) JawaBot: Free for 2009-06-16 - 2009-06-16: xen19, jawa09 (SOA), xen64, jawa07 (SOA), jawa08 (SOA), jawa14, jawa05 (Productization), jawa15 (EWS), jawa06 (SOA), jawa16 (OpenJDK), jawa03 (EAP), jawa17, jawa04 (EAP), jawa01 (EAP), jawa10, jawa02 (EAP), jawa11, jawa12, jawa13
```

## List example: ##

```
(05:52:20) ozizka: JawaBot: list
(05:52:20) JawaBot: xen19, jawa18, jawa09, xen64, jawa07, jawa08, jawa14, jawa05, jawa15, jawa06, jawa16, jawa03, jawa17, jawa04, jawa01, jawa10, jawa02, jawa11, jawa12, jawa13
```

```
(00:48:05) test: JawaBot-debug: list ozizka
(00:48:06) JawaBot-debug: ozizka: 2010-05-05 2010-05-05 jawa11
(00:48:07) JawaBot-debug: ozizka: 2009-11-25 2009-11-25 jawa15
(00:48:08) JawaBot-debug: ozizka: 2009-11-21 2009-11-23 jawa18
(00:48:09) JawaBot-debug: ozizka: 2009-11-27 2009-11-29 jawa18
(00:48:10) JawaBot-debug: ozizka: 2010-01-22 2010-01-22 jawa18
(00:48:11) JawaBot-debug: ozizka: 2009-11-27 2009-11-29 jawa17 
(00:48:26) test: JawaBot-debug:  list jawa18
(00:48:26) JawaBot-debug: Reservations for jawa18:
(00:48:27) JawaBot-debug: 2009-11-21 - 2009-11-23 : ozizka
(00:48:28) JawaBot-debug: 2009-11-27 - 2009-11-29 : ozizka
(00:48:29) JawaBot-debug: 2010-01-22 - 2010-01-22 : ozizka
```

## Take examples: ##

```
 (05:50:02) ozizka: JawaBot: take jawa12
 (05:50:02) JawaBot: jawa12 was succesfully reserved for ozizka from 2009-06-16 to 2009-06-16.
 (05:50:11) ozizka: JawaBot: list jawa12
 (05:50:11) JawaBot: Reservations for jawa12:
 (05:50:12) JawaBot: 2009-06-16 - 2009-06-16 : ozizka
 (05:50:32) ozizka: JawaBot: leave jawa12
 (05:50:32) JawaBot: ozizka has left jawa12.
 
 (05:55:39) ozizka: JawaBot: take jawa16 today +2
 (05:55:39) JawaBot: jawa16 was succesfully reserved for ozizka from 2009-06-16 to 2009-06-18.
 (05:56:08) ozizka: JawaBot: list jawa16
 (05:56:08) JawaBot: Reservations for jawa16:
 (05:56:09) JawaBot: 2009-06-16 - 2009-06-18 : ozizka
 (05:56:18) ozizka: JawaBot: leave jawa16
 (05:56:19) JawaBot: ozizka has left jawa16.
 (05:56:28) ozizka: JawaBot: list jawa16
 (05:56:28) JawaBot: Reservations for jawa16:
 
 (05:59:19) ozizka: JawaBot: take jawa17 +2
 (05:59:19) JawaBot: jawa17 was succesfully reserved for ozizka from 2009-06-16 to 2009-06-18.
```
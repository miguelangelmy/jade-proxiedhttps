#!/bin/sh

java -classpath .:./lib/jade.jar:./lib/commons-codec-1.3.jar:./lib/jade-pingpongagent.jar jade.Boot -services jade.core.event.NotificationService -local-host 127.0.0.1 -port 1099 -mtps jade.mtp.http.MessageTransportProtocol(https://192.168.0.5:7778/plat5) -jade_mtp_http_https_keyStoreFile dummy-KeyStore.jks -jade_mtp_http_https_keyStorePass password -jade_mtp_http_https_trustManagerClass jade.mtp.http.https.FriendListAuthentication -jade_mtp_http_https_friendListFile proxied-jade-TrustStore.jks -jade_mtp_http_https_friendListPass password -platform-id plat5 -agents "PiPoAg:de.unidue.stud.sehawagn.pingpongagent.PingPongAgent(PiPoAg@plat1,https://192.168.0.1:443/plat1)"


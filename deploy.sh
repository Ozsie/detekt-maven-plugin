#!/usr/bin/env bash
curl -fL https://getcli.jfrog.io | sh
chmod +x jfrog
./jfrog bt vc --user $BIN_TRAY_USER --key $BIN_TRAY_PWD 1.0.0.RC8-1-SNAPSHOT
./jfrog bt u --user $BIN_TRAY_USER --key $BIN_TRAY_PWD --override --publish "target/detekt-maven-plugin*" ozsie/maven/detekt-maven-plugin/1.0.0.RC8-1-SNAPSHOT
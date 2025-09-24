#!/bin/bash

./gradlew lwjgl3:packageWinX64
./gradlew lwjgl3:packageMacX64
./gradlew lwjgl3:packageMacM1
./gradlew lwjgl3:packageLinuxX64


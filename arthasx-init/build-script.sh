#!/bin/bash

cp -r ../arthasx-daemon/arthas-dir/arthas-packaging-bin arthas-packaging-bin

sudo docker build -t arthasx-init .

rm -fr arthas-packaging-bin

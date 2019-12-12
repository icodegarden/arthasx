FROM busybox

WORKDIR /var/lib

ADD arthas-packaging-bin arthas-packaging-bin

CMD cp -r arthas-packaging-bin/. arthasx && chmod 544 arthasx/attach.sh


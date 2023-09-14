FROM amd64/eclipse-temurin:17-jre-alpine

# prepare container
RUN apk add \
    curl \
    jq \
    git

# run entrypoint.sh
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT [ "/entrypoint.sh" ]
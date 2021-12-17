#Step 1: Use debian
FROM debian:bookworm-slim

#Required packages
ENV TOOLS ca-certificates curl jq zip unzip p7zip-full git openjdk-11-jdk maven

#Step 2: Install tools
RUN apt-get -q update && \
    apt-get -q install -y --no-install-recommends $TOOLS
    
#Copy scipts, sources and templates
COPY scripts scripts
COPY templates templates
COPY jcefmaven jcefmaven
RUN chmod +x scripts/*


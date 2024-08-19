#!/bin/bash

# 현재 스크립트의 디렉토리로 이동
cd "$(dirname "$0")"

# Docker Compose 파일이 있는 디렉토리로 이동
cd ../docker

# Docker Compose를 사용하여 서비스 시작
docker-compose -f docker-compose.yml up --build
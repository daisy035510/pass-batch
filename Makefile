# -d: 백그라운드 실행, --force-recreate: 강제 재생성 -> docker compose 파일을 수정했다면 이 option을 주어야함
db-up:
	docker-compose up -d --force-recreate

# -v: volume 삭제
db-down:
	docker-compose down -v
# videoExample
Video upload and Convert using Spring &amp; FFMpeg

구현목표
---
---

주어진 요구사항은 크게 네가지로 파악하였습니다.

- 영상 업로드 및 해상도 변환

- 영상 정보 조회

- 원본 영상 썸네일 생성

- 변환 과정 진행도 조회

기술적인 요구사항은 다음과 같이 파악하였습니다.


- 응답과 요청은 JSON으로 구성
- gradle or maven으로 build
- Docker를 통한 배포


이슈
----
----
1. FFMPEG 경로 설정으로 인한 Docker 미사용
    
    - FFMPEG.exe와 FFPROBE.exe 사용을 위해 경로 설정이 필요했으나 build 환경과 배포 환경의 차이를 고려하지 못하여 기한 내 구현 실패하였습니다.


2. 개발 환경과 운영 환경의 차이 발생

    - 개발과 운영 환경 구분을 고려하지 않고 개발을 진행하여 1과 같은 문제가 발생하였으며 또한 코드 내에서도 환경에 따른 값을 따로 설정하지 못하였습니다. 


3. 진행도 조회 미구현

    - FFMPEG 라이브러리를 활용하여 진행도를 저장하고 요청 시 이를 반환하고자 했으나 id에 따른 진행도를 구하지 못하여 구현에 실패하였습니다.


프로젝트 실행 방법
---
---
1. git clone
```
$ git clone https://github.com/Overiron/videoExample.git
```

2. 프로젝트 build
```
$ ./gradle build -x test
```

3. jar 실행
```
$ java -jar videoExample-0.0.1-SNAPSHOT.jar
```

4. Local Host 접속
```
http://localhost:8080
```


* 별도 테스트 코드를 작성하지 못하여 브라우저로 접속하신 후 테스트 하는것을 권장드립니다.
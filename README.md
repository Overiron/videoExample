# videoExample
Video upload and Convert using Spring &amp; FFMpeg


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


이슈
----
----

1. 개발 환경과 운영 환경의 차이 발생

    - 개발과 운영 환경 구분을 고려하지 않고 개발을 진행하여 1과 같은 문제가 발생하였으며 또한 코드 내에서도 환경에 따른 값을 따로 설정하지 못하였습니다. 

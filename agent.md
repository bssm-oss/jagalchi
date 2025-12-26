# agent.md

이 문서는 이 저장소에서 작업하는 사람/에이전트가 반드시 지켜야 하는 개발 규칙을 정의한다.
목표는 일관된 코드 품질, 예측 가능한 브랜치 흐름, 빠른 리뷰/배포를 만드는 것이다.

## 1. 기본 원칙

1) 작은 단위로 쪼개서 구현한다
   1.1 한 이슈는 한 목적만 가진다
   1.2 한 브랜치는 한 이슈만 처리한다
   1.3 한 커밋은 한 책임만 가진다

2) 동작이 바뀌는 변경에는 반드시 테스트가 따라야 한다
   2.1 최소 통합 테스트 1개
   2.2 핵심 로직은 단위 테스트 추가

3) API는 명세가 먼저다
   3.1 request/response DTO와 status code부터 확정
   3.2 예외(400/401/403/404/409/422) 정책을 문서로 남긴다
   3.3 OpenAPI가 있다면 항상 최신 상태 유지

4) 리팩토링은 기능 커밋과 분리한다
   4.1 feat 커밋에 refactor 섞지 않는다
   4.2 리뷰가 어려워진다

## 2. 브랜치 전략(필수)

브랜치 종류(상단일수록 안정)
1) main (본서버, 프로덕션)
2) develop (테스트서버, 통합)
3) main-feature (1 애자일 단위 큰 묶음)
4) semi-feature (도메인 연결/정책/설계)
5) feature (엔드포인트/기능 최소 단위)
6) bugfix (버그 수정만)
7) hotfix (프로덕션 긴급 수정)

머지 흐름(기본)
1) feature -> semi-feature
2) semi-feature -> main-feature
3) main-feature -> develop
4) develop -> main

버그 흐름
1) 버그가 발생한 브랜치로 bugfix를 머지한다
2) 기능 추가는 bugfix 브랜치에서 금지

핫픽스 흐름
1) hotfix -> main
2) hotfix -> develop (역머지 필수)
3) 필요 시 main-feature에도 역머지

## 3. 브랜치 네이밍(필수)

형식
{flow}/{issue번호}/{title:한글}

예시
feature/220/로드맵-생성
semi-feature/201/로드맵-디렉토리-권한정책
bugfix/305/트리조회-정렬버그
hotfix/401/토큰검증-누락수정

주의
1) title은 짧고 명확하게 쓴다
2) 공백 대신 하이픈을 사용한다
3) 브랜치에는 이슈 1개만 매핑한다

## 4. 이슈 규칙(필수)

이슈 1개가 포함해야 하는 내용
1) 목적(한 문장)
2) 범위(포함/제외)
3) API 스펙(method, path, request/response, status code)
4) 권한/정책(누가 호출 가능한지)
5) 예외 케이스(최소 3개)
6) 완료 조건(DoD)

라벨 추천
1) type: main-feature / semi-feature / feature / bugfix / hotfix
2) area: roadmap / directory / progress / auth / docs / test
3) priority: p0 / p1 / p2

## 5. 커밋 규칙(필수)

커밋 메시지 규칙(Conventional Commits)
1) feat(scope): 기능 추가
2) fix(scope): 버그 수정
3) refactor(scope): 리팩토링(동작 동일)
4) test(scope): 테스트
5) docs(scope): 문서
6) chore(scope): 설정/빌드/기타

이슈 연결(필수)
커밋 메시지 끝에 refs #이슈번호를 붙인다

예시
feat(roadmap): add create roadmap endpoint refs #220
test(progress): add integration test for node complete refs #230

커밋 크기 기준
1) DTO 추가
2) 도메인/엔티티
3) repository
4) service/usecase
5) controller/handler
6) test
7) docs(OpenAPI)
   위 단계를 한 커밋에 몰아넣지 않는다

금지
1) 기능 커밋에 대규모 포매팅을 섞지 않는다
2) 여러 unrelated 수정(죽은 코드 삭제, 변수명 변경, 리네이밍)을 한 커밋에 섞지 않는다

## 6. 코드 스타일 규칙

일반
1) 한 파일은 한 역할만 가진다
2) public API(컨트롤러/핸들러, DTO)는 얇게 유지한다
3) 비즈니스 규칙은 service/usecase에 둔다
4) repository는 저장/조회만 담당한다(정책/계산 로직 금지)

네이밍
1) Controller/Handler: RoadmapController, DirectoryController
2) Service/Usecase: CreateRoadmapService, GetDirectoryTreeService
3) Repository: RoadmapRepository, DirectoryRepository
4) DTO: CreateRoadmapRequest, RoadmapDetailResponse

함수
1) 함수 하나는 한 가지 일만 한다
2) early return을 선호한다
3) null을 억지로 퍼뜨리지 않는다(가능하면 명시적 타입/결과로 분리)

포맷터/린터
1) ktlint/detekt가 있으면 반드시 통과시킨다
2) 자동 포매팅은 별도 커밋으로 분리한다

## 7. API 설계 규칙(이 프로젝트 기준)

Base URL
/roadmap

응답 규칙
1) 성공
   1.1 생성: 201 Created + 생성된 리소스 id 포함
   1.2 조회/수정: 200 OK
   1.3 삭제: 200 OK 또는 204 No Content 중 하나로 통일
2) 에러는 공통 포맷을 사용한다

공통 에러 포맷(예시)
{
"error": {
"code": "VALIDATION_ERROR",
"message": "title is required",
"details": { "field": "title", "reason": "must not be blank" }
}
}

상태 코드 정책(권장)
1) 400: 요청 형식 오류(파싱 불가, 잘못된 쿼리 파라미터)
2) 401: 인증 실패(토큰 없음/만료)
3) 403: 권한 없음
4) 404: 리소스 없음
5) 409: 충돌(버전 충돌, 중복, 정책 위반 충돌)
6) 422: 검증 실패(비즈니스 규칙 위반)

권한
1) 공개 로드맵: 누구나 조회 가능
2) 비공개 로드맵: 소유자/협업자만 가능
3) 진행률 API: 기본은 본인만 가능, 타인 조회는 정책이 있을 때만 열어준다

페이지네이션
1) page는 0부터 시작
2) size 기본 10, 최대 50
3) 응답은 content/pageable/totalElements/totalPages/hasNext 형태로 통일

그래프 버전(낙관적 락)
1) PUT /roadmaps/{id}/graph 요청에 baseVersion 포함
2) 서버 버전과 다르면 409 GRAPH_VERSION_CONFLICT

## 8. 도메인 규칙(최소 보장)

Directory
1) parentId가 null이면 루트
2) 삭제 정책은 반드시 명시(mode=delete|move 등)
3) 트리 조회는 정렬 규칙을 고정한다(이름 오름차순 등)

Roadmap
1) directoryId는 null이면 루트
2) isPublic false인 리소스는 권한 없으면 404 또는 403 중 하나로 통일(숨김/노출 정책 결정 후 고정)
3) viewCount 증가 정책을 고정한다(상세 조회 시 증가, 내 로드맵은 증가 제외 등)

Progress
1) completedAt은 완료 처리 시에만 채운다
2) 미완료로 바꾸면 completedAt은 null
3) 진행률은 totalNodes 대비 completedNodes로 계산(소수점 처리 규칙 고정)

## 9. 테스트 규칙

필수 테스트
1) 핵심 서비스 로직 단위 테스트
2) 엔드포인트 통합 테스트(성공 1개 + 실패 1개 이상)

통합 테스트 체크 포인트
1) 권한(401/403)
2) 존재하지 않는 리소스(404)
3) 검증 실패(400/422)
4) 페이지네이션 파라미터
5) 트리/목록 정렬

테스트 데이터
1) fixture/seed는 재사용 가능하게 만든다
2) 테스트는 서로 의존하지 않는다(순서 무관)

## 10. 문서(OpenAPI) 규칙

1) 엔드포인트 추가/변경 시 OpenAPI도 같이 수정한다
2) request/response 예시는 반드시 포함한다
3) 에러 케이스(최소 2개)는 문서에 포함한다

## 11. PR 규칙(필수)

PR 제목
[#{issue}] 한글 요약

PR 본문 체크리스트(필수)
1) 구현 범위
2) API 스펙 변화(있으면)
3) 테스트 결과(어떤 테스트를 추가했는지)
4) 깨지는 변경 여부(breaking change)

머지 조건
1) 빌드 성공
2) 린터 통과
3) 테스트 통과
4) 리뷰 1명 이상 승인

## 12. 작업 시작 템플릿(에이전트용)

작업 시작 전
1) 현재 브랜치 확인
2) 이슈 내용에 API 스펙이 확정되어 있는지 확인
3) 필요한 정책(권한/검증/정렬/버전)이 문서화되어 있는지 확인
4) 브랜치 생성: {flow}/{issue}/{title}

작업 순서(권장)
1) DTO/Validation
2) Service/Usecase
3) Repository
4) Controller/Handler
5) Test
6) Docs(OpenAPI)

작업 종료 전
1) 포매팅/린터 통과
2) 테스트 통과
3) 커밋 메시지에 refs #이슈 포함
4) PR 생성 및 체크리스트 작성

언어 
java 25버전
springboot 4버전을쓴다
커밋은 최대한 많이 그리고 이슈를 올리때도 한글
커밋을 남길때도 한글로 남긴다
그리고 테스트 메서드 이름도 한글로 when given then을 기반으로 만든다.
디비 마이그레이션은 Liquibase이걸로 관리한다.


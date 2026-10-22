# JEONG YEOWON Beauty Portfolio

Vue 3 + Vite + JavaScript + Vue Router / Java 21 + Spring Boot + Gradle + Spring Data JPA + H2 공개 조회 포트폴리오입니다. 소개·경력·교육·자격·연락처 및 채널 수치는 사용자가 제공한 정보입니다. 채널 전체 조회 12.4만 회는 2026.10.21 제공 캡처의 최근 30일 기준이며 개별 콘텐츠 조회수·도달 인원·뷰티 콘텐츠만의 성과를 의미하지 않습니다. Feed 3개 분야의 이미지 9개와 Reels 3개는 레이아웃 확인용 플레이스홀더이며 실제 작업 정보는 미입력입니다. 인스타그램 자동 수집을 사용하지 않습니다.

## 구조

```text
beauty-portfolio/
  frontend/           Vue 페이지, 카드, API 모듈, public/media SVG
  backend/            Gradle Wrapper, Java 엔티티·API·초기 데이터
  README.md
```

## Windows PowerShell 실행

Java 21과 Node.js 22.12 이상이 필요합니다. 첫 실행에는 의존성을 내려받을 인터넷 연결이 필요합니다.

첫 번째 터미널:

```powershell
Set-Location D:\jyw\beauty-portfolio\backend
.\gradlew.bat bootRun
```

두 번째 터미널:

```powershell
Set-Location D:\jyw\beauty-portfolio\frontend
npm.cmd ci
npm.cmd run dev
```

브라우저에서 http://localhost:5173 을 엽니다. 백엔드는 http://localhost:8081 입니다. Vite가 `/api` 요청을 백엔드로 프록시하므로 개발 시 별도 CORS 설정이 필요 없습니다. 각 터미널에서 Ctrl+C로 종료합니다. H2는 메모리 DB이며 재시작하면 샘플 데이터로 초기화됩니다.

## IntelliJ

`D:\jyw\beauty-portfolio` 폴더를 엽니다. `backend/build.gradle`을 Gradle 프로젝트로 연결하고 Project SDK와 Gradle JVM을 Java 21로 선택합니다. `PortfolioApplication`의 main 또는 Gradle `bootRun`을 실행합니다. 프론트는 IntelliJ 터미널에서 위 npm 명령으로 실행합니다. Gradle 배포판은 Wrapper를 사용합니다.

## 빌드 및 검증

```powershell
Set-Location D:\jyw\beauty-portfolio\backend
.\gradlew.bat clean build
Set-Location D:\jyw\beauty-portfolio\frontend
npm.cmd run build
Invoke-RestMethod http://localhost:8081/api/v1/profile
Invoke-RestMethod http://localhost:8081/api/v1/projects
Invoke-RestMethod http://localhost:8081/api/v1/projects/1
Invoke-RestMethod http://localhost:8081/api/v1/contents
```

## 실제 콘텐츠로 교체

- `frontend/src/data/portfolio.json`: 소개·수치·기준일·경력·교육·자격·도구·연락처의 원본입니다. 인스타 작업물은 `frontend/src/data/gallery.json`의 `items`에서 형식·설명·미디어·원본 링크를 한곳에서 관리합니다. Feed/Reels 두 탭을 사용합니다. Feed 대표 카드(스킨케어·색조·프래그런스)는 `/feed/:slug` 상세로 이동하며 PC에서 사진·설명을 좌우 교차 배치합니다. 모바일에서는 사진 다음 설명으로 표시합니다. Reels는 분야 구분 없이 PC 3열이며, 모바일 너비에 따라 2열 또는 1열입니다. 별도 분야 메뉴·필터·모달은 사용하지 않습니다. 빈 설명은 숨기며 빈 videoUrl은 준비 중으로 표시합니다. 백엔드도 Gradle 빌드 시 같은 파일을 읽으므로 변경 후 프론트 빌드와 백엔드 빌드·재시작이 필요합니다. 추가·교체 방법은 `frontend/CONTENT.md`를 확인하세요. 메인 대표 비주얼 설정은 `frontend/src/portfolio.js`에 유지합니다.
- `frontend/public/media/`: 직접 제작한 SVG 플레이스홀더를 실제 이미지·MP4로 교체하고 SampleData의 URL을 `/media/파일명`으로 지정합니다. REEL의 `videoUrl`을 설정하면 상세에서 영상 플레이어가 표시됩니다. 외부 게시물은 `externalUrl`로 연결합니다.
- 실제 콘텐츠 등록 후 해당 데이터의 `sample` 값을 수정하고, `App.vue`, `Page.vue`, `ContentCard.vue`의 예시 안내 문구도 실제 공개 상태에 맞게 수정하세요. 현재 전체 사이트는 예시임을 명시하도록 구성했습니다.
- `frontend/src/api.js`: API 호출 전용 모듈. 별도 배포 시 `VITE_API_BASE_URL`을 빌드 환경에 설정하고 백엔드 CORS 또는 동일 출처 리버스 프록시를 구성합니다. Vite `dist`는 프론트 빌드 결과이며 Spring Boot JAR에 자동 포함되지 않습니다.
- `frontend/src/style.css`: 컬러, 반응형, 공통 레이아웃. 외부 폰트 연결이 없어도 시스템 폰트로 표시됩니다.

API는 GET `/api/v1/profile`, `/projects`, `/projects/{id}`, `/contents`를 제공합니다. Feed/Reels 탭은 페이지 이동 없이 전환합니다. 관리 로그인, 쓰기 API, 업로드, 문의 전송은 포함하지 않습니다. 등록되지 않은 프로젝트는 404, 잘못된 ID 형식은 400을 반환합니다.

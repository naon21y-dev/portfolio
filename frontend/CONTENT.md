# 콘텐츠 교체 안내

## 캐릭터 테마

전체 캐릭터 테마는 `src/character-theme.css`, 첨부 원본 자산은 `public/media/theme/`에 있습니다. 메인 캐릭터 경로는 `src/portfolio.js`의 hero에서 교체합니다. 실제 작업 사진과 인사이트 수치는 그대로 유지합니다. 테마 스타일만 바꾸려면 character-theme.css를 수정하세요.

추가 픽셀 캐릭터·포인터·NEXT/BACK 이미지는 `src/portfolio.js`의 `pixelAssets`에서 관리하고 세부 스타일은 `src/pixel-details.css`에서 수정합니다. NEXT/BACK은 Home → About → Work → Feed & Reels → Contact 순서로 이동합니다. About 팝업은 이미지 속 창을 참고한 실제 HTML 창과 격자 배경으로 구현하여 본문 선택·스크롤·키보드 기능을 유지합니다.

노트·말풍선·메모·로딩·오류 장식은 `src/scrapbook-details.css`에 모았습니다. Work 설명의 실제 노트 이미지, Contact와 About의 말풍선, 인사이트 해석의 테이프 메모를 적용하며 사진과 지표 원본은 바꾸지 않습니다. 로딩·오류 장식은 실제 상태 화면에서만 표시합니다.

## Work 페이지 사진

`src/data/work.json`이 Work 페이지의 문구·사진 경로·순서·작업명(`title`)·직접 담당한 역할(`role`)의 원본입니다. `sections`와 각 `photos` 배열 순서대로 표시합니다. 섹션당 3~4장 사용을 권장합니다. 사진은 `public/media/`에 넣고 `src`를 `/media/파일명.jpg`로 변경하세요. `alt`도 사진 내용에 맞게 수정하고 실제 사진은 `placeholder: false`로 지정합니다. 빈 작업명·역할은 숨깁니다. `draftNote`는 작성용으로만 보관하며 화면에는 표시하지 않습니다. 사진 클릭 시 원본 비율로 확대합니다. Work는 `/work`에서 확인할 수 있습니다.

## 인스타그램 작업물

작업물의 단일 원본은 `frontend/src/data/gallery.json`입니다. `items` 배열에 항목을 추가하거나 기존 값을 교체합니다. 임의의 브랜드·역할·성과는 입력하지 않습니다.

- `type`: `FEED`(3:4) 또는 `REEL`(9:16).
- `title`: 작업물 제목. `caption`: 짧은 설명.
- `brand`, `productName`, `intention`, `role`: 실제 확인된 브랜드·제품·기획 의도·담당 역할. 미디어 바로 아래에 표시하며 빈 값은 숨깁니다.
- `imageUrl`: 피드 이미지. `thumbnailUrl`: 릴스 대체 이미지. `videoUrl`: MP4 등의 브라우저 재생 가능 영상. 비어 있으면 로컬 이미지와 영상 준비 중 안내를 표시하고, 등록되면 페이지 내 플레이어에서 바로 재생합니다.
- `externalUrl`: 원본 인스타그램 게시물의 HTTPS 주소. 빈 값이면 링크를 숨깁니다.
- `sample`: 플레이스홀더는 true, 실제 작업물은 false.
- `projectId`: 기존 프로젝트 API와의 호환용 연결 ID(현재 1~3). 기존 그룹에 추가할 때 해당 ID를 유지합니다. 카드 식별 ID는 DB에서 자동 생성됩니다.

이미지·영상 파일은 `frontend/public/media/`에 넣고 JSON에는 `/media/파일명.jpg`, `/media/파일명.mp4`처럼 입력합니다. 기존 로컬 SVG는 그대로 사용할 수 있습니다. 탭은 Feed / Reels 두 개이며 별도 분야 메뉴·필터·모달은 사용하지 않습니다. 영상 파일 등록 시 실제 브라우저 재생 지원 형식을 확인하세요.

`feedGroups`는 Feed 대표 카드 설정입니다. `title`은 스킨케어·색조·프래그런스 제목, `coverUrl`은 대표 이미지, `slug`는 상세 경로(`/feed/skincare`, `/feed/makeup`, `/feed/fragrance`), `projectId`는 해당 분야 작업물 연결 ID입니다. Feed 작업물은 `items`에서 `type: "FEED"`와 해당 `projectId`로 연결합니다. 같은 ID로 추가하면 배열 순서대로 상세에서 반복되며 PC에서는 좌우 교차, 모바일에서는 사진 다음 설명 순서입니다. 현재 각 분야의 3개 이미지는 모두 레이아웃 확인용 플레이스홀더로, 실제 작업을 의미하지 않습니다. 필요 없는 샘플 항목은 제거하고 실제 작업으로 교체하세요.

Reels는 `items`에서 `type: "REEL"`인 작업물을 배열 순서대로 표시합니다. 색조·스킨케어·프래그런스·이너케어·클렌징·기타 6개를 PC에서 3열×2행으로 배치하며, 모바일은 너비에 따라 2열 또는 1열입니다. 분야명은 카드 제목으로만 사용합니다.

릴스는 `thumbnailSrc`의 로컬 썸네일을 누르면 콘텐츠 설명과 인사이트 모달을 엽니다. 영상 플레이어·자동재생은 없으며 `videoSrc`는 현재 화면에서 사용하지 않습니다. `instagramUrl`은 모달의 ‘인스타그램 원본 보기’ 링크로만 사용합니다. 기존 API 필드는 호환을 위해 유지합니다.

릴스별 `brand`, `productName`, `insights`가 설명·수치·해석의 단일 원본입니다. `insights.reactions` 순서는 좋아요·댓글·공유·저장·리포스트이며 반응 합계는 이 배열의 합입니다. 조회수는 `views`, 도달은 `reach`, 조회한 사람은 `viewers`, 평균 조회 시간(초)은 `averageWatchSeconds`, 건너뛰기 비율(%)은 `skipRate`, 팔로우는 `follows`입니다. 미제공 값은 null, 실제 0은 숫자 0으로 입력합니다. 원래 지표명은 유지되며 null만 숨깁니다. `description`, `interpretation`, `activity`에 제공된 콘텐츠 설명·성과 해석·활동 경험을 보관합니다. `reelInsights`에서 반응 항목 이름과 공통 주석을 관리합니다. API에도 동일한 insights 데이터가 전달됩니다.

차트는 Apache ECharts의 실제 가로 막대그래프입니다. 모달이 열린 뒤 초기화하고 ResizeObserver와 resize 이벤트로 크기를 조정합니다. 닫힘·컴포넌트 해제 시 차트를 dispose합니다. 초별·일별 추이 데이터는 생성하지 않습니다.

수정 후 백엔드를 빌드·재시작해야 JSON이 JPA 초기 데이터와 `GET /api/v1/contents`에 반영됩니다. H2 메모리 DB는 재시작 시 초기화됩니다. 프론트 배포 전에도 다시 빌드합니다.

```powershell
Set-Location D:\jyw\beauty-portfolio\backend
.\gradlew.bat build
.\gradlew.bat bootRun
```

기존 백엔드 실행 터미널에서는 먼저 Ctrl+C로 종료하세요. 프론트 개발 서버는 파일 변경을 자동 반영합니다. 배포 빌드는 다음 명령을 사용합니다.

```powershell
Set-Location D:\jyw\beauty-portfolio\frontend
npm.cmd run build
```

## 페이지와 API

메인에는 갤러리를 한 번만 표시합니다. `/gallery`와 기존 `/projects`는 같은 갤러리 컴포넌트를 사용하며 탭은 페이지 이동 없이 전환합니다. Feed 카드는 `/feed/:slug`의 분야별 상세로 이동하며 상세에서 Feed 목록으로 돌아갈 수 있습니다. 기존 `/projects/:id` 경로와 조회 API도 유지합니다. 롯데홈쇼핑·리얼닥터·현장 실습은 About의 텍스트 이력으로만 유지합니다. 기존 API의 category 필드는 호환을 위해 남아 있지만 입력·표시·필터링에 사용하지 않습니다.

## 소개·연락처

About 데스크톱 화면은 `src/components/AboutDesktop.vue`, 전용 스타일은 `src/about.css`입니다. `src/data/portfolio.json`의 `aboutDesktop`에서 폴더 이름·아이콘·팝업 문구·프로필 경로를 변경합니다. Experience는 같은 파일의 기존 career 목록에서 날짜가 입력된 경력을 읽습니다. `public/media/about-profile.svg`는 사진 교체용이며 폴더 아이콘은 `folder-*.svg`입니다. 이 UI는 About에만 적용됩니다.

`frontend/src/data/portfolio.json`에서 소개·수치·기준일·역량·경력·교육·연락처를 관리합니다. 메인 대표 비주얼과 공통 이미지 실패 대체 경로는 `src/portfolio.js`에서 관리합니다. 채널 12.4만 회는 제공된 2026.10.21 캡처의 최근 30일 채널 전체 조회 기준입니다.

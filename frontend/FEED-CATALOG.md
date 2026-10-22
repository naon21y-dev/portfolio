# Feed 사진 도감

- 데이터: `src/data/feed-catalog.json`
- 원본 사진: `public/media/feed/`
- 대표 카드는 `groups`의 `cover`, 대표 사진은 제품의 `images[0]`에서 교체합니다.
- 같은 제품의 추가 사진은 `images`에 추가하면 썸네일 슬롯에 표시됩니다.
- 각 이미지의 `sourceFile`은 첨부 원본 파일명입니다. 중복 첨부 파일도 모두 연결되어 있습니다.
- `brand`, `productName`, `visualPoint`, `concept`, `role`을 제품별로 관리합니다. 미입력 기획 의도·역할은 숨깁니다.
- 브랜드·제품명은 사진에서 읽을 수 있는 표기만 사용했습니다. 제형·발색 설명은 보이는 외관에 한정하며 효능·성과를 추정하지 않습니다.
- 기존 분류 경로 `/feed/styled`, `/feed/texture`, `/feed/ai`는 각각 스킨케어·색조·프래그런스로 연결됩니다.
- API 콘텐츠 요청은 유지하며 실제 추가 Feed 작업물은 기존 API 경로로 표시됩니다. 도감 사진은 로컬 데이터에서 관리합니다.

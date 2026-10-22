<script setup>
import { ref, computed, nextTick, onBeforeUnmount, useId } from 'vue'
import work from './data/work.json'

const activePhotos = ref([]), photoIndex = ref(0), dialog = ref(null)
const selected = computed(() => activePhotos.value[photoIndex.value])
const pages = ref({})
const stageIndex = ref(0)
const sections = computed(() => work.sections.map(section => {
  const groups = new Map()
  section.photos.forEach(photo => {
    const key = photo.galleryGroup || photo.id
    if (!groups.has(key)) groups.set(key, { ...photo, photos: [] })
    groups.get(key).photos.push(photo)
  })
  return { ...section, groups: [...groups.values()] }
}))
function visibleGroups(section) {
  const start = (pages.value[section.id] || 0) * 4
  return section.groups.slice(start, start + 4)
}
function movePhoto(direction) {
  photoIndex.value = Math.max(0, Math.min(activePhotos.value.length - 1, photoIndex.value + direction))
}
const headingId = useId()
const hasText = value => typeof value === 'string' && value.trim().length > 0
let previousFocus, scrollState
function fallback(event) {
  if (event.target.getAttribute('src') !== '/media/work-design.svg') event.target.src = '/media/work-design.svg'
}
async function open(group) {
  previousFocus = document.activeElement
  activePhotos.value = group.photos
  photoIndex.value = 0
  await nextTick()
  if (!dialog.value?.isConnected) return
  scrollState = { y: window.scrollY, html: document.documentElement.style.overflow, position: document.body.style.position, top: document.body.style.top, width: document.body.style.width, overflow: document.body.style.overflow }
  document.documentElement.style.overflow = 'hidden'
  Object.assign(document.body.style, { position: 'fixed', top: `-${scrollState.y}px`, width: '100%', overflow: 'hidden' })
  dialog.value.showModal()
}
function restore() {
  if (scrollState) {
    const saved = scrollState
    scrollState = null
    document.documentElement.style.overflow = saved.html
    Object.assign(document.body.style, { position: saved.position, top: saved.top, width: saved.width, overflow: saved.overflow })
    window.scrollTo({ top: saved.y, behavior: 'instant' })
  }
  if (previousFocus?.isConnected) previousFocus.focus({ preventScroll: true })
  previousFocus = null
}
function close() { dialog.value?.close(); restore() }
function closed() { restore(); activePhotos.value = [] }
function backdrop(event) {
  if (event.target !== dialog.value) return
  const bounds = dialog.value.getBoundingClientRect()
  if (event.clientX < bounds.left || event.clientX > bounds.right || event.clientY < bounds.top || event.clientY > bounds.bottom) close()
}
onBeforeUnmount(close)
</script>
<template>
  <section class="work-page"><div class="work-intro"><p class="eyebrow">WORK / A CREATIVE JOURNEY</p><h1>{{ work.title }}</h1><p>{{ work.intro }}</p></div>
    <div class="work-stage-viewer"><button class="work-stage-arrow work-stage-prev" :disabled="stageIndex === 0" aria-label="이전 스테이지" @click="stageIndex--">←</button><button class="work-stage-arrow work-stage-next" :disabled="stageIndex === sections.length - 1" aria-label="다음 스테이지" @click="stageIndex++">→</button><section v-for="(section, index) in sections" v-show="index === stageIndex" :key="section.id" class="work-chapter" :aria-labelledby="`${section.id}-title`"><div class="work-stage-label">STAGE {{ index + 1 }}</div><div class="work-chapter-copy"><p class="eyebrow">{{ String(index + 1).padStart(2, '0') }} / {{ section.subtitle }}</p><h2 :id="`${section.id}-title`">{{ section.title }}</h2><p v-for="paragraph in section.paragraphs" :key="paragraph">{{ paragraph }}</p></div><div class="work-chapter-navigation" :aria-label="`${section.subtitle} 페이지`"><button :disabled="!(pages[section.id] || 0)" :aria-label="`${section.subtitle} 이전 작업물`" @click="pages[section.id]--">←</button><span aria-live="polite">{{ (pages[section.id] || 0) + 1 }} / {{ Math.ceil(section.groups.length / 4) }}</span><button :disabled="(pages[section.id] || 0) >= Math.ceil(section.groups.length / 4) - 1" :aria-label="`${section.subtitle} 다음 작업물`" @click="pages[section.id] = (pages[section.id] || 0) + 1">→</button></div><div class="work-collage" :class="{ 'four-photos': visibleGroups(section).length === 4 }"><button v-for="group in visibleGroups(section)" :key="group.id" class="scrap-photo" :aria-label="`${group.title || group.alt} 크게 보기, 사진 ${group.photos.length}장`" aria-haspopup="dialog" @click="open(group)"><img :src="group.src" :alt="group.alt" loading="lazy" @error="fallback"><span v-if="group.photos.length > 1" class="work-photo-count">{{ group.photos.length }} PHOTOS</span><span v-if="hasText(group.title)" class="scrap-title">{{ group.title }}</span></button></div></section></div>
    <dialog ref="dialog" class="work-photo-dialog" :aria-labelledby="headingId" @cancel.prevent="close" @close="closed" @click="backdrop" @keydown.left.prevent="movePhoto(-1)" @keydown.right.prevent="movePhoto(1)"><template v-if="selected"><div class="work-photo-toolbar"><h2 :id="headingId">{{ selected.title || '사진 미리보기' }}</h2><button aria-label="사진 닫기" autofocus @click="close">×</button></div><div class="work-photo-content" tabindex="0"><img :src="selected.src" :alt="selected.alt" @error="fallback"><p v-if="selected.placeholder" class="note">교체용 플레이스홀더입니다.</p><p v-if="hasText(selected.role)" class="photo-role"><strong>직접 담당한 역할</strong><br>{{ selected.role }}</p></div><div v-if="activePhotos.length > 1" class="work-modal-navigation"><button :disabled="photoIndex === 0" aria-label="이전 사진" @click="movePhoto(-1)">←</button><span aria-live="polite">{{ photoIndex + 1 }} / {{ activePhotos.length }}</span><button :disabled="photoIndex === activePhotos.length - 1" aria-label="다음 사진" @click="movePhoto(1)">→</button></div></template></dialog>
  </section>
</template>

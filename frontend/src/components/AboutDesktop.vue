<script setup>
import { ref, computed, nextTick, useId, onMounted, onBeforeUnmount } from 'vue'
import { portfolio, pixelAssets } from '../portfolio'
const desktop = portfolio.aboutDesktop
const selected = ref(null), page = ref(0), panel = ref(null), heading = ref(null)
const panelId = useId()
const desktopFrame = ref(null)
let frameObserver
function fitDesktop() {
  const frame = desktopFrame.value
  if (!frame) return
  const top = frame.getBoundingClientRect().top + window.scrollY
  frame.style.setProperty('--desktop-height', `${Math.max(1, window.innerHeight - top - 18)}px`)
}
onMounted(() => {
  frameObserver = new ResizeObserver(fitDesktop)
  const header = document.querySelector('.site-header')
  if (header) frameObserver.observe(header)
  window.addEventListener('resize', fitDesktop)
  fitDesktop()
})
onBeforeUnmount(() => {
  frameObserver?.disconnect()
  window.removeEventListener('resize', fitDesktop)
})
let previousFocus
const lines = computed(() => {
  const folder = selected.value
  if (!folder) return []
  return [
    ...(folder.paragraphs || []).map(text => ({ text })),
    ...(folder.id === 'experience' ? portfolio.career.filter(entry => entry.period).sort((first, second) => first.period.localeCompare(second.period)).map(entry => ({ title: entry.title, text: entry.period })) : []),
    ...(folder.items || []).map(item => ({ title: item.title, text: item.description })),
    ...(folder.steps || []).map((text, index) => ({ title: `STEP ${index + 1}`, text })),
  ]
})
async function open(folder, event) {
  previousFocus = event.currentTarget
  selected.value = folder
  page.value = 0
  await nextTick()
  panel.value?.scrollIntoView({ behavior: 'instant', block: 'nearest' })
  heading.value?.focus({ preventScroll: true })
}
function close() {
  selected.value = null
  previousFocus?.focus({ preventScroll: true })
}
</script>
<template>
  <section ref="desktopFrame" class="about-desktop">
    <div class="desktop-heading"><p class="eyebrow">ABOUT / {{ portfolio.name }}</p><div class="desktop-signal"><img :src="pixelAssets.wifi" alt=""><span>MY DESKTOP</span></div></div>
    <div class="desktop-surface">
      <div class="desktop-profile"><div class="profile-frame"><img :src="desktop.profileImage" alt="정여원 프로필 사진"><span class="profile-file">profile.jpg</span></div><h1>{{ desktop.title }}</h1><p>{{ portfolio.jobTitle }}</p><img class="desktop-cursor" :src="pixelAssets.pointer" alt=""></div>
      <button v-for="folder in desktop.folders" :key="folder.id" class="desktop-folder" :class="[`folder-${folder.id}`, { 'folder-speaking': selected?.id === folder.id }]" :aria-expanded="selected?.id === folder.id" :aria-controls="panelId" @click="open(folder, $event)"><img :src="pixelAssets.folders[folder.id] || folder.icon" alt=""><span>{{ folder.title }}</span></button>
    </div>
    <div v-if="selected" :id="panelId" ref="panel" class="about-conversation" role="region" :aria-labelledby="`${panelId}-title`" @keydown.esc="close">
      <div class="conversation-speaker"><img :src="pixelAssets.folders[selected.id]" alt=""><span>{{ portfolio.koreanName }}</span></div>
      <div class="conversation-heading"><h2 :id="`${panelId}-title`" ref="heading" tabindex="-1">{{ selected.dialogTitle || selected.title }}</h2><button aria-label="대화 마치기" @click="close">×</button></div>
      <div class="conversation-line" aria-live="polite" aria-atomic="true"><h3 v-if="lines[page]?.title">{{ lines[page].title }}</h3><p>{{ lines[page]?.text }}</p></div>
      <div class="conversation-controls"><span>{{ page + 1 }} / {{ lines.length }}</span><div><button :disabled="page === 0" @click="page--">◀ 이전</button><button v-if="page < lines.length - 1" @click="page++">다음 ▶</button><button v-else @click="close">대화 마치기 ✓</button></div></div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed, watch, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { api } from './api'
import InstagramGallery from './components/InstagramGallery.vue'
import AboutDesktop from './components/AboutDesktop.vue'
import PortfolioOutro from './components/PortfolioOutro.vue'
import { portfolio } from './portfolio'
const route = useRoute(), profile = ref(null), contents = ref([]), detail = ref(null), loading = ref(false), error = ref('')
let controller
const heroViewport = ref(null)
let heroObserver
function fitHero() {
  const viewport = heroViewport.value
  if (!viewport) return
  const top = viewport.getBoundingClientRect().top + window.scrollY
  const height = Math.max(1, window.innerHeight - top - 14)
  viewport.style.height = `${height}px`
  const scale = Math.min(viewport.clientWidth / 1100, height / 600)
  viewport.style.setProperty('--hero-scale', scale)
  viewport.style.setProperty('--hero-layout-width', `${viewport.clientWidth / scale}px`)
}
watch(heroViewport, viewport => {
  heroObserver?.disconnect()
  if (!viewport) return
  heroObserver = new ResizeObserver(fitHero)
  heroObserver.observe(viewport)
  const header = document.querySelector('.site-header')
  if (header) heroObserver.observe(header)
  fitHero()
})
window.addEventListener('resize', fitHero)
onUnmounted(() => {
  heroObserver?.disconnect()
  window.removeEventListener('resize', fitHero)
})
const path = computed(() => route.path)
const known = computed(() => ['/', '/about', '/projects', '/gallery', '/contact'].includes(path.value) || !!route.params.id)
async function load() {
  controller?.abort(); controller = new AbortController(); const signal = controller.signal
  loading.value = true; error.value = ''; detail.value = null
  try {
    if (!known.value) return
    if (route.params.id) detail.value = await api.project(route.params.id, signal)
    else if (path.value === '/') [profile.value, contents.value] = await Promise.all([api.profile(signal), api.contents(signal)])
    else if (['/about', '/contact'].includes(path.value)) profile.value = await api.profile(signal)
    else if (path.value === '/projects') contents.value = await api.contents(signal)
    else contents.value = await api.contents(signal)
  } catch (reason) { if (!signal.aborted) error.value = reason.message }
  finally { if (!signal.aborted) loading.value = false }
}
watch(() => route.fullPath, load, { immediate: true })
onUnmounted(() => controller?.abort())
</script>
<template>
  <div v-if="error" class="state" role="alert"><h1>잠시 연결이 끊겼어요.</h1><p>{{ error }}</p><button @click="load">다시 불러오기</button></div>
  <div v-else-if="!known" class="state"><p>404 / PAGE NOT FOUND</p><h1>페이지를 찾을 수 없습니다.</h1><RouterLink to="/">메인으로 돌아가기 →</RouterLink></div>
  <div v-else-if="path === '/'" class="home-screen">
    <div ref="heroViewport" class="home-hero-viewport"><section class="hero"><div class="hero-images"><img v-for="src in portfolio.hero" :key="src" :src="src" alt="콘텐츠 마케터 정여원 프로필 사진"></div><div class="hero-copy"><p class="eyebrow">{{ portfolio.name }} / {{ portfolio.jobTitle }}</p><h1 class="hero-title-ko preserve-lines">{{ portfolio.headline }}</h1><p class="hero-description">{{ portfolio.description }}</p><div class="hero-actions"><RouterLink class="pill hero-link" to="/projects">{{ portfolio.buttons.projects }} ↗</RouterLink><a class="pill hero-link" :href="portfolio.instagram" target="_blank" rel="noopener noreferrer">{{ portfolio.buttons.instagram }} ↗</a></div></div></section></div>
    <nav class="chapter-select" aria-label="포트폴리오 둘러보기"><div class="chapter-select-heading"><span>SELECT A CHAPTER</span><span>어떤 이야기가 궁금하세요?</span></div><RouterLink to="/about"><img src="/media/theme/pixel-kirby-wow.png" alt=""><small>01 / ABOUT</small><strong>저를 소개합니다</strong><span>소개와 역량 살펴보기 →</span></RouterLink><RouterLink to="/work"><img src="/media/theme/game-camera.png" alt=""><small>02 / WORK</small><strong>직접 만든 경험들</strong><span>실습과 디자인 작업 보기 →</span></RouterLink><RouterLink to="/gallery"><img src="/media/theme/game-potion.png" alt=""><small>03 / PROJECTS</small><strong>콘텐츠 아카이브</strong><span>Feed와 Reels 살펴보기 →</span></RouterLink><RouterLink to="/contact"><img src="/media/theme/game-cards.png" alt=""><small>04 / CONTACT</small><strong>다음 이야기 함께하기</strong><span>연락 채널 확인하기 →</span></RouterLink></nav>
    <section class="channel-section"><p class="eyebrow">{{ portfolio.channel.title }}</p><div class="channel-metrics"><div v-for="metric in portfolio.channel.metrics" :key="metric.label"><strong>{{ metric.value }}</strong><span>{{ metric.label }}</span></div></div><p class="note">{{ portfolio.channel.source }}<br>{{ portfolio.channel.note }}</p></section>
    <section class="editorial"><div class="editorial-title">A fresh<br><span>perspective.</span></div><div><p class="eyebrow">THE WAY I SEE BEAUTY</p><h2 class="preserve-lines">{{ portfolio.about.title }}</h2><p>{{ portfolio.about.paragraphs[1] }}</p><RouterLink class="text-link" to="/about">About {{ portfolio.koreanName }} ↗</RouterLink></div></section>
    <section id="instagram-works"><div class="section-heading"><div><p class="eyebrow">INSTAGRAM / FEED & REELS</p><h2 class="section-title-ko">{{ portfolio.gallery.title }}</h2></div></div><p>{{ portfolio.gallery.description }}</p><InstagramGallery :items="contents" /></section>
    <section class="about-strip"><p class="eyebrow">03 / CAPABILITIES</p><div class="section-heading"><h2>콘텐츠 제작 역량</h2><RouterLink class="text-link" to="/about">About me ↗</RouterLink></div><div class="capability-list"><p v-for="(item, index) in portfolio.capabilities" :key="item.title"><span>{{ String(index + 1).padStart(2,'0') }}</span>{{ item.title }}<small>{{ item.description }}</small></p></div></section>
  </div>
  <AboutDesktop v-else-if="path === '/about'" />
  <section class="projects-overview" v-else-if="path === '/projects' || path === '/gallery' || detail"><p class="eyebrow">INSTAGRAM / FEED & REELS</p><h1 class="page-title-ko">{{ portfolio.gallery.title }}</h1><p>{{ portfolio.gallery.description }}</p><RouterLink v-if="detail" class="text-link" to="/gallery">전체 작업물 보기 ↗</RouterLink><InstagramGallery :key="route.fullPath" :items="detail ? detail.contents : contents" /></section>
  <PortfolioOutro v-else-if="path === '/contact'" />
</template>

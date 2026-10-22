<script setup>
import { portfolio } from './portfolio'
import { computed, ref, nextTick } from 'vue'
import PortfolioIntro from './components/PortfolioIntro.vue'
import { useRoute, useRouter } from 'vue-router'
import { pixelAssets } from './portfolio'
const links = [['/about','About'],['/work','Work'],['/projects','Projects'],['/contact','Contact']]
const route = useRoute()
const router = useRouter()
const pages = [['/', 'Home'], ['/about', 'About'], ['/work', 'Work'], ['/gallery', 'Feed & Reels'], ['/contact', 'Contact']]
const pageIndex = computed(() => pages.findIndex(([path]) => path === (route.path.startsWith('/projects') || route.path.startsWith('/feed/') ? '/gallery' : route.path)))
const previousPage = computed(() => pages[pageIndex.value - 1])
const nextPage = computed(() => pageIndex.value >= 0 ? pages[pageIndex.value + 1] : null)
const entered = ref(false)
const transitionPhase = ref('')
const destination = ref('')
async function returnHome(event) {
  if (event.metaKey || event.ctrlKey || event.shiftKey || event.altKey || event.button !== 0) return
  event.preventDefault()
  await router.push('/')
  document.getElementById('main')?.focus({ preventScroll: true })
}
async function enterPortfolio() {
  await router.push('/')
  entered.value = true
  await nextTick()
  document.getElementById('main')?.focus({ preventScroll: true })
}
</script>
<template>
  <PortfolioIntro v-if="!entered" @start="enterPortfolio" />
  <template v-else>
  <div v-if="transitionPhase" class="game-route-transition" :class="transitionPhase" role="status" aria-live="polite" :aria-label="`${destination} 페이지로 이동`"><div class="game-route-pixels" aria-hidden="true"><i v-for="pixel in 960" :key="pixel" :style="pixelStyle(pixel)"></i></div><div class="game-route-message" aria-hidden="true"><span class="game-route-star">✦</span><small>NEXT WORLD</small><strong>{{ destination.toUpperCase() }}</strong><span class="game-route-loading">■ ■ ■</span></div></div>
  <a class="skip" href="#main">본문 바로가기</a>
  <header class="site-header"><RouterLink class="wordmark" to="/" @click="returnHome">{{ portfolio.name }}</RouterLink><nav aria-label="주 메뉴"><RouterLink v-for="item in links" :key="item[0]" :to="item[0]">{{ item[1] }}</RouterLink></nav><a class="header-social" :href="portfolio.instagram" target="_blank" rel="noopener noreferrer" aria-label="인스타그램 새 창으로 열기">↗</a></header>
  <main id="main" tabindex="-1"><RouterView /></main>
  <nav v-if="previousPage || nextPage" class="pixel-page-nav" aria-label="포트폴리오 페이지 이동"><RouterLink v-if="previousPage" :to="previousPage[0]" :aria-label="`이전: ${previousPage[1]}`"><span class="pixel-nav-art"><img :src="pixelAssets.back" alt="BACK"></span><span>{{ previousPage[1] }}</span></RouterLink><RouterLink v-if="nextPage" class="pixel-next-link" :to="nextPage[0]" :aria-label="`다음: ${nextPage[1]}`"><span class="pixel-nav-art"><img :src="pixelAssets.next" alt="NEXT"></span><span>{{ nextPage[1] }}</span></RouterLink></nav>
  <footer><div class="footer-top"><div><p class="eyebrow">{{ portfolio.koreanName }} / {{ portfolio.jobTitle }}</p><h2 class="footer-title-ko">{{ portfolio.contact.title }}</h2><p>{{ portfolio.contact.description }}</p></div><div class="footer-contact"><a :href="`mailto:${portfolio.email}`">{{ portfolio.email }} ↗</a><a :href="portfolio.instagram" target="_blank" rel="noopener noreferrer">{{ portfolio.handle }} ↗</a></div></div><div class="footer-bottom"><span>{{ portfolio.name }} · {{ portfolio.jobTitle }}</span><span>프로젝트 이미지는 플레이스홀더입니다.</span><RouterLink to="/gallery">Reels & Feed ↗</RouterLink></div></footer>
  </template>
</template>

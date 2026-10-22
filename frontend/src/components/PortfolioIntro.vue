<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { portfolio } from '../portfolio'

const emit = defineEmits(['start'])
const loading = ref(true)
const introducing = ref(false)
const leaving = ref(false)
const typedMessage = ref('')
const messages = ['환영합니다.', '제품의 매력을 장면으로 풀어내는\n콘텐츠 마케터 정여원입니다.']
const startButton = ref(null)
let loadingTimer
let typingTimer

function startIntroduction() {
  if (introducing.value) return
  introducing.value = true
  const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches
  function typeMessage(messageIndex, characterIndex = 0) {
    const characters = Array.from(messages[messageIndex])
    typedMessage.value = characters.slice(0, reducedMotion ? characters.length : characterIndex).join('')
    if (!reducedMotion && characterIndex < characters.length) {
      typingTimer = setTimeout(() => typeMessage(messageIndex, characterIndex + 1), 85)
    } else {
      typingTimer = setTimeout(() => {
        if (messageIndex + 1 < messages.length) typeMessage(messageIndex + 1)
        else {
          leaving.value = true
          typingTimer = setTimeout(() => emit('start'), reducedMotion ? 0 : 1200)
        }
      }, messageIndex === 0 ? 1000 : 1600)
    }
  }
  typeMessage(0)
}

function replayLoading() {
  clearTimeout(loadingTimer)
  loading.value = true
  loadingTimer = setTimeout(async () => {
    loading.value = false
    await nextTick()
    startButton.value?.focus({ preventScroll: true })
  }, 3000)
}

onMounted(() => {
  for (const source of portfolio.hero) {
    const image = new Image()
    image.src = source
    image.decode().catch(() => {})
  }
  replayLoading()
})
onBeforeUnmount(() => { clearTimeout(loadingTimer); clearTimeout(typingTimer) })
</script>

<template>
  <section class="portfolio-intro" :class="{ 'intro-leaving': leaving }" aria-label="포트폴리오 인트로">
    <div v-if="loading" class="intro-loading" role="status" aria-live="polite">
      <div class="intro-progress" aria-hidden="true"><span></span></div>
      <p>로딩 중…</p>
    </div>
    <div v-else-if="introducing" class="intro-story">
      <p class="intro-typed" aria-hidden="true">{{ typedMessage }}<span class="intro-caret">▌</span></p>
      <p class="intro-story-accessible" role="status" aria-live="polite">{{ typedMessage === messages[0] ? messages[0] : typedMessage === messages[1] ? messages[1] : '' }}</p>
    </div>
    <div v-else class="intro-ready">
      <h1>{{ portfolio.name }}</h1>
      <img class="intro-character" src="/media/yeowon-intro.png" alt="정여원 프로필 사진">
      <div class="intro-actions">
        <button class="intro-back" aria-label="BACK: 로딩 다시 보기" @click="replayLoading"><img src="/media/theme/pixel-button-frame.svg" alt=""><span>BACK</span></button>
        <button ref="startButton" class="intro-start" aria-label="START: 포트폴리오 들어가기" @click="startIntroduction"><img src="/media/theme/pixel-button-frame.svg" alt=""><span>START</span></button>
      </div>
      <p class="intro-hint">START를 눌러 포트폴리오를 둘러보세요.</p>
    </div>
  </section>
</template>

<script setup>
import { ref, nextTick, watch, onBeforeUnmount } from 'vue'
import { portfolio } from '../portfolio'

const resultHeading = ref(null)
const stage = ref('question')
const question = '저와 함께하시겠습니까?'
const typedQuestion = ref('')
let questionTimer
watch(stage, value => {
  clearTimeout(questionTimer)
  if (value !== 'question') return
  typedQuestion.value = ''
  if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
    typedQuestion.value = question
    return
  }
  const characters = Array.from(question)
  function typeNext() {
    typedQuestion.value = characters.slice(0, Array.from(typedQuestion.value).length + 1).join('')
    if (typedQuestion.value !== question) questionTimer = setTimeout(typeNext, 110)
  }
  questionTimer = setTimeout(typeNext, 200)
}, { immediate: true })
onBeforeUnmount(() => clearTimeout(questionTimer))
let previousOverflow
function restoreOverflow() {
  if (previousOverflow !== undefined) {
    document.documentElement.style.overflow = previousOverflow
    previousOverflow = undefined
  }
}
watch(stage, value => {
  if (value === 'no') {
    previousOverflow = document.documentElement.style.overflow
    document.documentElement.style.overflow = 'hidden'
  } else restoreOverflow()
})
onBeforeUnmount(restoreOverflow)
const celebration = ref(0)
const sparks = Array.from({ length: 96 }, (_, index) => ({
  '--angle': `${(index % 16) * 22.5}deg`,
  '--distance': `${140 + (index % 5) * 25}px`,
  '--burst-x': `${[18, 50, 82, 28, 72, 50][Math.floor(index / 16)]}%`,
  '--burst-y': `${[25, 18, 25, 55, 55, 38][Math.floor(index / 16)]}%`,
  '--delay': `${Math.floor(index / 16) * .32}s`,
  '--spark-color': ['#ffe974', '#f478bd', '#a89aff', '#80dce9'][index % 4],
}))
async function choose(value) {
  if (value === 'yes') celebration.value++
  stage.value = value
  await nextTick()
  resultHeading.value?.focus({ preventScroll: true })
}
</script>

<template>
  <Teleport to="body" :disabled="stage !== 'no'">
  <section class="outro-dialog contact-selection" :class="{ 'outro-game-over': stage === 'no' }" aria-label="함께하기 선택창">
    <div class="outro-toolbar"><span>✦ THANK YOU FOR VISITING</span><span>CONTACT</span></div>
    <div class="outro-content" :class="{ 'outro-clear': stage === 'yes' }">
      <template v-if="stage === 'question'">
        <p class="outro-label">CONTINUE TOGETHER?</p>
        <h1 ref="resultHeading" tabindex="-1" :aria-label="question"><span aria-hidden="true">{{ typedQuestion || '\u00a0' }}</span></h1>
        <div class="outro-choices"><button @click="choose('yes')">YES</button><button @click="choose('no')">NO</button></div>
      </template>
      <template v-else-if="stage === 'yes'">
        <div :key="celebration" class="clear-fireworks" aria-hidden="true"><i v-for="(spark, index) in sparks" :key="index" :style="spark"></i></div>
        <div class="clear-title"><span class="clear-star clear-star-one" aria-hidden="true">✦</span><span class="clear-star clear-star-two" aria-hidden="true">✧</span><h2 ref="resultHeading" tabindex="-1">GAME CLEAR</h2><span class="clear-star clear-star-three" aria-hidden="true">✦</span><span class="clear-star clear-star-four" aria-hidden="true">✧</span></div>
        <div class="outro-contacts"><a href="tel:01038764808"><span>PHONE</span>010-3876-4808</a><a :href="`mailto:${portfolio.email}`"><span>EMAIL</span>{{ portfolio.email }}</a><a :href="portfolio.instagram" target="_blank" rel="noopener noreferrer"><span>INSTAGRAM ↗</span>{{ portfolio.handle }}</a></div>
      </template>
      <template v-else>
        <p class="outro-label">THANK YOU FOR YOUR TIME</p>
        <h2 ref="resultHeading" tabindex="-1">GAME OVER</h2>
        <button class="outro-return" @click="choose('question')">다시 생각하기</button>
      </template>
    </div>
  </section>
  </Teleport>
</template>

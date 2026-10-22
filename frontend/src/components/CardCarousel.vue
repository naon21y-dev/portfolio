<script setup>
import { ref, onMounted, onBeforeUnmount, useId } from 'vue'

defineProps({ label: { type: String, required: true } })
const track = ref(null)
const trackId = useId()
const atStart = ref(true)
const atEnd = ref(false)
let observer

function updatePosition() {
  const element = track.value
  if (!element) return
  atStart.value = element.scrollLeft <= 2
  atEnd.value = element.scrollLeft + element.clientWidth >= element.scrollWidth - 2
}

function move(direction) {
  const element = track.value
  const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches
  element.scrollBy({ left: direction * (element.clientWidth + 28), behavior: reducedMotion ? 'instant' : 'smooth' })
}

onMounted(() => {
  observer = new ResizeObserver(updatePosition)
  observer.observe(track.value)
  updatePosition()
})
onBeforeUnmount(() => observer?.disconnect())
</script>

<template>
  <section class="card-carousel" :aria-label="label" aria-roledescription="캐러셀">
    <div class="card-carousel-controls">
      <button type="button" :disabled="atStart" :aria-controls="trackId" :aria-label="`${label} 이전 보기`" @click="move(-1)">←</button>
      <button type="button" :disabled="atEnd" :aria-controls="trackId" :aria-label="`${label} 다음 보기`" @click="move(1)">→</button>
    </div>
    <div :id="trackId" ref="track" class="card-carousel-track" @scroll.passive="updatePosition"><slot /></div>
  </section>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

const layer = ref(null)
const platforms = ref([])
const position = ref({ x: 0, y: 0 })
const progress = ref(0)
const ready = ref(false)
const items = ['/media/theme/game-camera.png', '/media/theme/game-potion.png', '/media/theme/game-cards.png']
let frame, observer, root, stops = [], media
let currentX = 0, currentY = 0, initialized = false

function measure() {
  if (!root) return
  const origin = root.getBoundingClientRect()
  const intro = root.querySelector('.work-intro').getBoundingClientRect()
  const levels = [...root.querySelectorAll('.work-collage')].map(element => {
    const rect = element.getBoundingClientRect()
    return { x: rect.left - origin.left, y: rect.bottom - origin.top + 24, width: rect.width }
  })
  platforms.value = levels
  stops = [{ x: intro.left - origin.left + 20, y: intro.bottom - origin.top - 50, scroll: 0 }]
  levels.forEach(level => {
    const scroll = origin.top + window.scrollY + level.y - window.innerHeight * .78
    stops.push({ x: level.x + 8, y: level.y - 48, scroll: Math.max(stops.at(-1).scroll + 1, scroll) })
    stops.push({ x: level.x + level.width - 58, y: level.y - 48, scroll: Math.max(stops.at(-1).scroll + 1, scroll + window.innerHeight * .48) })
  })
  ready.value = true
  requestFrame()
}
function requestFrame() { if (!frame) frame = requestAnimationFrame(update) }
function update() {
  frame = null
  if (!stops.length) return
  const scroll = window.scrollY
  let index = stops.findIndex(stop => stop.scroll > scroll)
  if (index < 0) index = stops.length
  const start = stops[Math.max(0, index - 1)], end = stops[Math.min(index, stops.length - 1)]
  const fraction = start === end ? 0 : Math.max(0, Math.min(1, (scroll - start.scroll) / (end.scroll - start.scroll)))
  const targetX = start.x + (end.x - start.x) * fraction
  const targetY = start.y + (end.y - start.y) * fraction
  const smoothing = !initialized || media.matches ? 1 : .18
  currentX += (targetX - currentX) * smoothing
  currentY += (targetY - currentY) * smoothing
  initialized = true
  position.value = { x: currentX, y: currentY }
  progress.value = Math.max(0, index - 1) + fraction
  if (Math.abs(targetX - currentX) + Math.abs(targetY - currentY) > .2) requestFrame()
}
onMounted(() => {
  root = layer.value.parentElement
  media = window.matchMedia('(prefers-reduced-motion: reduce)')
  observer = new ResizeObserver(measure)
  observer.observe(root)
  window.addEventListener('scroll', requestFrame, { passive: true })
  window.addEventListener('resize', measure)
  measure()
})
onBeforeUnmount(() => {
  cancelAnimationFrame(frame)
  observer?.disconnect()
  window.removeEventListener('scroll', requestFrame)
  window.removeEventListener('resize', measure)
})
</script>

<template>
  <div ref="layer" class="work-journey" aria-hidden="true">
    <div v-for="(platform, index) in platforms" :key="index" class="journey-platform" :style="{ left: `${platform.x}px`, top: `${platform.y}px`, width: `${platform.width}px` }">
      <img v-for="(item, itemIndex) in items" :key="item" :src="item" alt="" class="journey-item" :class="{ collected: progress >= index * 2 + 1 + (itemIndex + 1) / 4 }" :style="{ left: `${(itemIndex + 1) * 25}%` }">
    </div>
    <img v-if="ready" src="/media/theme/pixel-kirby-red.png" alt="" class="journey-kirby" :style="{ transform: `translate3d(${position.x}px, ${position.y}px, 0)` }">
  </div>
</template>

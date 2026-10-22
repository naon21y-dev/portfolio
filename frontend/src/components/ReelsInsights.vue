<script setup>
import { ref, computed, nextTick, onBeforeUnmount, useId } from 'vue'
import { init, use } from 'echarts/core'
import { BarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, AriaComponent } from 'echarts/components'
import { SVGRenderer } from 'echarts/renderers'
import gallery from '../data/gallery.json'
import CardCarousel from './CardCarousel.vue'
import { portfolio, imageFallback } from '../portfolio'

use([BarChart, GridComponent, TooltipComponent, AriaComponent, SVGRenderer])
defineProps({ items: { type: Array, default: () => [] } })
const selected = ref(null), dialog = ref(null), chartElement = ref(null)
const imageIndex = ref(0)
const contentImages = computed(() => selected.value?.contentImages?.length ? selected.value.contentImages : selected.value ? [thumbnail(selected.value)] : [])
const headingId = useId()
const labels = gallery.reelInsights.reactionLabels
const format = value => new Intl.NumberFormat('ko-KR').format(value)
const thumbnail = item => item.thumbnailSrc || item.thumbnailUrl || portfolio.fallback
const insights = computed(() => selected.value?.insights)
const total = computed(() => insights.value?.reactions?.reduce((sum, value) => sum + value, 0))
const metrics = computed(() => [['조회수', insights.value?.views], ['반응 합계', total.value], ['공유', insights.value?.reactions?.[2]], ['저장', insights.value?.reactions?.[3]]].filter(([, value]) => value != null))
const auxiliary = computed(() => [['도달', insights.value?.reach, ''], ['조회한 사람', insights.value?.viewers, ''], ['평균 조회 시간', insights.value?.averageWatchSeconds, '초'], ['건너뛰기 비율', insights.value?.skipRate, '%'], ['팔로우', insights.value?.follows, '']].filter(([, value]) => value != null))
const originalLink = computed(() => {
  try { const url = new URL(selected.value?.instagramUrl || selected.value?.externalUrl); return url.protocol === 'https:' && ['instagram.com', 'www.instagram.com'].includes(url.hostname) ? url.href : '' } catch { return '' }
})
let chart, observer, previousFocus, scrollState
function resize() { chart?.resize() }
function disposeChart() { observer?.disconnect(); observer = null; window.removeEventListener('resize', resize); chart?.dispose(); chart = null }
function restore() {
  if (scrollState) {
    const saved = scrollState; scrollState = null
    document.documentElement.style.overflow = saved.html
    Object.assign(document.body.style, { position: saved.position, top: saved.top, width: saved.width, overflow: saved.overflow })
    window.scrollTo({ top: saved.y, behavior: 'instant' })
  }
  if (previousFocus?.isConnected) previousFocus.focus({ preventScroll: true })
  previousFocus = null
}
async function open(item) {
  imageIndex.value = 0
  previousFocus = document.activeElement; selected.value = item
  await nextTick()
  if (!dialog.value?.isConnected) return
  scrollState = { y: window.scrollY, html: document.documentElement.style.overflow, position: document.body.style.position, top: document.body.style.top, width: document.body.style.width, overflow: document.body.style.overflow }
  document.documentElement.style.overflow = 'hidden'
  Object.assign(document.body.style, { position: 'fixed', top: `-${scrollState.y}px`, width: '100%', overflow: 'hidden' })
  dialog.value.showModal()
  if (!chartElement.value || !insights.value?.reactions) return
  chart = init(chartElement.value, null, { renderer: 'svg' })
  chart.setOption({ textStyle: { fontFamily: 'Galmuri11, sans-serif' } })
  chart.setOption({ animation: false, aria: { enabled: true }, grid: { left: 65, right: 40, top: 12, bottom: 30 }, tooltip: { trigger: 'axis', renderMode: 'richText', axisPointer: { type: 'shadow' }, valueFormatter: value => `${format(value)}건` }, xAxis: { type: 'value', minInterval: 1, axisLabel: { color: '#88717f', fontSize: 10 }, splitLine: { lineStyle: { color: '#f0e6ec' } } }, yAxis: { type: 'category', inverse: true, data: labels, axisLine: { show: false }, axisTick: { show: false }, axisLabel: { color: '#65505d', fontSize: 12 } }, series: [{ type: 'bar', data: insights.value.reactions, barMaxWidth: 19, itemStyle: { color: '#bd88a5', borderRadius: [0, 3, 3, 0] }, label: { show: true, position: 'right', color: '#5f4354', formatter: params => format(params.value) } }] })
  observer = new ResizeObserver(resize); observer.observe(chartElement.value)
  window.addEventListener('resize', resize)
}
function close() { disposeChart(); dialog.value?.close(); restore() }
function closed() { disposeChart(); restore(); selected.value = null }
function backdrop(event) {
  if (event.target !== dialog.value) return
  const bounds = dialog.value.getBoundingClientRect()
  if (event.clientX < bounds.left || event.clientX > bounds.right || event.clientY < bounds.top || event.clientY > bounds.bottom) close()
}
onBeforeUnmount(close)
</script>
<template>
  <CardCarousel label="Reels 콘텐츠"><article v-for="item in items" :key="item.id" class="reel-insight-card"><button class="insight-thumbnail" :aria-label="`${item.title} 콘텐츠 설명과 인사이트 보기`" aria-haspopup="dialog" @click="open(item)"><img :src="thumbnail(item)" :alt="`${item.title} 교체용 썸네일`" loading="lazy" @error="imageFallback"><span class="insight-open-icon" aria-hidden="true">↗</span><span class="insight-thumbnail-label">콘텐츠 & 인사이트</span></button><h3>{{ item.title }}</h3></article></CardCarousel>
  <dialog ref="dialog" class="reel-insight-dialog" :aria-labelledby="headingId" @cancel.prevent="close" @close="closed" @click="backdrop"><template v-if="selected"><div class="insight-toolbar"><h2 :id="headingId">{{ [selected.title, selected.brand, selected.productName].filter(Boolean).join(' · ') }}</h2><button class="insight-close" aria-label="인사이트 닫기" autofocus @click="close">×</button></div><div class="insight-body" tabindex="0"><div class="insight-columns"><div class="insight-story"><div class="insight-image-gallery" role="region" aria-label="콘텐츠 이미지" @keydown.left.prevent="imageIndex = Math.max(0, imageIndex - 1)" @keydown.right.prevent="imageIndex = Math.min(contentImages.length - 1, imageIndex + 1)"><img class="insight-preview" :src="contentImages[imageIndex]" :alt="`${selected.title} 콘텐츠 이미지 ${imageIndex + 1}`" @error="imageFallback"><div v-if="contentImages.length > 1" class="insight-image-controls"><button :disabled="imageIndex === 0" aria-label="이전 콘텐츠 이미지" @click="imageIndex--">◀ 이전</button><span aria-live="polite">{{ imageIndex + 1 }} / {{ contentImages.length }}</span><button :disabled="imageIndex === contentImages.length - 1" aria-label="다음 콘텐츠 이미지" @click="imageIndex++">다음 ▶</button></div></div><h3>콘텐츠 설명</h3><p v-if="insights?.description">{{ insights.description }}</p><p v-if="insights?.activity" class="insight-activity">{{ insights.activity }}</p></div><div class="insight-numbers"><div class="insight-metrics"><div v-for="[label, value] in metrics" :key="label"><span>{{ label }}</span><strong>{{ format(value) }}</strong></div></div><template v-if="insights?.reactions"><h3>반응별 수치</h3><div ref="chartElement" class="reaction-chart" role="img" aria-label="좋아요·댓글·공유·저장·리포스트 가로 막대그래프. 동일 수치는 아래 목록에 표시됩니다."></div><ul class="reaction-text"><li v-for="(label, index) in labels" :key="label"><span>{{ label }}</span><strong>{{ format(insights.reactions[index]) }}</strong></li></ul></template><dl v-if="auxiliary.length" class="insight-auxiliary"><template v-for="[label, value, unit] in auxiliary" :key="label"><dt>{{ label }}</dt><dd>{{ format(value) }}{{ unit }}</dd></template></dl></div></div><div v-if="insights?.interpretation" class="insight-interpretation"><h3>성과 해석</h3><p>{{ insights.interpretation }}</p></div><p v-if="!insights" class="note">인사이트를 준비 중입니다.</p><p class="insight-footnote">{{ gallery.reelInsights.note }}</p><p class="insight-footnote">{{ gallery.reelInsights.caution }}</p><a v-if="originalLink" class="text-link" :href="originalLink" target="_blank" rel="noopener noreferrer">인스타그램 원본 보기 ↗</a></div></template></dialog>
</template>

<script setup>
import { ref, computed, useId, defineAsyncComponent } from 'vue'
import gallery from '../data/gallery.json'
import catalog from '../data/feed-catalog.json'
import CardCarousel from './CardCarousel.vue'
import { imageFallback } from '../portfolio'
const ReelsInsights = defineAsyncComponent(() => import('./ReelsInsights.vue'))

const props = defineProps({ items: { type: Array, default: () => [] } })
const activeTab = ref('FEED')
const galleryId = useId()
const reels = computed(() => props.items.filter(item => item.type === 'REEL').map(item => {
  if (['스킨케어', '올리브영 추천템'].includes(item.title)) {
    const replacement = gallery.items.find(local => local.type === 'REEL' && local.title === '올리브영 추천템')
    return { ...item, ...replacement, externalUrl: '' }
  }
  const media = gallery.items.find(local => local.contentImages?.length && local.instagramUrl === (item.instagramUrl || item.externalUrl))
  return media ? { ...item, title: media.title, thumbnailSrc: media.thumbnailSrc || media.contentImages[0], contentImages: media.contentImages } : item
}))
function moveTab(event, index) {
  if (!['ArrowLeft', 'ArrowRight', 'Home', 'End'].includes(event.key)) return
  event.preventDefault()
  const next = event.key === 'Home' ? 0 : event.key === 'End' ? 1 : (index + 1) % 2
  activeTab.value = gallery.tabs[next][0]
  event.currentTarget.parentElement.querySelectorAll('[role=tab]')[next].focus()
}
</script>
<template>
  <div class="instagram-gallery">
    <div class="gallery-tabs" role="tablist" aria-label="작업물 형식"><button v-for="(tab, index) in gallery.tabs" :id="`${galleryId}-${tab[0]}`" :key="tab[0]" role="tab" :aria-selected="activeTab === tab[0]" :aria-controls="`${galleryId}-panel`" :tabindex="activeTab === tab[0] ? 0 : -1" @click="activeTab = tab[0]" @keydown="moveTab($event, index)">{{ tab[1] }}</button></div>
    <div :id="`${galleryId}-panel`" role="tabpanel" :aria-labelledby="`${galleryId}-${activeTab}`">
      <CardCarousel v-if="activeTab === 'FEED'" label="Feed 카테고리"><RouterLink v-for="group in catalog.groups" :key="group.slug" class="feed-cover" :to="`/feed/${group.slug}`"><div class="feed-cover-image"><img :src="group.cover" :alt="`${group.title} 피드 작업물`" loading="lazy" @error="imageFallback"><span class="sample">OPEN COLLECTION ↗</span></div><h3>{{ group.title }} <span aria-hidden="true">↗</span></h3></RouterLink></CardCarousel>
      <ReelsInsights v-else-if="reels.length" :items="reels" />
      <p v-else class="gallery-empty" role="status">영상을 준비 중입니다.</p>
    </div>
  </div>
</template>

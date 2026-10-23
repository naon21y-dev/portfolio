<script setup>
import { ref, computed, useId, defineAsyncComponent } from 'vue'
import gallery from '../data/gallery.json'
import catalog from '../data/feed-catalog.json'
import CardCarousel from './CardCarousel.vue'
import { imageFallback } from '../portfolio'
const ReelsInsights = defineAsyncComponent(() => import('./ReelsInsights.vue'))

const props = defineProps({ items: { type: Array, default: () => [] }, inventory: Boolean })
const categorySlug = ref(catalog.groups[0]?.slug)
const selectedCategory = computed(() => catalog.groups.find(group => group.slug === categorySlug.value) || catalog.groups[0])
const categoryCount = slug => catalog.products.filter(product => product.category === slug).length
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
  <div class="instagram-gallery" :class="{ 'project-inventory': inventory }">
    <div v-if="inventory" class="project-inventory-bar"><span>✦ CONTENT INVENTORY</span><span>FEED {{ catalog.products.length }} · REELS {{ reels.length }}</span></div>
    <div class="gallery-tabs" role="tablist" aria-label="작업물 형식"><button v-for="(tab, index) in gallery.tabs" :id="`${galleryId}-${tab[0]}`" :key="tab[0]" role="tab" :aria-selected="activeTab === tab[0]" :aria-controls="`${galleryId}-panel`" :tabindex="activeTab === tab[0] ? 0 : -1" @click="activeTab = tab[0]" @keydown="moveTab($event, index)">{{ tab[1] }}</button></div>
    <div :id="`${galleryId}-panel`" role="tabpanel" :aria-labelledby="`${galleryId}-${activeTab}`">
      <div v-if="inventory && activeTab === 'FEED'" class="project-inventory-layout">
        <div class="project-category-bag"><div class="project-bag-label">MY COLLECTIONS <span>{{ catalog.groups.length }} CATEGORIES</span></div><p>슬롯을 선택하고 컬렉션을 열어보세요.</p><div class="project-category-grid"><button v-for="(group, index) in catalog.groups" :key="group.slug" class="project-category-slot" :class="{ 'is-selected': selectedCategory.slug === group.slug }" :aria-pressed="selectedCategory.slug === group.slug" @click="categorySlug = group.slug"><small>{{ String(index + 1).padStart(2, '0') }}</small><img :src="group.cover" :alt="`${group.title} 대표 이미지`" decoding="async" @error="imageFallback"><strong>{{ group.title }}</strong><span>{{ categoryCount(group.slug) }} ITEMS</span></button><div class="project-category-slot project-slot-coming" aria-label="06 아이템 준비 중"><small>06</small><span class="project-coming-icon" aria-hidden="true">＋</span><strong>아이템 준비 중</strong><span>COMING SOON</span></div></div></div>
        <article class="project-selected-category" aria-live="polite"><div class="project-bag-label">✦ SELECTED COLLECTION</div><div class="project-category-preview"><img :src="selectedCategory.cover" :alt="`${selectedCategory.title} 피드 작업물`" decoding="async" @error="imageFallback"></div><div class="project-category-description"><small>COLLECTION / {{ selectedCategory.slug.toUpperCase() }}</small><h2>{{ selectedCategory.title }} 작업물</h2><p>작업물의 일부를 담았습니다. 컬렉션을 열면 제품별 사진과 구성 설명, 추가 이미지를 살펴볼 수 있습니다.</p><RouterLink :to="`/feed/${selectedCategory.slug}`">컬렉션 열기 ▶</RouterLink></div></article>
      </div>
      <CardCarousel v-else-if="activeTab === 'FEED'" label="Feed 카테고리"><RouterLink v-for="group in catalog.groups" :key="group.slug" class="feed-cover" :to="`/feed/${group.slug}`"><div class="feed-cover-image"><img :src="group.cover" :alt="`${group.title} 피드 작업물`" loading="lazy" @error="imageFallback"><span class="sample">OPEN COLLECTION ↗</span></div><h3>{{ group.title }} <span aria-hidden="true">↗</span></h3></RouterLink></CardCarousel>
      <ReelsInsights v-else-if="reels.length" :items="reels" />
      <p v-else class="gallery-empty" role="status">영상을 준비 중입니다.</p>
    </div>
  </div>
</template>

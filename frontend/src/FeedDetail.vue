<script setup>
import { ref, computed, watch, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { api } from './api'
import gallery from './data/gallery.json'
import WorkEntry from './components/WorkEntry.vue'
import FeedCatalog from './components/FeedCatalog.vue'
import catalog from './data/feed-catalog.json'

const route = useRoute(), items = ref([]), loading = ref(false), error = ref('')
const group = computed(() => catalog.groups.find(item => item.slug === route.params.slug || item.aliases.includes(route.params.slug)))
const products = computed(() => catalog.products.filter(item => item.category === group.value?.slug))
const works = computed(() => items.value.filter(item => item.type === 'FEED' && !item.sample && item.projectId === gallery.feedGroups.find(entry => entry.legacySlug === group.value?.slug)?.projectId))
let controller
async function load() {
  controller?.abort(); controller = new AbortController(); const signal = controller.signal
  items.value = []; error.value = ''; loading.value = !!group.value
  if (!group.value) return
  try { items.value = await api.contents(signal) }
  catch (reason) { if (!signal.aborted) error.value = reason.message }
  finally { if (!signal.aborted) loading.value = false }
}
watch(() => route.params.slug, load, { immediate: true })
onUnmounted(() => controller?.abort())
</script>
<template>
  <section class="feed-detail catalog-page"><RouterLink class="catalog-back" to="/gallery">◀ 목록으로 돌아가기</RouterLink><template v-if="group"><p class="eyebrow">VISUAL COLLECTION / {{ group.slug.toUpperCase() }}</p><h1 class="page-title-ko">{{ group.title }} 작업물 도감</h1><p class="catalog-count">ITEMS {{ String(products.length).padStart(2, '0') }}</p><FeedCatalog :key="group.slug" :products="products" /><p v-if="loading" role="status" class="note">연동 작업물을 확인하고 있습니다.</p><p v-else-if="error" role="status" class="note">추가 API 작업물을 불러오지 못했습니다. 사진 도감은 계속 볼 수 있습니다. <button @click="load">다시 연결</button></p><div v-else-if="works.length" class="feed-story"><WorkEntry v-for="item in works" :key="item.id" :item="item" /></div></template><div v-else class="state"><h1>페이지를 찾을 수 없습니다.</h1></div></section>
</template>

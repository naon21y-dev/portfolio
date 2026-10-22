<script setup>
import { ref, computed, nextTick, onBeforeUnmount, useId } from 'vue'

const props = defineProps({ products: { type: Array, default: () => [] } })
const activeId = ref(null)
const activeProduct = computed(() => props.products.find(product => product.id === activeId.value) || props.products[0])
const dialog = ref(null), selected = ref(null), imageIndex = ref(0)
const titleId = useId()
let previousFocus, scrollState
async function open(product, index, event) {
  previousFocus = event.currentTarget
  selected.value = product
  imageIndex.value = index
  await nextTick()
  scrollState = { y: window.scrollY, overflow: document.documentElement.style.overflow, position: document.body.style.position, top: document.body.style.top, width: document.body.style.width }
  document.documentElement.style.overflow = 'hidden'
  Object.assign(document.body.style, { position: 'fixed', top: `-${scrollState.y}px`, width: '100%' })
  dialog.value.showModal()
}
function restore() {
  if (scrollState) {
    const saved = scrollState
    scrollState = null
    document.documentElement.style.overflow = saved.overflow
    Object.assign(document.body.style, { position: saved.position, top: saved.top, width: saved.width })
    window.scrollTo({ top: saved.y, behavior: 'instant' })
  }
  previousFocus?.isConnected && previousFocus.focus({ preventScroll: true })
  previousFocus = null
}
function close() { dialog.value?.close(); restore() }
function backdrop(event) {
  if (event.target !== dialog.value) return
  const bounds = dialog.value.getBoundingClientRect()
  if (event.clientX < bounds.left || event.clientX > bounds.right || event.clientY < bounds.top || event.clientY > bounds.bottom) close()
}
function move(event) {
  if (event.key === 'ArrowLeft') { event.preventDefault(); imageIndex.value = Math.max(0, imageIndex.value - 1) }
  if (event.key === 'ArrowRight') { event.preventDefault(); imageIndex.value = Math.min(selected.value.images.length - 1, imageIndex.value + 1) }
}
onBeforeUnmount(close)
</script>

<template>
  <div class="feed-catalog inventory-catalog" :class="{ 'inventory-makeup': products[0]?.category === 'makeup' }">
    <aside class="inventory-bag" aria-label="제품 인벤토리">
      <div class="inventory-bar"><span>✦ INVENTORY</span><span>{{ products.length }} ITEMS</span></div>
      <p class="inventory-hint">아이템을 눌러 작업물을 살펴보세요.</p>
      <div class="inventory-grid"><button v-for="(product, index) in products" :key="product.id" class="inventory-slot" :class="{ 'is-selected': activeProduct?.id === product.id }" :aria-pressed="activeProduct?.id === product.id" @click="activeId = product.id"><span class="inventory-number">{{ String(index + 1).padStart(2, '0') }}</span><img decoding="async" :src="product.images[0].src" :alt="product.productName" loading="lazy"><span v-if="product.images.some(photo => photo.aiGenerated)" class="inventory-ai-badge">AI 이미지 포함</span><span class="inventory-name">{{ product.brand || product.productName }}</span></button></div>
      <p class="inventory-footer">SELECT AN ITEM ▶</p>
    </aside>
    <article v-for="product in activeProduct ? [activeProduct] : []" :key="product.id" class="catalog-entry inventory-detail" :aria-labelledby="`${titleId}-${product.id}`">
      <div class="catalog-photo"><div class="catalog-window-label">✦ SELECTED ITEM / 선택한 아이템</div><button class="catalog-main-photo" :aria-label="`${product.productName} 대표 사진 크게 보기`" aria-haspopup="dialog" @click="open(product, 0, $event)"><img decoding="async" :src="product.images[0].src" :alt="product.images[0].alt"><span>확대해서 보기 ↗</span></button><p v-if="product.images[0].aiGenerated" class="catalog-ai-notice">AI로 생성한 이미지입니다.</p></div>
      <div class="catalog-info"><span class="catalog-info-label">ITEM INFORMATION</span><h2 :id="`${titleId}-${product.id}`">{{ [product.brand, product.productName].filter(Boolean).join(' · ') }}</h2><dl><dt>VISUAL POINT</dt><dd>{{ product.visualPoint }}</dd><template v-if="product.concept"><dt>CONCEPT</dt><dd>{{ product.concept }}</dd></template><template v-if="product.role"><dt>ROLE</dt><dd>{{ product.role }}</dd></template></dl></div>
      <div v-if="product.images.length > 1" class="catalog-slots"><p>ADDITIONAL VIEWS <span>{{ product.images.length - 1 }}</span></p><div><button v-for="(photo, photoIndex) in product.images.slice(1)" :key="photo.src" :aria-label="`${product.productName} 추가 사진 ${photoIndex + 1} 크게 보기`" aria-haspopup="dialog" @click="open(product, photoIndex + 1, $event)"><span v-if="photo.aiGenerated" class="catalog-thumb-ai">AI</span><img decoding="async" :src="photo.src" :alt="photo.alt" loading="lazy"></button></div></div>
    </article>
    <dialog ref="dialog" class="catalog-dialog" :aria-labelledby="titleId" @cancel.prevent="close" @close="restore" @click="backdrop" @keydown="move"><template v-if="selected"><div class="catalog-dialog-bar"><h2 :id="titleId">{{ selected.productName }}</h2><button aria-label="이미지 닫기" autofocus @click="close">×</button></div><div class="catalog-dialog-image"><img decoding="async" :src="selected.images[imageIndex].src" :alt="selected.images[imageIndex].alt"></div><p v-if="selected.images[imageIndex].aiGenerated" class="catalog-ai-notice" aria-live="polite">AI로 생성한 이미지입니다.</p><div class="catalog-modal-controls"><button :disabled="imageIndex === 0" @click="imageIndex--">◀ 이전</button><span aria-live="polite">{{ imageIndex + 1 }} / {{ selected.images.length }}</span><button :disabled="imageIndex === selected.images.length - 1" @click="imageIndex++">다음 ▶</button></div></template></dialog>
  </div>
</template>

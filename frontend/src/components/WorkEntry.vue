<script setup>
import { portfolio, imageFallback } from '../portfolio'
defineProps({ item: { type: Object, required: true } })
const hasText = value => typeof value === 'string' && value.trim().length > 0
function originalLink(item) {
  try { const url = new URL(item.instagramUrl || item.externalUrl); return url.protocol === 'https:' && ['instagram.com', 'www.instagram.com'].includes(url.hostname) ? url.href : '' } catch { return '' }
}
</script>
<template>
  <article class="instagram-work"><div class="work-visual"><div class="work-media"><img :src="item.imageUrl || item.thumbnailSrc || item.thumbnailUrl || portfolio.fallback" :alt="item.title" loading="lazy" @error="imageFallback"><span v-if="item.sample" class="sample">PLACEHOLDER</span></div></div><div class="work-description"><h3>{{ [item.brand, item.productName].filter(hasText).join(' · ') || item.title }}</h3><p v-if="hasText(item.caption)" class="work-caption">{{ item.caption }}</p><dl v-if="hasText(item.intention) || hasText(item.role)"><template v-if="hasText(item.intention)"><dt>기획 의도</dt><dd>{{ item.intention }}</dd></template><template v-if="hasText(item.role)"><dt>직접 담당한 역할</dt><dd>{{ item.role }}</dd></template></dl><a v-if="originalLink(item)" class="text-link" :href="originalLink(item)" target="_blank" rel="noopener noreferrer">인스타그램 원본 게시물 ↗</a></div></article>
</template>

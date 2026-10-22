import content from './data/portfolio.json'

export const pixelAssets = {
  folders: { about: '/media/theme/pixel-kirby-wow.png', experience: '/media/theme/pixel-kirby-red.png', skills: '/media/theme/pixel-kirby-blue.png', approach: '/media/theme/pixel-kirby-rest.png', personality: '/media/theme/pixel-kirby-yawn.png' },
  pointer: '/media/theme/pink-cursor.svg',
  wifi: '/media/theme/pixel-wifi.png',
  next: '/media/theme/pixel-nav-next.svg',
  back: '/media/theme/pixel-intro-back.svg',
}

export const portfolio = {
  ...content,
  aboutDesktop: { ...content.aboutDesktop, profileImage: '/media/yeowon-about.png' },
  hero: ['/media/yeowon-hero.png'],
  fallback: '/media/editorial-compact.svg',
}
export function imageFallback(event) {
  if (event.target.getAttribute('src') !== portfolio.fallback) event.target.src = portfolio.fallback
}

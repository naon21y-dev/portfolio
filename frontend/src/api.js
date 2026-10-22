const base = import.meta.env.VITE_API_BASE_URL || '/api/v1'
async function get(path, signal) {
  const response = await fetch(`${base}${path}`, { signal })
  if (!response.ok) throw new Error(response.status === 404 ? '요청한 페이지를 찾을 수 없습니다.' : '데이터를 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.')
  return response.json()
}
export const api = { profile: signal => get('/profile', signal), projects: signal => get('/projects', signal), project: (id, signal) => get(`/projects/${encodeURIComponent(id)}`, signal), contents: signal => get('/contents', signal) }

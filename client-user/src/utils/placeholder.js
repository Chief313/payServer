/**
 * 根据商品 ID 生成渐变色占位图（无封面图时使用）
 * @param {number|string} id 商品 ID
 * @returns {string} SVG data URI
 */
export function productPlaceholder(id) {
  const hues = [220, 260, 320, 180, 30, 200, 280, 350]
  const hue = hues[Number(id) % hues.length]
  const hue2 = (hue + 40) % 360
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="400" height="400">
    <defs>
      <linearGradient id="g" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" style="stop-color:hsl(${hue},70%,55%)"/>
        <stop offset="100%" style="stop-color:hsl(${hue2},65%,45%)"/>
      </linearGradient>
    </defs>
    <rect fill="url(#g)" width="400" height="400"/>
    <text x="50%" y="52%" text-anchor="middle" fill="rgba(255,255,255,0.9)" font-size="18" font-family="sans-serif">PayServer</text>
  </svg>`
  return `data:image/svg+xml,${encodeURIComponent(svg)}`
}

/** 分类图标映射 */
export const categoryIcons = {
  数码电子: '📱',
  家用电器: '🏠',
  服装鞋包: '👔',
  食品生鲜: '🍎',
  美妆护肤: '💄',
  家居生活: '🛋️',
  default: '📦',
}

/**
 * 获取分类图标
 * @param {string} name 分类名称
 */
export function getCategoryIcon(name) {
  if (!name) return categoryIcons.default
  for (const [key, icon] of Object.entries(categoryIcons)) {
    if (key !== 'default' && name.includes(key)) return icon
  }
  return categoryIcons.default
}

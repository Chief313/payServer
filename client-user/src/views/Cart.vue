<template>
  <div class="cart-page">
    <h2 class="page-title">购物车</h2>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="items.length === 0" class="empty">
      <p>购物车是空的</p>
      <router-link to="/" class="btn-primary">去逛逛</router-link>
    </div>
    <div v-else>
      <div class="cart-list">
        <div v-for="item in items" :key="item.id" class="cart-item">
          <img :src="item.coverUrl || placeholder" class="cover" @error="onImgError" />
          <div class="item-info">
            <h3>{{ item.productName }}</h3>
            <p class="sku">{{ item.skuName }}</p>
            <p class="price">¥{{ item.price }}</p>
          </div>
          <div class="quantity">
            <button @click="changeQty(item, item.quantity - 1)" :disabled="item.quantity <= 1">-</button>
            <span>{{ item.quantity }}</span>
            <button @click="changeQty(item, item.quantity + 1)" :disabled="item.quantity >= item.stock">+</button>
          </div>
          <div class="subtotal">¥{{ item.subtotal }}</div>
          <button class="btn-remove" @click="handleRemove(item)">删除</button>
        </div>
      </div>
      <div class="cart-footer">
        <div class="total">合计：<span>¥{{ totalAmount }}</span></div>
        <button class="btn-checkout" @click="handleCheckout">结算</button>
      </div>
    </div>

    <div v-if="showAddressDialog" class="dialog-overlay" @click.self="showAddressDialog = false">
      <div class="dialog">
        <h3>选择收货地址</h3>
        <div v-if="addresses.length === 0" class="no-address">
          <p>暂无收货地址，请先添加</p>
          <div class="address-form">
            <input v-model="newAddress.receiver" placeholder="收货人" />
            <input v-model="newAddress.phone" placeholder="手机号" />
            <input v-model="newAddress.province" placeholder="省份" />
            <input v-model="newAddress.city" placeholder="城市" />
            <input v-model="newAddress.district" placeholder="区县" />
            <input v-model="newAddress.detail" placeholder="详细地址" />
            <button @click="handleAddAddress">添加地址并下单</button>
          </div>
        </div>
        <div v-else class="address-list">
          <label v-for="addr in addresses" :key="addr.id" class="address-item">
            <input type="radio" v-model="selectedAddressId" :value="addr.id" />
            <span>{{ addr.receiver }} {{ addr.phone }} — {{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}</span>
          </label>
          <button class="btn-primary" @click="submitOrder">确认下单</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCart, updateCartItem, removeCartItem } from '../api/cart'
import { getAddresses, addAddress } from '../api/address'
import { createOrder } from '../api/order'

const router = useRouter()
const loading = ref(false)
const items = ref([])
const showAddressDialog = ref(false)
const addresses = ref([])
const selectedAddressId = ref(null)
const placeholder = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="80" height="80"><rect fill="#f0f0f0" width="80" height="80"/></svg>')

const newAddress = ref({
  receiver: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
})

const totalAmount = computed(() =>
  items.value.reduce((sum, i) => sum + Number(i.subtotal || 0), 0).toFixed(2),
)

function onImgError(e) {
  e.target.src = placeholder
}

async function fetchCart() {
  loading.value = true
  try {
    items.value = await getCart()
  } finally {
    loading.value = false
  }
}

async function changeQty(item, qty) {
  if (qty < 1 || qty > item.stock) return
  await updateCartItem(item.id, qty)
  fetchCart()
}

async function handleRemove(item) {
  if (!confirm('确定删除该商品吗？')) return
  await removeCartItem(item.id)
  fetchCart()
}

async function handleCheckout() {
  addresses.value = await getAddresses()
  if (addresses.value.length > 0) {
    selectedAddressId.value = addresses.value.find((a) => a.isDefault === 1)?.id || addresses.value[0].id
  }
  showAddressDialog.value = true
}

async function handleAddAddress() {
  const addr = await addAddress(newAddress.value)
  selectedAddressId.value = addr.id
  await submitOrder()
}

async function submitOrder() {
  if (!selectedAddressId.value) {
    alert('请选择收货地址')
    return
  }
  const res = await createOrder(selectedAddressId.value)
  showAddressDialog.value = false
  router.push(`/pay/${res.orderNo}`)
}

onMounted(fetchCart)
</script>

<style scoped>
.page-title {
  margin-bottom: 20px;
  font-size: 22px;
}

.loading, .empty {
  text-align: center;
  padding: 60px;
  color: #999;
}

.cart-list {
  background: #fff;
  border-radius: 8px;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  gap: 16px;
}

.cover {
  width: 80px;
  height: 80px;
  object-fit: contain;
  background: #fafafa;
  border-radius: 4px;
}

.item-info {
  flex: 1;
}

.item-info h3 {
  font-size: 15px;
  margin-bottom: 4px;
}

.sku {
  color: #999;
  font-size: 13px;
}

.price {
  color: #e4393c;
  font-weight: 600;
}

.quantity {
  display: flex;
  align-items: center;
  gap: 8px;
}

.quantity button {
  width: 28px;
  height: 28px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
  border-radius: 4px;
}

.subtotal {
  width: 80px;
  text-align: right;
  font-weight: 600;
}

.btn-remove {
  color: #999;
  background: none;
  border: none;
  cursor: pointer;
}

.cart-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 24px;
  padding: 20px;
  background: #fff;
  margin-top: 16px;
  border-radius: 8px;
}

.total span {
  color: #e4393c;
  font-size: 24px;
  font-weight: 700;
}

.btn-checkout, .btn-primary {
  background: #e4393c;
  color: #fff;
  border: none;
  padding: 10px 32px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 15px;
  text-decoration: none;
}

.dialog-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
}

.dialog {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  width: 480px;
  max-width: 90vw;
}

.dialog h3 {
  margin-bottom: 16px;
}

.address-item {
  display: flex;
  gap: 8px;
  padding: 12px;
  border: 1px solid #eee;
  border-radius: 4px;
  margin-bottom: 8px;
  cursor: pointer;
}

.address-form input {
  display: block;
  width: 100%;
  padding: 8px 12px;
  margin-bottom: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.address-form button, .address-list .btn-primary {
  width: 100%;
  margin-top: 12px;
}
</style>

<template>
  <div class="chat-widget">
    <button v-if="!open" class="fab" @click="open = true" title="AI 客服">
      💬
    </button>
    <div v-else class="panel">
      <div class="panel-header">
        <span>AI 客服</span>
        <button class="close-btn" @click="open = false">×</button>
      </div>
      <div class="messages" ref="messagesRef">
        <div v-for="(msg, idx) in messages" :key="idx" :class="['message', msg.role]">
          <div class="bubble">{{ msg.content }}</div>
        </div>
        <div v-if="sending" class="message assistant">
          <div class="bubble typing">...</div>
        </div>
      </div>
      <div class="input-area">
        <input v-model="input" placeholder="有问题？问我吧" @keyup.enter="send" :disabled="sending" />
        <button @click="send" :disabled="sending || !input.trim()">发送</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { sendChat } from '../api/chat'

const open = ref(false)
const messages = ref([{ role: 'assistant', content: '您好，有什么可以帮您？' }])
const input = ref('')
const sending = ref(false)
const sessionId = ref(null)
const messagesRef = ref(null)

async function send() {
  const text = input.value.trim()
  if (!text || sending.value) return
  messages.value.push({ role: 'user', content: text })
  input.value = ''
  sending.value = true
  scrollBottom()
  try {
    const res = await sendChat({ sessionId: sessionId.value, message: text })
    sessionId.value = res.sessionId
    messages.value.push({ role: 'assistant', content: res.reply })
  } catch {
    messages.value.push({ role: 'assistant', content: '服务暂不可用' })
  } finally {
    sending.value = false
    scrollBottom()
  }
}

function scrollBottom() {
  nextTick(() => {
    if (messagesRef.value) messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  })
}
</script>

<style scoped>
.fab {
  position: fixed;
  right: 24px;
  bottom: 24px;
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: #e4393c;
  color: #fff;
  border: none;
  font-size: 24px;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(228, 57, 60, 0.4);
  z-index: 999;
}

.panel {
  position: fixed;
  right: 24px;
  bottom: 24px;
  width: 360px;
  height: 480px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  z-index: 999;
  overflow: hidden;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #e4393c;
  color: #fff;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  color: #fff;
  font-size: 22px;
  cursor: pointer;
  line-height: 1;
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.message {
  margin-bottom: 10px;
  display: flex;
}

.message.user {
  justify-content: flex-end;
}

.bubble {
  max-width: 80%;
  padding: 8px 12px;
  border-radius: 10px;
  font-size: 13px;
  line-height: 1.5;
}

.message.user .bubble {
  background: #e4393c;
  color: #fff;
}

.message.assistant .bubble {
  background: #f0f0f0;
  color: #333;
}

.input-area {
  display: flex;
  padding: 10px;
  border-top: 1px solid #eee;
  gap: 8px;
}

.input-area input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 16px;
  font-size: 13px;
  outline: none;
}

.input-area button {
  padding: 8px 16px;
  background: #e4393c;
  color: #fff;
  border: none;
  border-radius: 16px;
  cursor: pointer;
  font-size: 13px;
}

.input-area button:disabled {
  opacity: 0.5;
}
</style>
